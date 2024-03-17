#!/bin/bash

# Define table name and throughput settings
TABLE_NAME="Posts"
READ_CAPACITY_UNITS=1
WRITE_CAPACITY_UNITS=1

# Check if the DynamoDB table exists
TABLE_EXISTS=$(aws dynamodb describe-table --table-name $TABLE_NAME 2>&1)

# If the table does not exist, create it
if echo "$TABLE_EXISTS" | grep -q "ResourceNotFoundException"; then
  echo "Table does not exist, creating table: $TABLE_NAME"
  aws dynamodb create-table \
    --table-name $TABLE_NAME \
    --attribute-definitions AttributeName=Id,AttributeType=S \
    --key-schema AttributeName=Id,KeyType=HASH \
    --provisioned-throughput ReadCapacityUnits=$READ_CAPACITY_UNITS,WriteCapacityUnits=$WRITE_CAPACITY_UNITS
  
  echo "Created DynamoDB table: $TABLE_NAME"
else
  echo "Table already exists. Table status: $(echo $TABLE_EXISTS | jq -r '.Table.TableStatus')"
fi
