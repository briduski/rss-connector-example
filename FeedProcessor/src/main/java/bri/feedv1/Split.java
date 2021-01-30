package bri.feedv1;

import bri.feedv1.model.Item;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Predicate;

import java.util.Properties;
import java.util.concurrent.CountDownLatch;

import static bri.feedv1.Constants.*;

public class Split {

    public static Serde<String> stringSerde = Serdes.String();

    public static void main(final String[] args) throws Exception {
        Properties props = getProps();
        final KafkaStreams streams = new KafkaStreams(buildTopology(), props);
        final CountDownLatch latch = new CountDownLatch(1);
        // attach shutdown handler to catch control-c
        Runtime.getRuntime().addShutdownHook(new Thread("streams-split-shutdown-hook") {
            @Override
            public void run() {
                streams.close();
                latch.countDown();
            }
        });
        try {
            System.out.println("Started Split feed app");
            streams.cleanUp();
            streams.start();
            latch.await();
        } catch (final Throwable e) {
            e.printStackTrace();
            System.exit(1);
        }
        System.exit(0);
    }

    public static Topology buildTopology(){
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        StreamsBuilder builder = new StreamsBuilder();
        Predicate<String, Item> isMarca = (String key, Item value) -> key.contains("marca");
        Predicate<String, Item> isMundo = (String key, Item value) -> key.contains("mundo");
        Predicate<String, Item> isSport = (String key, Item value) -> key.contains("sport");


        final KStream<String, String> stream = builder.<String, String>stream(INPUT_FEED_TOPIC, Consumed.with(stringSerde, stringSerde));

        KStream<String, Item>[] kstreamByDept =  stream
                .mapValues(jsonString-> gson.fromJson(jsonString, Item.class))
                .branch(isMarca, isSport, isMundo);



        kstreamByDept[0].selectKey((k,v)-> v.getTitle()).mapValues(x-> gson.toJson(x)).to(MARCA_FEED_TOPIC);
        kstreamByDept[1].selectKey((k,v)-> v.getTitle()).mapValues(x-> gson.toJson(x)).to(SPORT_FEED_TOPIC);
        kstreamByDept[2].selectKey((k,v)-> v.getTitle()).mapValues(x-> gson.toJson(x)).to(MUNDO_FEED_TOPIC);
        return builder.build();
    }
    public static Properties getProps(){
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "split-application-v2");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG,BROKER);
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        return props;
    }



}
