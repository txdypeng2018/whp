#!/bin/sh

export JAVA_HOME=/opt/jdk1.8.0_111
export PATH="$JAVA_HOME/bin:$PATH"

/opt/data-integration-7.0/kitchen.sh -file=/opt/kettle/isj_job_statistics.kjb
