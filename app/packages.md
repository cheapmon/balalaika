# Module app
## Balalaika
Framework for modern mobile dictionary applications

### Installation
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

### Resources
Balalaika was conceptualized and implemented as part of my master's thesis and is still in active
development.
For additional information, please refer to the following sources:
- _Sonja Bosch, Thomas Eckart, Bettina Klimek, Dirk Goldhahn and Uwe Quasthoff (2018): Preparation
  and Usage of Xhosa Lexicographical Data for a Multilingual, Federated Environment at 11th Edition
  of the Language Resources and Evaluation Conference LREC 2018, Miyazaki (Japan)._
- _Thomas Eckart, Sonja Bosch, Uwe Quasthoff, Erik Körner, Dirk Goldhahn, and Simon Kaleschke:
  Usability and Accessibility of Bantu Language Dictionaries in the Digital Age: Mobile Access in
  an Open Environment. In: First workshop on Resources for African Indigenous Languages (RAIL) at
  12th Edition of Language Resources and Evaluation Conference (LREC 2020)._


# Package com.github.cheapmon.balalaika
Main application classes


# Package com.github.cheapmon.balalaika.data
Models and interfaces for the application database and additional resources

# Package com.github.cheapmon.balalaika.domain.config
Configuration of database version and sources

# Package com.github.cheapmon.balalaika.data.entities
Entities, relations and their database representation

# Package com.github.cheapmon.balalaika.data.entities.cache
Cache entry entity and its associated types

# Package com.github.cheapmon.balalaika.data.entities.category
Category entity and its associated types

# Package com.github.cheapmon.balalaika.data.entities.entry
Dictionary entry entity and its associated types

# Package com.github.cheapmon.balalaika.data.entities.history
History entry entity and its associated types

# Package com.github.cheapmon.balalaika.data.entities.lexeme
Lexeme entity and its associated types

# Package com.github.cheapmon.balalaika.data.entities.property
Property entity and its associated types

# Package com.github.cheapmon.balalaika.data.entities.view
Data view entity and its associated types

# Package com.github.cheapmon.balalaika.domain.insert
Import of input files into the application database

# Package com.github.cheapmon.balalaika.domain.repositories
Repositories for local and remote data sources

# Package com.github.cheapmon.balalaika.core.resources
Retrieval of raw Android resources

# Package com.github.cheapmon.balalaika.core.storage
Local key-value storage


# Package com.github.cheapmon.balalaika.di
Components and models for dependency injection


# Package com.github.cheapmon.balalaika.ui
User interface fragments

# Package com.github.cheapmon.balalaika.ui.bookmarks
Bookmarks fragment and associated classes

# Package com.github.cheapmon.balalaika.ui.dictionary
Dictionary fragment and associated classes

# Package com.github.cheapmon.balalaika.ui.dictionary.widgets
User interface widgets for displaying small pieces of information

# Package com.github.cheapmon.balalaika.ui.history
History fragment and associated classes

# Package com.github.cheapmon.balalaika.ui.preferences
Preferences fragment and associated classes

# Package com.github.cheapmon.balalaika.ui.search
Search fragment and associated classes


# Package com.github.cheapmon.balalaika.util
Useful utilities and extension functions
