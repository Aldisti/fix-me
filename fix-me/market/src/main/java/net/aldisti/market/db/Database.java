package net.aldisti.market.db;

import com.mongodb.ConnectionString;
import com.mongodb.MongoException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.bson.BsonDocument;
import org.bson.BsonInt64;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class Database {
    private static final Logger log = LoggerFactory.getLogger(Database.class);

    private static MongoClient client = null;
    private static String databaseName;
    private static String collectionName;

    private Database() { }

    public static void create() {
        if (client != null)
            return; // instantiate just one time

        String host = getEnv("DB_HOST", "localhost:27017");
        String username = getEnv("DB_USERNAME", "");
        String password = getEnv("DB_PASSWORD", "");
        databaseName = getEnv("DB_NAME", "market");
        collectionName = getEnv("DB_COLLECTION", "transactions");

        String connectionUrl = String.format(
                "mongodb://%s:%s@%s/%s", username, password, host, databaseName
        );

        createConnection(new ConnectionString(connectionUrl));
    }

    public static void save(Document transaction) {
        client.getDatabase(databaseName)
                .getCollection(collectionName)
                .insertOne(transaction);
    }

    public static void close() {
        if (client != null) {
            client.close();
            client = null;
        }
    }

    public static String getEnv(String name, String defaultValue) {
        return Optional.ofNullable(System.getenv(name)).orElse(defaultValue);
    }

    private static void createConnection(ConnectionString uri) {
        Bson command = new BsonDocument("ping", new BsonInt64(1));
        try {
            log.info("Connecting to database {}", databaseName);
            client = MongoClients.create(uri);
            MongoDatabase database = client.getDatabase(databaseName);
            database.runCommand(command);
            log.info("Successfully connected to {} (MongoDB)", databaseName);
        } catch (MongoException e) {
            log.error("Cannot connect to database {}", databaseName);
            close();
            System.exit(42);
        }
    }
}
