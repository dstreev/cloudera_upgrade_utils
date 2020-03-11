#!/usr/bin/env python

# use this to parse the Ambari Layout Report that's generated with:
# http://${AMBARI_HOST_PORT}/api/v1/clusters/${CLUSTER_NAME}/hosts?fields=Hosts/host_name,host_components,Hosts/ip,Hosts/total_mem,Hosts/os_arch,Hosts/os_type,Hosts/rack_info,Hosts/cpu_count,Hosts/disk_info,metrics/disk,Hosts/ph_cpu_count

import os
import optparse
import logging
import sys
import json
from common import pprinttable, pprinttable2, pprinthtmltable, writehtmltable
from datetime import date


VERSION = "0.1.4"

logger = logging.getLogger('HDP_Eval')

HOSTS = {}
SERVICES = {}
CONTROL = {}
glayout = {}

layout_file = ''
bp_file = ''
run_date = ''

# A bitmask to associate to a hostgroup
componentDict = {}


def addToComponentDictionary(item):
    components = item["host_components"]
    for component in components:
        for ckey, cvalue in component.items():
            if ckey == "HostRoles":
                for hkey, hvalue in cvalue.items():
                    if hkey == "component_name":
                        if hvalue not in componentDict.keys():
                            dl = len(componentDict)
                            if dl == 0:
                                componentDict[hvalue] = 1
                            elif dl == 1:
                                componentDict[hvalue] = 2
                            else:
                                componentDict[hvalue] = 2**dl


def getHostGroup(item):
    location = 0
    components = item["host_components"]
    for component in components:
        for ckey, cvalue in component.items():
            if ckey == "HostRoles":
                for hkey, hvalue in cvalue.items():
                    if hkey == "component_name":
                        location = location | componentDict[hvalue]
    return location


def buildFieldPathFromAbbr(abbrs):
    fields = []
    paths = {}
    for abbr in abbrs:
        for group in CONTROL:
            for component in CONTROL[group]:
                # print group + ":" + component
                if abbr == CONTROL[group][component]['abbr']:
                    # print 'found'
                    path = [group, component]
                    paths[abbr] = path
                    fields.append(abbr)

    return paths, fields


def get_hostname(item):
    host_info = item["Hosts"]
    return host_info["host_name"]


def aggregateComponents(item):
    components = item['host_components']
    for component in components:
        for key, value in component.items():
            if key == 'HostRoles':
                for rkey, rvalue in value.items():
                    if rkey == 'component_name':
                        if rvalue in SERVICES:
                            SERVICES[rvalue] += 1
                        else:
                            SERVICES[rvalue] = 1


def is_component(item, componentName):
    # addToComponentDictionary(componentName)
    components = item["host_components"]
    for component in components:
        for ckey, cvalue in component.items():
            if ckey == "HostRoles":
                for hkey, hvalue in cvalue.items():
                    if hkey == "component_name":
                        if hvalue == componentName:
                            return True, componentDict[hvalue]
    return False, 0


def is_componentX(item, componentName):
    found, location = is_component(item, componentName)
    if found:
        return 'X'
    else:
        return ''


def loadControl(controlFile):
    global CONTROL
    CONTROL = json.loads(open(controlFile).read())


def get_info(layoutFile):
    layout = json.loads(open(layoutFile).read())
    items = layout['items']

    hosttable, compute_count, other_count = gen_hosttable(items)

    return hosttable, compute_count, other_count


def appendCSS(output):
    output.write('<style type="text/css">.TFtable{    width:100%;border-collapse:collapse;}.TFtable td{    padding:7px; border:#332200 1px solid;}')
    # /* provide some minimal visual accomodation for IE8 and below */
    output.write('    .TFtable tr{    background: #FFF7E6;}')
    # /*  Define the background color for all the ODD background rows  */
    output.write('    .TFtable tr:nth-child(odd){    background: #FFE6B3;}')
    # /*  Define the background color for all the EVEN background rows  */
    output.write('.TFtable tr:nth-child(even){    background: ##FF9966;}</style>')


