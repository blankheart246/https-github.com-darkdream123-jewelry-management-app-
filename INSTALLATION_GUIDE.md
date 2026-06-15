# 📱 Swarnaly Shilpaloy - APK Installation Guide

## Quick Start

### System Requirements
- **Android Version:** 7.0 or higher (API 24+)
- **RAM:** 2GB minimum (4GB recommended)
- **Storage:** 100MB free space
- **Internet:** Required for Gemini AI features

---

## Installation Method 1: Direct APK Install

### Step 1: Enable Installation from Unknown Sources
```
1. Open Settings app
2. Go to Security or Apps
3. Find "Unknown Sources" or "Install Unknown Apps"
4. Toggle ON for your file manager app
```

### Step 2: Download APK
- Get `app-debug.apk` from:
  - GitHub Actions Artifacts, OR
  - Build outputs folder, OR
  - Provided APK file

### Step 3: Install via File Manager
```
1. Open File Manager
2. Navigate to Downloads folder
3. Find app-debug.apk
4. Tap the file
5. Select "Install"
6. Wait for installation (usually 30-60 seconds)
7. Tap "Done" or "Open"
```

---

## Installation Method 2: Using ADB (Advanced)

### Prerequisites
- Android SDK Platform Tools installed
- Device connected via USB
- USB Debugging enabled on device

### Steps
```bash
# Connect device and verify
adb devices

# Install APK
adb install app/build/outputs/apk/debug/app-debug.apk

# Verify installation
adb shell pm list packages | grep swarnalyshilpaloy

# Launch app
adb shell am start -n com.company.swarnalyshilpaloy/.MainActivity
```

---

## First Launch Setup

### 1. Grant Permissions
The app will request:
- ✓ **Camera** - For jewelry photo capture
- ✓ **Storage** - For accessing files
- ✓ **Internet** - For API calls and cloud sync

**Tap "Allow" for all permissions**

### 2. Configure Gemini API Key
The app requires a Gemini API key:
1. Visit: https://ai.google.dev/
2. Create API key
3. Enter key when prompted by app
4. Or edit `.env` file before building

### 3. Sign In
- Use Google account for authentication
- Or create local account
- Data syncs to Firestore (if configured)

---

## Troubleshooting Installation

### Error: "Unknown app from an unknown source"
**Solution:**
- Go to Settings → Security → Unknown Sources
- Enable it
- Try installation again

### Error: "Insufficient storage"
**Solution:**
- Free up at least 100MB of storage
- Go to Settings → Storage → Delete unnecessary files

### Error: "Installation failed - Invalid APK"
**Solution:**
- Re-download the APK file
- Verify file integrity
- Try again

### Error: "App not installed"
**Solution:**
- Uninstall any existing version first
- Clear Play Store cache
- Restart device
- Try installation again

### App crashes at startup
**Solution:**
- Restart device
- Ensure internet connection is active
- Check if Gemini API key is set
- Clear app cache: Settings → Apps → Swarnaly → Storage → Clear Cache

---

## Post-Installation

### Verify Installation
1. Find app in App Drawer
2. App icon shows "স्वर्णालि" or jewelry icon
3. Tap to open
4. Should load without errors

### Initial Configuration
1. **Set up account** (Sign in with Google)
2. **Enter API Key** (Gemini)
3. **Grant permissions** (Camera, Storage)
4. **Test features** (Take photo, search)

---

## Uninstallation

### To Remove App
```
1. Long-press app icon
2. Select "Uninstall"
3. Confirm uninstall
4. Or: Settings → Apps → Swarnaly Shilpaloy → Uninstall
```

### Via ADB
```bash
adb uninstall com.company.swarnalyshilpaloy
```

---

## Features After Installation

✅ **Jewelry Management**
- Add/edit/delete jewelry items
- Store images and descriptions
- Track stock quantities

✅ **AI-Powered Search**
- Search by description
- Image-based search
- Gemini AI analysis

✅ **Camera Integration**
- Capture jewelry photos
- Direct database storage
- Auto-tagging with AI

✅ **Local Database**
- SQLite storage
- Offline access
- No sync required

✅ **Cloud Sync** (Optional)
- Sync with Firestore
- Firebase authentication
- Multi-device access

---

## Performance Tips

1. **Clear Cache Regularly**
   - Settings → Apps → Swarnaly → Storage → Clear Cache

2. **Close Background Apps**
   - Free up RAM for better performance

3. **Check Internet Connection**
   - Required for AI features
   - Use WiFi for faster operations

4. **Update Android**
   - Keep Android version up to date
   - Better compatibility and security

---

## Security Notes

🔒 **Data Privacy**
- Local database encrypted
- Firestore data secured (if configured)
- No data collection without consent

🔒 **API Key Safety**
- Never share your Gemini API key
- Keep in .env file only
- Rotate key periodically

---

## Support

**Having Issues?**
- Check GitHub Issues: https://github.com/blankheart246/https-github.com-darkdream123-jewelry-management-app-
- Review this guide again
- Reinstall the app

**Still Need Help?**
- Create a GitHub Issue with:
  - Error message
  - Device model and Android version
  - Steps to reproduce

---

## Next Steps

1. ✅ Install APK
2. ✅ Grant Permissions
3. ✅ Set up Account
4. ✅ Configure Gemini API Key
5. ✅ Start using the app!

---

**Happy Jewelry Management! 💍✨**
