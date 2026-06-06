@echo off
setlocal
set "JAVA_HOME=%~dp0jdk17\jdk-17.0.19+10"
set "PATH=%JAVA_HOME%\bin;%PATH%"

rem Delete existing keystore
if exist "%~dp0mirrorx-release.keystore" del "%~dp0mirrorx-release.keystore"

rem Generate keystore
keytool -genkeypair -v -keystore "%~dp0mirrorx-release.keystore" -alias mirrorx -keyalg RSA -keysize 2048 -validity 10000 -storepass password -keypass password -dname "CN=MirrorX, OU=Dev, O=MyCompany, L=City, S=State, C=US"

rem Create android-sdk directory
mkdir "%~dp0android-sdk"

rem Download command‑line‑tools
powershell -NoProfile -Command "Invoke-WebRequest -Uri 'https://dl.google.com/android/repository/commandlinetools-win-11076708_latest.zip' -OutFile '%~dp0android-sdk\cmdline-tools.zip'"

rem Unzip
powershell -NoProfile -Command "Expand-Archive -Path '%~dp0android-sdk\cmdline-tools.zip' -DestinationPath '%~dp0android-sdk' -Force"

rem Rename cmdline‑tools folder
rename "%~dp0android-sdk\cmdline-tools" latest

rem Add SDK tools to PATH
set "PATH=%~dp0android-sdk\latest\bin;%PATH%"

rem Sign APK
apksigner sign --ks "%~dp0mirrorx-release.keystore" --ks-key-alias mirrorx --ks-pass pass:password --key-pass pass:password --out "%~dp0app\build\outputs\apk\release\app-release-signed.apk" "%~dp0app\build\outputs\apk\release\app-release-unsigned.apk"

rem List devices
adb devices

rem Install APK
adb install -r "%~dp0app\build\outputs\apk\release\app-release-signed.apk"

endlocal
