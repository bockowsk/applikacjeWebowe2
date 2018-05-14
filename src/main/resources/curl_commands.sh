#!/bin/bash

while getopts t: option 
do
    echo "option: ${option} and OPTIND ${OPTIND} and OPTARG ${OPTARG}"
        case $option in
            (t)
                TESTING=1
                ;;
        esac
done

shift $(($OPTIND - 1))


CURL=/usr/bin/curl
PARAMS_GET="--request GET --write-out '\n' --header 'Content-type: application/json'"
HOSTNAME=localhost
PORT=8080
ENDPOINTS=(
        "participants" 
        "meetings"
);

for i in ${ENDPOINTS[@]}
do
    COMMAND=("${CURL} ${PARAMS_GET} http://${HOSTNAME}:${PORT}/${i}")
    eval $COMMAND
done


