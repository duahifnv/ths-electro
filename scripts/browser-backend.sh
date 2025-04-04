#!bin/sh

while ! curl --fail --silent --head http://localhost/api; do
  sleep 1
done
start http://localhost/api/swagger-ui