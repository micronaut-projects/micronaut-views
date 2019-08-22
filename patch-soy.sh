#!/bin/bash
set -e
EXIT_STATUS=0
SOY_VERSION=2019-08-22

echo "Downloading patched version of Soy..."
curl --progress-bar https://storage.googleapis.com/bloom-software/frontend/soy/soy-lib-b17.jar > soy.jar
mkdir -p views-soy/libs && cp -f soy.jar views-soy/libs/soy-$SOY_VERSION.jar

mvn install:install-file \
    -Dfile=soy.jar \
    -DgroupId=com.google.templates \
    -DartifactId=soy \
    -Dversion=$SOY_VERSION \
    -Dpackaging=jar

rm -f soy.jar
echo "Patched Soy version '$SOY_VERSION' installed."

exit $EXIT_STATUS
