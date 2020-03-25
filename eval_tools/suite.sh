#!/usr/bin/env bash

# This tool will run the suite of evaluation tools, as long as
# the file naming conventions are followed.  See the individual
# tool readme's for details.

EXT_BP="-blueprint"

for BP_FILE in `ls *${EXT_BP}.json`; do
  echo ${BP_FILE}

  echo "Running Ambari Diff Tool"
  ambari_cfg_diff.py -c "${BP_FILE}"

  echo "Running Ambari Blueprint v2 Tool"
  ambari_bp_tool.py -b "${BP_FILE}"

  echo "Running Cluster Eval Report"
  hdp_eval.py -b "${BP_FILE}"

  # Parse the '-blueprint.json' from the filename
  BP=${BP_FILE:0:${#BP_FILE}-5}
  # Build the Ambari v2 Blueprint filename.
  BP_V2="${BP}-v2.json"
  CM_FILE="${BP:0:${#BP}-9}cm.json"

  echo "Converting Ambari Blueprint v2 to CM Environment Template"
  am2cm.sh --blueprint-file "${BP_V2}" --deployment-template-file "${CM_FILE}"

done
