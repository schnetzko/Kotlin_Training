#!/bin/bash

# Start Spring Boot application with PostgreSQL. 
# Application provides multiple microservices. Each microservice represents
# 2 processes - PostgreSQL Server as container and Spring Boot application.
set -e

DEBUG_MODE=false

# Loop through all arguments passed to the script
for arg in "$@"; do
  if [[ "$arg" == "-debug" ]]; then
    DEBUG_MODE=true
    break # Stop looping once found
  else
    echo "Unknown argument: $arg"
    echo "Usage: ./start.sh [-debug]"
    exit 1 
  fi
done

is_service_running () {
  local service="$1"
  if pgrep -fl ${service} >/dev/null 2>&1; then
    return 0
  fi
  return 1
}

check_PostgreSQL_container () {
  local service="$1"
  local console_prefix="${service}: PostgreSQL container"
  echo "${console_prefix}: waiting for readiness..."
  for i in {1..30}; do
    docker exec -i kotlin_training_postgres_${service} pg_isready -U postgres >/dev/null 2>&1 && break || sleep 1
  done
  echo "${console_prefix}: is ready"

  echo "${console_prefix}: ensuring ${service}_db exists..."
  if docker exec -i kotlin_training_postgres_${service} psql -U postgres -tc "SELECT 1 FROM pg_database WHERE datname='${service}_db'" | grep -q 1; then
    echo "${console_prefix}: ${service}_db exists"
  else
    docker exec -i kotlin_training_postgres_${service} psql -U postgres -c "CREATE DATABASE ${service}_db;" && echo "$service: PostgreSQL container: Created ${service}_db"
  fi
}

start_services () {
  for service in "$@"; do
    check_PostgreSQL_container "$service"
    if (is_service_running "$service"); then
      echo "$service: Spring Boot service is already running..."
    else 
      echo "$service: Start Spring Boot service..."
      if [ "$DEBUG_MODE" = true ]; then
        ./gradlew ${service}:bootRunDebug --no-daemon > ${service}.log 2>&1 &
      else
        ./gradlew ${service}:bootRun --no-daemon > ${service}.log 2>&1 &
      fi
      echo "$service: Spring Boot service started as background process with PID $!"
      echo "$service: Logs are being written to ${service}.log"
    fi
  done
}

echo "Start PostgresSQL containers"
docker compose up -d

services=("patient" "examination" "diagnosis" "treatment")
start_services ${services[@]}