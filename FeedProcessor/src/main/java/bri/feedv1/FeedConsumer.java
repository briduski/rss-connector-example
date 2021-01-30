package bri.feedv1;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

import static bri.feedv1.Constants.*;

public class FeedConsumer implements Runnable {
    public org.apache.kafka.clients.consumer.Consumer<String, String> consumer;
    public boolean finalize=false;
    public String topic;
    public FeedConsumer(String _topic) {
        consumer = new KafkaConsumer<>(getProperties());
        topic = _topic;
        consumer.subscribe(Collections.singleton(topic));
    }

    public FeedConsumer() {
        consumer = new KafkaConsumer<>(getProperties());
        consumer.subscribe(Collections.singleton(Constants.INPUT_FEED_TOPIC));
    }
    public FeedConsumer(org.apache.kafka.clients.consumer.Consumer<String, String> mockConsumer) {
        this.consumer = mockConsumer;
    }

    @Override
    public void run() {
        System.out.println("Started consumer for topic: "+topic + ", broker: "+ Constants.BROKER);
        while (!finalize){
            try{
                final ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(1));
                for (final ConsumerRecord<String, String> record : records) {
                    String key = record.key();
                    String json = record.value();
                    System.out.println("Topic: "+topic + ", Key: "+key +", Value: "+json.substring(0, 25) + "...\n * * * * * * * * * * * ");
                /*    consumer.commitAsync((map, e) -> {
                        if (e != null) {
                            System.out.println(String.format("CommitAsync failed!!!! exception %s", e));
                        } else {
                            System.out.println(String.format("Committed OK read record, Offset %d, Topic %s", record.offset(), record.topic()));
                        }
                    });*/
                }
            } catch (Exception e) {
                finalize = true;
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
        }
        consumer.close();
    }

    public void finish(){
        System.out.println("Bye!");
        finalize = true;
    }
    public static void main(String[] args) {
        FeedConsumer stringConsumer = new FeedConsumer();
      //  Thread t1 = new Thread(stringConsumer);
      //  t1.start();

        FeedConsumer marcaConsumer = new FeedConsumer(MARCA_FEED_TOPIC);
        FeedConsumer sportConsumer = new FeedConsumer(SPORT_FEED_TOPIC);
        FeedConsumer mundoConsumer = new FeedConsumer(MUNDO_FEED_TOPIC);

        Thread t2 = new Thread(marcaConsumer);
        t2.start();
        Thread t3 = new Thread(sportConsumer);
        t3.start();
        Thread t4 = new Thread(mundoConsumer);
        t4.start();
      /*  try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        stringConsumer.finish();*/

    }


    static Properties getProperties() {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, Constants.BROKER);
        props.put(ConsumerConfig.CLIENT_ID_CONFIG, "cons-string-id2");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "group-string-id2");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        return props;
    }
}
