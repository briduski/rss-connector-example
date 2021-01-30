package bri.feedv1.model;

import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;

// Copy from https://github.com/kaliy/kafka-connect-rss


public class RssSchemas {
    public static final String SCHEMA_VALUE_FEED = "bri.kafka.stream.Feed";
    public static final String SCHEMA_VALUE_ITEM = "bri.kafka.stream.Item";

    public static final String FEED_TITLE_FIELD = "title";
    public static final String FEED_URL_FIELD = "url";

    public static final String ITEM_FEED_FIELD = "feed";
    public static final String ITEM_TITLE_FIELD = "title";
    public static final String ITEM_ID_FIELD = "id";
    public static final String ITEM_LINK_FIELD = "link";
    public static final String ITEM_CONTENT_FIELD = "content";
    public static final String ITEM_AUTHOR_FIELD = "author";
    public static final String ITEM_DATE_FIELD = "date";

    public static final Schema FEED_SCHEMA = SchemaBuilder.struct().name(SCHEMA_VALUE_FEED)
            .version(1)
            .field(FEED_TITLE_FIELD, Schema.OPTIONAL_STRING_SCHEMA)
            .field(FEED_URL_FIELD, Schema.STRING_SCHEMA)
            .build();

    public static final Schema VALUE_SCHEMA = SchemaBuilder.struct().name(SCHEMA_VALUE_ITEM)
            .version(1)
            .field(ITEM_FEED_FIELD, FEED_SCHEMA)
            .field(ITEM_TITLE_FIELD, Schema.STRING_SCHEMA)
            .field(ITEM_ID_FIELD, Schema.STRING_SCHEMA)
            .field(ITEM_LINK_FIELD, Schema.STRING_SCHEMA)
            .field(ITEM_CONTENT_FIELD, Schema.OPTIONAL_STRING_SCHEMA)
            .field(ITEM_AUTHOR_FIELD, Schema.OPTIONAL_STRING_SCHEMA)
            .field(ITEM_DATE_FIELD, Schema.OPTIONAL_STRING_SCHEMA)
            .build();
}
