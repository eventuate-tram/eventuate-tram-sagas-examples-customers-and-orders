#! /bin/bash -e

./java-development/build-docker-java-development-multi-arch.sh

docker pull localhost:5002/eventuate-tram-sagas-examples-customers-and-orders-java-development:multi-arch-local-build

./test-java-development.sh
