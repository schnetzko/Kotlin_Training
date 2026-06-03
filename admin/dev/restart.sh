#!/bin/bash

# Restart Spring Boot application and PostgreSQL container

echo "=========================================="
echo "Restarting Application & PostgreSQL"
echo "=========================================="
echo ""

# Stop everything
echo "Stopping services..."
./stop.sh

echo ""
echo "Waiting 2 seconds before restart..."
sleep 2

echo ""
# Start everything
echo "Starting services..."
./start.sh