def report(layoutFile, output_dir):

    index_filename = output_dir + '/index.html'
    index_output = open(index_filename, 'w')
    writeHeader(index_output)
    index_output.write('<ol>')
    index_output.write('<li><a href="./services.html">Services</a></li>')
    index_output.write('<li><a href="./count_types.html">Count Types</a></li>')
    index_output.write('<li><a href="./hosttable.html">Host Table</a></li>')
    index_output.write('<li><a href="./hoststorage.html">Host Storage</a></li>')
    index_output.write('<li><a href="./mem_alloc.html">Host Memory Allocation</a></li>')
    index_output.write('<li><a href="./hosts.json">Hosts json</a></li>')
    index_output.write('</ol>')
    index_output.close()

    services_filename = output_dir + '/services.html'
    services_output = open(services_filename, 'w')
    appendCSS(services_output)
    writeHeader(services_output)
    rpt_services(services_output)
    services_output.close()

    count_types = {}
    count_types['Storage'] = ['DATANODE']
    count_types['Compute'] = ['NODEMANAGER']
    count_types['Master'] = ['NAMENODE', 'RESOURCEMANAGER', 'OOZIE_SERVER', 'HIVE_SERVER', 'HIVE_SERVER_INTERACTIVE',
                             'HIVE_METASTORE']
    count_types['Kafka'] = ['KAFKA_BROKER']


    count_types_filename = output_dir + '/count_types.html'
    count_types_output = open(count_types_filename, 'w')
    appendCSS(count_types_output)
    writeHeader(count_types_output)
    rpt_count_type(count_types, count_types_output)
    count_types_output.close()


    mem_alloc_filename = output_dir + '/mem_alloc.html'
    mem_alloc_output = open(mem_alloc_filename, 'w')
    appendCSS(mem_alloc_output)
    writeHeader(mem_alloc_output)
    rpt_mem_allocations(mem_alloc_output)
    mem_alloc_output.close()

    # rpt_mem_allocations()

    hosttable_filename = output_dir + '/hosttable.html'
    hosttable_output = open(hosttable_filename, 'w')
    appendCSS(hosttable_output)
    writeHeader(hosttable_output)
    rpt_hosttable(hosttable_output)
    hosttable_output.close()

    # rpt_hosttable()

    # print ''
    # print '======================================='
    # print '  Location Details'
    # print '---------------------------------------'
    # print json.dumps(CONFIGS, indent=4, sort_keys=True)
    # print '======================================='
    # print ''

    # TODO: Get Memory Settings and use to find over allocated Hosts.
    hoststorage_filename = output_dir + '/hoststorage.html'
    hoststorage_output = open(hoststorage_filename, 'w')
    appendCSS(hoststorage_output)
    writeHeader(hoststorage_output)
    rpt_hoststorage(hoststorage_output)
    hoststorage_output.close()

    # rpt_hoststorage()

    # print ''
    # print '======================================='
    # print '  Host Details'
    # print '---------------------------------------'

    hostdump_filename = output_dir + '/hosts.json'
    hostdump_output = open(hostdump_filename, 'w')
    # appendCSS(hostdump_output)
    hostdump_output.write(json.dumps(HOSTS, indent=2, sort_keys=True))
    hostdump_output.close()

    # print json.dumps(HOSTS, indent=2, sort_keys=True)
    # print '======================================='
    # print ''


