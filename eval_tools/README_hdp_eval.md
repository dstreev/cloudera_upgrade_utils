# HDP Evaluation Tool

Use this tool to help increase the visibility of cluster configurations. 

It will provide a layout of the clusters main components, counts for each component type, information about drive layouts, counts of certain component group types, and the start of memory allocations for each host.

The input to this is a 'layout' file and an 'Ambari blueprint' of the cluster.


## Artifact Naming Conventions

The tools in this suite have made special considerations for file naming conventions for the 'blueprints' and 'layouts'.  In cases where the 'blueprint' and 'layout' are required, you can omit specifying the 'layout' if the blueprint file ends with '-blueprint.json' and the layout ends with '-layout.json'.  The prefix for each must match.  For example: Blueprint filename: mytest-cluster-prod-blueprint.json would automatically look for a layout file named mytest-cluster-prod-layout.json.  If these don't match, you will need to specify both the blueprint and layout options.

## Getting Artifacts from Ambari

Run these in a browser that has been logged into Ambari.  The results are JSON files.  Save and use as input to this process.

### Get a Blueprint:
```
http://${AMBARI_HOST_PORT}/api/v1/clusters/${CLUSTER_NAME}?format=blueprint
```

### Get a Layout:
```
http://${AMBARI_HOST_PORT}/api/v1/clusters/${CLUSTER_NAME}/hosts?fields=Hosts/host_name,host_components,Hosts/ip,Hosts/total_mem,Hosts/os_arch,Hosts/os_type,Hosts/rack_info,Hosts/cpu_count,Hosts/disk_info,metrics/disk,Hosts/ph_cpu_count
```

## Usage: hdp_eval.py [options]

```
Options:
  -h, --help            show this help message and exit
  -l AMBARI_LAYOUT, --ambari-layout=AMBARI_LAYOUT
                        .
  -b AMBARI_BLUEPRINT, --ambari-blueprint=AMBARI_BLUEPRINT
  -o OUTPUT_DIR, --output_dir
```
