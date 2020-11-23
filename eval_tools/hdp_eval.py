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
from os import path
from ambari import *

VERSION = "0.1.5"

logger = logging.getLogger('hdp_eval')

# HOSTS = {}
# SERVICES = {}
# CONTROL = {}
# glayout = {}
# blueprint = {}
cluster_creation_template = {}

layout_file = ''
bp_file = ''
run_date = ''
stack = ''


# A bitmask to associate to a hostgroup
# componentDict = {}


def build_field_path_from_abbr(control, abbrs):
    fields = []
    paths = {}
    for abbr in abbrs:
        for group in control:
            for component in control[group]:
                # print group + ":" + component
                if abbr == control[group][component]['abbr']:
                    # print 'found'
                    path = [group, component]
                    paths[abbr] = path
                    fields.append(abbr)

    return paths, fields


def get_hostname(item):
    host_info = item["Hosts"]
    return host_info["host_name"]


def aggregate_components(layout):
    services = {}

    items = layout['items']

    for item in items:
        components = item['host_components']
        for component in components:
            for key, value in component.items():
                if key == 'HostRoles':
                    for rkey, rvalue in value.items():
                        if rkey == 'component_name':
                            if rvalue in services:
                                services[rvalue] += 1
                            else:
                                services[rvalue] = 1
    return services


def is_component(item, componentName, componentDict):
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


def is_component_x(item, componentName):
    found, location = is_component(item, componentName)
    if found:
        return 'X'
    else:
        return ''


def get_control(controlFile):
    control = {}
    control = json.loads(open(controlFile).read())
    return control


def get_info(layoutFile):
    layout = json.loads(open(layoutFile).read())
    items = layout['items']

    hosttable, compute_count, other_count = gen_hosttable(items)

    return hosttable, compute_count, other_count


def append_css(output):
    output.write(
        '<style type="text/css">.TFtable{    width:100%;border-collapse:collapse;}.TFtable td{    padding:7px; border:#332200 1px solid;}')
    # /* provide some minimal visual accomodation for IE8 and below */
    output.write('    .TFtable tr{    background: #FFF7E6;}')
    # /*  Define the background color for all the ODD background rows  */
    output.write('    .TFtable tr:nth-child(odd){    background: #FFE6B3;}')
    # /*  Define the background color for all the EVEN background rows  */
    output.write('.TFtable tr:nth-child(even){    background: ##FF9966;}</style>')


