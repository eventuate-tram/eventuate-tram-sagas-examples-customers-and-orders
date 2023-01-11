#! /bin/bash

for CID in $(docker ps | grep postgres | cut -f1 -d\ ) ; do


export POSTGRES_USER=$(docker inspect $CID | jq '.[0].Config.Env' | sed -e '/POSTGRES_USER/!d' -e 's/.*=//' -e 's/".*//')
export POSTGRES_PASSWORD=$(docker inspect $CID | jq '.[0].Config.Env' | sed -e '/POSTGRES_PASSWORD/!d' -e 's/.*=//' -e 's/".*//')
export POSTGRES_DB=$(docker inspect $CID | jq '.[0].Config.Env' | sed -e '/POSTGRES_DB/!d' -e 's/.*=//' -e 's/".*//')

export HOST=$(docker inspect $CID | jq -r '.[0].NetworkSettings.Networks.foofoo.IPAddress')

echo ==== $CID

echo $POSTGRES_USER $POSTGRES_PASSWORD $POSTGRES_DB $HOST

echo 'select * from pg_stat_replication;' | \
echo docker run -i \
   --rm  \
   --network foofoo \
   -e POSTGRES_USER \
   -e POSTGRES_PASSWORD \
   -e HOST \
   -e POSTGRES_DB \
   postgres:12 \
   sh -c 'exec psql -h $HOST -U $POSTGRES_USER $POSTGRES_DB'

done

echo DONE
