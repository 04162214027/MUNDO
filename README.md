# Mobile Shop ERP

[![Build Android APK](https://github.com/YOUR_USERNAME/MobileShopERP/actions/workflows/build-apk.yml/badge.svg)](https://github.com/YOUR_USERNAME/MobileShopERP/actions/workflows/build-apk.yml)

A modern Android ERP application for mobile shop management, built with Jetpack Compose and Room Database.

## Features

- 📱 **Product Management** - Add, edit, and track mobile phone inventory
- 👥 **Customer Management** - Maintain customer records and purchase history
- 💰 **Sales Tracking** - Record and monitor sales transactions
- 📊 **Khata System** - Credit/debit ledger for customer accounts
- 🔒 **PIN Security** - Secure access with PIN authentication
- 🏪 **Shop Profile** - Manage shop information and settings
- 💾 **Local Database** - Offline-first with Room Database
- 🎨 **Material Design 3** - Modern UI with Jetpack Compose

## Tech Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Architecture**: MVVM with Repository Pattern
- **Database**: Room (SQLite)
- **Dependency Injection**: Hilt
- **Async Operations**: Kotlin Coroutines & Flow
- **Security**: EncryptedSharedPreferences
- **Navigation**: Compose Navigation

## Requirements

- Android Studio Hedgehog or later
- JDK 17
- Android SDK 26+ (API Level 26)
- Gradle 8.11.1

## Building the Project

### Local Build

#### Windows
```batch
# Debug APK
SIMPLE_BUILD.bat

# Or using Gradle directly
gradlew.bat assembleDebug
```

#### Linux/Mac
```bash
# Debug APK
./gradlew assembleDebug

# Release APK
./gradlew assembleRelease
```

### GitHub Actions

The project includes automated build workflows:

1. **Debug Build** - Triggers on push/PR to main/develop branches
2. **Release Build** - Triggers on version tags (v*)

To build via GitHub Actions:
1. Fork/clone this repository
2. Push to GitHub
3. Go to **Actions** tab
4. Select **Build Android APK**
5. Click **Run workflow**
6. Download APK from **Artifacts**

## Download APK

### From GitHub Actions
1. Go to [Actions](https://github.com/YOUR_USERNAME/MobileShopERP/actions)
2. Click on latest successful build
3. Download `app-debug` artifact
4. Extract and install `app-debug.apk`

### From Releases
Check the [Releases](https://github.com/YOUR_USERNAME/MobileShopERP/releases) page for stable versions.

## Installation

1. Download the APK from GitHub Actions or Releases
2. Enable "Install from Unknown Sources" on your Android device
3. Install the APK
4. Set up your PIN on first launch

## Project Structure

```
MobileShopERP/
├── app/
│   └── src/
│       └── main/
│           ├── java/com/mobileshop/erp/
│           │   ├── data/          # Database, DAOs, Entities
│           │   ├── di/            # Dependency Injection
│           │   └── ui/            # Compose UI & ViewModels
│           └── res/               # Resources
├── .github/
│   └── workflows/                 # CI/CD workflows
├── gradle/
├── build.gradle.kts
└── settings.gradle.kts
```

## Database Schema

### Entities
- **Product** - Mobile phone inventory
- **Customer** - Customer information
- **Sale** - Sales transactions
- **CustomerKhata** - Customer ledger accounts
- **KhataTransaction** - Credit/debit entries
- **ShopProfile** - Shop settings

## Security

- PIN authentication using EncryptedSharedPreferences
- Biometric authentication support (coming soon)
- Local data encryption

## Troubleshooting

### Build Issues

**Gradle Cache Corruption:**
```batch
# Windows
fix_gradle_network.bat

# Or manually
gradlew --stop
rd /s /q %USERPROFILE%\.gradle\caches
```

**Network Issues:**
```batch
check_network.bat
```

See [NETWORK_TROUBLESHOOTING.md](NETWORK_TROUBLESHOOTING.md) for detailed solutions.

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Author

**WAQAR**

## Support

For issues and questions:
- Open an [Issue](https://github.com/YOUR_USERNAME/MobileShopERP/issues)
- Check [Discussions](https://github.com/YOUR_USERNAME/MobileShopERP/discussions)

## Changelog

### v1.0.0 (Initial Release)
- Product management
- Customer management
- Sales tracking
- Khata system
- PIN authentication
- Material Design 3 UI

## Roadmap

- [ ] Biometric authentication
- [ ] Cloud backup & sync
- [ ] Reports & analytics
- [ ] Multi-language support
- [ ] Dark theme improvements
- [ ] Export data (PDF, Excel)
- [ ] Barcode scanner integration
- [ ] SMS/WhatsApp integration

---

**⭐ Star this repository if you find it helpful!**
"# MUNDO" 