def report(blueprint, hostMatrix, layout, control, componentDict, output_dir):
    index_filename = output_dir + '/index.html'
    index_output = open(index_filename, 'w')
    append_css(index_output)
    writeHeader(index_output)
    index_output.write('<br/>')
    index_output.write('<table class="TFtable">')
    index_output.write('<tr>')
    index_output.write('<th>')
    index_output.write('<a href="./services.html">Services</a>')
    index_output.write('</th></tr>')
    index_output.write('<tr>')
    index_output.write('<th>')
    index_output.write('<a href="./count_types.html">Count Types</a>')
    index_output.write('</th></tr>')
    index_output.write('<tr>')
    index_output.write('<th>')
    index_output.write('<a href="./hosttable.html">Host Table</a>')
    index_output.write('</th></tr>')
    index_output.write('<tr>')
    index_output.write('<th>')
    index_output.write('<a href="./hoststorage.html">Host Storage</a>')
    index_output.write('</th></tr>')
    index_output.write('<tr>')
    index_output.write('<th>')
    index_output.write('<a href="./mem_alloc.html">Host Memory Allocation</a>')
    index_output.write('</th></tr>')
    index_output.write('<tr>')
    index_output.write('<th>')
    index_output.write('<a href="./hosts.json">Hosts json</a>')
    index_output.write('</th></tr>')
    index_output.write('</table>')

    # index_output.write('<ol>')
    # index_output.write('<li><a href="./services.html">Services</a></li>')
    # index_output.write('<li><a href="./count_types.html">Count Types</a></li>')
    # index_output.write('<li><a href="./hosttable.html">Host Table</a></li>')
    # index_output.write('<li><a href="./hoststorage.html">Host Storage</a></li>')
    # index_output.write('<li><a href="./mem_alloc.html">Host Memory Allocation</a></li>')
    # index_output.write('<li><a href="./hosts.json">Hosts json</a></li>')
    # index_output.write('</ol>')

    index_output.close()

    services_filename = output_dir + '/services.html'
    services_output = open(services_filename, 'w')
    append_css(services_output)
    writeHeader(services_output)
    rpt_services(layout, services_output)
    services_output.close()

    count_types = {}
    count_types['Storage'] = ['DATANODE']
    count_types['Compute'] = ['NODEMANAGER']
    count_types['Master'] = ['NAMENODE', 'RESOURCEMANAGER', 'OOZIE_SERVER', 'HIVE_SERVER',
                             'HIVE_METASTORE']
    count_types['Kafka'] = ['KAFKA_BROKER']
    count_types['LLAP'] = ['HIVE_SERVER_INTERACTIVE']

    count_types_filename = output_dir + '/count_types.html'
    count_types_output = open(count_types_filename, 'w')
    append_css(count_types_output)
    writeHeader(count_types_output)
    rpt_count_type(blueprint, layout, count_types, count_types_output, componentDict)
    count_types_output.close()

    mem_alloc_filename = output_dir + '/mem_alloc.html'
    mem_alloc_output = open(mem_alloc_filename, 'w')
    append_css(mem_alloc_output)
    writeHeader(mem_alloc_output)
    rpt_mem_allocations(hostMatrix, control, mem_alloc_output)
    mem_alloc_output.close()

    # rpt_mem_allocations()

    hosttable_filename = output_dir + '/hosttable.html'
    hosttable_output = open(hosttable_filename, 'w')
    append_css(hosttable_output)
    writeHeader(hosttable_output)
    rpt_hosttable(hostMatrix, control, hosttable_output)
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
    append_css(hoststorage_output)
    writeHeader(hoststorage_output)
    rpt_hoststorage(hostMatrix, control, hoststorage_output)
    hoststorage_output.close()

    # rpt_hoststorage()

    # print ''
    # print '======================================='
    # print '  Host Details'
    # print '---------------------------------------'

    hostdump_filename = output_dir + '/hosts.json'
    hostdump_output = open(hostdump_filename, 'w')
    # appendCSS(hostdump_output)
    hostdump_output.write(json.dumps(hostMatrix, indent=2, sort_keys=True))
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

        record.append(is_component_x(item, 'KNOX_GATEWAY'))
        record.append(is_component_x(item, 'NAMENODE'))
        record.append(is_component_x(item, 'JOURNALNODE'))
        record.append(is_component_x(item, "ZKFC"))
        record.append(is_component_x(item, "DATANODE"))
        record.append(is_component_x(item, "RESOURCEMANAGER"))
        record.append(is_component_x(item, "NODEMANAGER"))
        record.append(is_component_x(item, "ZOOKEEPER_SERVER"))
        record.append(is_component_x(item, "HIVE_METASTORE"))
        record.append(is_component_x(item, "HIVE_SERVER"))
        record.append(is_component_x(item, "HIVE_SERVER_INTERACTIVE"))
        record.append(is_component_x(item, "OOZIE_SERVER"))
        record.append(is_component_x(item, "HBASE_MASTER"))
        record.append(is_component_x(item, "HBASE_REGIONSERVER"))
        record.append(is_component_x(item, "KAFKA_BROKER"))
        record.append(is_component_x(item, "NIFI_MASTER"))

        record.append(is_component_x(item, "LIVY2_SERVER"))
        record.append(is_component_x(item, "SPARK2_JOBHISTORY"))

        record.append(is_component_x(item, "DRUID_ROUTER"))
        record.append(is_component_x(item, "DRUID_OVERLOAD"))
        record.append(is_component_x(item, "DRUID_BROKER"))
        record.append(is_component_x(item, "DRUID_MIDDLEMANAGER"))
        record.append(is_component_x(item, "DRUID_HISTORICAL"))
        record.append(is_component_x(item, "DRUID_COORDINATOR"))

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


def host_matrix_from_layout(layout, control, componentDict):
    # layout = json.loads(open(layoutFile).read())
    items = layout['items']

    hostMatrix = {}

    services = {}

    for item in items:
        # Build total component counts for cluster while examining each item.
        # services = aggregateComponents(item)
        # add_to_component_dictionary(item, componentDict)

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
        for componentGroup in control:
            components[componentGroup] = {}
            for cKey in control[componentGroup]:
                # print cKey
                # print CONTROL[componentGroup][cKey]
                found, location = is_component(item, cKey, componentDict)
                if location > 0:
                    hostGroup = hostGroup | location
                if found:
                    cValue = control[componentGroup][cKey]
                    components[componentGroup].update({cKey: {'abbr': cValue['abbr']}})
        host['components'] = components
        host['HostGroupMask'] = get_host_group_mask(item, componentDict)

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

        hostMatrix[hostItem['host_name']] = host
    return hostMatrix


