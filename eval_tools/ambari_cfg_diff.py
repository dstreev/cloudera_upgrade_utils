#!/usr/bin/env python

import optparse
from optparse import OptionGroup
import logging
import sys
import os
import json
from dict_diff import dict_compare
from datetime import date

VERSION = "0.1.2"

logger = logging.getLogger('Ambari_cfg_diff')

appdir = os.path.dirname(os.path.realpath(__file__))
ref_cluster_filename = appdir + '/hdp_support/ref_3_1_cluster.json'
output_filename = 'default_compare.txt'

eval_file = open(appdir + '/hdp_support/bp_cfg.json', 'r')
eval_cfg = json.load(eval_file)
eval_sections = sorted(eval_cfg['evaluate_cfgs'])

section_width=100

part_sep = '{message:{fill}{align}{width}}\n'.format(
    message='',
    fill='-',
    align='^',
    width=section_width)

part_title = ">>> %s <<<\n"


def write(key, added, removed, modified, env_dep, same, output):
    output.write("\n\n")
    output.write('{message:{fill}{align}{width}}\n'.format(
        message='',
        fill='=',
        align='^',
        width=section_width,
    ))
    output.write('{message:{fill}{align}{width}}\n'.format(
        message=key,
        fill=' ',
        align='^',
        width=section_width,
    ))
    output.write('{message:{fill}{align}{width}}\n'.format(
        message='',
        fill='=',
        align='^',
        width=section_width,
    ))

    output.write(part_sep)
    output.write('{message:{fill}{align}{width}}\n'.format(
        message='\tADDITIONAL --> ('+key+')',
        fill=' ',
        align='<',
        width=section_width,
    ))
    # output.write(part_title % ("Extras",))
    output.write(part_sep)

    for akey in sorted(added):
        if len(akey) <= (section_width/10*6):
            output.write('{message:{fill}{align}{width}}'.format(
                message=akey,
                fill=' ',
                align='<',
                width=section_width/10*6,
            ) + ': ' + added.get(akey) + '\n')
        else:
            output.write("%s : %s\n" % (akey, added.get(akey)))
    output.write(part_sep)
    output.write('{message:{fill}{align}{width}}\n'.format(
        message='\tMISSING -----> ('+key+')',
        fill=' ',
        align='<',
        width=section_width,
    ))
    output.write(part_sep)

    for rkey in sorted(removed):
        if len(rkey) <= (section_width/10*6):
            output.write('{message:{fill}{align}{width}}'.format(
                message=rkey,
                fill=' ',
                align='<',
                width=section_width/10*6,
            ) + ': ' + removed.get(rkey) + '\n')
        else:
            output.write("%s : %s\n" % (rkey, removed.get(rkey)))

    output.write(part_sep)
    output.write('{message:{fill}{align}{width}}\n'.format(
        message='\tDIFFER ------> ('+key+')',
        fill=' ',
        align='<',
        width=section_width,
    ))
    output.write(part_sep)
    for mkey in sorted(modified):
        output.write("%s\n\t\tref    : %s\n\t\tcheck  : %s\n" % (mkey, modified[mkey][0], modified[mkey][1]))

    output.write(part_sep)
    output.write('{message:{fill}{align}{width}}\n'.format(
        message='\tENV. DIFF ---> ('+key+')',
        fill=' ',
        align='<',
        width=section_width,
    ))
    output.write(part_sep)
    for ekey in sorted(env_dep):
        output.write("%s\n\t\tref    : %s\n\t\tcheck  : %s\n" % (ekey, env_dep[ekey][0], env_dep[ekey][1]))
    output.write(part_sep)
    output.write('{message:{fill}{align}{width}}\n'.format(
        message='\tMATCHED -----> ('+key+')',
        fill=' ',
        align='<',
        width=section_width,
    ))
    output.write(part_sep)
    for item in sorted(same):
        output.write("%s\n" % (item,))


