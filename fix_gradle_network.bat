@echo off
REM ============================================
REM Gradle Network Fix Utility
REM ============================================

echo.
echo ============================================
echo Gradle Network Fix Utility
echo ============================================
echo.

echo This script will attempt to fix network issues:
echo 1. Clear Gradle cache
echo 2. Reset DNS cache
echo 3. Test connectivity
echo.
pause

echo.
echo [1/3] Clearing Gradle cache and wrapper...
if exist "%USERPROFILE%\.gradle\caches" (
    rmdir /s /q "%USERPROFILE%\.gradle\caches"
    echo Gradle cache cleared.
) else (
    echo No Gradle cache found.
)
if exist "%USERPROFILE%\.gradle\wrapper" (
    rmdir /s /q "%USERPROFILE%\.gradle\wrapper"
    echo Gradle wrapper cleared.
)

echo.
echo [2/3] Flushing DNS cache...
ipconfig /flushdns

echo.
echo [3/3] Testing connectivity...
echo.
echo Testing dl.google.com...
ping -n 2 dl.google.com

echo.
echo Testing repo.maven.apache.org...
ping -n 2 repo.maven.apache.org

echo.
echo ============================================
echo Fix Complete
echo ============================================
echo.
echo If connectivity tests failed:
echo 1. Check your internet connection
echo 2. Disable any VPN or proxy
echo 3. Check Windows Firewall settings
echo 4. Try using mobile hotspot
echo 5. Contact your network administrator
echo.
echo If tests passed, try building again with:
echo build_apk.bat
echo.
pause