def gen_hosttable(items):
    records = []
    compute_count = {}
    other_count = {}

    for item in items:
        record = []

        hostItem = item["Hosts"]

        record.append(hostItem["host_name"])
        record.append(hostItem["os_type"])
        record.append(hostItem["cpu_count"])
        record.append(hostItem["total_mem"] / (1024 * 1024))
        record.append(hostItem["rack_info"])

        record.append(is_componentX(item, 'KNOX_GATEWAY'))
        record.append(is_componentX(item, 'NAMENODE'))
        record.append(is_componentX(item, 'JOURNALNODE'))
        record.append(is_componentX(item, "ZKFC"))
        record.append(is_componentX(item, "DATANODE"))
        record.append(is_componentX(item, "RESOURCEMANAGER"))
        record.append(is_componentX(item, "NODEMANAGER"))
        record.append(is_componentX(item, "ZOOKEEPER_SERVER"))
        record.append(is_componentX(item, "HIVE_METASTORE"))
        record.append(is_componentX(item, "HIVE_SERVER"))
        record.append(is_componentX(item, "HIVE_SERVER_INTERACTIVE"))
        record.append(is_componentX(item, "OOZIE_SERVER"))
        record.append(is_componentX(item, "HBASE_MASTER"))
        record.append(is_componentX(item, "HBASE_REGIONSERVER"))
        record.append(is_componentX(item, "KAFKA_BROKER"))
        record.append(is_componentX(item, "NIFI_MASTER"))

        record.append(is_componentX(item, "LIVY2_SERVER"))
        record.append(is_componentX(item, "SPARK2_JOBHISTORY"))

        record.append(is_componentX(item, "DRUID_ROUTER"))
        record.append(is_componentX(item, "DRUID_OVERLOAD"))
        record.append(is_componentX(item, "DRUID_BROKER"))
        record.append(is_componentX(item, "DRUID_MIDDLEMANAGER"))
        record.append(is_componentX(item, "DRUID_HISTORICAL"))
        record.append(is_componentX(item, "DRUID_COORDINATOR"))

        try:
            disks = {}
            try:
                for disk in hostItem['disk_info']:
                    # diskCount += 1
                    diskCap = int(disk['size']) / (1024 * 1024)
                    diskMount = disk['mountpoint']
                    diskFormat = disk['type']
                    if diskCap in disks:
                        disks[diskCap]['count'] += 1
                        if diskFormat not in disks[diskCap]['format']:
                            disks[diskCap]['format'].append(diskFormat.encode("utf-8"))
                        disks[diskCap]['mount'].append(diskMount.encode("utf-8"))
                    else:
                        disks[diskCap] = {'count': 1, 'size': diskCap,
                                          'mount': [diskMount.encode("utf-8")],
                                          'format': [diskFormat.encode("utf-8")]}
                record.append(disks)
                hostRecord = {'os_type': hostItem["os_type"], 'cpu_count': hostItem["cpu_count"],
                              'total_mem': hostItem["total_mem"] / (1024 * 1024),
                              'rack': hostItem["rack_info"], 'components': components, 'disks': disks}

            except:
                host_detail = " No host detail information supplied"
        except:
            hello = "No host information supplied"

        records.append(record)

        compute = is_component(item, "NODEMANAGER")
        key = str(compute) + str(record[3]) + str(record[1])
        memory = record[3]
        cores = record[1]
        if compute and key not in compute_count:
            compute_count[key] = {'count': 1, 'memory': memory, 'cores': cores, }
        elif compute:
            compute_count[key]['count'] += 1
        elif not compute and key not in other_count:
            other_count[key] = {'count': 1, 'memory': memory, 'cores': cores, }
        elif not compute:
            other_count[key]['count'] += 1

        # print key + str(memory) + str(cores)

    return records, compute_count, other_count


def buildHostMatrix():
    # layout = json.loads(open(layoutFile).read())
    items = glayout['items']

    for item in items:
        # Build total component counts for cluster while examining each item.
        aggregateComponents(item)
        addToComponentDictionary(item)

        hostItem = item["Hosts"]

        host = {}

        host['Hostname'] = hostItem['host_name']
        host['OS'] = hostItem['os_type']
        host['vC'] = hostItem['cpu_count']
        host['Gb'] = hostItem['total_mem'] / (1024 * 1024)
        host['Rack'] = hostItem['rack_info']
        host['ip'] = hostItem['ip']

        components = {}
        hostGroup = 0
        for componentGroup in CONTROL:
            components[componentGroup] = {}
            for cKey in CONTROL[componentGroup]:
                # print cKey
                # print CONTROL[componentGroup][cKey]
                found, location = is_component(item, cKey)
                if location > 0:
                    hostGroup = hostGroup | location
                if found:
                    cValue = CONTROL[componentGroup][cKey]
                    components[componentGroup].update({cKey: {'abbr': cValue['abbr']}})
        host['components'] = components
        host['HostGroupMask'] = getHostGroup(item)

        disks = {}
        # Loop through the disks
        try:
            for disk in hostItem['disk_info']:
                diskCap = int(disk['size']) / (1024 * 1024)
                diskMount = disk['mountpoint']
                diskFormat = disk['type']
                if diskCap in disks:
                    disks[diskCap]['count'] += 1
                    if diskFormat not in disks[diskCap]['format']:
                        disks[diskCap]['format'].append(diskFormat.encode("utf-8"))
                    disks[diskCap]['mount'].append(diskMount.encode("utf-8"))
                else:
                    disks[diskCap] = {'count': 1, 'size': diskCap,
                                      'mount': [diskMount.encode("utf-8")],
                                      'format': [diskFormat.encode("utf-8")]}
            host['Disks'] = disks

        except:
            host_detail = " No host detail information supplied"

        HOSTS[hostItem['host_name']] = host


