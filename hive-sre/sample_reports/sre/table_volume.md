# Table / Partition Volume
| Database | Table | Type | Partition | Path | Dir. Count | File Count | Total Size | 
|:---|:---|:---|:---|:---|---:|---:|---:|
| credit_card | cc_balance | MANAGED_TABLE |   | hdfs://HOME90/warehouse/tablespace/managed/hive/credit_card.db/cc_balance | 5 | 8 | 3.7 K |
| credit_card | cc_trans | MANAGED_TABLE |   | hdfs://HOME90/warehouse/tablespace/managed/hive/credit_card.db/cc_trans | 1 | 0 | 0 |
| credit_card | cc_trans_stream | MANAGED_TABLE |   | hdfs://HOME90/warehouse/tablespace/managed/hive/credit_card.db/cc_trans_stream | 1 | 0 | 0 |
| default | test | MANAGED_TABLE |   | hdfs://HOME90/warehouse/tablespace/managed/hive/test | 1 | 0 | 0 |
| default | test1 | MANAGED_TABLE |   | hdfs://HOME90/apps/spark/warehouse/test1 | 1 | 1 | 2 hdfs://HOME90/apps/spark/warehouse/test1 |
| default | test_ext | EXTERNAL_TABLE |   | hdfs://HOME90/user/dstreev/test_ext | 1 | 3 | 1.0 K |
| default | test_ext2 | EXTERNAL_TABLE |   | hdfs://HOME90/warehouse/tablespace/managed/hive/test_ext2 | 1 | 0 | 0 |
| my_test | test1 | EXTERNAL_TABLE |   | hdfs://HOME90/warehouse/tablespace/external/hive/my_test.db/test1 | 1 | 0 | 0 |
| my_test | test2 | EXTERNAL_TABLE |   | hdfs://HOME90/data/ext/my_test.db/test2 | 1 | 0 | 0 |
| priv_dstreev | cc_trans_part | EXTERNAL_TABLE | section=5 | hdfs://HOME90/user/dstreev/datasets/external/cc_trans_part/section=5 | 1 | 1 | 5.3 M |
| priv_dstreev | cc_trans_part | EXTERNAL_TABLE | section=8 | hdfs://HOME90/user/dstreev/datasets/external/cc_trans_part/section=8 | 1 | 1 | 5.3 M |
| priv_dstreev | cc_trans_part | EXTERNAL_TABLE | section=6 | hdfs://HOME90/user/dstreev/datasets/external/cc_trans_part/section=6 | 1 | 1 | 5.3 M |
| priv_dstreev | cc_trans_part | EXTERNAL_TABLE | section=2 | hdfs://HOME90/user/dstreev/datasets/external/cc_trans_part/section=2 | 1 | 1 | 5.3 M |
| priv_dstreev | cc_trans_part | EXTERNAL_TABLE | section=7 | hdfs://HOME90/user/dstreev/datasets/external/cc_trans_part/section=7 | 1 | 1 | 5.3 M |
| priv_dstreev | cc_trans_part | EXTERNAL_TABLE | section=15 | hdfs://HOME90/user/dstreev/datasets/external/cc_trans_part/section=15 | 1 | 1 | 5.3 M |
| priv_dstreev | cc_trans_part | EXTERNAL_TABLE | section=19 | hdfs://HOME90/user/dstreev/datasets/external/cc_trans_part/section=19 | 1 | 1 | 5.3 M |
| priv_dstreev | cc_trans_part | EXTERNAL_TABLE | section=9 | hdfs://HOME90/user/dstreev/datasets/external/cc_trans_part/section=9 | 1 | 1 | 5.3 M |
| priv_dstreev | cc_trans_part | EXTERNAL_TABLE | section=4 | hdfs://HOME90/user/dstreev/datasets/external/cc_trans_part/section=4 | 1 | 1 | 5.3 M |
| priv_dstreev | cc_trans_part | EXTERNAL_TABLE | section=13 | hdfs://HOME90/user/dstreev/datasets/external/cc_trans_part/section=13 | 1 | 1 | 5.3 M |
| priv_dstreev | cc_trans_part | EXTERNAL_TABLE | section=11 | hdfs://HOME90/user/dstreev/datasets/external/cc_trans_part/section=11 | 1 | 1 | 5.3 M |
| priv_dstreev | cc_trans_part | EXTERNAL_TABLE | section=18 | hdfs://HOME90/user/dstreev/datasets/external/cc_trans_part/section=18 | 1 | 1 | 5.3 M |
| priv_dstreev | cc_trans_part | EXTERNAL_TABLE | section=14 | hdfs://HOME90/user/dstreev/datasets/external/cc_trans_part/section=14 | 1 | 1 | 5.3 M |
| priv_dstreev | cc_trans_part | EXTERNAL_TABLE | section=17 | hdfs://HOME90/user/dstreev/datasets/external/cc_trans_part/section=17 | 1 | 1 | 5.3 M |
| priv_dstreev | cc_trans_part | EXTERNAL_TABLE | section=3 | hdfs://HOME90/user/dstreev/datasets/external/cc_trans_part/section=3 | 1 | 1 | 5.3 M |
| priv_dstreev | cc_trans_part | EXTERNAL_TABLE | section=10 | hdfs://HOME90/user/dstreev/datasets/external/cc_trans_part/section=10 | 1 | 1 | 5.3 M |
| priv_dstreev | cc_trans_part | EXTERNAL_TABLE | section=1 | hdfs://HOME90/user/dstreev/datasets/external/cc_trans_part/section=1 | 1 | 1 | 5.3 M |
| priv_dstreev | cc_trans_part | EXTERNAL_TABLE | section=16 | hdfs://HOME90/user/dstreev/datasets/external/cc_trans_part/section=16 | 1 | 1 | 5.3 M |
| priv_dstreev | cc_trans_part | EXTERNAL_TABLE | section=12 | hdfs://HOME90/user/dstreev/datasets/external/cc_trans_part/section=12 | 1 | 1 | 5.3 M |
| priv_dstreev | junk1 | EXTERNAL_TABLE |   | hdfs://HOME90/warehouse/tablespace/external/hive/priv_dstreev.db/junk1 | 1 | 0 | 0 |
| priv_dstreev | my_managed_table | MANAGED_TABLE |   | hdfs://HOME90/warehouse/tablespace/managed/hive/priv_dstreev.db/my_managed_table | 2 | 2 | 870 hdfs://HOME90/warehouse/tablespace/managed/hive/priv_dstreev.db/my_managed_table |
| priv_dstreev | my_spark_managed | EXTERNAL_TABLE |   | hdfs://HOME90/warehouse/tablespace/external/hive/priv_dstreev.db/my_spark_managed | 1 | 1 | 6 hdfs://HOME90/warehouse/tablespace/external/hive/priv_dstreev.db/my_spark_managed |
| priv_dstreev | my_test | MANAGED_TABLE |   | hdfs://HOME90/warehouse/tablespace/managed/hive/priv_dstreev.db/my_test | 3 | 3 | 790 hdfs://HOME90/warehouse/tablespace/managed/hive/priv_dstreev.db/my_test |
| priv_dstreev | my_test_01 | MANAGED_TABLE |   | hdfs://HOME90/warehouse/tablespace/managed/hive/priv_dstreev.db/my_test_01 | 3 | 4 | 1.5 K |
| priv_dstreev | my_test_convert | EXTERNAL_TABLE |   | hdfs://HOME90/warehouse/tablespace/managed/hive/priv_dstreev.db/my_test_convert | 1 | 0 | 0 |
| priv_dstreev | my_test_ext | EXTERNAL_TABLE |   | hdfs://HOME90/warehouse/tablespace/managed/hive/priv_dstreev.db/my_test_ext | 3 | 3 | 790 hdfs://HOME90/warehouse/tablespace/managed/hive/priv_dstreev.db/my_test_ext |
| priv_dstreev | my_test_extr | EXTERNAL_TABLE |   | hdfs://HOME90/warehouse/tablespace/external/hive/priv_dstreev.db/my_test_extr | 1 | 0 | 0 |
| priv_dstreev | spark_test_01 | EXTERNAL_TABLE |   | hdfs://HOME90/warehouse/tablespace/external/hive/priv_dstreev.db/spark_test_01 | 1 | 0 | 0 |
| priv_dstreev | spark_test_02 | EXTERNAL_TABLE |   | hdfs://HOME90/user/dstreev/dataset/spark_test_02 | 1 | 0 | 0 |
| priv_dstreev | test | MANAGED_TABLE |   | hdfs://HOME90/user/dstreev/datasets/internal.db/test | 1 | 0 | 0 |
| priv_dstreev | test_array | MANAGED_TABLE |   | hdfs://HOME90/user/dstreev/datasets/internal.db/test_array | 3 | 4 | 1.5 K |
| priv_dstreev | test_ext2 | EXTERNAL_TABLE |   | hdfs://HOME90/warehouse/tablespace/external/hive/priv_dstreev.db/test_ext2 | 1 | 0 | 0 |
| priv_jonsnow | my_ext_test_01 | EXTERNAL_TABLE |   | hdfs://HOME90/warehouse/tablespace/external/hive/priv_jonsnow.db/my_ext_test_01 | 1 | 0 | 0 |
| priv_jonsnow | my_table | EXTERNAL_TABLE |   | hdfs://HOME90/warehouse/tablespace/external/hive/priv_jonsnow.db/my_table | 1 | 0 | 0 |
| priv_jonsnow | my_test_01 | MANAGED_TABLE |   | hdfs://HOME90/warehouse/tablespace/managed/hive/priv_jonsnow.db/my_test_01 | 2 | 2 | 651 hdfs://HOME90/warehouse/tablespace/managed/hive/priv_jonsnow.db/my_test_01 |
| priv_jonsnow | my_test_02 | MANAGED_TABLE |   | hdfs://HOME90/warehouse/tablespace/managed/hive/priv_jonsnow.db/my_test_02 | 1 | 0 | 0 |
| priv_winterfell | test_01 | EXTERNAL_TABLE |   | hdfs://HOME90/warehouse/tablespace/external/hive/priv_winterfell.db/test_01 | 1 | 0 | 0 |
| priv_winterfell | test_managed | MANAGED_TABLE |   | hdfs://HOME90/warehouse/tablespace/managed/hive/priv_winterfell.db/test_managed | 1 | 0 | 0 |
| streaming_cc | cc_acct | MANAGED_TABLE |   | hdfs://HOME90/warehouse/tablespace/managed/hive/streaming_cc.db/cc_acct | 1 | 0 | 0 |
| streaming_cc | cc_acct_daily | MANAGED_TABLE |   | hdfs://HOME90/warehouse/tablespace/managed/hive/streaming_cc.db/cc_acct_daily | 1 | 0 | 0 |
| streaming_cc | cc_acct_delta | MANAGED_TABLE |   | hdfs://HOME90/warehouse/tablespace/managed/hive/streaming_cc.db/cc_acct_delta | 1 | 0 | 0 |
| streaming_cc | cc_mrch_daily | MANAGED_TABLE |   | hdfs://HOME90/warehouse/tablespace/managed/hive/streaming_cc.db/cc_mrch_daily | 1 | 0 | 0 |
| streaming_cc | cc_trans_alt_from_streaming | MANAGED_TABLE | processing_cycle=2019-02-14 | hdfs://HOME90/warehouse/tablespace/managed/hive/streaming_cc.db/cc_trans_alt_from_streaming/processing_cycle=2019-02-14 | 4 | 7 | 44.9 G |
| streaming_cc | cc_trans_alt_from_streaming | MANAGED_TABLE | processing_cycle=2019-02-15 | hdfs://HOME90/warehouse/tablespace/managed/hive/streaming_cc.db/cc_trans_alt_from_streaming/processing_cycle=2019-02-15 | 4 | 7 | 29.7 G |
| streaming_cc | cc_trans_alt_from_streaming_2 | MANAGED_TABLE |   | hdfs://HOME90/warehouse/tablespace/managed/hive/streaming_cc.db/cc_trans_alt_from_streaming_2 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=13-12 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=13-12 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=13-13 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=13-13 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=13-14 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=13-14 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=13-15 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=13-15 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=13-16 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=13-16 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=13-17 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=13-17 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=13-18 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=13-18 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=13-19 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=13-19 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=13-20 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=13-20 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=13-21 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=13-21 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=13-22 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=13-22 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=13-23 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=13-23 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=13-24 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=13-24 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=13-25 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=13-25 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=13-26 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=13-26 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=13-27 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=13-27 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=13-28 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=13-28 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=13-29 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=13-29 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=13-30 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=13-30 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=13-31 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=13-31 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=13-32 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=13-32 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=13-33 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=13-33 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=13-34 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=13-34 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=13-35 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=13-35 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=13-36 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=13-36 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=13-37 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=13-37 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=13-38 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=13-38 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=13-39 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=13-39 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=13-40 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=13-40 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=13-41 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=13-41 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=13-42 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=13-42 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=13-43 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=13-43 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=13-44 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=13-44 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=13-45 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=13-45 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=13-46 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=13-46 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=13-47 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=13-47 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=13-48 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=13-48 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=13-49 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=13-49 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=13-50 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=13-50 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=13-51 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=13-51 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=13-52 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=13-52 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=13-53 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=13-53 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=13-54 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=13-54 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=13-55 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=13-55 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=13-56 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=13-56 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=13-57 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=13-57 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=13-58 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=13-58 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=13-59 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=13-59 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=14-00 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=14-00 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=14-01 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=14-01 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=14-02 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=14-02 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=14-03 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=14-03 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=14-04 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=14-04 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=14-05 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=14-05 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=14-06 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=14-06 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=14-07 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=14-07 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=14-08 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=14-08 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=14-09 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=14-09 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=14-10 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=14-10 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=14-11 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=14-11 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=14-12 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=14-12 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=14-13 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=14-13 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=14-14 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=14-14 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=14-15 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=14-15 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=14-16 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=14-16 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=14-17 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=14-17 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=14-18 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=14-18 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=14-19 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=14-19 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=14-20 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=14-20 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=14-21 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=14-21 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=14-22 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=14-22 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=14-23 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=14-23 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=14-24 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=14-24 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=14-25 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=14-25 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=14-26 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=14-26 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=14-27 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=14-27 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=14-28 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=14-28 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=14-29 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=14-29 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=14-30 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=14-30 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=14-31 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=14-31 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=14-32 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=14-32 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=14-33 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=14-33 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=14-34 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=14-34 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=14-35 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=14-35 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=14-36 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=14-36 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=14-37 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=14-37 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=14-38 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=14-38 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=14-39 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=14-39 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=14-40 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=14-40 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=14-41 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=14-41 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=14-42 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=14-42 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=14-43 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=14-43 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=14-44 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=14-44 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=14-45 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=14-45 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=14-46 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=14-46 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=14-47 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=14-47 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=14-48 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=14-48 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=14-49 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=14-49 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=14-50 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=14-50 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=14-51 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=14-51 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=14-52 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=14-52 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=14-53 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=14-53 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=14-54 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=14-54 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=14-55 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=14-55 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=14-56 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=14-56 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=14-57 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=14-57 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=14-58 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=14-58 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=14-59 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=14-59 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=15-00 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=15-00 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=15-01 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=15-01 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=15-02 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=15-02 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=15-03 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=15-03 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=15-04 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=15-04 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=15-05 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=15-05 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=15-06 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=15-06 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=15-07 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=15-07 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=15-08 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=15-08 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=15-09 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=15-09 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=15-10 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=15-10 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=15-11 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=15-11 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=15-12 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=15-12 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=15-13 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=15-13 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=15-14 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=15-14 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=15-15 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=15-15 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=15-16 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=15-16 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=15-17 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=15-17 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=15-18 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=15-18 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=15-19 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=15-19 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=15-20 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=15-20 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=15-21 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=15-21 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=15-22 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=15-22 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=15-23 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=15-23 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=15-24 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=15-24 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=15-25 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=15-25 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=15-26 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=15-26 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=15-27 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=15-27 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=15-28 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=15-28 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=15-29 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=15-29 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=15-30 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=15-30 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=15-31 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=15-31 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=15-32 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=15-32 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=15-33 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=15-33 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=15-34 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=15-34 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=15-35 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=15-35 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=15-36 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=15-36 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=15-37 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=15-37 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=15-38 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=15-38 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=15-39 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=15-39 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=15-40 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=15-40 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=15-41 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=15-41 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=15-42 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=15-42 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=15-43 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=15-43 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=15-44 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=15-44 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=15-45 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=15-45 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=15-46 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=15-46 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=15-47 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=15-47 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=15-48 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=15-48 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=15-49 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=15-49 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=15-50 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=15-50 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=15-51 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=15-51 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=15-52 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=15-52 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=15-53 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=15-53 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=15-54 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=15-54 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=15-55 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=15-55 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=15-56 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=15-56 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=15-57 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=15-57 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=15-58 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=15-58 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=15-59 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=15-59 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=16-00 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=16-00 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=16-01 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=16-01 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=16-02 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=16-02 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=16-03 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=16-03 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=16-04 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=16-04 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=16-05 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=16-05 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=16-06 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=16-06 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=16-07 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=16-07 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=16-08 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=16-08 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=16-09 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=16-09 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=16-10 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=16-10 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=16-11 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=16-11 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=16-12 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=16-12 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=16-13 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=16-13 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=16-14 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=16-14 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=16-15 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=16-15 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=16-16 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=16-16 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=16-17 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=16-17 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=16-18 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=16-18 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=16-19 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=16-19 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=16-20 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=16-20 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=16-21 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=16-21 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=16-22 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=16-22 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=16-23 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=16-23 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=16-24 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=16-24 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=16-25 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=16-25 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=16-26 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=16-26 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=16-27 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=16-27 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=16-28 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=16-28 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=16-29 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=16-29 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=16-30 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=16-30 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=16-31 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=16-31 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=16-32 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=16-32 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=16-33 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=16-33 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=16-34 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=16-34 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=16-35 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=16-35 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=16-36 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=16-36 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=16-37 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=16-37 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=16-38 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=16-38 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=16-39 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=16-39 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=16-40 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=16-40 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=16-41 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=16-41 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=16-42 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=16-42 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=16-43 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=16-43 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=16-44 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=16-44 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=16-45 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=16-45 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=16-46 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=16-46 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=16-47 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=16-47 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=16-48 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=16-48 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=16-49 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=16-49 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=16-50 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=16-50 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=16-51 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=16-51 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=16-52 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=16-52 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=16-53 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=16-53 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=16-54 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=16-54 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=16-55 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=16-55 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=16-56 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=16-56 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=16-57 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=16-57 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=16-58 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=16-58 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=16-59 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=16-59 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=17-00 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=17-00 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=17-01 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=17-01 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=17-02 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=17-02 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=17-03 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=17-03 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=17-04 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=17-04 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=17-05 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=17-05 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=17-06 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=17-06 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=17-07 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=17-07 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=17-08 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=17-08 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=17-09 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=17-09 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=17-10 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=17-10 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=17-11 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=17-11 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=17-12 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=17-12 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=17-13 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=17-13 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=17-14 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=17-14 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=17-15 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=17-15 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=17-16 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=17-16 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=17-17 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=17-17 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=17-18 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=17-18 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=17-19 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=17-19 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=17-20 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=17-20 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=17-21 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=17-21 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=17-22 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=17-22 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=17-23 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=17-23 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=17-24 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=17-24 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=17-25 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=17-25 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=17-26 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=17-26 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=17-27 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=17-27 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=17-28 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=17-28 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=17-29 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=17-29 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=17-30 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=17-30 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=17-31 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=17-31 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=17-32 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=17-32 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=17-33 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=17-33 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=17-34 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=17-34 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=17-35 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=17-35 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=17-36 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=17-36 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=17-37 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=17-37 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=17-38 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=17-38 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=17-39 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=17-39 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=17-40 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=17-40 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=17-41 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=17-41 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=17-42 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=17-42 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=17-43 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=17-43 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=17-44 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=17-44 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=17-45 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=17-45 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=17-46 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=17-46 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=17-47 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=17-47 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=17-48 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=17-48 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=17-49 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=17-49 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=17-50 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=17-50 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=17-51 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=17-51 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=17-52 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=17-52 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=17-53 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=17-53 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=17-54 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=17-54 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=17-55 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=17-55 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=17-56 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=17-56 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=17-57 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=17-57 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=17-58 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=17-58 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=17-59 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=17-59 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=18-00 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=18-00 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=18-01 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=18-01 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=18-02 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=18-02 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=18-03 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=18-03 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=18-04 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=18-04 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=18-05 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=18-05 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=18-06 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=18-06 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=18-07 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=18-07 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=18-08 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=18-08 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=18-09 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=18-09 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=18-10 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=18-10 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=18-11 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=18-11 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=18-12 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=18-12 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=18-13 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=18-13 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=18-14 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=18-14 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=18-15 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=18-15 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=18-16 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=18-16 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=18-17 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=18-17 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=18-18 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=18-18 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=18-19 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=18-19 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=18-20 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=18-20 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=18-21 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=18-21 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=18-22 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=18-22 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=18-23 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=18-23 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=18-24 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=18-24 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=18-25 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=18-25 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=18-26 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=18-26 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=18-27 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=18-27 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=18-28 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=18-28 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=18-29 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=18-29 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=18-30 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=18-30 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=18-31 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=18-31 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=18-32 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=18-32 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=18-33 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=18-33 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=18-34 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=18-34 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=18-35 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=18-35 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=18-36 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=18-36 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=18-37 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=18-37 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=18-38 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=18-38 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=18-39 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=18-39 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=18-40 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=18-40 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=18-41 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=18-41 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=18-42 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=18-42 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=18-43 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=18-43 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=18-44 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=18-44 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=18-45 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=18-45 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=18-46 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=18-46 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=18-47 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=18-47 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=18-48 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=18-48 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=18-49 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=18-49 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=18-50 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=18-50 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=18-51 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=18-51 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=18-52 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=18-52 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=18-53 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=18-53 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=18-54 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=18-54 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=18-55 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=18-55 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=18-56 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=18-56 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=18-57 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=18-57 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=18-58 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=18-58 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=18-59 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=18-59 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=19-00 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=19-00 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=19-01 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=19-01 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=19-02 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=19-02 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=19-03 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=19-03 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=19-04 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=19-04 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=19-05 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=19-05 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=19-06 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=19-06 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=19-07 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=19-07 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=19-08 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=19-08 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=19-09 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=19-09 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=19-10 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=19-10 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=19-11 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=19-11 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=19-12 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=19-12 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=19-13 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=19-13 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=19-14 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=19-14 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=19-15 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=19-15 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=19-16 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=19-16 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=19-17 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=19-17 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=19-18 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=19-18 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=19-19 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=19-19 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=19-20 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=19-20 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=19-21 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=19-21 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=19-22 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=19-22 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=19-23 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=19-23 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=19-24 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=19-24 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=19-25 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=19-25 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=19-26 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=19-26 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=19-27 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=19-27 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=19-28 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=19-28 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=19-29 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=19-29 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=19-30 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=19-30 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=19-31 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=19-31 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=19-32 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=19-32 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=19-33 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=19-33 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=19-34 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=19-34 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=19-35 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=19-35 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=19-36 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=19-36 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=19-37 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=19-37 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=19-38 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=19-38 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=19-39 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=19-39 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=19-40 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=19-40 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=19-41 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=19-41 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=19-42 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=19-42 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=19-43 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=19-43 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=19-44 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=19-44 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=19-45 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=19-45 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=19-46 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=19-46 | 1 | 0 | 0 |
| streaming_cc | cc_trans_bridge | EXTERNAL_TABLE | processing_cycle=19-47 | hdfs://HOME90/user/nifi/datasets/external/cc_trans_bridge/processing_cycle=19-47 | 1 | 0 | 0 |

> truncated for brevity