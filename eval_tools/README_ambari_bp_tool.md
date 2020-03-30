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
  -r, --v2-reduction    WIP: Remove and consolidate HostGroups for CM
                        Conversion
  -w WORKER_SCALE, --worker-scale=WORKER_SCALE
                        Reduce Cardinality of Worker Host Groups to this
                        Cardinality
  -s SUB_HOSTS, --sub-hosts-file=SUB_HOSTS
                        Substitute Hosts in Blueprint with host in a file.
  -o OUTPUT_DIR, --output-dir=OUTPUT_DIR
                        Output Directory
```
  
To create a 'Cluster Creation Template', use option `-l` and `-b`.  This will create a `*-cct.json` file.  If a layout is specified and a 'CCT' is not, we will create one.

The tool will create an 'Ambari v2 Blueprint' regardless.  Use the `-2` option to control the output.

To build clusters with Blueprint V2 with a lower number of hosts, use the `-s <number>` to control how many hosts to reduce them down to.

The `-s <host_json_file>` option allows you to replace the host in the output Blueprint V2 file with those in the host file.  See [host file](./hdp_support/sub_host_default.json) for the format.

## Noteworthy Observations

### Building v2 output from v1 Blueprint and Layout

When Ambari Managed groups are used (especially for worker nodes) an accurate mapping of hosts and host_groups can't be guaranteed.  There isn't enough information to associate the host to the intended host_group.  If a "Cluster Creation Template" is provided, this mapping is already implied.  Regardless, when a 'layout' is used we will 'strip' the host_group configurations out, reduce the host_group supported services to those that will be supported in Cloudera Manager and then consolidate the remaining host_groups to removed duplicates created through this reduction process.  In the end, a smaller number of 'unique' host_groups will exist and only the main configurations will be translated to the output Blueprint V2 file.

## Example Uses

- Create a V2 Blueprint from a V1 Blueprint and Cluster Creation Template.

`ambari_bp_tool.py -b my-test-blueprint.json -c my-test-cct.json`

- Create a V2 Blueprint from a V1 Blueprint when a Layout OR Cluster Creation Template exist in the same directory.

`ambari_bp_tool.py -b my-test-blueprint.json`

> Expects to find `my-test-layout.json` in the same directory
> If not found, will look for `my-test-cct.json` in the same directory.

- Create a V2 Blueprint from a V1 Blueprint with a Large Cluster Layout.  Goal is to produce a small cluster for testing, based on the larger clusters configuration.  And replace the host names with new host fqdn that match your test environment.

`ambari_bp_tool.ph -b my-big-cluster-blueprint.json -l my-big-cluster-layout.json -2 my-small-test-cluster-blueprint_v2.json -w 3 -s replacement_hosts.json`                 
