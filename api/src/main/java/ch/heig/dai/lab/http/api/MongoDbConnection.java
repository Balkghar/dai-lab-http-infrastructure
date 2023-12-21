package ch.heig.dai.lab.http.api;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

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

    static {
        final String username = System.getenv("MONGO_INITDB_ROOT_USERNAME");
        final String password = System.getenv("MONGO_INITDB_ROOT_PASSWORD");
        final String host = System.getenv("MONGO_INITDB_ROOT_HOST");
        final String uri = String.format("mongodb://%s:%s@%s", username, password, host);
        CodecRegistry pojoCodecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(), fromProviders(PojoCodecProvider.builder().automatic(true).build()));

        try {
            MongoClient mongoClient = MongoClients.create(uri);
            database = mongoClient.getDatabase("dai").withCodecRegistry(pojoCodecRegistry);
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
