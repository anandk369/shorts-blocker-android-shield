
# YouTube Shorts Blocker - Installation Guide

This guide will help you set up the YouTube Shorts Blocker app on your Android device.

## Prerequisites

- Android device running Android 6.0 (Marshmallow) or higher
- YouTube app installed
- USB debugging enabled (for installing from source)

## Installation from APK

1. Download the APK file from the releases page
2. Enable installation from unknown sources in your device settings
3. Open the APK file and follow the installation prompts
4. Launch the app

## Building from Source

### 1. Clone and Prepare the Project

```bash
# Clone the repository
git clone <repository-url>
cd <repository-directory>

# Install dependencies
npm install

# Build the web app
npm run build
```

### 2. Set Up Android Project

```bash
# Add Android platform
npx cap add android

# Update Capacitor
npx cap update android

# Sync the built web app with the Android project
npx cap sync android
```

### 3. Open in Android Studio

```bash
# Open the Android project in Android Studio
npx cap open android
```

### 4. Build and Run

- In Android Studio, select your device from the device dropdown
- Click the "Run" button to build and install the app

## Required Permissions

When you first open the app, you'll need to grant two permissions:

1. **Accessibility Service**: This allows the app to detect when you're viewing YouTube Shorts
   - Tap "Grant Permission" in the app
   - Select "YouTube Shorts Blocker" from the list of services
   - Toggle the service ON

2. **Display Over Other Apps**: This allows the app to show the blocking overlay
   - Tap "Grant Permission" in the app
   - Toggle "Allow display over other apps" to ON

## Starting the Blocker

Once permissions are granted:
1. Go to the "Protection" tab
2. Toggle the "Shorts Blocker" switch to ON

The app will now run in the background and block YouTube Shorts content automatically.

## Troubleshooting

If the blocker is not working:

1. Make sure both permissions are granted
2. Check that the service is running (toggle should be ON)
3. Restart the app and try again
4. Make sure battery optimization is disabled for the app

For more support, check the README or open an issue in the repository.
