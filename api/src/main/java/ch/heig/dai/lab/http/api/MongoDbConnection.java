package ch.heig.dai.lab.http.api;

import ch.heig.dai.lab.http.api.blog.BlogCodec;
import ch.heig.dai.lab.http.api.comment.CommentCodec;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.bson.codecs.DocumentCodec;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

/**
 * Singleton class that provides a connection to the MongoDB database.
 *
 * @author Aubry Mangold <aubry.mangold@heig-vd.ch>
 * @author Hugo Germano <hugo.germano@heig-vd.ch>
 */
public class MongoDbConnection {
    /**
     * The database connection.
     */
    private static final MongoDatabase database;

    /**
     * The MongoDB client.
     */
    private static final MongoClient client;

    static {
        final String username = System.getenv("MONGO_INITDB_ROOT_USERNAME");
        final String password = System.getenv("MONGO_INITDB_ROOT_PASSWORD");
        final String host = System.getenv("MONGO_INITDB_ROOT_HOST");
        final String uri = String.format("mongodb://%s:%s@%s", username, password, host);
        // Setup the codecs for the java classes (POJOs)
        CodecRegistry pojoCodecRegistry = CodecRegistries.fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
                                                                         CodecRegistries.fromCodecs(
                                                                                 new CommentCodec(new DocumentCodec()),
                                                                                 new BlogCodec(new DocumentCodec())),
                                                                         CodecRegistries.fromProviders(
                                                                                 PojoCodecProvider.builder()
                                                                                                  .automatic(true)
                                                                                                  .build()));
        // This is not a try-with-resources because it would automatically close the connection. Because it is a
        // singleton, the resources is not closed until the end of the lifetime of the application.
        try {
            client = MongoClients.create(uri);
            database = client.getDatabase("dai").withCodecRegistry(pojoCodecRegistry);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Get the database connection.
     *
     * @return The database connection singleton.
     */
    public static MongoDatabase getDatabase() {
        return database;
    }
}
