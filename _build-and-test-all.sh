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

echo -------------------------------
echo              RUNNING MIGRATION RELATED TESTS
echo -------------------------------

compose="docker-compose -f docker-compose-${DATABASE}.yml "

. ./_set-image-version-env-vars.sh

./.circleci/print-container-ips.sh

$compose stop cdc-service api-gateway-service

curl -s https://raw.githubusercontent.com/eventuate-foundation/eventuate-common/master/migration/db-id/migration.sh &> /dev/stdout | bash

echo === $compose cdc-service

$compose start cdc-service

echo === ${dockerall}Up

${dockerall}Up -P envFile=docker-compose-env-files/db-id-gen.env

./.circleci/print-container-ips.sh

echo === Running end to end tests


./gradlew :end-to-end-tests:cleanTest :end-to-end-tests:test

./gradlew -P verifyDbIdMigration=true :migration-tests:cleanTest migration-tests:test

${dockerall}Down
