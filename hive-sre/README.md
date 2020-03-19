## Hive SRE Tooling

### Hive JDBC Performance Testing Tool (perf)

JDBC Performance Testing tool.  Will provide connection timing details and rolling windows of performance for long running queries.  Details in the windows will show not only records but also an estimate of the data volume.

*Example Output*
```
========== v.2.0.1-SNAPSHOT ===========
URL        : jdbc:hive2://os04.streever.local:2181,os05.streever.local:2181,os10.streever.local:2181/;serviceDiscoveryMode=zooKeeper;zooKeeperNamespace=hiveserver2;principal=hive/_HOST@STREEVER.LOCAL
Batch Size : 10000
SQL        : SELECT field1_1,field1_2,field1_3,field1_4 FROM perf_test.wide_table
Lite       : false
----------------------------
Connect Attempt  : 0ms
Connected        : 2201ms
Create Statement : 2205ms
Before Query     : 2205ms
Query Return     : 2697ms
Start Iterating Results   : 2698ms
Completed Iterating Results: 79408ms
Statement Closed           : 79452ms
Resultset Closed           : 79452ms
Process Completed          : 79471ms

----------------------------
Window Length(ms) | Record Average | Records per/sec | Data Size per/sec
60000		7710000		128500		14970260
180000		10020000		125250		14642405
300000		10020000		125250		14642405
600000		10020000		125250		14642405

===========================
Running for: 80966ms		Started: 2020-03-06 13:57:40.492		Record Count: 10020000		Data Size: 1171392406
```

#### Environment and Connection via Knox
*Example*
```
URL="jdbc:hive2://os06.streever.local:8443/;ssl=true;sslTrustStore=/home/dstreev/certs/bm90-gateway.jks;trustStorePassword=hortonworks;transportMode=http;httpPath=gateway/default/hive"
QUERY="SELECT field1_1,field1_2,field1_3,field1_4 FROM perf_test.wide_table"
SRE_CP=./hive-sre-<version>-SNAPSHOT-shaded.jar:<hive-standalone-jdbc-driver.jar>
BATCH_SIZE=10000
PW=<set_me>

java -cp $SRE_CP com.streever.hive.Sre perf -u "${URL}" -e "${QUERY}" -b $BATCH_SIZE -n ${USER} -p <password> 
```

#### Environment and Connection via Kerberos from Edge
*Example*
```
URL="jdbc:hive2://os05.streever.local:10601/default;httpPath=cliservice;principal=hive/_HOST@STREEVER.LOCAL;transportMode=http"
QUERY="SELECT field1_1,field1_2,field1_3,field1_4 FROM perf_test.wide_table"
# Note that `hadoop classpath` statement to bring in all necessary libs.
SRE_CP=./hive-sre-<version>-SNAPSHOT-shaded.jar:<hive-standalone-jdbc-driver.jar>:`hadoop classpath`
BATCH_SIZE=10000

java -cp $SRE_CP com.streever.hive.Sre perf -u "${URL}" -e "${QUERY}" -b $BATCH_SIZE 
```

### SRE Application (sre)

The Sre Tool brings together information from the HMS RDBMS and HDFS to provide reports and potential actions to address areas of concern.  This process is a READ-ONLY process and does not perform any actions automatically.

