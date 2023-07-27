#!/bin/bash 

set -e -o pipefail

./gradlew build

./gradlew mysqlOnlyComposeUp postgresOnlyComposeUp

echo 'show databases;' | ./mysql-customer-service-cli.sh -i
echo 'show databases;' | ./mysql-order-service-cli.sh -i

echo '\l' | ./postgres-cli.sh -i

./gradlew mysqlOnlyComposeDown postgresOnlyComposeDown
