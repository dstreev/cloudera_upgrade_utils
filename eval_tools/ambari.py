import copy

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


def get_component_dictionary(layout):
    component_dict = {}
    items = layout['items']
    for item in items:
        components = item["host_components"]
        for component in components:
            for ckey, cvalue in component.items():
                if ckey == "HostRoles":
                    for hkey, hvalue in cvalue.items():
                        if hkey == "component_name":
                            if hvalue not in component_dict.keys():
                                dl = len(component_dict)
                                if dl == 0:
                                    component_dict[hvalue] = 1
                                elif dl == 1:
                                    component_dict[hvalue] = 2
                                else:
                                    component_dict[hvalue] = 2 ** dl
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
                check = 'Component in Host that is not in the Layouts: ' + component['name']
        host_detail['host_name'] = item['Hosts']['host_name']
        host_detail['bit_mask'] = hgbitmask
        if 'rack_info' in item['Hosts'].keys():
            host_detail['rack_info'] = item['Hosts']['rack_info']
        host_bitmask[item['Hosts']['host_name']] = host_detail
    return host_bitmask


def build_creation_template_from_layout(blueprint, layout):
    cluster_creation_template = {}
    # Generate Counts for Blueprint Host Groups.
    # Go through the Merged Blueprint and count the hosts in each host_group.
    host_groups = blueprint['host_groups']
    componentDict = get_component_dictionary(layout)

    hostgroupsbitmask = calc_host_group_bit_masks(host_groups, componentDict)

    hostbitmask = calc_host_bit_masks(layout['items'], componentDict)

    cluster_creation_template['blueprint'] = 'need-to-set-me'

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
        # print("hello")
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
    return mergedBlueprint
