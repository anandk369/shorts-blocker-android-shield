
# YouTube Shorts Blocker

<p align="center">
  <img src="public/placeholder-logo.svg" width="128" height="128" alt="YouTube Shorts Blocker Logo">
</p>

<p align="center">
  <b>Take control of your screen time by blocking YouTube Shorts content</b>
</p>

## Overview

YouTube Shorts Blocker is an Android application that uses accessibility services to detect and block YouTube Shorts content. When YouTube Shorts are detected, the app displays a fullscreen overlay to prevent you from watching them, helping you maintain focus and reduce screen time.

## Features

- **Real-time detection** of YouTube Shorts content
- **Automatic blocking** with a clean, non-intrusive overlay
- **Smart detection** using multiple methods to identify Shorts content
- **Auto-start capability** to maintain protection after device restart
- **Low resource usage** with minimal battery impact
- **Privacy-focused** - no data collection or transmission

## How It Works

1. The app runs an Accessibility Service in the background to monitor the YouTube app
2. When YouTube Shorts content is detected, an overlay is displayed
3. The overlay disappears automatically when you leave the Shorts interface
4. All processing happens locally on your device - no data is sent anywhere

## Installation

See [INSTALLATION.md](INSTALLATION.md) for detailed installation instructions.

Quick start:
1. Install the app
2. Grant Accessibility Service permission
3. Grant Display Over Other Apps permission
4. Toggle the protection switch to ON

## Requirements

- Android 6.0 (Marshmallow) or higher
- YouTube app installed

## Privacy

YouTube Shorts Blocker respects your privacy. The app:
- Does not collect any personal data
- Does not transmit any information off your device
- Only accesses the content necessary to detect Shorts

Read our full [Privacy Policy](PRIVACY_POLICY.md) for more details.

## Contributing

Contributions are welcome! See [CONTRIBUTION.md](CONTRIBUTION.md) for guidelines.

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Support

If you encounter any issues or have questions, please open an issue in this repository.

---

<p align="center">
  <i>Your screen time, your control</i>
</p>
