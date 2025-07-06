@echo off
echo Building LexiAI Backend...
cd lexiai-backend

echo Cleaning previous builds...
if exist target rmdir /s /q target

echo Compiling with Maven...
call mvn clean compile

if %ERRORLEVEL% == 0 (
    echo Build successful!
    echo.
    echo To run the application:
    echo 1. Make sure MySQL is running
    echo 2. Create database 'lexiai_db'
    echo 3. Run: mvn spring-boot:run
    echo.
    echo Or use run.bat for automatic startup
) else (
    echo Build failed! Check Maven installation and dependencies.
)

pause
