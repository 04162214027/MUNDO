@echo off
title Mobile Shop ERP - Building APK
echo.
echo ========================================
echo  Mobile Shop ERP - APK Builder
echo ========================================
echo.
echo IMPORTANT: Do NOT close this window!
echo The build will take 10-15 minutes.
echo.
echo Progress:
echo [1/4] Cleaning cache...
rd /s /q "%USERPROFILE%\.gradle\caches\8.11.1" 2>nul
rd /s /q "build" 2>nul
rd /s /q "app\build" 2>nul

echo [2/4] Stopping Gradle...
call gradlew.bat --stop 2>nul

echo [3/4] Starting download and build...
echo       This will take time - BE PATIENT!
echo.

gradlew.bat assembleDebug --no-daemon --refresh-dependencies

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ========================================
    echo  BUILD SUCCESS!
    echo ========================================
    echo.
    echo APK Location:
    echo app\build\outputs\apk\debug\app-debug.apk
    echo.
    start "" "%~dp0app\build\outputs\apk\debug"
) else (
    echo.
    echo ========================================
    echo  BUILD FAILED
    echo ========================================
    echo.
    echo Check network connection and try again.
)

echo.
pause
