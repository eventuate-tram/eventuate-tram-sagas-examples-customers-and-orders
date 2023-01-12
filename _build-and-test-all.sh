#! /bin/bash

set -e

./gradlew compileAll test

dockerall="./gradlew ${DATABASE?}OnlyCompose"

${dockerall}Down
${dockerall}Up

if [ "${DATABASE}" == "mysql" ]; then
  echo 'show databases;' | ./mysql-customer-service-cli.sh -i
  echo 'show databases;' | ./mysql-order-service-cli.sh -i
elif [ "${DATABASE}" == "postgres" ]; then
  echo '\l' | ./postgres-cli.sh -i
else
  echo "Unknown Database"
  exit 99
fi

${dockerall}Down

./gradlew build

./gradlew ${DATABASE?}ComposeDown ${DATABASE?}ComposeUp endToEndTestsUsingDockerCompose
./gradlew ${DATABASE?}ComposeDown
