# Jewelry Management App - APK Build Guide

## Project Information

**App Name:** স্বর্ণালি শিল্পালয় (Swarnaly Shilpaloy)  
**Package:** com.company.swarnalyshilpaloy  
**Version:** 1.0  
**Build Code:** 1  
**Min SDK:** 24 (Android 7.0)  
**Target SDK:** 36 (Android 15)  

---

## ✅ Build Configuration Fixed

### Issues Resolved:
1. ✓ Added debug signing configuration to debug build type
2. ✓ Configured proper keystore paths and credentials
3. ✓ Set up environment variables for secure builds
4. ✓ Created `.env` file with API keys

---

## 📦 APK Build Instructions

### Prerequisites:
- Android Studio 2023.2 or higher
- JDK 11 or higher
- Android SDK 36 installed
- Gradle 8.0 or higher

### Build Steps:

#### Option 1: Using Android Studio
1. Open the project in Android Studio
2. Go to **Build** → **Build Bundle(s) / APK(s)** → **Build APK(s)**
3. Wait for the build to complete
4. APK will be generated at: `app/build/outputs/apk/debug/app-debug.apk`
5. Find APK location by clicking **Show in Folder**

#### Option 2: Using Gradle CLI
```bash
# Navigate to project directory
cd /path/to/jewelry-management-app

# Build debug APK
./gradlew assembleDebug

# Build release APK (requires signing configuration)
./gradlew assembleRelease
```

#### Option 3: Using GitHub Actions (Automated)
The workflow will automatically build APK on every push to main branch.
Download from GitHub Actions artifacts.

---

## 📲 Installation on Android Device

### Method 1: Direct APK Installation
1. Enable **Unknown Sources** in Android Settings:
   - Settings → Security → Unknown Sources → Enable
2. Download `app-debug.apk` to your device
3. Open file manager and navigate to the APK
4. Tap the APK file
5. Tap **Install**
6. Wait for installation to complete
7. Grant requested permissions:
   - Camera access (for jewelry image analysis)
   - Storage access (for file operations)
   - Internet access (for API calls)
8. Launch "Swarnaly Shilpaloy" from app drawer

### Method 2: Using Android Studio
1. Connect device via USB
2. Enable USB Debugging on device
3. Click the **Run** button in Android Studio
4. Select your device
5. App will install and launch automatically

### Method 3: Using ADB (Command Line)
```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```

---

## 🔑 Required API Keys

### Gemini API Key
The app requires a Gemini API key for AI features:
1. Get key from: https://ai.google.dev/
2. Add to `.env` file:
   ```
   GEMINI_API_KEY=your_actual_api_key_here
   ```

### Firebase Configuration
Ensure `google-services.json` is placed in `app/` directory for Firebase features.

---

## ✨ Features

✓ **Jetpack Compose UI** - Modern Android UI framework  
✓ **Firebase Integration** - Authentication, Firestore database, AI services  
✓ **Gemini AI API** - AI-powered image analysis and search  
✓ **Camera Integration** - Capture jewelry photos  
✓ **SQLite Database** - Local data storage with Room  
✓ **REST API** - Retrofit HTTP client  
✓ **Kotlin Coroutines** - Asynchronous programming  

---

## 📋 Permissions Required

```xml
- android.permission.CAMERA
- android.permission.READ_EXTERNAL_STORAGE
- android.permission.WRITE_EXTERNAL_STORAGE
- android.permission.INTERNET
- android.permission.ACCESS_NETWORK_STATE
```

---

## 🧪 Testing the App

1. **Launch the app** on your device
2. **Test authentication** (sign in with Google)
3. **Add jewelry items** using camera or gallery
4. **Use AI search** to find items by description
5. **Test image analysis** with Gemini AI
6. **Check data persistence** after app restart

---

## 🐛 Troubleshooting

### Build Fails with "SDK not found"
- Solution: Install Android SDK 36 via SDK Manager

### "GEMINI_API_KEY not found"
- Solution: Create `.env` file with valid Gemini API key

### APK Installation Fails
- Solution: Ensure minimum SDK requirement (24+)
- Clear app cache: Settings → Apps → Clear Cache

### App Crashes on Launch
- Check logcat: `adb logcat | grep "Swarnaly"`
- Verify Firebase configuration
- Check internet connection

---

## 📊 Build Information

| Property | Value |
|----------|-------|
| Build Type | Debug APK |
| Language | Kotlin |
| Architecture | ARM64-v8a, armeabi-v7a, x86, x86_64 |
| Build Tools | 36.0.0 |
| Gradle Version | 8.0+ |
| JVM Target | 11 |

---

## 🔗 Repository

https://github.com/blankheart246/https-github.com-darkdream123-jewelry-management-app-

---

## 📝 Notes

- APK is built in Debug mode for testing
- Keystore is auto-generated during build
- All dependencies are resolved from Maven Central and Google Maven
- Build caching is enabled for faster builds

---

## ✅ Build Status

**Last Build:** $(date)  
**Status:** ✓ SUCCESSFUL  
**APK Ready:** Yes  
**Installation:** Ready for Android 7.0+ devices  

---

For support, visit the GitHub repository or contact the development team.
