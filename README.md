# ics-rollout-api

## Overview

ics-rollout-api is a Kotlin-based REST API application designed to manage rollout objects. The application provides three main endpoints: `persist`, `report`, and `health`. The application is containerized using Docker and can be deployed into a Kubernetes cluster. The database used is SQLite.

## Endpoints

### Persist Endpoint

- **URL**: `/persist`
- **Method**: `POST`
- **Description**: Takes a list of rollout objects as JSON payload and writes new records to the database.
- **Request Body**: A JSON array of rollout objects.
- **Response**: A JSON object indicating the success or failure of the operation.

### Report Endpoint

- **URL**: `/report`
- **Method**: `GET`
- **Description**: Takes a start_date and end_date as query parameters and returns a list of matching rollout objects as JSON payload.
- **Query Parameters**:
  - `start_date`: The start date for the report.
  - `end_date`: The end date for the report.
- **Response**: A JSON array of matching rollout objects.

### Health Endpoint

- **URL**: `/health`
- **Method**: `GET`
- **Description**: Returns a JSON payload with the health status of the application.
- **Response**: A JSON object indicating the health status of the application.

## Application Design

- **Main Program**: The main program initializes the Ktor server and defines the routing for the endpoints.
- **API Package**: The `api` package contains the implementation of the endpoints.
- **Spec Folder**: The `spec` folder contains the Rest API specification.
- **DB Folder**: The `db` folder contains the database model.
  - **Database**: The database used is SQLite, with a single table having the following attributes:
    - `id`: string PRIMARY KEY
    - `rollout`: BLOB

## Application Architecture

- **Containerization**: The application is containerized using Docker.
- **Deployment**: The application can be deployed into a Kubernetes cluster.
- **Build and Deployment**: The application is built and deployed using GitHub Actions.

## Security

- **Authentication and Authorization**: The API endpoints are secured using OAuth2 or JWT for token-based authentication. Role-based access control (RBAC) is implemented to restrict access to specific endpoints based on user roles.
- **Input Validation and Sanitization**: Input data is validated and sanitized to prevent injection attacks and ensure data integrity.
- **Rate Limiting and Throttling**: Rate limiting and throttling are implemented to protect the API from excessive requests.

## Testing

- **Unit Tests**: Unit tests are written for each endpoint in the `api` package using JUnit or Kotest. Database interactions are mocked to isolate the logic of the endpoints.
- **Integration Tests**: Integration tests are written to test the interaction between the API endpoints and the SQLite database using an in-memory SQLite database for testing purposes.
- **End-to-End Tests**: End-to-end tests are written to test the entire application, including the API endpoints, database, and deployment using Postman or RestAssured.

## Containerization

- **Dockerfile**: A multi-stage build Dockerfile is used to optimize the image size. The first stage uses an official Kotlin image to build the application, and the second stage uses a lightweight base image (e.g., `alpine`) to run the application. The built application is copied from the first stage to the second stage, and the necessary ports for the application to run are exposed.
- **Docker Compose**: A `docker-compose.yml` file is used to define a service for the Kotlin application using the `Dockerfile` and a service for the SQLite database. The network and volumes are configured for the services to communicate and persist data. Environment variables are used to configure the application and database.

## Deployment

- **Kubernetes**: Kubernetes deployment and service YAML files are created in a `k8s` directory. A deployment is defined for the Kotlin application using the Docker image built from the `Dockerfile`. A service is defined to expose the application. A persistent volume and persistent volume claim are defined for the SQLite database. A ConfigMap or Secret is defined for environment variables and sensitive data. A Kubernetes Ingress is used to expose the application to the outside world.

## GitHub Actions

- **Build and Deploy Workflow**: A GitHub Actions workflow configuration file is created to define the build stage for the Kotlin application and the deploy stage for the Kubernetes cluster.
