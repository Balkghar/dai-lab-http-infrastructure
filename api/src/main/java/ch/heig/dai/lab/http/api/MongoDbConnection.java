package ch.heig.dai.lab.http.api;

import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;

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
//        final String uri = System.getenv("MONGODB_URI");
        final String uri = "mongodb://root:password@localhost:27017";
        try {
            MongoClient mongoClient = MongoClients.create(uri);
            database = mongoClient.getDatabase("dai");
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
