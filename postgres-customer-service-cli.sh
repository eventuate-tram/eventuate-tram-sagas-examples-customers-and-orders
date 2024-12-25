#! /bin/bash -e

IMAGE=$(docker ps --filter "network=CustomersAndOrdersEndToEndTest" --format "{{.Image}}" \
  | grep postgres | head -1)

docker run ${1:--it} \
   --network=CustomersAndOrdersEndToEndTest \
   --rm "$IMAGE" \
   sh -c 'export PGPASSWORD=postgrespw; exec psql -h "customer-service-db" -U postgresuser eventuate'