def calcHostGroupBitMasks(hostgroups):
    for hostgroup in hostgroups:
        hgbitmask = 0
        for component in hostgroup['components']:
            try:
                hgbitmask = hgbitmask | componentDict[component['name']]
            except:
                check = 'Component in Host that is not in the Layouts: ' + component['name']
        hostgroup['HostGroupMask'] = hgbitmask


def mergeConfigsWithHostMatrix(blueprintFile):
    blueprint = json.loads(open(blueprintFile).read())
    configurations = blueprint['configurations']
    hostgroups = blueprint['host_groups']
    calcHostGroupBitMasks(hostgroups)

    # Loop through Hosts
    for hostKey in HOSTS:
        # Retrieve Host
        host = HOSTS[hostKey]
        # print host
        for hostgroup in hostgroups:
            if host['HostGroupMask'] == hostgroup['HostGroupMask']:
                host['host_group'] = str(hostgroup['name'])
                hosts = []
                if 'hosts' in hostgroup.keys():
                    hosts = hostgroup['hosts']
                    lclHost = {}
                    lclHost['hostname'] = host['Hostname']
                    lclHost['rackId'] = host['Rack']
                    lclHost['ip'] = host['ip']
                    hosts.append(lclHost)
                else:
                    lclHost = {}
                    lclHost['hostname'] = host['Hostname']
                    lclHost['rackId'] = host['Rack']
                    lclHost['ip'] = host['ip']
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
                        config = CONTROL[cGroup][component][componentSection]
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
    return blueprint


def rpt_mem_allocations(output):
    output.write('\n<h2>Host Memory Allocations</h2>\n')
    fields = ['Hostname', 'Gb', 'Allocated', 'Components']
    mem_recs = []
    for hostKey in HOSTS:
        mem_rec = {}
        host = HOSTS[hostKey]
        mem_rec['Hostname'] = host['Hostname']
        mem_rec['Gb'] = host['Gb']
        mem_rec_component_heaps = {}
        mem_rec['Components'] = {}
        for controlKey in CONTROL:
            for component in CONTROL[controlKey]:
                for hostGroupKey in host['components']:
                    if hostGroupKey == controlKey:
                        for hostComponentKey in host['components'][hostGroupKey]:
                            mem = {}
                            try:
                                mem['heap'] = host['components'][hostGroupKey][hostComponentKey]['heap']
                                try:
                                    mem['off.heap'] = host['components'][hostGroupKey][hostComponentKey]['off.heap']
                                except:
                                    # No off.heap information
                                    pass
                            except:
                                no_heap = 'No HEAP Information->' + host[
                                    'Hostname'] + ':' + component + ':' + hostGroupKey + ':' + hostComponentKey
                            # print hostComponentKey
                            if len(mem) > 0:
                                mem_rec['Components'][hostComponentKey] = mem
                # print 'host'
        total_mem = 0
        for mem_alloc_key in mem_rec['Components']:
            mem_type = mem_rec['Components'][mem_alloc_key]
            for type in mem_type:
                mem_raw = mem_type[type]
                try:
                    mem = int(mem_raw)
                except:
                    mem = int(mem_raw[:-1])
                total_mem += mem
        mem_rec['Allocated'] = total_mem / 1024
        mem_recs.append(mem_rec)
    writehtmltable(mem_recs, fields, output)


def rpt_services(output):
    output.write('\n<h2>Service Counts</h2>\n')
    lcl_services = []
    fields = ['Service', 'Count']
    for service in SERVICES:
        lcl_service = {}
        lcl_service['Service'] = service
        lcl_service['Count'] = SERVICES[service]
        lcl_services.append(lcl_service)
    writehtmltable(lcl_services, fields, output)


def get_hostbase(host, fields):
    hostRec = {}
    for field in fields:
        hostRec[field] = host[field]
    return hostRec


