# HDP Evaluation Tool

Use this tool to help increase the visibility of cluster configurations. 

It will provide a layout of the clusters main components, counts for each component type, information about drive layouts, counts of certain component group types, and the start of memory allocations for each host.

The input to this is a 'layout' file and an 'Ambari blueprint' of the cluster.

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
