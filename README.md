# post-manager
This app handles post management

# Starting the app in dev mode

1. Start up docker-compose-localstack.yml
2. Publish the ports using:
   docker run -p 4566:4566 localstack/localstack
3. Start the application by running PostManagerApplication with the dev profile

# Running Integration Tests

1. Start up docker-compose-localstack.yml
2. Publish the ports using:
   docker run -p 4566:4566 localstack/localstack
3. Run the tests

# Documentation

Once app is running in dev mode the documentation can be accessed
via: http://localhost:8080/swagger-ui/index.html#/post-controller/getPostById
