#! /bin/bash -e

docker run $* \
   --name mysqlterm --network=eventuate_network --rm \
   -e MYSQL_HOST=mysql \
   mysql:5.7.13 \
   sh -c 'exec mysql -h"$MYSQL_HOST"  -uroot -prootpassword -o eventuate'
