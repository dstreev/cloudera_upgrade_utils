#!/usr/bin/env python

import optparse
from optparse import OptionGroup
import logging
import sys
import json
import os
from datetime import date
import requests

from requests.auth import HTTPBasicAuth

VERSION = "0.2.0"

# logger = logging.getLogger('ranger_policy_migration')

SERVICE_ENDPOINT = 'service'

host_url = ''
username = ''
password = ''

REST_POLICY = 'public/v2/api/policy'
REST_SERVICE = 'public/v2/api/service'

log_output_file = None
include_list = []
exclude_list = []


def main():
    global username
    global password
    global host_url
    global log_output_file
    global include_list
    global exclude_list

    parser = optparse.OptionParser(usage='usage: %prog [options]')

    connect_group = OptionGroup(parser, 'Connection', 'URL, User and password information needed to connect to Ranger')

    connect_group.add_option('-u', '--ranger-host-url', dest='host_url', help='Ranger Host Base URL')
    connect_group.add_option('-n', '--username', dest='user', help='Username')
    connect_group.add_option('-p', '--password', dest='password', help='Password')
    connect_group.add_option('-c', '--credentials', dest='credentials', help='Credentials File')
    parser.add_option_group(connect_group)

    # parser.add_option('-r', '--service-type', dest='service_type', help='service Type(HDFS,HIVE,TAGS)')
    service_group = OptionGroup(parser, 'Service Details', 'service details for migration')

    service_group.add_option('-f', '--from-service', dest='service_from',
                      help='Ranger service "from" Name')
    service_group.add_option('-t', '--to-service', dest='service_to', help='Ranger service "to" Name')
    service_group.add_option('-m', '--migration-method', dest='method', default='u', help='Migration Method: m(move) or u(upsert). u is default')
    parser.add_option_group(service_group)

    parser.add_option('-l', '--log', dest='log', help='Activity Log')

    detail_group = OptionGroup(parser, 'Migration Control', 'Optional, define include/exclude policy "id\'s" to handle')
    detail_group.add_option('-e', '--exclude-policies', dest='exclude_policies',
                      help='Comma separated list of policy id\'s to exclude')
    detail_group.add_option('-i', '--include-policies', dest='include_policies',
                      help='Comma separated list of policy id\'s to include')
    parser.add_option_group(detail_group)

    # debug_group = OptionGroup(parser, 'Debug Control', '')
    # debug_group.add_option('-d', '--debug', dest='debug',
    #                         help='Increase Logging output level')
    # debug_group.add_option('-v', '--validate', dest='validate',
    #                        help='Validate connection and services')

    (options, args) = parser.parse_args()

    # logger.setLevel(logging.INFO)
    # formatter = logging.Formatter('%(asctime)s %(levelname)s %(message)s')
    # stdout_handler = logging.StreamHandler(sys.stdout)
    # stdout_handler.setLevel(logging.INFO)
    # stdout_handler.setFormatter(formatter)
    # logger.addHandler(stdout_handler)

    if options.credentials:
        print('Not supported yet')

    if options.user and options.password:
        username = options.user
        password = options.password

    host_url = options.host_url

    if options.include_policies:
        include_list = list(map(int, options.include_policies.split(',')))
    if options.exclude_policies:
        exclude_list = list(map(int, options.exclude_policies.split(',')))

    service_info = validate_services(options.service_from, options.service_to)
    if len(service_info) != 2:
        if 'from' not in service_info.keys():
            print ("Couldn't find [FROM] service: " + options.service_from)
        if 'to' not in service_info.keys():
            print ("Couldn't find [TO] service: " + options.service_to)
        print ('Check that use are using the "Service Name" name and NOT the "Display Name"')
        exit(-1)
    if service_info['from']['type'] != service_info['to']['type']:
        print ("Services aren't the same type. \n\t" + service_info['from']['name'] + " is type: " + service_info['from']['type'] +
               "\n\t" + service_info['to']['name'] + " is type: " + service_info['to']['type'])
        exit(-1)

    ## With services, get list of Policies in each.
    service_info['to']['policies'] = get_service_policies(service_info['to']['id'])
    service_info['from']['policies'] = get_service_policies(service_info['from']['id'])

    if options.log:
        log_output = options.log
    else:
        log_output = 'ranger_policy_migration_' + str(date.today()) + '_log.txt'

    log_output_file = open(log_output, 'w')

    logit('Before:', False)
    logit(json.dumps(service_info, indent=2, sort_keys=False), False)

    try:
        # migrate(service_info) # Won't work without REST PUT support.
        # OR
        if options.method is None or options.method == 'u':
            upsert(service_info)
        else:
            move(service_info)

    except Exception as e:
        print ('Processing Issue: ')
        print (e)
    finally:
        # log_output_file.write(json.dumps(activity_log, indent=2, sort_keys=False))
        log_output_file.close()
        print ('Activity Log: ' + log_output)


def check(policy):
    if len(include_list) > 0:
        if policy['id'] not in include_list:
            return False
    if len(exclude_list) > 0:
        if policy['id'] in exclude_list:
            return False
    return True

