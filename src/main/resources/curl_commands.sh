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

# COMMANDS
# POST meetings:
#--------------------------------------------------
#  curl --request POST --header "Content-type: application/json" --data '{"title": "OpenShift w praktyce", "description": "wstep do Openshift", "date":"2018-05-23"}' http://localhost:8080/meetings
#  curl --request POST --header "Content-type: application/json" --data '{"title": "OpenShift for developers", "description": "Openshift w swiecie wytwarzania oprogramowania", "date":"2018-05-23"}' http://localhost:8080/meetings
# POST registration
# curl --write-out '\n'  --request POST --header "Content-type: application/json" --data '{"login":"user4", "password":"password"}' http://localhost:8080/meetings/2/registration
# DELETE meeting
# curl --write-out '\n'  --request DELETE --header "Content-type: application/json" http://localhost:8080/meetings/2 {"id":2,"title":"some title","description":"some description","date":"some date"}
# curl --write-out '\n' --request PUT --header "Content-type: application/json" --data '{"title":"Docker containers", "description":"Introduction to Docker containers", "date":"2018-05-25"}' http://localhost:8080/meetings/2
# DELETE participant from MEETING
# curl --write-out '\n'  --request DELETE --header "Content-type: application/json" http://localhost:8080/meetings/2/user2 {"id":2,"title":"some title","description":"some description","date":"some date"}
# POST search meetings po title i description
# curl   --verbose --write-out '\n' --header "Content-type: application/json" --request POST --data '{"title":"dupa", "description":"jakis"}' http://localhost:8080/meetings/search
# search meetingow na podstawie zapisanego usera
# curl  --write-out '\n' --verbose --header "Content-type: application/json" --request GET http://localhost:8080/meetings/search/user4
