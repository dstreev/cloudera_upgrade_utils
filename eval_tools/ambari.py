import copy
import collections
import re

# List of services to translate
supported_services = ['NAMENODE', 'HBASE_REGIONSERVER', 'KAFKA_BROKER', 'HISTORYSERVER', 'DATANODE',
                      'ZOOKEEPER_SERVER', 'HIVE_SERVER', 'RESOURCEMANAGER', 'HBASE_MASTER',
                      'HIVE_METASTORE', 'ZKFC', 'SPARK2_JOBHISTORYSERVER', 'JOURNALNODE',
                      'OOZIE_SERVER', 'NODEMANAGER', 'TEZ_CLIENT', 'SPARK2_CLIENT']

master_services = ['NAMENODE', 'HBASE_REGIONSERVER', 'HISTORYSERVER',
                   'ZOOKEEPER_SERVER', 'HIVE_SERVER', 'RESOURCEMANAGER', 'HBASE_MASTER',
                   'HIVE_METASTORE', 'ZKFC', 'SPARK2_JOBHISTORYSERVER', 'JOURNALNODE',
                   'OOZIE_SERVER']

def get_host_group_mask(item, componentDict):
    location = 0
    components = item["host_components"]
    for component in components:
        for ckey, cvalue in component.items():
            if ckey == "HostRoles":
                for hkey, hvalue in cvalue.items():
                    if hkey == "component_name":
                        location = location | componentDict[hvalue]
    return location


# Mismatches happen here.  Use the bp version
# def get_component_dictionary(layout):
#     component_dict = {}
#     items = layout['items']
#     for item in items:
#         components = item["host_components"]
#         for component in components:
#             for ckey, cvalue in component.items():
#                 if ckey == "HostRoles":
#                     for hkey, hvalue in cvalue.items():
#                         if hkey == "component_name":
#                             if hvalue not in component_dict.keys():
#                                 dl = len(component_dict)
#                                 if dl == 0:
#                                     component_dict[hvalue] = 1
#                                 elif dl == 1:
#                                     component_dict[hvalue] = 2
#                                 else:
#                                     component_dict[hvalue] = 2 ** dl
#     return component_dict


def get_component_dictionary_from_bp(blueprint):
    component_dict = {}
    host_groups = blueprint['host_groups']
    for host_group in host_groups:
        components = host_group["components"]
        for component in components:
            for ckey, cvalue in component.items():
                if ckey == "name":
                    if cvalue not in component_dict.keys():
                        dl = len(component_dict)
                        if dl == 0:
                            component_dict[cvalue] = 1
                        elif dl == 1:
                            component_dict[cvalue] = 2
                        else:
                            component_dict[cvalue] = 2 ** dl
    return component_dict


def calc_host_group_bit_masks(hostgroups, componentDict):
    host_groups_bitmask = {}
    # working_host_groups = copy.deepcopy(hostgroups)
    for hostgroup in hostgroups:
        hgbitmask = 0
        for component in hostgroup['components']:
            try:
                hgbitmask = hgbitmask | componentDict[component['name']]
            except:
                check = 'Component in Host that is not in the Layouts: ' + component['name']
        host_groups_bitmask[hostgroup['name']] = hgbitmask
    return host_groups_bitmask


def calc_host_bit_masks(layout_hosts, componentDict):
    host_bitmask = {}
    # working_host_groups = copy.deepcopy(hostgroups)
    for item in layout_hosts:
        hgbitmask = 0
        host_detail = {}
        for component in item['host_components']:
            try:
                hgbitmask = hgbitmask | componentDict[component['HostRoles']['component_name']]
            except:
                check = 'Component in Host that is not in the Layouts: ' + component['HostRoles']['component_name']
        host_detail['host_name'] = item['Hosts']['host_name']
        host_detail['bit_mask'] = hgbitmask
        if 'rack_info' in item['Hosts'].keys():
            host_detail['rack_info'] = item['Hosts']['rack_info']
        host_bitmask[item['Hosts']['host_name']] = host_detail
    return host_bitmask