Action commands for identified scenarios are written out to file(s), which can be reviewed / edited and run through either "beeline" for "hive" actions or in [Hadoop-CLI](https://github.com/dstreev/hadoop-cli) for hdfs commands.

This process is driven by a control file.  A template is [here](configs/driver.yaml.template).  Make a copy, edit the needed parameters and reference it with the '-cfg' parameter when running the process.

#### Application Help

```
Launching: sre
usage: Sre
 -cfg,--config <arg>     Config with details for the Sre Job.  Must match
                         the either sre or u3 selection.
 -db,--database <arg>    Comma separated list of Databases.  Will override
                         config. (upto 100)
 -o,--output-dir <arg>   Output Directory to save results from Sre.
```

The `-db` parameter is optional.  When specified, it will limit the search to the databases listed as a parameter.  IE: `-db my_db,test_db`

The `-o` parameter is *required*.

Sre needs to be run by a user with READ access to all the potential HDFS locations presented by the database/table/partition defined locations.
 
#### The Configuration File

```
# Required to connect to Metastore RDBMS.  RDBMS driver needs to be included in the classpath
metastore_direct:
  uri: "FULL_DB_URL"
  connectionProperties:
    user: "DB_USER"
    password: "DB_PASSWORD"
  connectionPool:
    min: 3
    max: 5
# Optional at this time
hs2:
  uri: "<OPTIONAL>"
  connectionPool:
    min: 3
    max: 5
# Control the number of threads to run scans with.  Should not exceed host core count.
# Increase parallelism will increase HDFS namenode pressure.  Advise monitoring namenode
# RPC latency while running this process.
parallelism: 4
queries:
  db_tbl_count:
    parameters:
      dbs:
        override: "%"
```

#### Running

`java -cp ./mariadb-java-client-2.5.3.jar:./hive-sre-<version>-SNAPSHOT-shaded.jar com.streever.hive.Sre sre -db priv_dstreev -cfg /tmp/test.yaml -o ./sre-out` 


### Hive Upgrade Check (u3)`

Review Hive Metastore Databases and Tables for upgrade or simply to evaluate potential issues.  Using [HDP Upgrade Utils](https://github.com/dstreev/hdp3_upgrade_utils) as the baseline for this effort.  The intent is to make that process much more prescriptive and consumable by Cloudera customers.  The application is 'Hive' based, so it should work against both 'HDP', 'CDH', and 'CDP' clusters.

#### Application Help

```
Launching: u3
usage: Sre
 -cfg,--config <arg>     Config with details for the Sre Job.  Must match
                         the either sre or u3 selection.
 -db,--database <arg>    Comma separated list of Databases.  Will override
                         config. (upto 100)
 -o,--output-dir <arg>   Output Directory to save results from Sre.
 ```

#### Running

`java -cp ./mariadb-java-client-2.5.3.jar:./hive-sre-<version>-SNAPSHOT-shaded.jar com.streever.hive.Sre u3 -db priv_dstreev -cfg /tmp/test.yaml -o ./sre-out` 

#### Check and Validations Performed

Actions are NOT taken by this process.  The output of each section will contain 'actions' for you to take when a scenario is discovered.  It is up to you to carry out those actions after reviewing them.

1. Hive 3 Upgrade Checks - Locations Scan
    - Missing Directories
    > Missing Directories cause the upgrade conversion process to fail.  To prevent that failure, there are two choices for a 'missing directory'.  Either create it of drop the table/partition.
2. Hive 3 Upgrade Checks - Bad ORC Filenames
    - Bad Filename Format
    > Tables that would be convert from a Managed Non-Acid table to an ACID transactional table require the files to match a certain pattern. This process will scan the potential directories of these tables for bad filename patterns.  When located, it will indicate which tables/partitions have been file naming conventions that would prevent a successful conversion to ACID.  The best and easiest way to correct these files names is to use HiveSQL to rewrite the contents of the table/partition with a simple 'INSERT OVERWRITE TABLE xxx SELECT * FROM xxx'.  This type of statement will replace the current bad filenames with valid file names by rewriting the contents in HiveSQL.
3. Hive 3 Upgrade Checks - Managed Table Migrations
    - Ownership Check
    - Conversion to ACID tables
    > This process will list tables that will and 'could' be migrated to "Managed ACID" tables during the upgrade process.  If these tables are used by Spark OR data is managed by a separate process that interacts with the FileSystem, DO NOT LET THESE conversion happen.  The output of this process will supply Hive DDL commands to convert these tables to "EXTERNAL / PURGE" tables in Hive 3, which is the same as the 'classic' Hive 1/2 Managed Non-Acid table.                                                                      
4. Hive 3 Upgrade Checks - Compaction Check
    - Compaction Check
    > Review ACID tables for 'delta' directories.  Where 'delta' directories are found, we'll 
5. Questionable Serde's Check
6. Managed Table Shadows



