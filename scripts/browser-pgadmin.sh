#!bin/sh

while ! curl --fail --silent --head http://localhost:7000/; do
  sleep 1
done
start http://localhost:7000/