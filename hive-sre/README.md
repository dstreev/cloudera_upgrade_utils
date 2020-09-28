# Hive SRE Tooling

This application has 3 sub-programs:

- [`sre`](./sre.md) is used to find potential 'Hive' performance issues caused by small files and excessive partitions.
- [`u3`](./u3.md) is used to review 'Hive 1/2' environments for Hive3 upgrade planning.
- [`cli`](https://github.com/dstreev/hadoop-cli/blob/master/README.md) is an hdfs interactive client.  It is a core part of the `hive-sre` application, so we've exposed the shell here via the `hive-sre-cli` executable.
- [`perf`](./perf.md) is used to check the throughput of a JDBC connection.

### [Trouble-Shooting](./troubleshooting.md)

## Supported Metastore DB's

| Sub-Program | Database | Version | Tested | Notes |
|:---|:---|:---|:---|:---|
| `u3` | MySql | 5.6 | Limited | Recommend upgrading 5.7.  This is the lower MySql supported env for HDP |
|  |  | 5.7 | Yes |  |
|  |  | 5.7 | Yes |  |
|  |  | 8.0 | No | Not supported by HDP |
|  | MariaDb | 10.1 | No, but should work as 10.2 does |   |
|  |  | 10.2 | Yes |  |
|  | Postgresql | 9.6 | No, but should work |  |
|  |  | 10 | Yes | Field Tested, May still be a few rough edges |
|  |  | 11 | No, but should work at 10 does |  |
|  | Oracle | 12 | Yes | Field Tested, May still be a few rough edges |
| `sre` | MySql | 5.6 | Limited | Recommend upgrading 5.7.  This is the lower MySql supported env for HDP |
|  |  | 5.7 | Yes |  |
|  |  | 5.7 | Yes |  |
|  |  | 8.0 | No | Not supported by HDP |
|  | MariaDb | 10.1 | No, but should work as 10.2 does |   |
|  |  | 10.2 | Yes |  |
|  | Postgresql | 9.6 | No, but should work |  |
|  |  | 10 | Yes | Field Tested, May still be a few rough edges |
|  |  | 11 | No, but should work at 10 does |  |
|  | Oracle | 12 | Yes | Field Tested, May still be a few rough edges |

Ensure you have the database appropriate driver in the `${HOME}/.hive-sre/aux_lib` directory.

I've tried to match supported DB's for HDP 2.6.5 and 3.1.x as much as I could.

## Get the Binary

USE THE PRE-BUILT BINARY!!!  You won't have the necessary dependencies to build this from scratch without downloading and building the 'Hadoop Cli'.

_**Don't Build, Download the LATEST binary here!!!**_ 

[![Download the LATEST Binary](./images/download.png)](https://github.com/dstreev/cloudera_upgrade_utils/releases)

* Download the release 'tar.gz' file to a temp location.
* Untar the file (tar.gz).
```
tar xzvf <release>.tar.gz
cd hive-sre
```  
* As a root user, chmod +x the 3 shell script files.
* Run the 'setup.sh'.
```
./setup
```  

This will create and install the `hive-sre` and `hive-sre-cli` applications to your path.

Try it out on a host with default configs (if kerberized, get a ticket first):

    hive-sre-cli
OR
    
    hive-sre

## Configuring `hive-sre`

See the [config docs](./config.md) for details.


### Running

To ease the launch of the application below, configure these core environment variables.

```
hive-sre sre -db priv_dstreev -cfg /tmp/test.yaml -o ./sre-out` 
```

### Output

The output is a set of files with actions and error (when encountered).  The files maybe `txt` files or `markdown`.  You may want to use a `markdown` viewer for easier viewing of those reports.  The markdown viewer needs to support [github markdown tables](https://github.com/adam-p/markdown-here/wiki/Markdown-Cheatsheet#tables) .

                                            
### UI Details for `sre` and `u3`

Only active processes will show up in the UI.  The UI will refresh every second and display the current details below.

There are several 'processes' that are defined in `u3`.  Each process will run 1 or more 'sub-processes'.  The counters lists in the UI are specific to the 'process' and 'sub-processes' in that section.

The number of concurrent processes is controlled by the `parallelism` variable in the configuration yaml defined above.

1. Process Header - [process id: process name]
2. Sub Process - [table name: process state]
3. Sub Process Progress - [processed count / issues count / total in process count]
4. Process Footer Details.  Count of sub processes in various states. [Constructed:Waiting:Started:Processing:Error:Complete].  In the example below there are 5 sub-processes waiting for a slot to run, 3 sub-processes running, and 11 sub-processes that have completed.
5. Process Counts - [issues: processed so far]. The example below shows 6 issues in the 'Location Scan' and 9186 'completed checks' thus far.

![UI Details](images/hive-ui-details.png)
                         
### Tips

- Sorting results for loc_scan..

```
sort -k 1 --field-separator="|" loc_scan_missing_dirs.md > loc_scan_missing_dirs_sorted.txt
```




