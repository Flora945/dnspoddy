#!/bin/zsh

CUR_DIR=$(cd $(dirname $0); pwd)

ROOT_DIR=$CUR_DIR/../

JAR=$ROOT_DIR/dnspoddy-1.0.0.jar

java -jar -Djava.net.preferIPv6Addresses=true -Xbootclasspath/a:$ROOT_DIR/config $JAR
