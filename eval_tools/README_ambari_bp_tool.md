# Ambari Blueprint Tool

This tool assist with building Blueprints that support the Ambari to Cloudera Manager conversion process available from Cloudera.  This is an important step in the process of converting Ambari (HDP) managed clusters to CDP-DC.

Another function of this tool is to build a standard 'Cluster Creation Template' by combining a Blueprint and Layout from an existing Ambari Cluster.

## Artifact Naming Conventions

The tools in this suite have made special considerations for file naming conventions for the 'blueprints', 'layouts', and 'Cluster Creation Templates'.  In cases where the 'blueprint' and ('layout' or 'Cluster Creation Template') are required, you can omit specifying the 'layout' or 'cct' if the blueprint file ends with '-blueprint.json' and the layout ends with '-layout.json' or the cct ends with '-cct.json'.  The prefix for each must match.  For example: Blueprint filename: mytest-cluster-prod-blueprint.json would automatically look for a layout file named mytest-cluster-prod-layout.json or mytest-cluster-prod-cct.json.  If these don't match, you will need to specify both the blueprint and layout or cct options.

## Getting Artifacts from Ambari

Run these in a browser that has been logged into Ambari.  The results are JSON files.  Save and process.

### Get a Blueprint
http://${AMBARI_HOST_PORT}/api/v1/clusters/${CLUSTER_NAME}?format=blueprint

### Get a Layout:
http://${AMBARI_HOST_PORT}/api/v1/clusters/${CLUSTER_NAME}/hosts?fields=Hosts/host_name,host_components,Hosts/ip,Hosts/total_mem,Hosts/os_arch,Hosts/os_type,Hosts/rack_info,Hosts/cpu_count,Hosts/disk_info,metrics/disk,Hosts/ph_cpu_count
 
## Usage

```
Usage: ambari_bp_tool.py [options]

Options:
  -h, --help            show this help message and exit
  -l AMBARI_LAYOUT, --ambari-layout=AMBARI_LAYOUT
                        Ambari Layout File
  -c AMBARI_CREATION_TEMPLATE, --ambari-creation-template=AMBARI_CREATION_TEMPLATE
                        Ambari Cluster Creation Template
  -b AMBARI_BLUEPRINT, --ambari-blueprint=AMBARI_BLUEPRINT
                        Ambari Blueprint File
  -2 AMBARI_BLUEPRINT_V2, --ambari-blueprint-v2=AMBARI_BLUEPRINT_V2
                        Ambari Blueprint V2 File
  -o OUTPUT_DIR, --output-dir=OUTPUT_DIR
                        Output Directory
```
  
To create a 'Cluster Creation Template', use option `-l` and `-b`.  This will create a `*-cct.json` file.  If a layout is specified and a 'CCT' is not, we will create one.

The tool will create an 'Ambari v2 Blueprint' regardless.  Use the `-2` option to control the output.                        
