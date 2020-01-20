# Ambari Diff Tool

Used to compare Ambari Blueprints with the 'reference' cluster OR against another cluster blueprint.

## Usage

```
Usage: ambari_cfg_diff.py [options]

Options:
  -h, --help            show this help message and exit
  -r REFERENCE, --reference-file=REFERENCE
                        The standard (reference-file) file to compare against.
  -c CHECK, --check-file=CHECK
                        The file (check-file) that you want to compare.
  -o OUTPUT, --output=OUTPUT
                        The output report file.
```
                        
## Uses for this tool
- Compare the configuration between two clusters and identify items that may not be configured the same in each. IE: DEV vs. PROD
- Compare the cluster against a 'reference' configuration to identify anomolies. 

