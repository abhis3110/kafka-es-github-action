#!/bin/bash

# Script to help with local development and deployment

function show_help {
    echo "Usage: ./run.sh [COMMAND]"
    echo "Commands:"
    echo "  build          - Build the application"
    echo "  test           - Run unit tests"
    echo "  integration    - Run integration tests"
    echo "  docker-build   - Build Docker image"
    echo "  docker-up      - Start all containers with Docker Compose"
    echo "  docker-down    - Stop all containers"
    echo "  docker-logs    - Show logs from all containers"
    echo "  help           - Show this help message"
}

case "$1" in
    build)
        echo "Building application..."
        mvn clean package -DskipTests
        ;;
    test)
        echo "Running unit tests..."
        mvn test
        ;;
    integration)
        echo "Running integration tests..."
        mvn verify -DskipUnitTests
        ;;
    docker-build)
        echo "Building Docker image..."
        docker build -t kafka-elasticsearch-demo .
        ;;
    docker-up)
        echo "Starting containers..."
        docker-compose up -d
        ;;
    docker-down)
        echo "Stopping containers..."
        docker-compose down
        ;;
    docker-logs)
        echo "Showing logs..."
        docker-compose logs -f
        ;;
    help|*)
        show_help
        ;;
esac
