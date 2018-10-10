# vdl-java

[![GitHub license](https://img.shields.io/badge/license-Apache%202-blue.svg)](https://raw.githubusercontent.com/qscx9512/vdl-java/master/LICENSE)

VLive Video Downloader(a.k.a vdl) is video downloader inspired by [youtube-dl](https://github.com/rg3/youtube-dl)



## Changelog

 - 2.0.0 (2017.03.30) - Codename Alwayz
   1. Download video from Instagram
   2. Download video from Facebook
   3. Improve Code Structure
   4. Bug fix

 - 2.1.0 (2018.10.11) - Codename Muse
   1. Removed downloading video from Facebook (End of Facebook official API support)
   2. Changed build system to Gradle
   3. Improve Code Structure
   4. Bug fix
  
## Features

 - Download specific video from VLive homepage
 - Download video from VLive channel
 - Download video from Naver TVCast
 - Download video from Daum TVPot
 - Download video from Kakao TV
 - Download video from Instagram
 
 
## Usage

> vdl [-v | -d | -l `<url1>` ... | -s] 

**Options**
 - -h,--help :                     Prints options. Other options will be ignored.
 - -l,--list <arg1> <arg2> ... :   Directly download from list. Values are separated to ' '(Blank)
 - -s,--subtitle :                 Download subtitle if possible
 - -v,--version :                  prints version. Other options will be ignored.

or execute directly.



## Planned

 - Support download from
   - Twitter

and more...!



## How to Build

To create an distribution executable contained in zip file using Gradle, 
run the following command in the directory where build.gradle is (Note: Gradle must be installed):

    gradle clean build jar

This will create the executable contained zip file under `build/distributions/vdl-{version}.zip`

## API

[ ![Download](https://api.bintray.com/packages/polaris9017/vdl/vdl-core/images/download.svg) ](https://bintray.com/polaris9017/vdl/vdl-core/_latestVersion)

Cores has been seperated to [vdl-core](https://github.com/polaris9017/vdl-core)



##Links
- [Github project](https://github.com/polaris9017/vdl-java)
- [Issue tracker](https://github.com/polaris9017/vdl-java/issues)
- [vdl core API Bintray page](https://bintray.com/polaris9017/vdl/vdl-core)
- [vdl core API Github project](https://github.com/polaris9017/vdl-core)


## License

This program is licensed under the Apache Software License, Version 2.0.
 
 Dependencies for this program follows by...


 - Apache Commons Library (CLI, Codec, Logging) (http://commons.apache.org)
 
 Copyright (c) 2002-2018 The Apache Software Foundation, licensed under the Apache Software License, Version 2.0.
 
  - Apache Log4j 2 (https://logging.apache.org/log4j)
  
  Copyright (c) 1999-2018 The Apache Software Foundation, licensed under the Apache Software License, Version 2.0.

 
 ## Bug report
 
 
 Use 'Issues' tab or send to moonrise917@gmail.com


## Contact

If you have any problems, suggestions or questions, please contact to moonrise917@gmail.com

Your contribution will be great help for developing this.
