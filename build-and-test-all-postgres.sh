#! /bin/bash

set -e

export DATABASE=postgres
export READER=PostgresPollingReader

export SPRING_PROFILES_ACTIVE=postgres

./_build-and-test-all.sh
