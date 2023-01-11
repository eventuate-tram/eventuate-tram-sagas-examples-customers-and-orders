#! /bin/bash -e

# See https://github.com/testcontainers/testcontainers-java/issues/5524

docker run --privileged --rm tonistiigi/binfmt --install amd64

./build-and-test-all-mysql.sh