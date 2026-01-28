@echo off
REM ============================================
REM Mobile Shop ERP - Release APK Builder
REM ============================================

echo.
echo ============================================
echo Mobile Shop ERP - Release APK Builder
echo ============================================
echo.
echo WARNING: Release APK requires signing configuration
echo.

REM Check if we're in the correct directory
if not exist "gradlew.bat" (
    echo ERROR: gradlew.bat not found!
    echo Please run this script from the MobileShopERP directory.
    pause
    exit /b 1
)

echo Cleaning previous builds...
call gradlew.bat clean

echo.
echo Starting Release APK build...
echo This will create an optimized, minified APK
echo.

REM Build Release APK (optimized with ProGuard)
call gradlew.bat assembleRelease

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo ============================================
    echo BUILD FAILED!
    echo ============================================
    echo.
    echo Possible issues:
    echo 1. No internet connection - Run check_network.bat
    echo 2. Missing signing configuration (keystore)
    echo 3. Firewall blocking Gradle downloads
    echo.
    pause
    exit /b 1
)

echo.
echo ============================================
echo BUILD SUCCESSFUL!
echo ============================================
echo.
echo Release APK location:
echo app\build\outputs\apk\release\app-release.apk
echo.
echo Opening output folder...
start "" "%~dp0app\build\outputs\apk\release"

pause
