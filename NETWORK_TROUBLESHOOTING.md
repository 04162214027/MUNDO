# Network Troubleshooting Guide

## Error Summary
Your Gradle build failed because it cannot reach:
- `dl.google.com` (Google Maven Repository)
- `repo.maven.apache.org` (Maven Central)

Error message: **"No such host is known"**

## Quick Fixes

### 1. Check Internet Connection
```batch
# Run this command in PowerShell:
Test-NetConnection dl.google.com -Port 443
Test-NetConnection repo.maven.apache.org -Port 443
```

Or simply run: `check_network.bat`

### 2. Flush DNS Cache
```batch
ipconfig /flushdns
```

### 3. Disable VPN/Proxy
- Temporarily disable any VPN or proxy software
- Try using your direct internet connection

### 4. Check Windows Firewall
1. Open Windows Defender Firewall
2. Click "Allow an app through firewall"
3. Make sure Java and Gradle can access network

### 5. Use Mobile Hotspot
- If office/home network is restricted
- Connect to mobile hotspot temporarily
- Run the build again

### 6. Configure Gradle Proxy (If Behind Corporate Proxy)
Create/edit: `gradle.properties` in your user folder:
`C:\Users\WAQAR\.gradle\gradle.properties`

Add these lines (replace with your proxy details):
```properties
systemProp.http.proxyHost=your.proxy.host
systemProp.http.proxyPort=8080
systemProp.https.proxyHost=your.proxy.host
systemProp.https.proxyPort=8080
systemProp.http.nonProxyHosts=localhost|127.0.0.1

# If proxy requires authentication:
systemProp.http.proxyUser=username
systemProp.http.proxyPassword=password
systemProp.https.proxyUser=username
systemProp.https.proxyPassword=password
```

## Build Files Created

### 1. `build_apk.bat`
- Builds DEBUG APK (faster, for testing)
- Opens output folder automatically
- Shows clear error messages

### 2. `build_release_apk.bat`
- Builds RELEASE APK (optimized, production-ready)
- Applies ProGuard minification
- Requires signing configuration

### 3. `check_network.bat`
- Tests connectivity to required repositories
- Diagnoses network issues
- Shows which connections are failing

### 4. `fix_gradle_network.bat`
- Clears Gradle cache
- Flushes DNS cache
- Tests connectivity automatically

## Steps to Build APK

1. **First time setup:**
   ```batch
   # Check network connectivity
   check_network.bat
   ```

2. **If network tests pass:**
   ```batch
   # Build APK
   build_apk.bat
   ```

3. **If network tests fail:**
   - Fix your internet connection
   - Or run: `fix_gradle_network.bat`
   - Then try building again

## APK Output Locations

- **Debug APK:** `app\build\outputs\apk\debug\app-debug.apk`
- **Release APK:** `app\build\outputs\apk\release\app-release.apk`

## Additional Help

If problems persist:
1. Check antivirus software (may block Gradle)
2. Ensure Java/JDK is properly installed
3. Try building from different network
4. Contact network administrator about firewall rules