def populate_components(paths, hostComponents, hostRec):
    for pabbr in paths:
        path = paths[pabbr]
        value = hostComponents[path[0]]
        try:
            value = hostComponents[path[0]][path[1]]
            hostRec[pabbr] = 'X'
        except:
            pass


def rpt_hosttable(output):
    output.write('\n<h2>Host Table</h2>\n')
    # master = datanode & compute
    fields_base = ['Hostname', 'OS', 'vC', 'Gb', 'Rack']

    paths, bfields = buildFieldPathFromAbbr(['KX', 'NN', 'JN', 'ZKFC', 'DN', 'RM', 'NM',
                                             'ZK', 'HMS', 'HS2', 'HS2i', 'OZ', 'HM', 'RS',
                                             'KB', 'NF', 'LV2', 'S2H', 'DR', 'DO', 'DB',
                                             'DM', 'DH', 'DH'])

    fields = fields_base + bfields

    hosttable = []
    for hostKey in HOSTS:
        host = HOSTS[hostKey]
        hostRec = get_hostbase(host, fields_base)
        populate_components(paths, host['components'], hostRec)

        hosttable.append(hostRec)

    writehtmltable(hosttable, fields, output)


def rpt_hoststorage(output):
    output.write('\n<h2>Host Storage</h2>\n')
    fields_base = ['Hostname', 'vC', 'Gb', 'Rack']

    paths, bfields = buildFieldPathFromAbbr(['NN', 'JN', 'DN', 'ZK', 'NM', 'KB', 'NF'])

    fields = fields_base + bfields
    fields.append('DataDirs')
    fields.append('LogsDirs')
    fields.append('Disks')

    hosttable = []
    for hostKey in HOSTS:
        host = HOSTS[hostKey]
        hostRec = get_hostbase(host, fields_base)
        populate_components(paths, host['components'], hostRec)
        hostRec['DataDirs'] = getDataDirs(host['components'])
        hostRec['LogsDirs'] = getLogsDirs(host['components'])
        hostRec['Disks'] = host['Disks']

        hosttable.append(hostRec)

    writehtmltable(hosttable, fields, output)


def getDataDirs(components):
    dataDirs = {}
    for componentKey in components:
        for partKey in components[componentKey]:
            part = components[componentKey][partKey]
            if 'data.dir' in part.keys():
                dataDirs[partKey] = part['data.dir']
    return dataDirs


def getLogsDirs(components):
    logsDirs = {}
    for componentKey in components:
        for partKey in components[componentKey]:
            part = components[componentKey][partKey]
            if 'logs.dir' in part.keys():
                logsDirs[partKey] = part['logs.dir']
    return logsDirs


def rpt_count_type(types, output):
    output.write('\n<h2>Count Types</h2>\n')
    # layout = json.loads(open(layoutFile).read())
    items = glayout['items']

    # master = datanode & compute
    # type = { category: ['DN','NN']}
    table = []
    fields = ['Category', 'Types', 'Count', 'Min Cores', 'Max Cores', 'Min Gb', 'Max Gb']
    for category in types:
        type_rec = {}
        type_rec['Category'] = category
        type_rec['Count'] = 0
        type_rec['Types'] = types[category]
        type_rec['Min Cores'] = 10000
        type_rec['Max Cores'] = 0
        type_rec['Min Gb'] = 10000
        type_rec['Max Gb'] = 0
        table.append(type_rec)

        for item in items:
            found = 0
            for comp in types[category]:
                componentFound, hgbitmask = is_component(item, comp)
                if componentFound:
                    found += 1
                    host = item['Hosts']
                    mem = host['total_mem'] / (1024 * 1024)
                    # CPU Min
                    if host['cpu_count'] < type_rec['Min Cores']:
                        type_rec['Min Cores'] = host['cpu_count']
                    # CPU Max
                    if host['cpu_count'] > type_rec['Max Cores']:
                        type_rec['Max Cores'] = host['cpu_count']
                    # Mem Min
                    if mem < type_rec['Min Gb']:
                        type_rec['Min Gb'] = mem
                    # Mem Max
                    if mem > type_rec['Max Gb']:
                        type_rec['Max Gb'] = mem
                if found == 1:
                    found += 1;
                    type_rec['Count'] += 1

    writehtmltable(table, fields, output)


