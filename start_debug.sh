#!/bin/bash

# Start Spring Boot application in debug mode with PostgreSQL
set -e

if docker ps --filter "name=kotlin_training_postgres" --filter "status=running" --format "{{.Names}}" | grep -q kotlin_training_postgres; then
  echo "PostgreSQL already running"
elif docker ps -a --filter "name=kotlin_training_postgres" --format "{{.Names}}" | grep -q kotlin_training_postgres; then
  echo "Starting existing PostgreSQL container"
  docker start kotlin_training_postgres >/dev/null
else
  echo "Creating PostgreSQL container"
  docker run -d --name kotlin_training_postgres -p 5432:5432 -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=postgres -e POSTGRES_DB=medical_data postgres:15 >/dev/null
fi

echo "Waiting for PostgreSQL readiness..."
for i in {1..30}; do
  docker exec -i kotlin_training_postgres pg_isready -U postgres >/dev/null 2>&1 && break || sleep 1
done

echo "PostgreSQL is ready"
echo "Ensuring medical_data exists..."

if docker exec -i kotlin_training_postgres psql -U postgres -tc "SELECT 1 FROM pg_database WHERE datname='medical_data'" | grep -q 1; then
  echo "Database medical_data exists"
else
  docker exec -i kotlin_training_postgres psql -U postgres -c "CREATE DATABASE medical_data;" && echo "Created database medical_data"
fi

./gradlew bootRunDebug --no-daemon
