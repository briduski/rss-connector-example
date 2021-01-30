# FeedProcessor

Java application for handling data sent from Rss Source Connector.

**src/main/java/bri/feedv1/Split.java**
    
    - Split the feed topic, based on the source(uil) of the information. 
    - Rekey the every event depending on the title of the event
 
**src/main/java/bri/feedv1/FeedConsumer.java**

    - Consumer of 3 new created topics** 