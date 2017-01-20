#!/bin/bash

docker() {
            port=($(sudo /opt/docker/docker/docker ps -a | awk '{print $NF}' | grep -v "NAMES" | grep $1))
            printf '{\n'
            printf '\t"data":[\n'
               for key in ${!port[@]}
                   do
                       if [[ "${#port[@]}" -gt 1 && "${key}" -ne "$((${#port[@]}-1))" ]];then
                          printf '\t {\n'
                          printf "\t\t\t\"{#CONTAINERNAME}\":\"${port[${key}]}\"},\n"

                     else [[ "${key}" -eq "((${#port[@]}-1))" ]]
                          printf '\t {\n'
                          printf "\t\t\t\"{#CONTAINERNAME}\":\"${port[${key}]}\"}\n"

                       fi
               done

                          printf '\t ]\n'
                          printf '}\n'
}
docker $1
