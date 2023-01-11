#! /bin/bash

export POSTGRES_USER=postgresuser
export POSTGRES_PASSWORD=postgrespw
export POSTGRES_DB=eventuate

export HOST=customer-service-mysql

echo ==== $CID


echo 'select * from pg_stat_replication;' | \
docker run -i \
   --rm \
   --network foofoo \
   -e POSTGRES_USER \
   -e POSTGRES_PASSWORD \
   -e HOST \
   -e POSTGRES_DB \
   postgres:12 \
   sh -c 'export PGPASSWORD=postgrespw; exec psql -h customer-service-mysql -U postgresuser eventuate'

echo DONE
