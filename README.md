# balalaika
Framework for modern mobile dictionary applications

Please note that everything presented here is still very much WIP!

## Prerequisites
* [Android SDK](https://developer.android.com/studio/command-line/sdkmanager)
* [Gradle](https://gradle.org/)
* [Android Debug Bridge](https://developer.android.com/studio/command-line/adb)

For ease of use, utilizing [Android Studio](https://developer.android.com/studio) is highly encouraged.

## Installation

* Clone the repository
```
git clone https://github.com/cheapmon/balalaika.git
cd balalaika
```
* Add your files under `app/src/main/res/raw` (Will be documented in the future)
* Install and run the app to your device or emulator
```
gradle wrapper
./gradlew app:assembleDebug
adb install -t ./app/build/outputs/apk/debug/app-debug.apk
```
In Android Studio, simply click the green triangle in the top right corner or press Shift+F10
