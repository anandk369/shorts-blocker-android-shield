
# Contributing to YouTube Shorts Blocker

Thank you for your interest in contributing to the YouTube Shorts Blocker project! This document provides guidelines and instructions for contributing.

## Getting Started

1. Fork the repository
2. Clone your fork: `git clone https://github.com/YOUR_USERNAME/youtube-shorts-blocker.git`
3. Install dependencies: `npm install`
4. Run the development server: `npm run dev`

## Development Workflow

1. Create a new branch for your feature/fix: `git checkout -b feature/your-feature-name`
2. Make your changes
3. Run tests: `npm test`
4. Commit your changes using conventional commit messages
5. Push to your fork and submit a pull request

## Project Structure

```
├── android/                # Android specific code
│   └── src/main/           # Native Android implementation
├── src/                    # React web app source
│   ├── components/         # UI components
│   ├── pages/              # App pages
│   └── services/           # JavaScript services
├── capacitor.config.ts     # Capacitor configuration
└── README.md               # Project documentation
```

## Native Android Development

If you're working on the native Android side:

1. Make sure you have Android Studio installed
2. After making changes to web code: `npm run build`
3. Sync changes with the Android project: `npx cap sync android`
4. Open in Android Studio: `npx cap open android`

### Accessibility Service Development

When modifying the accessibility service:

- Be mindful of performance, as the service runs continuously
- Test thoroughly with different versions of the YouTube app
- Keep detection logic up to date with YouTube UI changes

### Overlay Implementation

When working on the blocker overlay:

- Keep it simple and non-intrusive
- Ensure it doesn't consume excessive resources
- Test on different screen sizes and orientations

## Pull Request Process

1. Update documentation if needed
2. Add or update tests for your changes
3. Ensure all tests pass
4. Update the README.md with details of changes if appropriate
5. The pull request will be reviewed by maintainers

## Code Style

- Follow the established code style in the project
- Use TypeScript for web code and Kotlin for Android code
- Document complex functions and components

## Reporting Issues

When reporting issues, please include:

- A clear, descriptive title
- Steps to reproduce the issue
- Expected behavior
- Actual behavior
- Screenshots if applicable
- Device and OS version information

Thank you for contributing to YouTube Shorts Blocker!
