# Deploying to Google Cloud Platform

## Prerequisites
1. A Google Cloud Platform account
2. A project created in Google Cloud Console

## Steps to Deploy

1. **Create a Project in Google Cloud Console**
   - Go to https://console.cloud.google.com
   - Click "Create Project"
   - Name it "zm-backend"
   - Note down the Project ID

2. **Enable Required APIs**
   - Go to "APIs & Services" > "Library"
   - Enable these APIs:
     - Cloud Build API
     - Cloud Run API
     - Cloud SQL Admin API
     - App Engine Admin API

3. **Create a Cloud SQL Instance**
   - Go to "SQL" in the console
   - Click "Create Instance"
   - Choose MySQL
   - Set instance ID: zm-mysql-instance
   - Set password
   - Choose region: us-central1
   - Click "Create"

4. **Create a Database**
   - In the SQL instance, click "Databases"
   - Click "Create Database"
   - Name: zm_data_base
   - Click "Create"

5. **Deploy to App Engine**
   - Go to "App Engine" in the console
   - Click "Create Application"
   - Choose region: us-central1
   - Click "Create"

6. **Set Environment Variables**
   - Go to "App Engine" > "Settings"
   - Under "Environment Variables", add:
     - SPRING_PROFILES_ACTIVE: gcp
     - SPRING_DATASOURCE_URL: [Your Cloud SQL connection string]
     - SPRING_DATASOURCE_USERNAME: root
     - SPRING_DATASOURCE_PASSWORD: [Your database password]

7. **Deploy the Application**
   - Go to "Cloud Build" in the console
   - Click "Create Trigger"
   - Connect your GitHub repository
   - Set build configuration to use the app.yaml file
   - Click "Create"

## Accessing the Application
After deployment, your application will be available at:
https://[PROJECT_ID].appspot.com

## Monitoring
- Go to "App Engine" > "Dashboard" to monitor your application
- Set up alerts in "Monitoring" > "Alerting"
- View logs in "Logging" > "Logs Explorer" 