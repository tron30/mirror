# MirrorX

MirrorX is a lightweight Android mirror app that opens directly into a full-screen front-camera preview. It has no ads, analytics, account system, internet permission, image capture, video capture, or media storage.

[🌐 **Visit Website**](https://tron30.github.io/mirror) | [⬇️ **Download APK**](https://github.com/tron30/mirror/releases) | [☕ **Buy Me a Coffee**](https://www.buymeacoffee.com/tron30) | [🧡 **Patreon**](https://www.patreon.com/tron30)

---

## Requirements

- Android Studio with Android SDK
- Android 10+ device or emulator with a front-facing camera
- JDK 17+ supported by the bundled Gradle version

## Tech Stack

- Kotlin
- Jetpack Compose
- Material 3
- CameraX
- DataStore Preferences
- MVVM

## Build

```bash
./gradlew assembleDebug
```

On Windows:

```powershell
.\gradlew.bat assembleDebug
```

## Privacy

MirrorX requests only `CAMERA` permission.

MirrorX uses the front camera only for real-time display. No photos, videos, or personal data are stored or transmitted.

## Settings

- Default zoom level
- Auto-hide controls duration
- Start with brightness boost on or off
- Start mirrored on or off
- Theme: system, light, or dark

Settings are stored locally with DataStore Preferences. No network permission is declared.