def build_creation_template_from_layout(blueprint, layout):
    print (" The Blueprint_v2 was built via a Layout, there isn't enough information\n" +
           "  to associate a Host to a HostGroup when Host Groups contain the same\n" +
           "  services, but have different configurations (managed groups in ambari).\n" +
           "  So the reduction process will consolidate and strip all host group\n" +
           "  configurations.")

    reduce_to_supported_services(blueprint)
    consolidate_blueprint_host_groups(blueprint, False)

    cluster_creation_template = {}
    # Generate Counts for Blueprint Host Groups.
    # Go through the Merged Blueprint and count the hosts in each host_group.
    host_groups = blueprint['host_groups']
    component_dict = get_component_dictionary_from_bp(blueprint)
    # componentDict = get_component_dictionary(layout)

    hostgroupsbitmask = calc_host_group_bit_masks(host_groups, component_dict)

    hostbitmask = calc_host_bit_masks(layout['items'], component_dict)

    cluster_creation_template['blueprint'] = 'need-to-set-me'

    # Stubout Credentials
    credential = {'alias': 'kdc.admin.credentials', 'key': 'NEED_TO_SET', 'principal': 'NEED_TO_SET',
                  'type': 'TEMPORARY'}
    credentials = [credential]
    cluster_creation_template['credential'] = credentials

    cct_host_groups = []
    for host_group in host_groups:
        cct_host_group = {}
        cct_host_group['name'] = host_group['name']
        cct_hosts = []
        for layout_host in hostbitmask.keys():
            if hostbitmask[layout_host]['bit_mask'] == hostgroupsbitmask[host_group['name']]:
                cct_host = {}
                cct_host['fqdn'] = hostbitmask[layout_host]['host_name']
                if 'rack_info' in hostbitmask[layout_host]:
                    cct_host['rack_info'] = hostbitmask[layout_host]['rack_info']
                cct_hosts.append(cct_host)
        cct_host_group['hosts'] = cct_hosts
        cct_host_groups.append(cct_host_group)

    cluster_creation_template['host_groups'] = cct_host_groups
    # Stub out more cct items.
    cluster_creation_template['provision_action'] = 'INSTALL_ONLY'
    cluster_creation_template['repository_version'] = 'NOT_TO_SET'
    kerb = {'type': 'KERBEROS'}
    cluster_creation_template['security'] = kerb

    return cluster_creation_template


def build_ambari_blueprint_v2(blueprint, creationTemplate):
    # def mergeConfigsWithHostMatrix(blueprint, hostMatrix, control):
    blueprintV2 = copy.deepcopy(blueprint)
    # configurations = blueprintV2['configurations']
    # stack = blueprint['Blueprints']['stack_name'] + ' ' + blueprint['Blueprints']['stack_version']
    ct_hostgroups = creationTemplate['host_groups']
    bp_hostgroups = blueprintV2['host_groups']
    # calcHostGroupBitMasks(hostgroups)
    for bp_host_group in bp_hostgroups:
        # Loop thru ct host_groups and collect hosts for each group
        for ct_hostgroup in ct_hostgroups:
            if bp_host_group['name'] == ct_hostgroup['name']:
                hosts = []
                for ct_host in ct_hostgroup['hosts']:
                    lclHost = {}
                    lclHost['hostname'] = ct_host['fqdn']
                    if 'rack_info' in ct_host.keys():
                        lclHost['rack_info'] = ct_host['rack_info']
                    hosts.append(lclHost)
                bp_host_group['hosts'] = hosts
                # bp_host_group['cardinality'] = str(len(hosts))
    # if consolidate:
    #     consolidate_blueprint_host_groups(blueprintV2)
    remove_empty_host_groups(blueprintV2)
    return blueprintV2


def reduce_to_supported_services(blueprint):
    host_groups = blueprint['host_groups']

    # Filter out unsupported components
    for host_group in host_groups:
        unsupported = []
        for index, component in enumerate(host_group['components']):
            if component['name'] not in supported_services:
                unsupported.append(index)
        for index in reversed(unsupported):
            del host_group['components'][index]
        for index in range(len(host_group['configurations'])-1, -1, -1):
            del host_group['configurations'][index]
    # remove host_groups that have no components
    empty_host_groups = []
    for index, host_group in enumerate(host_groups):
        if len(host_group['components']) == 0:
            empty_host_groups.append(index)
            print('Host group: ' + host_group['name'] + ' has no supported components left.  Will remove it.')
    for index in reversed(empty_host_groups):
        del host_groups[index]


