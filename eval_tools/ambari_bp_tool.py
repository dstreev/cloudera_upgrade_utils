#!/usr/bin/env python

import copy
import optparse
import logging
import sys
import json
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

    layout = None
    if options.ambari_layout:
        layout_file = options.ambari_layout
        layout = json.loads(open(layout_file).read())
    elif not options.ambari_creation_template:
        # Making assumption on layout file based on BP filename
        layout_file = options.ambari_blueprint[:-14] + 'layout.json'
        if path.exists(layout_file):
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
            cct = json.loads(open(cct_file).read())

    if cct is None and layout is not None:
        cct = build_creation_template_from_layout(blueprint, layout)

    if cct is None and layout is None:
        print ("You must provide either a 'layout' (-l) or a 'CCT' (-c)")
        exit(-1)

    if options.ambari_blueprint_v2:
        bp_v2_file = options.ambari_blueprint_v2
    else:
        bp_v2_file = output_dir + '/' + options.ambari_blueprint[:-5] + '-v2.json'

    print "bp_v2 output file: " + bp_v2_file

    bp_v2_output = open(bp_v2_file, 'w')
    bp_v2 = build_ambari_blueprint_v2(blueprint, cct)
    bp_v2_output.write(json.dumps(bp_v2, indent=2, sort_keys=False))
    bp_v2_output.close()

main()