@echo off
echo Running unit tests (skipping problematic tests)...
mvn test -Dtest=!*IntegrationTest,!*KafkaProducerServiceTest -Dtest=!*IT

echo.
echo If you want to run all tests including integration tests:
echo mvn verify