# def calcHostGroupBitMasks(hostgroups):
#     for hostgroup in hostgroups:
#         hgbitmask = 0
#         for component in hostgroup['components']:
#             try:
#                 hgbitmask = hgbitmask | componentDict[component['name']]
#             except:
#                 check = 'Component in Host that is not in the Layouts: ' + component['name']
#         hostgroup['HostGroupMask'] = hgbitmask


def rpt_mem_allocations(hostMatrix, control, output):
    output.write('\n<h2>Host Memory Allocations</h2>\n')
    fields = ['Hostname', 'Gb', 'Allocated', 'Components']
    mem_recs = []
    cluster_total_mem = 0
    for hostKey in hostMatrix:
        mem_rec = {}
        host = hostMatrix[hostKey]
        mem_rec['Hostname'] = host['Hostname']
        mem_rec['Gb'] = host['Gb']
        mem_rec_component_heaps = {}
        mem_rec['Components'] = {}
        for controlKey in control:
            for component in control[controlKey]:
                for hostGroupKey in host['components']:
                    if hostGroupKey == controlKey:
                        for hostComponentKey in host['components'][hostGroupKey]:
                            mem = {}
                            try:
                                mem['heap'] = host['components'][hostGroupKey][hostComponentKey]['heap']
                                try:
                                    mem['off.heap'] = host['components'][hostGroupKey][hostComponentKey]['off.heap']
                                    cluster_total_mem += mem['off.heap']
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
    output.write("<br/>")
    output.write("<br/>")
    output.write("<h3>Total Memory Footprint: " + str(cluster_total_mem) + " GB </h3>")


def rpt_services(layout, output):
    output.write('\n<h2>Service Counts</h2>\n')
    lcl_services = []
    fields = ['Service', 'Count']
    services = aggregate_components(layout)
    for service in services:
        lcl_service = {}
        lcl_service['Service'] = service
        lcl_service['Count'] = services[service]
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


def rpt_hosttable(hostMatrix, control, output):
    output.write('\n<h2>Host Table</h2>\n')
    # master = datanode & compute
    fields_base = ['Hostname', 'OS', 'vC', 'Gb', 'Rack']

    paths, bfields = build_field_path_from_abbr(control, ['KX', 'NN', 'JN', 'ZKFC', 'DN', 'RM', 'NM',
                                                      'ZK', 'HMS', 'HS2', 'HS2i', 'OZ', 'HM', 'RS',
                                                      'KB', 'NF', 'LV2', 'S2H', 'DR', 'DO', 'DB',
                                                      'DM', 'DH', 'DH'])

    fields = fields_base + bfields

    hosttable = []
    for hostKey in hostMatrix:
        host = hostMatrix[hostKey]
        hostRec = get_hostbase(host, fields_base)
        populate_components(paths, host['components'], hostRec)

        hosttable.append(hostRec)

    writehtmltable(hosttable, fields, output)


def rpt_hoststorage(hostMatrix, control, output):
    output.write('\n<h2>Host Storage</h2>\n')
    fields_base = ['Hostname', 'vC', 'Gb', 'Rack']

    paths, bfields = build_field_path_from_abbr(control, ['NN', 'JN', 'DN', 'ZK', 'NM', 'KB', 'NF'])

    fields = fields_base + bfields
    fields.append('DataDirs')
    fields.append('LogsDirs')
    fields.append('Disks')

    hosttable = []
    for hostKey in hostMatrix:
        host = hostMatrix[hostKey]
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


