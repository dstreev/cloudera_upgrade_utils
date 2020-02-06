# Cloudera Cluster Upgrade Utils

## Eval Tools

[Ambari Configuration Diff](./eval_tools/README_ambari_cfg_diff.md)
[HDP Eval Tool](./eval_tools/README_hdp_eval.md)

## Hive SRE Tooling

### Hive SRE
1. Hive 3 Performance Checks - Locations Scan
    - Small Files
2. Table Partition Count

### Hive Upgrade Check

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



