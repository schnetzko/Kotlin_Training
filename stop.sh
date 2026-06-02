#!/bin/bash

# Stop Spring Boot application and PostgreSQL container

echo "Stopping Spring Boot application..."
pkill -f "DemoApplicationKt|bootRun|bootRunDebug" || true
sleep 1
echo "Application stopped"

echo "Stopping PostgreSQL container..."
docker stop kotlin_training_postgres >/dev/null 2>&1 && echo "PostgreSQL stopped" || echo "PostgreSQL container not running or already stopped"