def rpt_count_type(blueprint, layout, types, output, componentDict):
    cluster_creation_template = {}

    output.write('\n<h2>Count Types</h2>\n')
    # layout = json.loads(open(layoutFile).read())
    items = layout['items']

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
                componentFound, hgbitmask = is_component(item, comp, componentDict)
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

    daemon_count = 0
    for cat in table:
        if cat['Category'] == 'LLAP' and cat['Count'] > 0:
            for config in blueprint['configurations']:
                if 'hive-interactive-env' in config.keys():
                    daemon_count = int(config['hive-interactive-env']['properties']['num_llap_nodes_for_llap_daemons'])
                    if config['hive-interactive-env']['properties']['num_llap_nodes'] > daemon_count:
                        daemon_count = int(config['hive-interactive-env']['properties']['num_llap_nodes'])
                    break

    if daemon_count > 0:
        output.write('\n<h2>LLAP Daemon Count: ' + str(daemon_count) + '</h2>\n')

    output.write('\n<h2>Unique Host Count: ' + str(len(layout['items'])) + '</h2>\n')


    # Generate Counts for Blueprint Host Groups.
    # Go through the Merged Blueprint and count the hosts in each host_group.
    hg_table = []
    hg_fields = ['Host Group', 'Count', 'Components', 'Hosts']
    cluster_creation_template['blueprint'] = 'need-to-set-me'
    host_groups = blueprint['host_groups']
    cct_host_groups = []
    for host_group in host_groups:
        cct_host_group = {}
        hgrec = {}
        hgrec['Host Group'] = host_group['name']
        cct_host_group['name'] = host_group['name']
        cct_hosts = []

        hgrec['Count'] = len(host_group['hosts'])
        hgrec_components = []
        for comps in host_group['components']:
            hgrec_components.append(comps['name'])
        hgrec['Components'] = hgrec_components
        hgrec_hosts = []
        for hst in host_group['hosts']:
            hgrec_hosts.append(hst['hostname'])
            cct_host = {}
            cct_host['fqdn'] = hst['hostname']
            cct_hosts.append(cct_host)
        cct_host_group['hosts'] = cct_hosts
        cct_host_groups.append(cct_host_group)

        hgrec['Hosts'] = hgrec_hosts
        hg_table.append(hgrec)
    cluster_creation_template['host_groups'] = cct_host_groups

    output.write('\n<h2>Ambari Host Group Info</h2>\n')

    writehtmltable(hg_table, hg_fields, output)

    return cluster_creation_template


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
    global stack

    output.write('<table class="TFtable">')
    output.write('<tr>')
    output.write('<th>Date</th>')
    output.write('<td>' + run_date + '</td>')
    output.write('</tr><tr>')
    output.write('<tr>')
    output.write('<th>Stack Version</th>')
    output.write('<td>' + stack + '</td>')
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
    global stack

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

    control = get_control(os.path.dirname(os.path.realpath(__file__)) + "/hdp_support/control.json")

    hostMatrix = {}
    layout = {}
    componentDict = {}
    # mergedBlueprint = {}

    # if not (options.ambari_layout):
    #     print ("Need to specify a 'layout'")
    #     exit(-1)

    if options.ambari_layout:
        layout_file = options.ambari_layout
        layout = json.loads(open(options.ambari_layout).read())
    else:
        layout_file = options.ambari_blueprint[:-14] + 'layout.json'
        if path.exists(layout_file):
            layout = json.loads(open(layout_file).read())
        else:
            print("Can't locate layout file (based on blueprint filename: " + layout_file)
            exit(-1)


    if options.ambari_blueprint:
        bp_file = options.ambari_blueprint
        blueprint = json.loads(open(bp_file).read())

        consolidate_blueprint_host_groups(blueprint, False)

        if layout is not None:
            componentDict = get_component_dictionary_from_bp(blueprint)
            hostMatrix = host_matrix_from_layout(layout, control, componentDict)
        else:
            print ("Couldn't find layout file: " + layout_file)
            exit(-1)

        # hostgroups = blueprint['host_groups']
        # calc_host_group_bit_masks(hostgroups, componentDict)

        stack = blueprint['Blueprints']['stack_name'] + ' ' + \
                blueprint['Blueprints']['stack_version']

        mergedBlueprint = merge_configs_with_host_matrix(blueprint, hostMatrix, componentDict, control)

        run_date = str(date.today())

        output_dir = ''
        if options.output_dir:
            output_dir = options.output_dir
        else:
            output_dir = './' + options.ambari_blueprint[:-5] + '_eval'
            # run_date + '_' +

        try:
            os.stat(output_dir)
        except:
            os.mkdir(output_dir)

        # if options.ambari_blueprint:
        #     newblueprint = mergeConfigsWithHostMatrix(options.ambari_blueprint)
        # new_bp_filename = options.ambari_blueprint[:-14] + '_bp_host_matrix.json'
        # new_bp_output = open(new_bp_filename, 'w')
        # new_bp_output.write(json.dumps(mergedBlueprint, indent=2, sort_keys=False))
        # new_bp_output.close()

        report(mergedBlueprint, hostMatrix, layout, control, componentDict, output_dir)
    else:
        print ("Missing input")


main()
