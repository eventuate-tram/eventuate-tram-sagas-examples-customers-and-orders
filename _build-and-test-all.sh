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

migration_file="migration_scripts/${DATABASE}/migration.sql"

rm -f $migration_file
if [ "${DATABASE}" == "mysql" ]; then
  curl https://raw.githubusercontent.com/eventuate-foundation/eventuate-common/wip-db-id-gen/mysql/4.initialize-database-db-id.sql --output $migration_file --create-dirs
  cat $migration_file | ./mysql-cli.sh -i
elif [ "${DATABASE}" == "postgres" ]; then
  curl https://raw.githubusercontent.com/eventuate-foundation/eventuate-common/wip-db-id-gen/postgres/5.initialize-database-db-id.sql --output $migration_file --create-dirs
  cat $migration_file | ./postgres-cli.sh -i
else
  echo "Unknown Database"
  exit 99
fi
rm -f $migration_file

${dockerall}Up -P envFile=docker-compose-env-files/db-id-gen.env

./gradlew :end-to-end-tests:cleanTest :end-to-end-tests:test

./gradlew -P verifyDbIdMigration=true :migration-tests:cleanTest migration-tests:test

${dockerall}Down