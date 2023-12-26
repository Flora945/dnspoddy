#!/bin/zsh

ROOT_DIR=$(cd "$(dirname "$0")"; cd ..; pwd)

# create logs dir if not exists
if [ ! -d "$ROOT_DIR/logs" ]; then
  mkdir $ROOT_DIR/logs
fi

nohup $ROOT_DIR/bin/start.sh > $ROOT_DIR/logs/out.log 2>&1 &