#! /bin/bash

set -e

export DATABASE=mysql

docker network inspect eventuate_network >/dev/null 2>&1 || docker network create eventuate_network

./_build-and-test-all.sh

docker network prune -f