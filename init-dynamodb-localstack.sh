#!/bin/bash

# Define table creation JSON
read -r -d '' TABLE_DEFINITION << EOF
{
    "TableName": "Posts",
    "AttributeDefinitions": [
        {
            "AttributeName": "id",
            "AttributeType": "S"
        }
    ],
    "KeySchema": [
        {
            "AttributeName": "id",
            "KeyType": "HASH"
        }
    ],
    "ProvisionedThroughput": {
        "ReadCapacityUnits": 1,
        "WriteCapacityUnits": 1
    }
}
EOF

# Create the DynamoDB table
aws dynamodb create-table --cli-input-json "$TABLE_DEFINITION"

echo "Table creation initiated..."
