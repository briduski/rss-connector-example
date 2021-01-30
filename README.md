# rss-connector-example
Example of how to run Kafka [Connect RSS Source](https://github.com/kaliy/kafka-connect-rss)  

	


1. Run in the directory /docker-compose: > docker-compose up -d

2. At this point there is one connector source running: RssSourceConnectorSportv1
---> >> The key it is json {"title": "bla", "url": "blabla"}

3.  Check Topic sports_topics_v1 using Kafka UI: localhost:8080
   http://localhost:8080/ui/clusters/Broker1/topics/sports_topics_v1/messages

## RssSourceConnectorSportv1
 rss-feed-url --> RssSourceConnectorSportv1 --> Kafka Topic (sports_topics_v1)
```
 Connector configuration - RssSourceConnectorSportv1 => file: rss_sport_key_feed_json.json:  

   1.- Record value => Convert/Parse to json
   "value.converter":"org.apache.kafka.connect.json.JsonConverter",
   "value.converter.schemas.enable":"false",

   2.- Transformations
    "key.ignore" : "false",
    "transforms" : "createKey",
       
   3.- Create key => Extract from the json record value the element feed   
   "transforms.createKey.type":"org.apache.kafka.connect.transforms.ValueToKey",
   "transforms.createKey.fields":"feed",
         
   4.- Key value => Convert/Parse the key to json
   "key.converter":"org.apache.kafka.connect.json.JsonConverter",
   "key.converter.schemas.enable":"false"
```


4. We can Start RssSourceConnectorSportv2
curl -X POST -s --header "Content-Type: application/json" --data @../connector-config/rss_sport_key_feed_url.json http://localhost:8083/connectors
---> >> The key it is a string(the url of the feed)

```
 Connector configuration - RssSourceConnectorSportv2 => file: rss_sport_key_feed_url.json:  

   1.- Record value => Convert/Parse to json
   "value.converter":"org.apache.kafka.connect.json.JsonConverter",
   "value.converter.schemas.enable":"false",

   2.- Transformations
    "key.ignore" : "false",
    "transforms" : "createKey, extractFeed, extractTitle",
       
   3.- Create key => Extract from the json record value the element feed   
   "transforms.createKey.type":"org.apache.kafka.connect.transforms.ValueToKey",
   "transforms.createKey.fields":"feed",
         
   4.- Extract feed json object from key
    "transforms.extractFeed.type":"org.apache.kafka.connect.transforms.ExtractField$Key",
   "transforms.extractFeed.field":"feed",

   5.- Extract url element (under feed json)
   "transforms.extractTitle.type":"org.apache.kafka.connect.transforms.ExtractField$Key",
   "transforms.extractTitle.field":"url",

   6.- Key value => Convert/Parse the key to json
   "key.converter":"org.apache.kafka.connect.json.JsonConverter",
   "key.converter.schemas.enable":"false"

 
```

5. Run src/main/java/bri/feedv1/Split.java 
6. Run src/main/java/bri/feedv1/FeedConsumer.java

7. Destroy container: docker-compose down



### Others
#### https://github.com/provectus/kafka-ui/blob/master/docker/kafka-ui.yaml
#### docker run -e KAFKA_CLUSTERS_0_BOOTSTRAPSERVERS=localhost:29092 -e KAFKA_CLUSTERS_0_NAME=local -d provectuslabs/kafka-ui:latest 
	
	
## Others:

### Kafka Connect Rest UI useful commands:
**Overview: https://docs.confluent.io/platform/current/connect/references/restapi.html**
 
- curl -X GET -s --header "Content-Type: application/json" http://localhost:8083/connectors 

- curl -X POST -s --header "Content-Type: application/json" --data @../connector-config/rss_sport_key_feed_json.json http://localhost:8083/connectors
- curl -X POST -s --header "Content-Type: application/json" --data @../connector-config/rss_sport_key_feed_url.json http://localhost:8083/connectors
- curl -X PUT -s --header "Content-Type: application/json" --data @../connector-config/rss_sport_key_feed_url.json http://localhost:8083/connectors/RssSourceConnectorSportv2/config
- curl -X POST -s --header "Content-Type: application/json" http://localhost:8083/connectors/RssSourceConnectorSportv2/restart
- curl -X DELETE -s --header "Content-Type: application/json" http://localhost:8083/connectors/RssSourceConnectorSportv2


### Connect to kafka1 
Delete topic: 
docker exec -it kafka1 kafka-topics --zookeeper zookeeper:2181 --delete --topic sports_topics_v2

Start console consumer: 
docker exec -it kafka1 kafka-console-consumer --bootstrap-server localhost:29092 --topic sports_topics_v1 --from-beginning --property print.key=true --property key.separator=" - "

docker exec kafka1 kafka-topics --list --zookeeper zookeeper:2181


### How to check health checks within a container
 docker inspect --format "{{json .State.Health }}" <container-name> | jq
 
 Example: 
  docker inspect --format "{{json .State.Health }}" zookeeper | jq
  docker inspect --format "{{json .State.Health }}" kafka1 | jq
  docker inspect --format "{{json .State.Health }}" connect | jq
  docker inspect --format "{{json .State.Health }}" schema-registry | jq
 
  
### Run a command infinite every X seconds
while true; do foo; sleep X; done

Example:   
while true; do docker ps; sleep 15; done  
 	
 	
### Check if Kafka UI is up & running
  nc -v -z localhost 8080 
  
  while true; do nc -v -z localhost 8080; sleep 1; done  
    
    
## Resources
- https://github.com/confluentinc/demo-scene/blob/master/ksql-atm-fraud-detection/scripts/create-es-sink.sh
- https://github.com/confluentinc/demo-scene/blob/master/ksql-atm-fraud-detection/docker-compose.yml
- https://rmoff.net/2018/12/15/docker-tips-and-tricks-with-kafka-connect-ksqldb-and-kafka/
    