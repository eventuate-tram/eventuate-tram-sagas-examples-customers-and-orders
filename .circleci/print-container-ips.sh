#! /bin/bash -e

docker ps -a | cut -f1 -d\ | tail +2  | xargs -n1 docker inspect -f '{{.Name}}  {{.ID}} {{range.NetworkSettings.Networks}}{{.IPAddress}}{{end}}'