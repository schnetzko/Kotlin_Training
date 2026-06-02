#!/bin/bash

# Check if Spring Boot application and PostgreSQL server are running

echo "=========================================="
echo "Status Check: Spring Boot & PostgreSQL"
echo "=========================================="
echo ""

# Check PostgreSQL container
echo "Checking PostgreSQL container..."
if docker ps --filter "name=kotlin_training_postgres" --filter "status=running" --format "{{.Names}}" | grep -q kotlin_training_postgres; then
  echo "✓ PostgreSQL container is RUNNING"
  
  # Check PostgreSQL connectivity
  if docker exec -i kotlin_training_postgres pg_isready -U postgres >/dev/null 2>&1; then
    echo "✓ PostgreSQL is READY (responding to queries)"
  else
    echo "✗ PostgreSQL is NOT ready (not responding)"
  fi
  
  # Check database existence
  if docker exec -i kotlin_training_postgres psql -U postgres -tc "SELECT 1 FROM pg_database WHERE datname='medical_data'" | grep -q 1; then
    echo "✓ Database 'medical_data' EXISTS"
  else
    echo "✗ Database 'medical_data' NOT found"
  fi
else
  echo "✗ PostgreSQL container is NOT running"
fi

echo ""

# Check Spring Boot application
echo "Checking Spring Boot application..."
if pgrep -f "DemoApplicationKt|java.*bootRun" >/dev/null; then
  echo "✓ Spring Boot application is RUNNING"
  
  # Check HTTP port
  if nc -z 127.0.0.1 8081 >/dev/null 2>&1; then
    echo "✓ HTTP port 8081 is LISTENING"
    
    # Check HTTP response
    if curl -s -o /dev/null -w "%{http_code}" http://127.0.0.1:8081/ | grep -q 200; then
      echo "✓ Application is RESPONSIVE (HTTP 200)"
    else
      STATUS=$(curl -s -o /dev/null -w "%{http_code}" http://127.0.0.1:8081/)
      echo "✗ Application returned HTTP $STATUS (expected 200)"
    fi
  else
    echo "✗ HTTP port 8081 is NOT listening"
  fi
  
  # Check debug port if available
  if nc -z 127.0.0.1 5005 >/dev/null 2>&1; then
    echo "✓ Debug port 5005 is LISTENING"
  fi
else
  echo "✗ Spring Boot application is NOT running"
fi

echo ""
echo "=========================================="
