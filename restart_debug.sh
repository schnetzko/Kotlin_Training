#!/bin/bash

# Restart Spring Boot application in debug mode and PostgreSQL container

echo "=========================================="
echo "Restarting Application (debug) & PostgreSQL"
echo "=========================================="
echo ""

# Stop everything
echo "Stopping services..."
./stop.sh

echo ""
echo "Waiting 2 seconds before restart..."
sleep 2

echo ""
# Start everything in debug mode
echo "Starting services (debug)..."
./start_debug.sh
