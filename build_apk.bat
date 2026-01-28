@echo off
REM ============================================
REM Mobile Shop ERP - APK Builder
REM ============================================

echo.
echo ============================================
echo Mobile Shop ERP - APK Builder
echo ============================================
echo.

REM Check if we're in the correct directory
if not exist "gradlew.bat" (
    echo ERROR: gradlew.bat not found!
    echo Please run this script from the MobileShopERP directory.
    pause
    exit /b 1
)

echo Stopping any running Gradle daemons...
call gradlew.bat --stop >nul 2>&1
timeout /t 2 /nobreak >nul

echo Cleaning Gradle cache (fixing corruption)...
rd /s /q "%USERPROFILE%\.gradle\caches\8.11.1" 2>nul
rd /s /q "%USERPROFILE%\.gradle\caches\transforms-4" 2>nul
rd /s /q "build" 2>nul
rd /s /q "app\build" 2>nul
rd /s /q ".gradle" 2>nul

echo.
echo Starting APK build...
echo This may take 10-15 minutes on first run...
echo Please be patient - downloading dependencies...
echo.

REM Build Debug APK with --no-daemon to avoid cache issues
call gradlew.bat assembleDebug --no-daemon --refresh-dependencies

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo ============================================
    echo BUILD FAILED!
    echo ============================================
    echo.
    echo Possible issues:
    echo 1. No internet connection - Run check_network.bat
    echo 2. Firewall blocking Gradle downloads
    echo 3. Missing dependencies
    echo.
    echo Try running: check_network.bat
    echo.
    pause
    exit /b 1
)

echo.
echo ============================================
echo BUILD SUCCESSFUL!
echo ============================================
echo.
echo Debug APK location:
echo app\build\outputs\apk\debug\app-debug.apk
echo.
echo Opening output folder...
start "" "%~dp0app\build\outputs\apk\debug"

pause
