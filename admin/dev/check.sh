#!/bin/bash

echo "======================================"
echo "Status Check: Spring Boot & PostgreSQL"
echo "======================================"
echo ""

check_spring_boot_service () {
  echo "---------------------------------"
  echo "check Spring Boot service - BEGIN"
  echo "---------------------------------"
  local -n spring_boot_services="$1"

  for array_name in "${spring_boot_services[@]}"; do
    declare -n service_ref="$array_name"
    local service="${service_ref[name]}"
    local port="${service_ref[port]}"
    local debug_port="${service_ref[debug_port]}"

    local console_prefix="${service}: checking Spring Boot service"
    # Note: ^ ensures first letter is in uppercase, e.g. ->D<-iagnosisApplication
    if pgrep -f "${service^}Application" >/dev/null; then
      echo "${console_prefix} - application is RUNNING ✓"
      
      # Check HTTP port
      if nc -z 127.0.0.1 ${port} >/dev/null 2>&1; then
        echo "${console_prefix} - HTTP port ${port} is LISTENING ✓"
        
        # Check HTTP response
        if curl -s -o /dev/null -w "%{http_code}" http://127.0.0.1:${port}/ | grep -q 200; then
          echo "${console_prefix} - Application is RESPONSIVE (HTTP 200) ✓"
        else
          STATUS=$(curl -s -o /dev/null -w "%{http_code}" http://127.0.0.1:${port}/)
          echo "${console_prefix} - Application returned HTTP $STATUS (expected 200) ✗"
        fi
      else
        echo "${console_prefix} - HTTP port ${port} is NOT listening ✗"
      fi
      
      # Check debug port if available
      if nc -z 127.0.0.1 ${debug_port} >/dev/null 2>&1; then
        echo "${console_prefix} Debug port ${debug_port} is LISTENING ✓"
      fi
    else
      echo "${console_prefix} - application is NOT running ✗"
    fi
  done

  echo "-------------------------------"
  echo "check Spring Boot service - END"
  echo "-------------------------------"
  echo ""
}

check_PostgreSQL_container () {
  echo "----------------------------------"
  echo "check PostgreSQL container - BEGIN"
  echo "----------------------------------"

  local -n spring_boot_services="$1"

  for array_name in "${spring_boot_services[@]}"; do
    declare -n service_ref="$array_name"
    local service="${service_ref[name]}"
  
    local console_prefix="${service}: checking PostgreSQL container"

    if docker ps --filter "name=kotlin_training_postgres_${service}" --filter "status=running" --format "{{.Names}}" | grep -q kotlin_training_postgres_${service}; then
      echo "${console_prefix}: PostgreSQL container is RUNNING ✓"

      # Check PostgreSQL connectivity
      if docker exec -i kotlin_training_postgres_${service} pg_isready -U postgres >/dev/null 2>&1; then
        echo "${console_prefix}: PostgreSQL is READY (responding to queries) ✓"
      else
        echo "${console_prefix}: PostgreSQL is NOT ready (not responding) ✗"
      fi

      echo "${console_prefix}: ensuring ${service}_db exists..."
      if docker exec -i kotlin_training_postgres_${service} psql -U postgres -tc "SELECT 1 FROM pg_database WHERE datname='${service}_db'" | grep -q 1; then
        echo "${console_prefix}: '${service}_db' exists ✓"
      else
        echo "${console_prefix}: '${service}_db' NOT found ✗"
      fi
    else
      echo "${console_prefix}: PostgreSQL container is NOT running ✗"
    fi
  done

  echo "--------------------------------"
  echo "check PostgreSQL container - END"
  echo "--------------------------------"
  echo ""
}

declare -A patient
patient[name]="patient"
patient[port]=8081
patient[debug_port]=5006

declare -A examination
examination[name]="examination"
examination[port]=8084
examination[debug_port]=5007

declare -A diagnosis
diagnosis[name]="diagnosis"
diagnosis[port]=8083
diagnosis[debug_port]=5008

declare -A treatment
treatment[name]="treatment"
treatment[port]=8082
treatment[debug_port]=5005


services=(patient examination diagnosis treatment)

check_PostgreSQL_container services
check_spring_boot_service services