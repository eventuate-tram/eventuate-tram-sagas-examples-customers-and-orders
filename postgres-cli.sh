#! /bin/bash -e

docker run ${1:--it} \
   --name postgresterm --network=${PWD##*/}_default \
   --rm postgres:12 \
   sh -c 'export PGPASSWORD=eventuate; exec psql -h "customer-service-postgres" -U eventuate customer_service'
