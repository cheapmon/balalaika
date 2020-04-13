# balalaika
Framework for modern mobile dictionary applications

## Installation
Balalaika should work out of the box using [Android Studio](https://developer.android.com/studio).

Without Android Studio, follow these steps:
1. Install the [JDK](https://java.com/en/download/help/download_options.xml)
2. Install the [Android SDK](https://developer.android.com/studio/command-line/sdkmanager)
    - Download the [command line tools](https://developer.android.com/studio/#downloads)
    - Extract the `.zip` file
    - Download and install by running
        ```
        ./tools/bin/sdkmanager --install "platforms;android-29" --sdk_root=$HOME/android-sdk
        ```
    - Accept the License Agreement by typing `y`
3. Clone the repository
    ```
    git clone https://github.com/cheapmon/balalaika.git
    cd balalaika
    ```
4. Notify Gradle of your SDK Location
    ```
    echo "sdk.dir=$HOME/android-sdk" >> local.properties
    ```
5. Add your files under `app/src/main/res/raw` or copy an example from `example`
(Will be documented in the future)
6. Install and run the app to your device or emulator
    ```
    ./gradlew app:assembleDebug
    adb install -t ./app/build/outputs/apk/debug/app-debug.apk
    ```
