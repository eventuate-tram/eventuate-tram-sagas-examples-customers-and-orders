#! /bin/bash -e

IMAGE=$(docker ps --filter "network=CustomersAndOrdersEndToEndTest" --format "{{.Image}}" \
  | grep mysql8 | head -1)

docker run ${1:--it} --rm \
   --network=CustomersAndOrdersEndToEndTest --rm \
   "${IMAGE?}" \
   sh -c 'exec mysql -hcustomer-service-db  -uroot -prootpassword  -o eventuate'
