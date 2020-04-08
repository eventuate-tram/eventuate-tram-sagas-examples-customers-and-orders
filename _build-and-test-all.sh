#! /bin/bash

set -e


./gradlew testClasses

dockercdc="./gradlew ${DATABASE?}cdcCompose"
dockerall="./gradlew ${DATABASE?}Compose"

${dockerall}Down
${dockercdc}Build
${dockercdc}Up

./wait-for-services.sh localhost "8099"

#Testing db cli
if [ "${DATABASE}" == "mysql" ]; then
  echo 'show databases;' | ./mysql-cli.sh -i
elif [ "${DATABASE}" == "postgres" ]; then
  echo '\l' | ./postgres-cli.sh -i
else
  echo "Unknown Database"
  exit
fi

./gradlew -x :end-to-end-tests:test build

${dockerall}Build
${dockerall}Up

./wait-for-services.sh localhost "8081 8082"

./gradlew :end-to-end-tests:cleanTest :end-to-end-tests:test

${dockerall}Down