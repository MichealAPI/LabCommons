package it.mikeslab.widencommons.api.database.impl;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.model.UpdateOptions;
import it.mikeslab.widencommons.WidenCommons;
import it.mikeslab.widencommons.api.database.Database;
import it.mikeslab.widencommons.api.database.SerializableMapConvertible;
import it.mikeslab.widencommons.api.database.pojo.URIBuilder;
import it.mikeslab.widencommons.api.logger.LoggerUtil;
import lombok.RequiredArgsConstructor;
import org.bson.BsonDocument;
import org.bson.BsonInt64;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

@RequiredArgsConstructor
public class MongoDatabaseImpl<T extends SerializableMapConvertible<T>> implements Database<T> {

    private final URIBuilder uriBuilder;
    private MongoClient mongoClient;
    private MongoDatabase mongoDatabase;


    @Override
    public boolean connect(T pojoObject) {
        // Construct a ServerApi instance using the ServerApi.builder() method
        ServerApi serverApi = ServerApi.builder()
                .version(ServerApiVersion.V1)
                .build();

        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(uriBuilder.getUri()))
                .serverApi(serverApi)
                .build();

        // Create a new client and connect to the server
        mongoClient = MongoClients.create(settings);
        mongoDatabase = mongoClient.getDatabase(uriBuilder.getDatabase());

        // Create an index on the identifier field
        mongoDatabase.getCollection(uriBuilder.getTable())
                .createIndex(Indexes.text(pojoObject.getIdentifierName()));

        return isConnected();
    }

    @Override
    public boolean isConnected() {

        try {
            // Send a ping to confirm a successful connection
            Bson command = new BsonDocument("ping", new BsonInt64(1));
            Document commandResult = mongoDatabase.runCommand(command);
            return true;
        } catch (Exception e) {
            return false;
        }

    }

    @Override
    public boolean disconnect() {
        mongoClient.close();
        mongoClient = null;

        return !isConnected();
    }



    @Override
    public boolean upsert(T pojoObject) {

        try {

            Map<String, Object> values = pojoObject.toMap();

            Document document = new Document(values);

            UpdateOptions updateOptions = new UpdateOptions().upsert(true);
            mongoDatabase.getCollection(uriBuilder.getTable())
                    .updateOne(
                            generateIdFilter(pojoObject),
                            new Document("$set", document),
                            updateOptions
                    );

        } catch (Exception e) {

            LoggerUtil.log(
                    WidenCommons.PLUGIN_NAME,
                    Level.WARNING,
                    LoggerUtil.LogSource.DATABASE,
                    "Error during upsert: " + e
            );

            return false;
        }


        return true;
    }

    @Override
    public T findOne(T pojoObject) {
        return this.findMany(pojoObject)
                .stream()
                .findFirst()
                .orElse(null);
    }


    @Override
    public Set<T> findMany(T pojoObject) {
        Map<String, Object> values = pojoObject.toMap();

        Document document = new Document(values);

        FindIterable<Document> resultDocument = mongoDatabase
                .getCollection(uriBuilder.getTable())
                .find(document);

        Set<T> pojoObjects = new HashSet<>();

        if(resultDocument.first() == null) return pojoObjects; // Return empty set if no results

        for(Document doc : resultDocument) {
            pojoObject.fromMap(doc);
            pojoObjects.add(pojoObject);
        }

        return pojoObjects;
    }


    @Override
    public boolean delete(T pojoObject) {

        try {
            Bson filter = generateIdFilter(pojoObject);

            mongoDatabase.getCollection(uriBuilder.getTable())
                    .deleteOne(filter);

        } catch (Exception e) {

            LoggerUtil.log(
                    WidenCommons.PLUGIN_NAME,
                    Level.WARNING,
                    LoggerUtil.LogSource.DATABASE,
                    "Error during delete: " + e);

            return false;
        }

        return true;
    }


    private Bson generateIdFilter(T pojoObject) {
        return Filters.eq(
                pojoObject.getIdentifierName(),
                pojoObject.getIdentifierValue()
        );
    }

}
