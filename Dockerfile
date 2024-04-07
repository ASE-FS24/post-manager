# Use an OpenJDK 17 Alpine base image
FROM amazoncorretto:17-alpine-jdk

# Optional: Set the environment variable for the app directory
ENV APP_HOME=/usr/app

# Set the working directory inside the container
WORKDIR $APP_HOME/

# Copy the compiled JAR into the image
COPY ./target/postmanager.jar $APP_HOME/app.jar

# Command to run the application
CMD ["java", "-jar", "app.jar"]
