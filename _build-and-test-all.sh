#! /bin/bash

set -e


./gradlew testClasses

dockerinfrastructure="./gradlew ${DATABASE?}infrastructureCompose"
dockerall="./gradlew ${DATABASE?}Compose"

${dockerall}Down
${dockerinfrastructure}Up

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

${dockerall}Up

./gradlew :end-to-end-tests:cleanTest :end-to-end-tests:test

./wait-for-services.sh localhost readers/${READER}/finished "8099"

compose="docker-compose -f docker-compose-${DATABASE}.yml "

. ./_set-image-version-env-vars.sh

$compose stop cdc-service
curl -s https://raw.githubusercontent.com/eventuate-foundation/eventuate-common/master/migration/db-id/migration.sh &> /dev/stdout | bash
$compose start cdc-service

${dockerall}Up -P envFile=docker-compose-env-files/db-id-gen.env

./gradlew :end-to-end-tests:cleanTest :end-to-end-tests:test

./gradlew -P verifyDbIdMigration=true :migration-tests:cleanTest migration-tests:test

${dockerall}Down
