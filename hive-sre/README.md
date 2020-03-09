## Hive SRE Tooling

### Hive JDBC Performance Testing Tool



### SRE Application

The Sre Tool brings together information from the HMS RDBMS and HDFS to provide reports and potential actions to address areas of concern.  This process is a READ-ONLY process and does not perform any actions automatically.

Action commands for identified scenarios are written out to file(s), which can be reviewed / edited and run through either "beeline" for "hive" actions or in [Hadoop-CLI](https://github.com/dstreev/hadoop-cli) for hdfs commands.

This process is driven by a control file.  A template is [here](configs/driver.yaml.template).  Make a copy, edit the needed parameters and reference it with the '-cfg' parameter when running the process.

#### Application Help

```
usage: java -cp <Sre-uber.jar> -h
 -cfg,--config <arg>     Config with details for the Sre Job.  Must match
                         the either sre or u3 selection.
 -db,--database <arg>    Comma separated list of Databases.  Will override
                         config. (upto 100)
 -o,--output-dir <arg>   Output Directory to save results from Sre.
 -sre,--sre              Run the SRE suite of checks.
 -u3,--upgrade3          Upgrade to Hive3 Checks
```

There are two modes for running the application.  `-u3` or `-sre`.  These are details below.

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

`java -cp /tmp/mariadb-java-client-2.5.3.jar:/tmp/hive-sre-1.0-SNAPSHOT-uber.jar com.streever.hive.Sre -sre -db priv_dstreev -cfg /tmp/test.yaml -o ./sre-out` 

#### Modes

##### Hive SRE `-sre`
1. Hive 3 Performance Checks - Locations Scan
    - Small Files
2. Table Partition Count

##### Hive Upgrade Check `-u3`

Review Hive Metastore Databases and Tables for upgrade or simply to evaluate potential issues.  Using [HDP Upgrade Utils](https://github.com/dstreev/hdp3_upgrade_utils) as the baseline for this effort.  The intent is to make that process much more prescriptive and consumable by Cloudera customers.  The application is 'Hive' based, so it should work against both 'HDP', 'CDH', and 'CDP' clusters.

#### Check and Validations Performed

1. Hive 3 Upgrade Checks - Locations Scan
    - Missing Directories
    > Missing Directories cause the upgrade conversion process to fail.  To prevent that failure, there are two choices for a 'missing directory'.  Either create it of drop the table/partition.
2. Hive 3 Upgrade Checks - Bad ORC Filenames
    - Bad Filename Format
    > Tables that would be convert from a Managed Non-Acid table to an ACID transactional table require the files to match a certain pattern. This process will scan the potential directories of these tables for bad filename patterns.
3. Hive 3 Upgrade Checks - Managed Table Migrations
    - Ownership Check
    > 
4. Hive 3 Upgrade Checks - Compaction Check
    - Compaction Check
    > Review ACID tables for 'delta' directories.  Where 'delta' directories are found, we'll 
5. Questionable Serde's Check
6. Managed Table Shadows



