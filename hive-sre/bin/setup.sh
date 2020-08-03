#!/usr/bin/env sh

# Should be run as root.

cd `dirname $0`

mkdir -p /usr/local/hive-sre/bin
mkdir -p /usr/local/hive-sre/lib

cp -f hive-sre /usr/local/hive-sre/bin
cp -f hive-sre-cli /usr/local/hive-sre/bin

# Cleanup previous installation
rm -f /usr/local/hive-sre/lib/*.jar

if [ -f ../target/hive-sre-shaded.jar ]; then
    cp -f ../target/hive-sre-shaded.jar /usr/local/hive-sre/lib
fi

if [ -f hive-sre-shaded.jar ]; then
    cp -f hive-sre-shaded.jar /usr/local/hive-sre/lib
fi

chmod -R +r /usr/local/hive-sre
chmod +x /usr/local/hive-sre/bin/hive-sre
chmod +x /usr/local/hive-sre/bin/hive-sre-cli

ln -sf /usr/local/hive-sre/bin/hive-sre /usr/local/bin/hive-sre
ln -sf /usr/local/hive-sre/bin/hive-sre-cli /usr/local/bin/hive-sre-cli


