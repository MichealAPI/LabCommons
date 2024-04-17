package it.mikeslab.widencommons.api.database.impl;

import com.google.common.base.Stopwatch;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import it.mikeslab.widencommons.api.database.Database;
import it.mikeslab.widencommons.api.database.SerializableMapConvertible;
import it.mikeslab.widencommons.api.database.pojo.RetrievedEntry;
import it.mikeslab.widencommons.api.database.pojo.URIBuilder;
import it.mikeslab.widencommons.api.database.util.PojoMapper;
import lombok.RequiredArgsConstructor;
import org.bson.BsonDocument;
import org.bson.BsonInt64;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
public class MongoDatabaseImpl<T extends SerializableMapConvertible<T>> implements Database<T> {

    private final URIBuilder uriBuilder;
    private MongoClient mongoClient;
    private MongoDatabase mongoDatabase;


    @Override
    public boolean connect(Class<T> pojoClass) {
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
    public T get(int id, T pojoClass) {

        Bson filter = Filters.eq("id", id);

        Document resultDocument = mongoDatabase
                .getCollection(uriBuilder.getTable())
                .find(filter)
                .first();

        if(resultDocument == null) return null;

        // todo Remove? Does this still useful?
        resultDocument.remove("_id"); // MongoDB's internal id
        resultDocument.remove("id"); // The id is not needed in the POJO

        return pojoClass.fromMap(resultDocument);
    }



    @Override
    public int upsert(Optional<Integer> id, T pojoObject) {

        Stopwatch stopwatch = Stopwatch.createStarted();
        Map<String, Object> values = pojoObject.toMap();
        stopwatch.stop();
        System.out.println("To map took " + stopwatch.elapsed().toMillis() + " milliseconds");

        Document document = new Document(values);

        int finalId = id.orElseGet(this::getNextId); // Get the next id if it is not present

        UpdateOptions updateOptions = new UpdateOptions().upsert(true);
        mongoDatabase.getCollection(uriBuilder.getTable())
                .updateOne(
                        Filters.eq("id", finalId),
                        new Document("$set", document),
                        updateOptions
                );


        return finalId;
    }

    @Override
    public RetrievedEntry find(T pojoObject) {

        Map<String, Object> values = pojoObject.toMap();

        Document document = new Document(values);

        Document resultDocument = mongoDatabase
                .getCollection(uriBuilder.getTable())
                .find(document)
                .first();

        if(resultDocument == null) return null;

        int id = resultDocument.getInteger("id");

        resultDocument.remove("_id"); // MongoDB's internal id
        resultDocument.remove("id"); // The id is not needed in the POJO

        return new RetrievedEntry(
                id,
                pojoObject.fromMap(resultDocument)
        );

    }


    @Override
    public boolean delete(int id) {
        Bson filter = Filters.eq("id", id);

        mongoDatabase.getCollection(uriBuilder.getTable())
                .deleteOne(filter);

        return true;
    }

    private int getNextId() {
        // Performance optimization may be needed
        Document document = mongoDatabase.getCollection(uriBuilder.getTable())
                .find()
                .sort(new Document("id", -1))
                .limit(1)
                .first();

        if(document == null) return 1;

        return document.getInteger("id") + 1;
    }

}
