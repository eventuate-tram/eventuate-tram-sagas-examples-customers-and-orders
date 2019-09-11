#! /bin/bash

set -e


. ./set-env-${DATABASE?}.sh

./gradlew testClasses

dockercdc="./gradlew ${DATABASE?}cdcCompose"
dockerall="./gradlew ${DATABASE?}Compose"

${dockerall}Down
${dockercdc}Build
${dockercdc}Up

./wait-for-services.sh $DOCKER_HOST_IP "8099"

./gradlew -x :end-to-end-tests:test build

${dockerall}Build
${dockerall}Up

./wait-for-services.sh $DOCKER_HOST_IP "8081 8082"

./gradlew :end-to-end-tests:cleanTest :end-to-end-tests:test

${dockerall}Down