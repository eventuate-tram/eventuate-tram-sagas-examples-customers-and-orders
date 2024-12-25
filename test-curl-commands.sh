#!/bin/bash -e

set -o pipefail

API_GATEWAY_SERVICE_CONTAINER=$(docker ps --filter "network=CustomersAndOrdersEndToEndTest" \
                    --filter "label=io.eventuate.name=api-gateway-service" -q)
echo "API_GATEWAY_SERVICE_CONTAINER=$API_GATEWAY_SERVICE_CONTAINER"

API_GATEWAY_SERVICE_PORT=$(docker port "$API_GATEWAY_SERVICE_CONTAINER" 8080 | cut -d':' -f2)

FILE_PATH="end-to-end-tests/src/endToEndTest/resources/templates/index.html"

awk '
  /<pre><code>/ {flag=1; next}
  /<\/code><\/pre>/ {flag=0; if (full_cmd != "") {print full_cmd; full_cmd=""}}
  flag {full_cmd = (full_cmd ? full_cmd " " : "") $0}
' "$FILE_PATH" | sed -e "s/\[\[\${apiGatewayUrl}\]\]/localhost:$API_GATEWAY_SERVICE_PORT/" | while read -r cmd; do
    echo "Executing: $cmd"
    eval "$cmd"
done

