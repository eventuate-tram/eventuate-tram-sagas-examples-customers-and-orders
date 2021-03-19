#! /bin/bash

set -e

export DATABASE=mysql
export READER=MySqlReader

./_build-and-test-all.sh
