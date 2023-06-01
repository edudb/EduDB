#!/bin/bash

#
# /*
# EduDB is made available under the OSI-approved MIT license.
# Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
# The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
# THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
# */
#

# Install bitmap-index.jar
./mvnw install:install-file -Dfile=lib/bitmap-index.jar -DgroupId=bitmap-index -DartifactId=bitmap-index -Dversion=1.0 -Dpackaging=jar

# Install gsp.jar
./mvnw install:install-file -Dfile=lib/gsp.jar -DgroupId=gsp -DartifactId=gsp -Dversion=1.0 -Dpackaging=jar

# Install translate-excel-7.1.8.jar
./mvnw install:install-file -Dfile=lib/translate-excel-7.1.8.jar -DgroupId=adipe -DartifactId=translate-excel -Dversion=7.1.8 -Dpackaging=jar

# Install translate-ng-7.1.8.jar
./mvnw install:install-file -Dfile=lib/translate-ng-7.1.8.jar -DgroupId=adipe -DartifactId=translate -Dversion=7.1.8 -Dpackaging=jar
