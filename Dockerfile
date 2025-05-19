# Build stage
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Run stage
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Install wget and unzip
RUN apt-get update && apt-get install -y wget unzip

# Download and install Cloud SQL proxy
RUN wget https://storage.googleapis.com/cloud-sql-connectors/cloud-sql-proxy/v2.8.1/cloud-sql-proxy.linux.amd64 -O /cloud-sql-proxy && \
    chmod +x /cloud-sql-proxy

COPY --from=build /app/target/*.jar app.jar

# Create a script to start both the proxy and the application
RUN echo '#!/bin/bash\n\
/cloud-sql-proxy --unix-socket /cloudsql ${CLOUD_SQL_CONNECTION_NAME} &\n\
java -jar app.jar\n\
' > /app/start.sh && chmod +x /app/start.sh

EXPOSE 8080
ENTRYPOINT ["/app/start.sh"] 