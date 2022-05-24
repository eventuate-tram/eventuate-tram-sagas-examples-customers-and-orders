#! /bin/bash -e

export JAVA_DEVELOPMENT_IMAGE=localhost:5002/eventuate-tram-sagas-examples-customers-and-orders-java-development:multi-arch-local-build

DC="docker-compose -f docker-compose-java-development.yml"
$DC up -d
$DC exec java-development docker ps
$DC exec java-development ./gradlew tasks
$DC exec java-development ./gradlew assemble
