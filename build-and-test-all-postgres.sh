#! /bin/bash

set -e

. ./set-env-postgres.sh

docker-compose -f docker-compose-postgres.yml down -v

docker-compose -f docker-compose-postgres.yml up -d --build zookeeper postgres kafka

./wait-for-postgres.sh

docker-compose -f docker-compose-postgres.yml up -d --build cdcservice

./gradlew assemble

docker-compose -f docker-compose-postgres.yml up -d --build

./gradlew :integration-tests:cleanTest
./gradlew :end-to-end-tests:cleanTest

./wait-for-services.sh $DOCKER_HOST_IP "8081 8082"
./gradlew build

docker-compose -f docker-compose-postgres.yml down -v
