@echo off
setlocal

if "%1"=="" goto help
if "%1"=="build" goto build
if "%1"=="test" goto test
if "%1"=="integration" goto integration
if "%1"=="docker-build" goto docker-build
if "%1"=="docker-up" goto docker-up
if "%1"=="docker-down" goto docker-down
if "%1"=="docker-logs" goto docker-logs
if "%1"=="help" goto help
goto help

:build
echo Building application...
call mvn clean package -DskipTests
goto end

:test
echo Running unit tests...
call mvn test
goto end

:integration
echo Running integration tests...
call mvn verify -DskipUnitTests
goto end

:docker-build
echo Building Docker image...
docker build -t kafka-elasticsearch-demo .
goto end

:docker-up
echo Starting containers...
docker-compose up -d
goto end

:docker-down
echo Stopping containers...
docker-compose down
goto end

:docker-logs
echo Showing logs...
docker-compose logs -f
goto end

:help
echo Usage: run.bat [COMMAND]
echo Commands:
echo   build          - Build the application
echo   test           - Run unit tests
echo   integration    - Run integration tests
echo   docker-build   - Build Docker image
echo   docker-up      - Start all containers with Docker Compose
echo   docker-down    - Stop all containers
echo   docker-logs    - Show logs from all containers
echo   help           - Show this help message
goto end

:end
endlocal
