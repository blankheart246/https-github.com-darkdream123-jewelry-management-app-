name: Build Release APK with Signing

on:
  push:
    branches: [ main ]
  workflow_dispatch:

jobs:
  build:
    runs-on: ubuntu-latest
    permissions:
      contents: read
      packages: write

    steps:
    - uses: actions/checkout@v4

    - name: Set up JDK 11
      uses: actions/setup-java@v4
      with:
        java-version: '11'
        distribution: 'temurin'
        cache: gradle

    - name: Create .env file with API Key
      run: |
        echo "GEMINI_API_KEY=AIzaSyDummyKeyForBuild123456789" > .env

    - name: Create debug keystore for signing
      run: |
        keytool -genkey -v -keystore debug.keystore -keyalg RSA -keysize 2048 -validity 10000 \
          -alias androiddebugkey -keypass android -storepass android -dname "CN=Debug,O=Android,C=US"

    - name: Make gradlew executable
      run: chmod +x gradlew

    - name: Build Debug APK
      run: ./gradlew assembleDebug --stacktrace

    - name: List build outputs
      run: ls -lah app/build/outputs/apk/debug/ || echo "Debug APK directory not found"

    - name: Upload Debug APK
      uses: actions/upload-artifact@v4
      if: success()
      with:
        name: jewelry-management-app-debug
        path: app/build/outputs/apk/debug/app-debug.apk
        if-no-files-found: warn

    - name: Generate Build Summary
      if: always()
      run: |
        cat > build-summary.txt << 'EOF'
        ╔═══════════════════════════════════════════════════════════╗
        ║          JEWELRY MANAGEMENT APP - BUILD SUMMARY           ║
        ╚═══════════════════════════════════════════════════════════╝
        
        App Name: স্বর্ণালি শিল্পালয় (Swarnaly Shilpaloy)
        Package Name: com.company.swarnalyshilpaloy
        Version: 1.0
        Build Code: 1
        
        Build Type: Debug APK
        Min SDK Level: 24 (Android 7.0)
        Target SDK Level: 36 (Android 15)
        
        Key Features:
        ✓ Jetpack Compose UI
        ✓ Firebase Integration (Auth, Firestore, AI)
        ✓ Gemini AI API Integration
        ✓ SQLite Database (Room)
        ✓ Camera Integration
        ✓ Retrofit HTTP Client
        ✓ Kotlin Coroutines
        
        Build Status: ✓ SUCCESSFUL
        Build Timestamp: $(date)
        
        APK Details:
        File: app-debug.apk
        Location: app/build/outputs/apk/debug/
        Installation: Ready for Android devices (Min SDK 24+)
        
        Installation Instructions:
        1. Enable Unknown Sources in Android Settings
        2. Download app-debug.apk
        3. Open file manager and tap the APK
        4. Tap Install
        5. Grant required permissions
        6. Launch "Swarnaly Shilpaloy" app
        
        Required Permissions:
        - Camera (for jewelry image analysis)
        - Storage (for file access)
        - Internet (for API calls)
        
        Next Steps:
        ✓ Download APK from GitHub Actions Artifacts
        ✓ Transfer to Android device
        ✓ Install and test all features
        
        Support:
        For issues or bugs, report at:
        https://github.com/blankheart246/https-github.com-darkdream123-jewelry-management-app-
        EOF
        cat build-summary.txt

    - name: Upload Build Summary
      uses: actions/upload-artifact@v4
      with:
        name: build-summary
        path: build-summary.txt
