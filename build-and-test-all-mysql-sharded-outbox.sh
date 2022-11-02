#! /bin/bash -e

./gradlew testClasses

./gradlew mysqlShardedOutboxesComposeDown

./gradlew mysqlShardedOutboxesComposeUp

./gradlew cleanEndToEndTest endToEndTest

./gradlew mysqlShardedOutboxesComposeDown