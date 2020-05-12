#! /bin/bash

set -e


./gradlew testClasses

dockercdc="./gradlew ${DATABASE?}cdcCompose"
dockerall="./gradlew ${DATABASE?}Compose"

${dockerall}Down
${dockercdc}Build
${dockercdc}Up

#Testing db cli
if [ "${DATABASE}" == "mysql" ]; then
  echo 'show databases;' | ./mysql-cli.sh -i
elif [ "${DATABASE}" == "postgres" ]; then
  echo '\l' | ./postgres-cli.sh -i
else
  echo "Unknown Database"
  exit 99
fi

./gradlew -x :end-to-end-tests:test build

${dockerall}Build
${dockerall}Up

./gradlew :end-to-end-tests:cleanTest :end-to-end-tests:test

${dockerall}Down