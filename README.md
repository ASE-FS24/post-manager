# post-manager
This app handles post management

# Starting the app in dev mode

1. Start up docker-compose-localstack.yml
2. Publish the ports using:
   docker run -p 4566:4566 localstack/localstack
3. Run init-dynamodb-localstack.sh
4. Start the application by running PostManagerApplication with the dev profile

# Populating the database

1. Start up docker-compose-localstack.yml
2. Publish the ports using:
   docker run -p 4566:4566 localstack/localstack
3. Run init-dynamodb-localstack-populate.sh to create the table and populate it with posts, comments, and likes.

# Running Integration Tests

1. Start up docker-compose-localstack.yml
2. Run init-dynamodb-localstack.sh
3. Publish the ports using:
   docker run -p 4566:4566 localstack/localstack
4. Run init-dynamodb-localstack-populate.sh to create the table.
5. Run the tests

# Documentation

Once app is running in dev mode the documentation can be accessed
via: http://localhost:8081/swagger-ui/index.html
