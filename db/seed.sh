#!/bin/bash

# MongoDB details
MONGO_HOST=mongodb
MONGO_COLLECTION=notes

sleep 10

# Run mongoimport
mongoimport --host $MONGO_HOST --username $MONGO_USERNAME --password $MONGO_PASSWORD --authenticationDatabase admin --db $MONGO_INITDB_DATABASE --collection $MONGO_COLLECTION --type json --file /seed_data.json --jsonArray