def rpt_totals(hosttable, output):

    output.write('\n<h2>Totals</h2>\n')
    totalFields = [[0, "Type"], [1, "Count"], [2, "OS"], [3, "CPU-Min"], [4, "CPU-Max"], [5, "Mem-Min"], [6, "Mem-Max"]]
    totalType = []

    datanodes = ["Data Nodes", 0, [], 10000, 0, 100000, 0]
    for record in hosttable:
        if record[9] == 'X':
            datanodes[1] += 1
            if (record[1].encode('utf-8') not in datanodes[2]):
                datanodes[2].append(record[1].encode('utf-8'))
            # CPU Min
            if record[2] < datanodes[3]:
                datanodes[3] = record[2]
            # CPU Max
            if record[2] > datanodes[4]:
                datanodes[4] = record[2]
            # Mem Min
            if record[3] < datanodes[5]:
                datanodes[5] = record[3]
            # Mem Max
            if record[3] > datanodes[6]:
                datanodes[6] = record[3]

    totalType.append(datanodes)

    computeNodes = ["Compute Nodes", 0, [], 10000, 0, 100000, 0]
    for record in hosttable:
        if record[11] == 'X':
            computeNodes[1] += 1
            if (record[1].encode('utf-8') not in computeNodes[2]):
                computeNodes[2].append(record[1].encode('utf-8'))
            # CPU Min
            if record[2] < computeNodes[3]:
                computeNodes[3] = record[2]
            # CPU Max
            if record[2] > computeNodes[4]:
                computeNodes[4] = record[2]
            # Mem Min
            if record[3] < computeNodes[5]:
                computeNodes[5] = record[3]
            # Mem Max
            if record[3] > computeNodes[6]:
                computeNodes[6] = record[3]

    totalType.append(computeNodes)

    writehtmltable(totalType, totalFields, output)

def writeHeader(output):
    output.write('<table class="TFtable">')
    output.write('<tr>')
    output.write('<th>Date</th>')
    output.write('<td>' + run_date + '</td>')
    output.write('</tr><tr>')
    output.write('<th>Blueprint</th>')
    output.write('<td>' + bp_file + '</td>')
    output.write('</tr><tr>')
    output.write('<th>Layout</th>')
    output.write('<td>' + layout_file + '</td>')
    output.write('</tr><tr>')
    output.write('</table>')


def main():
    global cluster
    global glayout
    global layout_file
    global bp_file
    global run_date


    parser = optparse.OptionParser(usage="usage: %prog [options]")

    parser.add_option("-l", "--ambari-layout", dest="ambari_layout", help="Ambari Layout File")
    parser.add_option("-b", "--ambari-blueprint", dest="ambari_blueprint", help="Ambari Blueprint File")
    parser.add_option("-o", "--output-dir", dest="output_dir", help="Output Directory")

    (options, args) = parser.parse_args()

    logger.setLevel(logging.INFO)
    formatter = logging.Formatter('%(asctime)s %(levelname)s %(message)s')
    stdout_handler = logging.StreamHandler(sys.stdout)
    stdout_handler.setLevel(logging.INFO)
    stdout_handler.setFormatter(formatter)
    logger.addHandler(stdout_handler)

    loadControl(os.path.dirname(os.path.realpath(__file__)) + "/control.json")

    if options.ambari_layout and options.ambari_layout:
        layout_file = options.ambari_layout
        glayout = json.loads(open(options.ambari_layout).read())
        buildHostMatrix()

    if options.ambari_blueprint:
        bp_file = options.ambari_blueprint
        newblueprint = mergeConfigsWithHostMatrix(options.ambari_blueprint)

        run_date = str(date.today())

        output_dir = ''
        if options.output_dir:
            output_dir = options.output_dir
        else:
            output_dir = './' + run_date + '_' + options.ambari_blueprint[:-5] + '_eval'

        try:
            os.stat(output_dir)
        except:
            os.mkdir(output_dir)

    # if options.ambari_blueprint:
    #     newblueprint = mergeConfigsWithHostMatrix(options.ambari_blueprint)
        new_bp_filename = output_dir + '/' + options.ambari_blueprint[:-5] + '_cm.json'
        new_bp_output = open(new_bp_filename, 'w')
        new_bp_output.write(json.dumps(newblueprint, indent=2, sort_keys=False))
        new_bp_output.close()

        report(options.ambari_layout, output_dir)
    else:
        print ("Missing input")

main()
