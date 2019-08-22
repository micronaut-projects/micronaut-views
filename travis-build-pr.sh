#!/bin/bash
set -e
EXIT_STATUS=0

./patch-soy.sh
./gradlew --stop
./gradlew testClasses --no-daemon
./gradlew check --no-daemon || EXIT_STATUS=$?


exit $EXIT_STATUS
