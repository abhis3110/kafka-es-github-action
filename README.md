# Spring Boot Kafka Elasticsearch CRUD Application

This is a Spring Boot application that demonstrates a CRUD API with Kafka as a message broker and Elasticsearch for data storage.

## Features

- RESTful CRUD API for managing products
- Kafka producer and consumer integration
- Elasticsearch for data storage and search capabilities
- Comprehensive unit and integration tests
- Docker containerization
- CI/CD with GitHub Actions

## Technology Stack

- Java 11
- Spring Boot 2.7.x
- Spring Data Elasticsearch
- Spring Kafka
- Maven
- Docker & Docker Compose
- GitHub Actions

## Project Structure

The application follows a standard Spring Boot project structure:

- `src/main/java`: Java source code
  - `com.example.demo`: Root package
    - `config`: Configuration classes for Elasticsearch and Kafka
    - `controller`: REST controllers
    - `exception`: Custom exceptions
    - `model`: Domain models
    - `repository`: Elasticsearch repositories
    - `service`: Business logic and services
- `src/main/resources`: Application properties and configurations
- `src/test`: Test classes
  - `unit tests`: Testing individual components
  - `integration tests`: Testing the application as a whole

## Getting Started

### Prerequisites

- Java 11+
- Maven
- Docker and Docker Compose

### Running Locally with Docker Compose

1. Clone the repository:
   ```
   git clone https://github.com/yourusername/spring-github-actions.git
   cd spring-github-actions
   ```

2. Build the application:
   ```
   mvn clean package -DskipTests
   ```

3. Start the application with Docker Compose:
   ```
   docker-compose up -d
   ```

4. The application will be available at http://localhost:8080

### API Endpoints

- `GET /api/products`: Get all products
- `GET /api/products/{id}`: Get a product by ID
- `GET /api/products/category/{category}`: Get products by category
- `GET /api/products/search?name={name}`: Search products by name
- `POST /api/products`: Create a new product
- `PUT /api/products/{id}`: Update an existing product
- `DELETE /api/products/{id}`: Delete a product

## Testing

### Running Unit Tests

```
mvn test
```

### Running Integration Tests

```
mvn verify -DskipUnitTests
```

## CI/CD Pipeline

The project includes a GitHub Actions workflow that:

1. Builds the application
2. Runs unit and integration tests
3. Builds a Docker image
4. Pushes the image to GitHub Container Registry
5. Tests the Docker Compose configuration

## License

This project is licensed under the MIT License - see the LICENSE file for details.