def consolidate_blueprint_host_groups(blueprint, transfer_hosts):
    host_groups = blueprint['host_groups']

    # Consolidate Host Groups that have the same components.
    component_dict = get_component_dictionary_from_bp(blueprint)
    hostgroupsbitmask = calc_host_group_bit_masks(host_groups, component_dict)
    bitmasks = []
    # Let's swap the keys and values.  In the process, we'll naturally condense to a set of
    # host groups that are unique by throwing out hostgroups with duplicate bitmasks.
    res = dict((v, k) for k, v in hostgroupsbitmask.iteritems())
    final_host_groups = []
    # Loop though and get final host groups.
    for key in res.keys():
        final_host_groups.append(res[key])

    del_hg_indexes = {}
    move_hg_hosts = {}
    for index, host_group in enumerate(host_groups):
        if host_group['name'] not in final_host_groups:
            del_hg_indexes[index] = hostgroupsbitmask[host_group['name']]
            move_hg_hosts[host_group['name']] = res[hostgroupsbitmask[host_group['name']]]
            # Migrate this host group's host to the remaining host group.

    # Transfer Hosts from the consolidated host groups.
    if transfer_hosts:
        for m_hg_name in move_hg_hosts:
            t_hg_name = move_hg_hosts[m_hg_name]
            target_host_group = None
            for host_group in host_groups:
                if host_group['name'] == t_hg_name:
                    target_host_group = host_group
            for host_group in host_groups:
                if host_group['name'] == m_hg_name:
                    if host_group['hosts'] is not None:
                        for host in host_group['hosts']:
                            target_host_group['hosts'].append(host)
            target_host_group['cardinality'] = len(target_host_group['hosts'])

    # Need to iterate over move_hg_groups and reset properties with a reference
    # to a host_group that will be removed.
    configs = blueprint['configurations']
    for move_key in move_hg_hosts.keys():
        re_move_key = move_key.replace('_','\_')
        # re.sub(re_move_key, '\\_', '\\_')
        print "Move host_group: " + move_key + " to " + move_hg_hosts[move_key]
        print " ---> Reconciling placeholders in configuration properties..."
        for config in configs:
            for config_key in config:
                # print 'Config Key: ' + config_key
                for property in config[config_key]['properties']:
                    # print 'Config Property: ' + property
                    check_value = config[config_key]['properties'][property]
                    status, new_value = compare_exists_replace(check_value, '%HOSTGROUP::'+move_hg_hosts[move_key]+'%', '%HOSTGROUP::'+move_key+'%')
                    if status:
                        config[config_key]['properties'][property] = new_value

    # Remove duplicate Host Groups
    del_hg_indexes_sorted = collections.OrderedDict(sorted(del_hg_indexes.items()))
    for key in reversed(del_hg_indexes_sorted.keys()):
        del host_groups[key]


def repair_host_references(blueprint_v2, replaced_hosts):
    configs = blueprint_v2['configurations']
    for replaced_host in replaced_hosts.keys():
        re_move_key = replaced_host.replace('_','\_')
        # re.sub(re_move_key, '\\_', '\\_')
        print "Replacing Host: " + replaced_host + " with " + replaced_hosts[replaced_host]
        print " ---> Reconciling placeholders in configuration properties..."
        for config in configs:
            for config_key in config:
                # print 'Config Key: ' + config_key
                for property in config[config_key]['properties']:
                    # print 'Config Property: ' + property
                    check_value = config[config_key]['properties'][property]
                    status, new_value = compare_exists_replace(check_value, replaced_hosts[replaced_host], replaced_host)
                    if status:
                        config[config_key]['properties'][property] = new_value


def compare_exists_replace(value, compare, replace):
    if compare in value:
        # Look for 'replace' in value and if it exists, remove it.
        if replace in value:
            value = value.replace(replace, '')
            # Remove empty item.
            value = value.replace(',,', ',')
            return True, value
        else:
            return False, None
    else:
        if replace in value:
            value = value.replace(replace, compare)
            return True, value
        else:
            return False, None


def cct_from_blueprint_v2(blueprint_v2):
    cct = {}
    # Generate Counts for Blueprint Host Groups.
    # Go through the Merged Blueprint and count the hosts in each host_group.
    host_groups = blueprint_v2['host_groups']

    cct['blueprint'] = 'need-to-set-me'

    # Stubout Credentials
    credential = {'alias': 'kdc.admin.credentials', 'key': 'NEED_TO_SET', 'principal': 'NEED_TO_SET',
                  'type': 'TEMPORARY'}
    credentials = [credential]
    cct['credential'] = credentials

    cct_host_groups = []
    for host_group in host_groups:
        cct_host_group = {'name': host_group['name']}
        cct_hosts = []
        for host in host_group['hosts']:
            cct_host = {}
            cct_host['fqdn'] = host['hostname']
            if host['rack_info'] is not None:
                cct_host['rack_info'] = host['rack_info']
            cct_hosts.append(cct_host)
        cct_host_group['hosts'] = cct_hosts
        cct_host_groups.append(cct_host_group)

    cct['host_groups'] = cct_host_groups

    # Stub out more cct items.
    cct['provision_action'] = 'INSTALL_ONLY'
    cct['repository_version'] = 'NOT_TO_SET'
    kerb = {'type': 'KERBEROS'}
    cct['security'] = kerb

    return cct


