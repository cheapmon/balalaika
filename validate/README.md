# validate
Checks `.csv` input files for correctness according to the Balalaika input file specification

## Setup
In the root folder run the following:
```
./gradlew jar
```

## Usage
```
usage: java -jar validate/build/libs/validate-0.0.jar [options]
 -h,--help         print this message
 -p,--path <arg>   path to files to check
```
For example, try running it with `-p ./example` or `-p ./app/src/main/res/raw`

## Checks
- Parsing
- Primary & foreign keys
- Widget types
- Resource references
- Sequence field
- Boolean fields

Please note that checks for widget fields are not yet implemented.