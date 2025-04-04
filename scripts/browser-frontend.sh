#!bin/sh

while ! curl --fail --silent --head http://localhost/; do
  sleep 1
done
start http://localhost/