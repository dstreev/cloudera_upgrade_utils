#!/usr/bin/env python

import copy
import optparse
import logging
import sys
import json
import os
from common import pprinttable, pprinttable2, pprinthtmltable, writehtmltable
from datetime import date
from ambari import *
from os import path

VERSION = "0.1.4"

logger = logging.getLogger('ambari_bp_tool')

def main():

    parser = optparse.OptionParser(usage="usage: %prog [options]")

    parser.add_option("-l", "--ambari-layout", dest="ambari_layout", help="Ambari Layout File")
    parser.add_option("-c", "--ambari-creation-template", dest="ambari_creation_template",
                      help="Ambari Cluster Creation Template")
    parser.add_option("-b", "--ambari-blueprint", dest="ambari_blueprint", help="Ambari Blueprint File")
    parser.add_option("-2", "--ambari-blueprint-v2", dest="ambari_blueprint_v2", help="Ambari Blueprint V2 File")
    parser.add_option("-r", "--v2-reduction", dest="v2_reduction", action="store_true", help="WIP: Remove and consolidate HostGroups for CM Conversion", )
    parser.add_option("-w", "--worker-scale", dest="worker_scale", help="Reduce Cardinality of Worker Host Groups to this Cardinality")
    parser.add_option("-s", "--sub-hosts-file", dest="sub_hosts", help="Substitute Hosts in Blueprint with host in a file.")

    parser.add_option("-o", "--output-dir", dest="output_dir", help="Output Directory")

    (options, args) = parser.parse_args()

    logger.setLevel(logging.INFO)
    formatter = logging.Formatter('%(asctime)s %(levelname)s %(message)s')
    stdout_handler = logging.StreamHandler(sys.stdout)
    stdout_handler.setLevel(logging.INFO)
    stdout_handler.setFormatter(formatter)
    logger.addHandler(stdout_handler)

    if options.output_dir:
        output_dir = options.output_dir
    else:
        # run_date = str(date.today())
        output_dir = '.'

    if options.ambari_blueprint:
        bp_file = options.ambari_blueprint
        blueprint = json.loads(open(bp_file).read())
    else:
        print("Need to specify a Blueprint")
        exit(-1)

    # if options.host_group_reduction:
    #     consolidate_blueprint_host_groups(blueprint)

    layout = None
    cct = None
    if options.ambari_layout:
        layout_file = options.ambari_layout
        layout = json.loads(open(layout_file).read())
    elif not options.ambari_creation_template:
        # Making assumption on layout file based on BP filename
        layout_file = options.ambari_blueprint[:-14] + 'layout.json'
        if path.exists(layout_file):
            print ("+++ Using Ambari Layout File: " + layout_file)
            layout = json.loads(open(layout_file).read())
            # cct = build_creation_template_from_layout(blueprint, layout)
        # else:
        #     print("Can't locate layout file (based on blueprint filename: " + layout_file)
        #     exit(-1)

    if options.ambari_creation_template:
        cct_file = options.ambari_creation_template
        cct = json.loads(open(cct_file).read())
    elif layout is None:
        # Didn't load a layout and didn't specify
        cct_file = options.ambari_blueprint[:-14] + 'cct.json'
        if path.exists(cct_file):
            print ("+++ Using Cluster Creation Template file: " + cct_file)
            cct = json.loads(open(cct_file).read())

    if cct is None and layout is not None:
        print("\n-->> Using Ambari Layout to build Cluster Creation Template.")
        cct = build_creation_template_from_layout(blueprint, layout)
    elif options.v2_reduction:
        print ('todo: v2_reduction')

    if cct is None and layout is None:
        print ("You must provide either a 'layout' (-l) or a 'CCT' (-c)")
        exit(-1)

    if options.ambari_blueprint_v2:
        bp_v2_file = options.ambari_blueprint_v2
    else:
        bp_v2_file = output_dir + '/' + options.ambari_blueprint[:-5] + '-v2.json'

    print "\n--> Blueprint V2 output file: " + bp_v2_file

    bp_v2_output = open(bp_v2_file, 'w')

    bp_v2 = build_ambari_blueprint_v2(blueprint, cct)

    if options.worker_scale:
        print("\n-->> Scaling down worker nodes: " + options.worker_scale)
        reduce_worker_scale(bp_v2, int(options.worker_scale))

    if options.sub_hosts:
        sub_hosts_file = options.sub_hosts
        if not path.exists(sub_hosts_file):
            sub_hosts_file = os.path.dirname(os.path.realpath(__file__)) + "/hdp_support/sub_hosts_default.json"
            print ("WARNING: Input 'sub_hosts' file not found.  Using default.")
        sub_hosts = json.loads(open(sub_hosts_file).read())
        print("\n-->> Substituting Host fqdn's")
        substitute_hosts(bp_v2, sub_hosts['hosts'])

    bp_v2_output.write(json.dumps(bp_v2, indent=2, sort_keys=False))
    bp_v2_output.close()

main()