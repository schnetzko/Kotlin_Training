#!/bin/bash

# Check if PostgreSQL servers are running

echo "============================================================================="
echo "Status Check: PostgreSQL containers for Treatment, Diagnosis, and Examination"
echo "============================================================================="
echo ""

# Check PostgreSQL container for treatment
echo "Checking PostgreSQL container for treatment..."
if docker ps --filter "name=kotlin_training_postgres_treatment" --filter "status=running" --format "{{.Names}}" | grep -q kotlin_training_postgres_treatment; then
  echo "✓ PostgreSQL container for treatment is RUNNING"
  
  # Check PostgreSQL connectivity
  if docker exec -i kotlin_training_postgres_treatment pg_isready -U postgres >/dev/null 2>&1; then
    echo "✓ PostgreSQL for treatment is READY (responding to queries)"
  else
    echo "✗ PostgreSQL for treatment is NOT ready (not responding)"
  fi
  
  # Check database existence
  if docker exec -i kotlin_training_postgres_treatment psql -U postgres -tc "SELECT 1 FROM pg_database WHERE datname='treatment_db'" | grep -q 1; then
    echo "✓ Database 'treatment_db' EXISTS"
  else
    echo "✗ Database 'treatment_db' NOT found"
  fi
else
  echo "✗ PostgreSQL container for treatment is NOT running"
fi

echo ""

# Check PostgreSQL container for diagnosis
echo "Checking PostgreSQL container for diagnosis..."
if docker ps --filter "name=kotlin_training_postgres_diagnosis" --filter "status=running" --format "{{.Names}}" | grep -q kotlin_training_postgres_diagnosis; then
  echo "✓ PostgreSQL container for diagnosis is RUNNING"
  
  # Check PostgreSQL connectivity
  if docker exec -i kotlin_training_postgres_diagnosis pg_isready -U postgres >/dev/null 2>&1; then
    echo "✓ PostgreSQL for diagnosis is READY (responding to queries)"
  else
    echo "✗ PostgreSQL for diagnosis is NOT ready (not responding)"
  fi
  
  # Check database existence
  if docker exec -i kotlin_training_postgres_diagnosis psql -U postgres -tc "SELECT 1 FROM pg_database WHERE datname='diagnosis_db'" | grep -q 1; then
    echo "✓ Database 'diagnosis_db' EXISTS"
  else
    echo "✗ Database 'diagnosis_db' NOT found"
  fi
else
  echo "✗ PostgreSQL container for diagnosis is NOT running"
fi

echo ""

# Check PostgreSQL container for examination
echo "Checking PostgreSQL container for examination..."
if docker ps --filter "name=kotlin_training_postgres_examination" --filter "status=running" --format "{{.Names}}" | grep -q kotlin_training_postgres_examination; then
  echo "✓ PostgreSQL container for examination is RUNNING"
  
  # Check PostgreSQL connectivity
  if docker exec -i kotlin_training_postgres_examination pg_isready -U postgres >/dev/null 2>&1; then
    echo "✓ PostgreSQL for examination is READY (responding to queries)"
  else
    echo "✗ PostgreSQL for examination is NOT ready (not responding)"
  fi
  
  # Check database existence
  if docker exec -i kotlin_training_postgres_examination psql -U postgres -tc "SELECT 1 FROM pg_database WHERE datname='examination_db'" | grep -q 1; then
    echo "✓ Database 'examination_db' EXISTS"
  else
    echo "✗ Database 'examination_db' NOT found"
  fi
else
  echo "✗ PostgreSQL container for examination is NOT running"
fi

echo ""
echo "============================================================================="
