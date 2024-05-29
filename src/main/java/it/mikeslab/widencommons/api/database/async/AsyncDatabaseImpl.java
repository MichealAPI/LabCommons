package it.mikeslab.widencommons.api.database.async;

import it.mikeslab.widencommons.api.database.Database;
import it.mikeslab.widencommons.api.database.SerializableMapConvertible;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bson.Document;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@Getter
@RequiredArgsConstructor
public class AsyncDatabaseImpl<T extends SerializableMapConvertible<T>> implements AsyncDatabase<T> {

    private final Database<T> syncDatabase;

    @Override
    public CompletableFuture<Boolean> connect(T pojoObject) {
        return CompletableFuture.supplyAsync(() -> syncDatabase.connect(pojoObject));
    }

    @Override
    public CompletableFuture<Boolean> isConnected() {
        return CompletableFuture.supplyAsync(syncDatabase::isConnected);
    }

    @Override
    public CompletableFuture<Boolean> disconnect() {
        return CompletableFuture.supplyAsync(syncDatabase::disconnect);
    }

    @Override
    public CompletableFuture<Boolean> upsert(T pojoObject) {
        return CompletableFuture.supplyAsync(() -> syncDatabase.upsert(pojoObject));
    }

    @Override
    public CompletableFuture<Boolean> delete(T pojoObject) {
        return CompletableFuture.supplyAsync(() -> syncDatabase.delete(pojoObject));
    }

    @Override
    public CompletableFuture<T> findOne(T pojoObject) {
        return CompletableFuture.supplyAsync(() -> syncDatabase.findOne(pojoObject));
    }

    @Override
    public CompletableFuture<List<T>> findMany(T pojoObject) {
        return CompletableFuture.supplyAsync(() -> syncDatabase.findMany(pojoObject));
    }

    @Override
    public CompletableFuture<Document> findDocument(Document document) {
        return CompletableFuture.supplyAsync(() -> syncDatabase.findDocument(document));
    }

    @Override
    public CompletableFuture<List<Document>> findDocuments(Document document) {
        return CompletableFuture.supplyAsync(() -> syncDatabase.findDocuments(document));
    }
}
