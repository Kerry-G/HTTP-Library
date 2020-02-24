[![Build Status](https://travis-ci.com/Kerry-G/HTTP-Library.svg?branch=master)](https://travis-ci.com/Kerry-G/HTTP-Library)
[![Code Coverage](https://codecov.io/github/Kerry-G/HTTP-Library/coverage.svg)](https://codecov.io/gh/Kerry-G/HTTP-Library)

# HTTP-Library
HTTP Library Java Implementation with cURL-like Command Line.

## Installation

* Java 8
* Maven 3

## Build

Use `mvn package` to create a .jar in /target. The executable class is HttpClientDriver. 

```
mvn package
java -jar {path-to-project}\target\HTTP-Library-1.0-SNAPSHOT.jar [args]
```

## Usage

### Windows

#### Prerequisite

* SH
* PowerShell

First step to make it work, is to make sure you have sh in your env. path. You can get it via Git Bash if you have
it installed on your PC. I had to add `C:\Program Files\Git\bin` in my env. variable, mileage may vary. After that,
add the location of the `/target` directory to your file path, e.g. `E:\Repos\HTTP-Library\target`. Add .sh as a file 
extension that cmd can run by adding `.SH;` to your PATHEXT. Once this is done, openning .sh will depend on the default 
application to run the file. Make Git Bash your default application to run .sh files on windows and you are good to go! 

Once that's done, you can use `whttpc [command]` to use the library.

## Authors

* Kerry Gougeon
* Jonathan Mongeau
