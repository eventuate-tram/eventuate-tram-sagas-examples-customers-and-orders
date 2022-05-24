#! /bin/bash -e

SCRIPT_DIR=$(cd $( dirname "${BASH_SOURCE[0]}" ) ; pwd)

docker-compose -f $SCRIPT_DIR/../docker-compose-registry.yml --project-name eventuate-common-registry up -d registry

IMAGE="${KAFKA_MULTI_ARCH_IMAGE:-${DOCKER_HOST_NAME:-host.docker.internal}:5002/eventuate-tram-sagas-examples-customers-and-orders-java-development:multi-arch-local-build}"
OPTS="${BUILDX_PUSH_OPTIONS:---output=type=image,push=true,registry.insecure=true}"

echo IMAGE=$IMAGE
echo OPTS=$OPTS

${SCRIPT_DIR}/../prepare-java-development-docker-image.sh

docker buildx build --platform linux/amd64,linux/arm64 \
  -t $IMAGE \
  -f $SCRIPT_DIR/Dockerfile \
  $OPTS \
  $SCRIPT_DIR
