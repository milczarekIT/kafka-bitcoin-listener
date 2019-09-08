# kafka-blockchain-listener

An experimental Bitcoin transaction listener that sends transactions events from Bitcoin P2P network into Kafka topic.

## Usage

### Download kafka
First download Kafka from 
https://kafka.apache.org/quickstart

### Start an ad-hoc zoopkeeper instance and kafka server

1. Navigate to the kafka directory:
`cd kafka_2.12-2.3.0`

2. Start zookeeper: `bin/zookeeper-server-start.sh config/zookeeper.properties`

3. Start a kafka server: `bin/kafka-server-start.sh config/server.properties`

4. Create a `transaction` topic: `bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic transactions`

5. Check the the topic was created successfully: `bin/kafka-topics.sh --list --zookeeper localhost:2181`


### Start a kafka consumer


In order to check that our data is being sent into kafka, we set up a simple consumer to view messages sent to the topic.

1. Navigate to the kafka directory:
`cd kafka_2.12-2.3.0`


2. Start a command line listener on transaction topic: 
`bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic transactions --from-beginning`
