#!/usr/bin/env python

import optparse
from optparse import OptionGroup
import logging
import sys
import os
import json
from dict_diff import dict_compare
from datetime import date

VERSION = "0.1.5"

logger = logging.getLogger('Ambari_cfg_diff')

appdir = os.path.dirname(os.path.realpath(__file__))
ref_cluster_filename = appdir + '/hdp_support/ref_3_1_cluster.json'
output_filename = 'default_compare.txt'

eval_file = open(appdir + '/hdp_support/bp_cfg.json', 'r')
eval_cfg = json.load(eval_file)
eval_sections = sorted(eval_cfg['evaluate_cfgs'])

section_width = 100

part_sep = '***\n'


# part_sep = '{message:{fill}{align}{width}}\n'.format(
#     message='',
#     fill='-',
#     align='^',
#     width=3)

# part_title = ">>> %s <<<\n"


def fix(text):
    return text.replace('|', '\|<br>').replace(',', ',<br>').replace('_', '\_').replace('\n', '<br>').replace('*',
                                                                                                          '\*').replace(
        ';', ';<br>')


def write(key, added, removed, modified, env_dep, same, output):
    output.write("\n\n")
    # output.write('{message:{fill}{align}{width}}\n'.format(
    #     message='',
    #     fill='=',
    #     align='^',
    #     width=section_width,
    # ))## For Configuration Sections
    output.write('## [' + key + '](#forconfigurationsections)\n')
    # output.write('## {message:{fill}{align}{width}}\n'.format(
    #     message=key,
    #     fill=' ',
    #     align='<',
    #     width=section_width,
    # ))
    # output.write('{message:{fill}{align}{width}}\n'.format(
    #     message='',
    #     fill='=',
    #     align='^',
    #     width=section_width,
    # ))

    output.write(part_sep)
    if len(added) > 0:
        output.write('{message:{fill}{align}{width}}\n'.format(
            message='##### [ADDITIONAL ' + key + '](#forconfigurationsections)',
            fill=' ',
            align='<',
            width=section_width,
        ))
        # output.write(part_title % ("Extras",))
        # output.write(part_sep)

        output.write("| Property | Current Value |\n|:---|:---|\n")
        for akey in sorted(added):
            if len(akey) <= (section_width / 10 * 6):
                output.write('| ' + akey + ' | ' + fix(added.get(akey)) + ' |\n')
            else:
                output.write("| %s | %s |\n" % (fix(akey), fix(added.get(akey))))

    # output.write(part_sep)
    if len(removed) > 0:
        output.write('{message:{fill}{align}{width}}\n'.format(
            message='##### [MISSING ' + key + '](#forconfigurationsections)',
            fill=' ',
            align='<',
            width=section_width,
        ))
        # output.write(part_sep)

        output.write("| Property | Missing Value |\n|:---|:---|\n")

        for rkey in sorted(removed):
            if len(rkey) <= (section_width / 10 * 6):
                output.write('| ' + rkey + ' | ' + fix(removed.get(rkey)) + ' |\n')
            else:
                output.write("| %s | %s |\n" % (rkey, fix(removed.get(rkey))))

    # output.write(part_sep)
    if len(modified) > 0:
        output.write('{message:{fill}{align}{width}}\n'.format(
            message='##### [DIFF ' + key + '](#forconfigurationsections)',
            fill=' ',
            align='<',
            width=section_width,
        ))
        # output.write(part_sep)
        output.write("<table><tr><th>Property</th><th>Reference Value</th><th>Check Value</th><tr>")
        # output.write("| Property | Reference Value | Check Value |\n|:---|:---|:---|\n")

        for mkey in sorted(modified):
            output.write('<tr><td>' + mkey + '</td>\n<td>' +
                         fix(modified[mkey][0]) + '</td>\n<td>' +
                         fix(modified[mkey][1]) + '</td></tr>\n')
            # output.write('| ' + mkey + ' | ' + modified[mkey][0].replace('\n', ' <br>').replace('|', '&#124') + ' | ' +
            #              modified[mkey][1].replace('\n', ' <br>').replace('|', '&#124') + ' |\n')
        output.write("</table>\n")

    # output.write(part_sep)
    if len(env_dep) > 0:
        output.write('{message:{fill}{align}{width}}\n'.format(
            message='##### [ENV. DIFF ' + key + '](#forconfigurationsections)',
            fill=' ',
            align='<',
            width=section_width,
        ))
        # output.write(part_sep)
        output.write("| Property | Reference Value | Check Value |\n|:---|:---|:---|\n")

        for ekey in sorted(env_dep):
            output.write('| ' + ekey + ' | ' + fix(env_dep[ekey][0]) + ' | ' +
                         fix(env_dep[ekey][1]) + ' |\n')
            # output.write("| %s | %s | %s |\n" % (ekey, env_dep[ekey][0], env_dep[ekey][1]))

    # output.write(part_sep)
    if len(same) > 0:
        output.write('{message:{fill}{align}{width}}\n'.format(
            message='##### [SAME ' + key + '](#forconfigurationsections)',
            fill=' ',
            align='<',
            width=section_width,
        ))
        # output.write(part_sep)
        output.write("| Property |\n|:---|\n")

        for item in sorted(same):
            output.write("| %s |\n" % (item,))

    output.write("\n")