# Make a copy, add to new service and delete old version.
def upsert(service_info):
    policy_url = host_url + '/' + SERVICE_ENDPOINT + '/public/v2/api/policy/apply'
    for policy in service_info['from']['policies']:
        if check(policy):
            action = {'action': 'move',
                      'from': service_info['from']['name'],
                      'to': service_info['to']['name'],
                      'policy': policy}
            # Create copy.
            upsert_policy = dict(policy)
            upsert_policy['service'] = service_info['to']['name']
            r = requests.post(policy_url, auth=HTTPBasicAuth(username, password),
                              json=upsert_policy)
            if r.status_code == 200:
                action['status'] = '200-success'
                action['message'] = json.loads(r.text)
                logit('Policy [' + upsert_policy['name'] + '] has been APPLIED to [' + service_info['to']['name'] + ']', True)
            else:
                logit('Issue adding policy [' + str(policy['id']) + ':' + policy['name'] + '] to the service [' +
                      service_info['to']['name'] + ']')
                logit('\t->Status Code: ' + str(r.status_code))
                logit('\t->Message: ' + r.text)
                logit('\t->This policy will remain in [' + service_info['from']['name'] + ']')

                action['status'] = r.status_code
                action['message'] = json.loads(r.text)
            logit('Action:', False)
            logit(json.dumps(action, indent=2, sort_keys=False), False)
        else:
            print 'NOTHING done with Policy [' + str(policy['id']) + ':' + policy['name'] + \
                  '] which was either NOT IN the include list, or IN the exclude list'


# Make a copy, add to new service and delete old version.
def move(service_info):
    policy_url = host_url + '/' + SERVICE_ENDPOINT + '/public/v2/api/policy'
    for policy in service_info['from']['policies']:
        if check(policy):
            action = {'action': 'move',
                      'from': service_info['from']['name'],
                      'to': service_info['to']['name'],
                      'policy': policy}
            # Create copy.
            move_policy = dict(policy)
            move_policy['service'] = service_info['to']['name']
            r = requests.post(policy_url, auth=HTTPBasicAuth(username, password),
                              json=move_policy)
            if r.status_code == 200:
                action['status'] = '200-success'
                action['message'] = json.loads(r.text)
                logit('Policy [' + move_policy['name'] + '] has been ADDED to [' + service_info['to']['name'] + ']', True)
                # Remove Policy from original service
                rd = requests.delete(policy_url + '/' + str(policy['id']), auth=HTTPBasicAuth(username, password),
                                     json=policy)
                if rd.status_code == 204:
                    logit('Policy  [' + str(policy['id']) + ':' + policy['name'] + '] has been REMOVED from [' +
                          service_info['from']['name'] + ']', True)
                else:
                    logit('Issue REMOVING the policy [' + str(policy['id']) + ':' + policy['name'] +
                          '] from the service [' + service_info['from']['name'] + ']')
                    logit('\t->Status Code: ' + str(rd.status_code))
                    logit('\t->Message: ' + rd.text)
                    logit('\t->This policy will remain in [' + service_info['from']['name'] + ']')

            else:
                logit('Issue adding policy [' + str(policy['id']) + ':' + policy['name'] + '] to the service [' +
                      service_info['to']['name'] + ']')
                logit('\t->Status Code: ' + str(r.status_code))
                logit('\t->Message: ' + r.text)
                logit('\t->This policy will remain in [' + service_info['from']['name'] + ']')

                action['status'] = r.status_code
                action['message'] = json.loads(r.text)
            logit('Action:', False)
            logit(json.dumps(action, indent=2, sort_keys=False), False)
        else:
            print 'NOTHING done with Policy [' + str(policy['id']) + ':' + policy['name'] + \
                  '] which was either NOT IN the include list, or IN the exclude list'


def logit(my_log_entry, with_print=True):
    log_output_file.write('\n' + my_log_entry)
    if with_print:
        print my_log_entry


def get_service_policies(service_id):
    url = host_url + '/' + SERVICE_ENDPOINT + '/plugins/policies/service/' + str(service_id)
    r = requests.get(url, auth=HTTPBasicAuth(username, password))
    policy_response = json.loads(r.text)
    return policy_response['policies']


def validate_services(service_from, service_to):
    url = host_url + '/' + SERVICE_ENDPOINT + '/' + REST_SERVICE
    r = requests.get(url, auth=HTTPBasicAuth(username, password))
    serviceList = json.loads(r.text)

    services = {}
    for service in serviceList:
        if service['name'] == service_from:
            from_service = {}
            from_service['name'] = service_from
            from_service['id'] = service['id']
            from_service['type'] = service['type']
            services['from'] = from_service
        if service['name'] == service_to:
            to_service = {}
            to_service['name'] = service_to
            to_service['id'] = service['id']
            to_service['type'] = service['type']
            services['to'] = to_service

    return services


# REST API PUT used to "update" policies.  But Ranger doesn't support PUT ops.
# Not sure this method will work without PUT.
def migrate(service_info):
    policy_url = host_url + '/' + SERVICE_ENDPOINT + '/public/v2/api/policy'
    for policy in service_info['from']['policies']:
        if check(policy):
            action = {'action': 'move',
                      'from': service_info['from']['name'],
                      'to': service_info['to']['name'],
                      'policy': policy}
            # Reset the Service Name.
            migrated_policy = dict(policy)
            migrated_policy['service'] = service_info['to']['name']
            r = requests.put(policy_url, auth=HTTPBasicAuth(username, password),
                             json=migrated_policy)
            if r.status_code == 200:
                action['status'] = '200-success'
                action['message'] = json.loads(r.text)
                logit('Policy: ' + migrated_policy['name'] + ' has been MIGRATED to: ' + service_info['to']['name'], True)
            else:
                logit('Issue MIGRATING policy ' + str(policy['id']) + ':' + policy['name'] + ' to the new service.')
                logit('->Status Code: ' + str(r.status_code))
                logit('->Message: ' + r.text)
                logit('->This policy will remain in: ' + service_info['from']['name'])

                action['status'] = r.status_code
                action['message'] = json.loads(r.text)
            logit('Action:', False)
            logit(json.dumps(action, indent=2, sort_keys=False), False)
        else:
            print 'NOTHING done with Policy [' + str(policy['id']) + ':' + policy['name'] + \
                  '] which was either NOT IN the include list, or IN the exclude list'


main()