def reduce_worker_scale(blueprint_v2, scale):
    print ("Applying worker Scale Reduction: " + str(scale))
    host_groups = blueprint_v2['host_groups']
    component_dict = get_component_dictionary_from_bp(blueprint_v2)
    hostgroupsbitmask = calc_host_group_bit_masks(host_groups, component_dict)

    # Generate Master Services BitMask
    masterbitmask = 0
    for service in master_services:
        try:
            masterbitmask = masterbitmask | component_dict[service]
        except KeyError:
            check = 'Not in lists for this blueprint.  This is ok.'
    removed_hosts = []
    for hostgroupname in hostgroupsbitmask.keys():
        check = hostgroupsbitmask[hostgroupname] & masterbitmask == hostgroupsbitmask[hostgroupname]
        if not check: # Not a master
            # Check Cardinality
            for host_group in host_groups:
                if host_group['name'] == hostgroupname:
                    if len(host_group['hosts']) > scale:
                        # Need to scale back hosts in host group.
                        for i in range(len(host_group['hosts']) - 1, scale-1, -1):
                            # print("del: " + str(i))
                            removed_hosts.append(host_group['hosts'][i]['hostname'])
                            del host_group['hosts'][i]
                    host_group['cardinality'] = len(host_group['hosts'])
    return removed_hosts


def substitute_hosts(blueprint_v2, hosts):
    index = 0
    replaced_hosts = {}
    host_groups = blueprint_v2['host_groups']
    for host_group in host_groups:
        for bp_host in host_group['hosts']:
            sub_host = hosts[index]
            replaced_hosts[bp_host['hostname']] = sub_host['host']
            bp_host['hostname'] = sub_host['host']
            if sub_host['rack_info'] is not None:
                bp_host['rack_info'] = sub_host['rack_info']
            index += 1
            if index >= len(hosts):
                print ('')
                print ('')
                print ('!!!!            ******************************** ')
                print ('WARNING: NOT enough hosts in list to replace blueprint hosts.')
                print ('    Replaced hosts until we exhausted list.  Need to add more host to sub list and try again!!!')
                print ('!!!!            ******************************** ')
                print ('')
                print ('')
                return replaced_hosts
    return replaced_hosts


def remove_empty_host_groups(blueprintV2):
    print ("Removing Empty Host Groups")
    bp_hostgroups = blueprintV2['host_groups']
    # Remove empty host-groups
    empty_idx = []
    for index, bp_host_group in enumerate(bp_hostgroups):
        # print (index)
        try:
            if 'hosts' in bp_host_group.keys() and len(bp_host_group['hosts']) == 0:
                print("Empty Host group: " + bp_host_group['name'] + ". Will be removed.")
                empty_idx.append(index)
            elif 'hosts' not in bp_host_group.keys():
                print("Empty Host group: " + bp_host_group['name'] + ". Will be removed.")
                empty_idx.append(index)

            if 'hosts' in bp_host_group.keys() and len(bp_host_group['hosts']) != int(
                    bp_host_group['cardinality']):
                # print("Mismatch Cardinality for: " + bp_host_group['name'] + ". " + str(
                #     len(bp_host_group['hosts'])) + ":"
                #       + str(bp_host_group['cardinality']))
                bp_host_group['cardinality'] = str(len(bp_host_group['hosts']))
        except ValueError:
            # print("Mismatch Cardinality for: " + bp_host_group['name'] + ". " + str(
            #     len(bp_host_group['hosts'])) + ":"
            #       + str(bp_host_group['cardinality']))
            bp_host_group['cardinality'] = str(len(bp_host_group['hosts']))

    for index in empty_idx:
        del bp_hostgroups[index]
    return blueprintV2


