package net.aldisti.market.db;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import net.aldisti.common.fix.Message;
import net.aldisti.common.fix.constants.Tag;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.print.Doc;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class DatabaseProvider {
    private static final Logger log = LoggerFactory.getLogger(DatabaseProvider.class);

    private static final MongoClient client;
    private static final String databaseName;
    private static final String collectionName;

    static {
        String host = getEnv("DB_HOST", "localhost:27017");
        String username = getEnv("DB_USERNAME", "user");
        String password = getEnv("DB_PASSWORD", "lQ26ygmxZbzr1oLkTJMgGA8NYhHKMz97tQd2MSPk");
        databaseName = getEnv("DB_NAME", "market");
        collectionName = getEnv("DB_COLLECTION", "transactions");

        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(String.format(
                        "mongodb://%s:%s@%s/%s",
                        username, password, host, databaseName
                )))
                .credential(MongoCredential.createCredential(username, databaseName, password.toCharArray()))
                .build();

        client = MongoClients.create(settings);
        log.info("Connected to {} (MongoDB)", databaseName);
    }

    private DatabaseProvider() { }

    public static void save(Document transaction) {
        client.getDatabase(databaseName)
                .getCollection(collectionName)
                .insertOne(transaction);
    }

    public static String getEnv(String name, String defaultValue) {
        return Optional.ofNullable(System.getenv(name)).orElse(defaultValue);
    }
}
