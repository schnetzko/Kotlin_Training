#!/bin/bash

set -e

echo "==================================="
echo "Restarting Application & PostgreSQL"
echo "==================================="
echo ""

DEBUG_MODE=false

for arg in "$@"; do
  if [[ "$arg" == "-debug" ]]; then
    DEBUG_MODE=true
    break
  else
    echo "Unknown argument: $arg"
    echo "Usage: ./restart.sh [-debug]"
    exit 1 
  fi
done

FILE_PATH_START_ROOT="admin/dev/start.sh"
FILE_PATH_START_CWD="./start.sh"

FILE_PATH_START_DEBUG_ROOT="admin/dev/start.sh -debug"
FILE_PATH_START_DEBUG_CWD="./start.sh -debug"

FILE_PATH_STOP_ROOT="admin/dev/stop.sh"
FILE_PATH_STOP_CWD="./stop.sh"

# check at least existence of stop.sh
if ! [ -f "$FILE_PATH_STOP_ROOT" ] && ! [ -f "$FILE_PATH_STOP_CWD" ]; then
    echo "Go to <project dir>/admin/dev/ to execute restart script from there"
    echo "exit ..."
    echo ""
    exit 0
fi

echo "Stopping services..."
if [ -f "$FILE_PATH_STOP_ROOT" ]; then
    ./$FILE_PATH_STOP_ROOT
else
    ./$FILE_PATH_STOP_CWD
fi

echo ""
echo "Waiting 2 seconds before restart..."
sleep 2
echo ""

echo "Starting services..."
if [ -f "$FILE_PATH_START_ROOT" ]; then
    if [ "$DEBUG_MODE" = true ]; then
        ./$FILE_PATH_START_DEBUG_ROOT
    else
        ./$FILE_PATH_START_ROOT
    fi
else
    if [ "$DEBUG_MODE" = true ]; then
        ./$FILE_PATH_START_DEBUG_CWD
    else
        ./$FILE_PATH_START_CWD
    fi
fi
