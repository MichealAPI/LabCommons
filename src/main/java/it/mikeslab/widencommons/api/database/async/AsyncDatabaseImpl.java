package it.mikeslab.widencommons.api.database.async;

import it.mikeslab.widencommons.api.database.Database;
import it.mikeslab.widencommons.api.database.pojo.RetrievedEntry;
import lombok.RequiredArgsConstructor;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
public class AsyncDatabaseImpl<T> implements AsyncDatabase<T> {

    private final Database<T> syncDatabase;

    @Override
    public CompletableFuture<Boolean> connect(Class<?> pojoClass) {
        return CompletableFuture.supplyAsync(() -> syncDatabase.connect(pojoClass));
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
    public CompletableFuture<T> get(int id, Class<T> pojoClass) {
        return CompletableFuture.supplyAsync(() -> syncDatabase.get(id, pojoClass));
    }

    @Override
    public CompletableFuture<Integer> upsert(Optional<Integer> id, Object pojoObject) {
        return CompletableFuture.supplyAsync(() -> syncDatabase.upsert(id, pojoObject));
    }

    @Override
    public CompletableFuture<Boolean> delete(int id) {
        return CompletableFuture.supplyAsync(() -> syncDatabase.delete(id));
    }

    @Override
    public CompletableFuture<RetrievedEntry> find(Object pojoObject) {
        return CompletableFuture.supplyAsync(() -> syncDatabase.find(pojoObject));
    }
}