def compare(referencebp, checkbp, output):
    output.write("# Ambari Configuration Diff Tool\n")
    output.write("> This is a comparison of the 'reference' blueprint and the 'check' blueprint.\n")
    output.write(" The output will include 5 parts for each section examined:\n")
    output.write(" - ADDITIONAL : Keys present in the 'check' blueprint and not in the 'reference' blueprint.\n")
    output.write(" - MISSING    : Keys missing from the 'check' blueprint, compared to the 'reference' blueprint.\n")
    output.write(" - DIFFER     : Keys the differ between the 'check' and 'reference' blueprint.\n")
    output.write(" - ENV. DIFF  : Keys that are in both blueprints, but differ mostly due to environment.\n")
    output.write(" - SAME       : Keys that match between the two blueprints.\n")
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
    output.write("## For Configuration Sections\n")
    output.write('| Section | Added | Missing | Diff | Env Diff | Same |\n')
    output.write('|:---|---|---|---|---|---|\n')
    cfg_list = map(lambda section: '| [' + section + '](#' + section.replace(' ', '-').lower() + ')' +
                                   ' | [link](#additional' + section.replace(' ',
                                                                             '-').lower() + ')' + ' | [link](#missing' + section.replace(
        ' ', '-').lower() + ')' +
                                   ' | [link](#diff' + section.replace(' ',
                                                                       '-').lower() + ')' + ' | [link](#envdiff' + section.replace(
        ' ', '-').lower() + ')' +
                                   ' | [link](#same' + section.replace(' ', '-').lower() + ')' + ' |', eSections)
    # output.write("\n- ".join('[' + eSections + '](' + eSections.replace(' ', '-').lower() + ')'))
    output.writelines(["%s\n" % item for item in cfg_list])
    # output.write(*cfg_list, sep='\n')
    output.write('\n|---:|:---|\n')
    output.write('| Date | ' + str(date.today()) + ' |\n')
    # today = date.today()
    # tdy = '{message:{fill}{align}{width}}'.format(
    #     message='Date',
    #     fill=' ',
    #     align='>',
    #     width=section_width/5,
    # ) + " : " + str(today) + "\n"
    #
    # output.write(tdy)

    # ref = '{message:{fill}{align}{width}}'.format(
    #     message='Reference Blueprint',
    #     fill=' ',
    #     align='>',
    #     width=section_width/5,
    # ) + " : " + referencebp
    output.write('| * | |\n')
    output.write('| Reference Blueprint | ' + referencebp + ' |\n')
    output.write('| Reference Blueprint Stack | ' + referencedict['Blueprints']['stack_name'] + " " +
                 referencedict['Blueprints']['stack_version'] + ' |\n')
    print('\n\nReference Blueprint : ' + referencebp)
    # print (ref)

    # output.write("\n")
    # check = '{message:{fill}{align}{width}}'.format(
    #     message='Check Blueprint',
    #     fill=' ',
    #     align='>',
    #     width=section_width/5,
    # ) + " : " + checkbp
    output.write('| * | |\n')
    output.write('| Check Blueprint | ' + checkbp + ' |\n')
    output.write('| Check Blueprint Stack | ' + checkdict['Blueprints']['stack_name'] + " " +
                 checkdict['Blueprints']['stack_version'] + ' |\n')
    print('Check Blueprint : ' + checkbp)
    # print (check)
    # output.write(check)

    # otpt = '{message:{fill}{align}{width}}'.format(
    #     message='Output Filename',
    #     fill=' ',
    #     align='>',
    #     width=section_width/5,
    # ) + " : " + output_filename
    output.write('| * | |\n')
    output.write('| Output Filename | ' + output_filename + ' |\n')
    print('Output Filename : ' + output_filename)

    # print(otpt)

    # output.write("\n")
    #
    # output.write('{message:{fill}{align}{width}}'.format(
    #     message='Tool Version',
    #     fill=' ',
    #     align='>',
    #     width=section_width/5,
    # ) + " : " + VERSION)
    output.write('| Tool Version | ' + VERSION + ' |\n')
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
    parser.add_option("-o", "--output", dest="output", help="The output report file will be in 'markdown'.")

    (options, args) = parser.parse_args()

    global ref_cluster_filename
    global output_filename

    if not options.check:
        print("Required: -c <check_blueprint_file>")
        exit - 1
    else:
        check_filename = options.check
        output_filename = os.path.splitext(check_filename)[0] + "_diff.md"
        # output_filename =

    if options.reference:
        ref_cluster_filename = options.reference

    if options.output:
        output_filename = options.output

    output = open(output_filename, 'w')

    compare(ref_cluster_filename, check_filename, output)
    output.close()


main()
