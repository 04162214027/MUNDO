@echo off
echo ======================================
echo Checking Network Connectivity
echo ======================================
echo.

echo Testing Google Maven Repository...
ping -n 1 dl.google.com
echo.

echo Testing Maven Central...
ping -n 1 repo.maven.apache.org
echo.

echo Testing General Internet...
ping -n 1 google.com
echo.

echo ======================================
echo Network Test Complete
echo ======================================
echo.
echo If all pings failed, please:
echo 1. Check your internet connection
echo 2. Disable VPN if active
echo 3. Check firewall settings
echo 4. Try using mobile hotspot
echo.
pause