def merge_configs_with_host_matrix(blueprint, hostMatrix, componentDict, control):
    mergedBlueprint = copy.deepcopy(blueprint)
    configurations = mergedBlueprint['configurations']
    # stack = blueprint['Blueprints']['stack_name'] + ' ' + blueprint['Blueprints']['stack_version']
    hostgroups = mergedBlueprint['host_groups']
    hostgroupbitmask = calc_host_group_bit_masks(hostgroups, componentDict)

    # Loop through Hosts
    for hostKey in hostMatrix:
        # Retrieve Host
        host = hostMatrix[hostKey]
        # print host
        for hostgroup in mergedBlueprint['host_groups']:
            if host['HostGroupMask'] == hostgroupbitmask[hostgroup['name']]:
                host['host_group'] = str(hostgroup['name'])
                hosts = []
                if 'hosts' in hostgroup.keys():
                    hosts = hostgroup['hosts']
                    lclHost = {}
                    lclHost['hostname'] = host['Hostname']
                    # lclHost['rackId'] = host['Rack']
                    # lclHost['ip'] = host['ip']
                    hosts.append(lclHost)
                else:
                    lclHost = {}
                    lclHost['hostname'] = host['Hostname']
                    # lclHost['rackId'] = host['Rack']
                    # lclHost['ip'] = host['ip']
                    hosts.append(lclHost)
                    hostgroup['hosts'] = hosts
        # Loop Host Components
        for cGroup in host['components']:
            # Proceed if component has a setting.
            if len(host['components'][cGroup]) > 0:
                # print "Group: " + cGroup
                # Cycle through the Hosts Group Components
                for component in host['components'][cGroup]:
                    # print "Component: " + component
                    # Get the component config from the CONTROL File
                    for componentSection in ['config', 'environment']:
                        # print "Section: " + componentSection
                        config = control[cGroup][component][componentSection]
                        #  Cycle through the configs in the Control File
                        cfgSection = config['section']
                        # bpProperties = {}
                        hostgroup = []
                        for shg in hostgroups:
                            if shg['name'] == host['host_group']:
                                hostgroup = shg
                        for bpSections in configurations:
                            if bpSections.keys()[0] == cfgSection:
                                # print "Config Section: " + cfgSection
                                bpProperties = bpSections.get(cfgSection)['properties']
                                #  Lookup Configuration in BP
                                for localProperty in config['configs']:
                                    # Get the Target BP Property to Lookup
                                    targetProperty = config['configs'][localProperty]
                                    # Find property in BP
                                    # print 'Local Prop: ' + localProperty + '\tTarget Prop: ' + targetProperty
                                    try:
                                        pValue = bpProperties[targetProperty]
                                        # print 'BP Property Value: ' + pValue
                                        try:
                                            pValue = int(pValue)
                                        except:
                                            # It could have a trailing char for type
                                            if localProperty in ['heap', 'off.heap']:
                                                pValue = int(pValue[:-1])
                                        if isinstance(pValue, int):
                                            # Account for some mem settings in Kb
                                            if localProperty in ['heap', 'off.heap'] and pValue > 1000000:
                                                host['components'][cGroup][component][localProperty] = pValue / 1024
                                            else:
                                                host['components'][cGroup][component][localProperty] = pValue
                                        else:
                                            host['components'][cGroup][component][localProperty] = pValue
                                    except:
                                        missing = "Missing from Blueprint: " + component + ":" + cfgSection + ":" + targetProperty
                                    # print pValue
                                break
                        #  go through the overrides
                        hostgroupCfg = hostgroup['configurations']
                        for bpSections in hostgroupCfg:
                            if bpSections.keys()[0] == cfgSection:
                                # print "Config Section: " + cfgSection
                                bpProperties = bpSections.get(cfgSection)
                                #  Lookup Configuration in BP
                                for localProperty in config['configs']:
                                    # Get the Target BP Property to Lookup
                                    targetProperty = config['configs'][localProperty]
                                    # Find property in BP
                                    # print 'Local Prop: ' + localProperty + '\tTarget Prop: ' + targetProperty
                                    try:
                                        pValue = bpProperties[targetProperty]
                                        # print 'BP Property Value: ' + pValue
                                        try:
                                            pValue = int(pValue)
                                        except:
                                            # It could have a trailing char for type
                                            if localProperty in ['heap', 'off.heap']:
                                                pValue = int(pValue[:-1])
                                        if isinstance(pValue, int):
                                            # Account for some mem settings in Kb
                                            if localProperty in ['heap', 'off.heap'] and pValue > 1000000:
                                                host['components'][cGroup][component][localProperty] = pValue / 1024
                                            else:
                                                host['components'][cGroup][component][localProperty] = pValue
                                        else:
                                            host['components'][cGroup][component][localProperty] = pValue
                                    except:
                                        override = "No override for: " + component + ":" + cfgSection + ":" + targetProperty
                                    # print pValue
                                break
    return remove_empty_host_groups(mergedBlueprint)
