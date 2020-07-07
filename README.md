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
5. Add .zip files under `app/src/main/assets` or copy an example from `example`
(Will be documented in the future)
6. Install and run the app to your device or emulator
    ```
    ./gradlew app:assembleDebug
    adb install -t ./app/build/outputs/apk/debug/app-debug.apk
    ```

## Tooling
Balalaika provides the following tools:
- `validate` checks input files for correctness

For further documentation, check the respective READMEs

## Screenshots
<img src="screenshot/xhosa-screen1.png?raw=true" alt="Dictionary" width="250"><img src="screenshot/xhosa-screen2.png?raw=true" alt="Additional functions" width="250"><img src="screenshot/xhosa-screen3.png?raw=true" alt="Preferences" width="250">

## Resources
Balalaika was conceptualized and implemented as part of my master's thesis and is still in active development.
For additional information, please refer to the following sources:
- _Sonja Bosch, Thomas Eckart, Bettina Klimek, Dirk Goldhahn and Uwe Quasthoff (2018): Preparation and Usage of Xhosa Lexicographical Data for a Multilingual, Federated Environment at 11th Edition of the Language Resources and Evaluation Conference LREC 2018, Miyazaki (Japan)._
- _Thomas Eckart, Sonja Bosch, Uwe Quasthoff, Erik Körner, Dirk Goldhahn, and Simon Kaleschke: Usability and Accessibility of Bantu Language Dictionaries in the Digital Age: Mobile Access in an Open Environment. In: First workshop on Resources for African Indigenous Languages (RAIL) at 12th Edition of Language Resources and Evaluation Conference (LREC 2020)._
