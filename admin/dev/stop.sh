#!/bin/bash

# Stop Spring Boot services and corresponding PostgreSQL containers
set -e

stop_services() {
    echo "Stop Spring Boot services"
    for service in "$@"; do
        echo "$service: started stopping..."
        # Note: ^ ensures first letter is in uppercase, e.g. ->D<-iagnosisApplication
        pkill -f "${service^}Application" >/dev/null 2>&1 && echo "$service: stopped" || echo "$service: process not running or already stopped"
        sleep 1
    done
}

services=("patient" "examination" "diagnosis" "treatment")
stop_services ${services[@]}

echo "Stop all PostgreSQL containers"
docker compose stop
