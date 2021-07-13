#!/usr/bin/env sh

# Should be run as root.

cd `dirname $0`

if (( $EUID != 0 )); then
  echo "Setting up as non-root user"
  BASE_DIR=$HOME/.hive-sre
else
  echo "Setting up as root user"
  BASE_DIR=/usr/local/hive-sre
fi

mkdir -p $BASE_DIR/bin
mkdir -p $BASE_DIR/lib

cp -f hive-sre $BASE_DIR/bin
cp -f hive-sre-cli $BASE_DIR/bin

# Cleanup previous installation
rm -f $BASE_DIR/lib/*.jar
rm -f $BASE_DIR/bin/*.*

if [ -f ../target/hive-sre-shaded.jar ]; then
    cp -f ../target/hive-sre-shaded.jar $BASE_DIR/lib
fi

if [ -f hive-sre-shaded.jar ]; then
    cp -f hive-sre-shaded.jar $BASE_DIR/lib
fi

chmod -R +r $BASE_DIR
chmod +x $BASE_DIR/bin/hive-sre
chmod +x $BASE_DIR/bin/hive-sre-cli

if (( $EUID == 0 )); then
  echo "Setting up global links"
  ln -sf $BASE_DIR/bin/hive-sre /usr/local/bin/hive-sre
  ln -sf $BASE_DIR/bin/hive-sre-cli /usr/local/bin/hive-sre-cli
else
  mkdir -p $HOME/bin
  ln -sf $BASE_DIR/bin/hive-sre $HOME/bin/hive-sre
  ln -sf $BASE_DIR/bin/hive-sre-cli $HOME/bin/hive-sre-cli
  echo "Executable in $HOME/bin .  Add this to the environment path."
fi

