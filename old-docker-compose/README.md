# rss-connector-example
Example of how to run Kafka Connect RSS Source 


1. Run in the directory /docker
> docker-compose up -d

2. Verify no connector is running
> curl -X GET -s --header "Content-Type: application/json" http://localhost:8083/connectors 

3.
 3.A Start RssSourceConnectorSportv1
curl -X POST -s --header "Content-Type: application/json" --data @../connector-config/rss_sport_key_feed_json.json http://localhost:8083/connectors

 3.B Start RssSourceConnectorSportv2
curl -X POST -s --header "Content-Type: application/json" --data @../connector-config/rss_sport_key_feed_url.json http://localhost:8083/connectors

4. Check Topic sports_topics_v1 using Kafka UI: localhost:8080
http://localhost:8080/ui/clusters/Broker1/topics/sports_topics_v1/messages

5. Destroy container
docker-compose down



###Others
#### https://github.com/provectus/kafka-ui/blob/master/docker/kafka-ui.yaml
#### docker run -e KAFKA_CLUSTERS_0_BOOTSTRAPSERVERS=localhost:29092 -e KAFKA_CLUSTERS_0_NAME=local -d provectuslabs/kafka-ui:latest 
	
	
	Based on 
	https://github.com/confluentinc/demo-scene/blob/master/ksql-atm-fraud-detection/scripts/create-es-sink.sh
	https://github.com/confluentinc/demo-scene/blob/master/ksql-atm-fraud-detection/docker-compose.yml
	https://rmoff.net/2018/12/15/docker-tips-and-tricks-with-kafka-connect-ksqldb-and-kafka/
	