def compare(referencebp, checkbp, output):
    output.write("\n")
    output.write("This is a comparison of the 'reference' blueprint and the 'check' blueprint.\n")
    output.write(" The output will include 5 sections:\n")
    output.write("  ADDITIONAL : Keys present in the 'check' blueprint and not in the 'reference' blueprint.\n")
    output.write("  MISSING    : Keys missing from the 'check' blueprint, compared to the 'reference' blueprint.\n")
    output.write("  DIFFER     : Keys the differ between the 'check' and 'reference' blueprint.\n")
    output.write("  ENV. DIFF  : Keys that are in both blueprints, but differ mostly due to environment.\n")
    output.write("  MATCHED    : Keys that match between the two blueprints.\n")
    output.write("\n")

    referencebpfile = open(referencebp, 'r')
    checkbpfile = open(checkbp, 'r')

    referencedict = json.load(referencebpfile)
    checkdict = json.load(checkbpfile)

    referencedictCfg = sorted(referencedict['configurations'])
    checkdictCfg = checkdict['configurations']

    eSections = []

    for eSection in eval_sections:
        for eKey in eSection:
            eSections.append(eKey)
    output.write("For Configuration Sections:\n\t")
    output.write("\n\t".join(eSections))
    output.write("\n\n")

    today = date.today()
    tdy = '{message:{fill}{align}{width}}'.format(
        message='Date',
        fill=' ',
        align='>',
        width=section_width/5,
    ) + " : " + str(today) + "\n"

    output.write(tdy)

    ref = '{message:{fill}{align}{width}}'.format(
        message='Reference Blueprint',
        fill=' ',
        align='>',
        width=section_width/5,
    ) + " : " + referencebp
    output.write(ref)
    print (ref)

    output.write("\n")
    check = '{message:{fill}{align}{width}}'.format(
        message='Check Blueprint',
        fill=' ',
        align='>',
        width=section_width/5,
    ) + " : " + checkbp
    print (check)
    output.write(check)

    otpt = '{message:{fill}{align}{width}}'.format(
        message='Output Filename',
        fill=' ',
        align='>',
        width=section_width/5,
    ) + " : " + output_filename

    print(otpt)

    output.write("\n")

    output.write('{message:{fill}{align}{width}}'.format(
        message='Tool Version',
        fill=' ',
        align='>',
        width=section_width/5,
    ) + " : " + VERSION)
    output.write("\n")

    # iterate over the reference cfgs:
    for referenceCfg in referencedictCfg:
        for key in referenceCfg:
            # Determine if the current key is one of the eval_sections
            if any(key in section for section in eval_sections):
                spec = {}
                # Locate the Eval Section for Special Processing Instructions
                for section in eval_sections:
                    if key in section:
                        spec = section[key]
                        # print ("other")
                # Get the value of the reference cfg.
                value = referenceCfg[key]
                # >>> ["foo", "bar", "baz"].index("bar")
                # Iterate over the check cfgs
                for checkCfg in checkdictCfg:
                    for ckey in checkCfg:
                        # match to the current reference key
                        if ckey == key:
                            # get the check value
                            cvalue = checkCfg[ckey]

                            # get the 'properties' key from each of the values.
                            ref = value.get('properties')
                            check = cvalue.get('properties')
                            added, removed, modified, env_dep, same = dict_compare(ref, check, spec)
                            write(key, added, removed, modified, env_dep, same, output)


def main():
    parser = optparse.OptionParser(usage="usage: %prog [options]")

    parser.add_option("-r", "--reference-file", dest="reference",
                      help="The standard (reference-file) file to compare against.")
    parser.add_option("-c", "--check-file", dest="check", help="The file (check-file) that you want to compare.")
    parser.add_option("-o", "--output", dest="output", help="The output report file.")

    (options, args) = parser.parse_args()

    global ref_cluster_filename
    global output_filename

    if not options.check:
        print("Required: -c <check_blueprint_file>")
        exit -1
    else:
        check_filename = options.check
        output_filename = os.path.splitext(check_filename)[0] + "_diff.txt"
        # output_filename =

    if options.reference:
        ref_cluster_filename = options.reference

    if options.output:
        output_filename = options.output

    output = open(output_filename, 'w')

    compare(ref_cluster_filename, check_filename, output)
    output.close()

main()
