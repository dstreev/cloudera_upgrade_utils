import sys
import os
import json
import operator


def dict_compare(base_dict, check_dict, cfg_spec):
    skip = []
    if 'skip' in cfg_spec:
        skip = cfg_spec['skip']
    skip_keys = set(skip)

    base_keys = set(base_dict.keys()) - skip_keys
    check_keys = set(check_dict.keys()) - skip_keys
    intersect_keys = base_keys.intersection(check_keys)
    added_keys = check_keys - base_keys
    added = {}
    for added_key in added_keys:
        added[added_key] = check_dict[added_key]
        # print "hello"

    removed_keys = base_keys - check_keys
    removed = {}
    for removed_key in removed_keys:
        removed[removed_key] = base_dict[removed_key]

    modified = {o : (base_dict[o], check_dict[o]) for o in intersect_keys if base_dict[o] != check_dict[o]}
    env_dep = {}
    env_dep_check = []
    if 'environment_dependent' in cfg_spec:
        env_dep_check = cfg_spec['environment_dependent']
    for ed in env_dep_check:
        if ed in modified.keys():
            env_dep[ed] = modified[ed]
            del modified[ed]

    # skip_keys = set(skip)

    # Check if
    # del modified[key]

    same = set(o for o in intersect_keys if base_dict[o] == check_dict[o])
    return added, removed, modified, env_dep, same

