#!/bin/bash

mongod --noprealloc --smallfiles &
PID="$!"

echo "WAITING FOR INIT"
sleep 3

echo "--> init"
mongo /code/devops/current/init.js
echo "--> init [DONE]"

kill $PID

echo "WAITING FOR KILL"
sleep 3

mongod --noprealloc --smallfiles --auth
