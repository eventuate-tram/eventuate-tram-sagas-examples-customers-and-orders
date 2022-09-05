#! /bin/bash -e

./gradlew testClasses

./gradlew mysqlShardedOutboxesComposeDown

./gradlew mysqlShardedOutboxesComposeUp

./gradlew :end-to-end-tests:cleanTest :end-to-end-tests:test

./gradlew mysqlShardedOutboxesComposeDown