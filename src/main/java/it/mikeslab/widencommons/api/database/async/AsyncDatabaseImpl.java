package it.mikeslab.widencommons.api.database.async;

import it.mikeslab.widencommons.api.database.Database;
import it.mikeslab.widencommons.api.database.SerializableMapConvertible;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.CompletableFuture;

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
    public CompletableFuture<T> find(T pojoObject) {
        return CompletableFuture.supplyAsync(() -> syncDatabase.find(pojoObject));
    }
}
