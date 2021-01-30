FROM confluentinc/cp-kafka-connect:latest

RUN \
  echo "PRE-INSTALLING CONNECTORS..." && \
  echo "===> Installing Kafka RSS Connector ..." && \
  confluent-hub install kaliy/kafka-connect-rss:0.1.0 --no-prompt
