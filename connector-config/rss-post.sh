#!/bin/sh

curl -X POST -s --header "Content-Type: application/json" --data @/connector-config/rss_sport_key_feed_json.json http://localhost:8083/connectors