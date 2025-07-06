@echo off
echo Starting LexiAI Backend...
cd lexiai-backend

echo Checking if application is already built...
if not exist target\classes (
    echo Application not built. Building first...
    call mvn clean compile
    if %ERRORLEVEL% NEQ 0 (
        echo Build failed! Please check Maven installation and dependencies.
        pause
        exit /b 1
    )
)

echo Starting Spring Boot application...
echo.
echo Application will be available at: http://localhost:8080
echo API Documentation: http://localhost:8080/api/public/info
echo.
echo Press Ctrl+C to stop the application
echo.

call mvn spring-boot:run
