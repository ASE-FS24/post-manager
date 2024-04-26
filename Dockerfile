# Use an Ubuntu image
FROM ubuntu:latest

RUN apt-get update && \
    apt-get install -y curl &&\
    apt install -y xdg-utils

# Install OpenJDK, AWS CLI, and LocalStack dependencies
RUN apt-get update && \
    apt-get install -y openjdk-17-jdk curl python3 python3-pip groff less zip iputils-ping python3-venv
    # && \
    # apt-get clean && \
    # rm -rf /var/lib/apt/lists/*

# Create a Python virtual environment and install AWS CLI using pip3
RUN python3 -m venv /opt/venv && \
    . /opt/venv/bin/activate && \
    pip3 install --upgrade pip && \
    pip3 install awscli awscli-local

# Optional: Set the environment variable for the app directory
ENV APP_HOME=/usr/app

# Set the working directory inside the container
WORKDIR $APP_HOME/

# Copy the compiled JAR into the image
COPY ./target/postmanager.jar $APP_HOME/app.jar

EXPOSE 8081

# Command to run the application
CMD ["java", "-jar", "app.jar"]
