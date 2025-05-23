#!/bin/bash

# Build the application
mvn clean package -DskipTests

# Create a temporary directory for deployment
mkdir -p deploy
cp target/store-1.0.0.jar deploy/
cp app.yaml deploy/

# Create a Dockerfile for the deployment
cat > deploy/Dockerfile << 'EOF'
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY store-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
EOF

# Create a .dockerignore file
cat > deploy/.dockerignore << 'EOF'
.git
.gitignore
target
*.md
EOF 