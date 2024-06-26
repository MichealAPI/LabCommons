package it.mikeslab.commons.api.database.impl;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import it.mikeslab.commons.api.database.Database;
import it.mikeslab.commons.api.database.SerializableMapConvertible;
import it.mikeslab.commons.api.database.pojo.URIBuilder;
import it.mikeslab.commons.api.logger.LogUtils;
import org.bson.Document;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class JSONDatabaseImpl<T extends SerializableMapConvertible<T>> implements Database<T> {

    private final URIBuilder uriBuilder;
    private final Gson gson;
    private File sourceFile;

    private List<T> cache;  // in-memory cache
    private Map<Object, T> index;  // index for identifierValue

    public JSONDatabaseImpl(URIBuilder uriBuilder) {
        this.uriBuilder = uriBuilder;
        this.gson = new Gson();
        this.cache = new ArrayList<>();
        this.index = new HashMap<>();
    }

    @Override
    public boolean connect(T pojoClass) {
        String filePath = uriBuilder.getUri();
        this.sourceFile = new File(filePath);

        if (sourceFile.exists()) {
            this.cache = readFromFile();
            this.index = cache.stream().collect(Collectors.toMap(T::getIdentifierValue, Function.identity()));
            return true;
        } else {
            return createSourceFile(sourceFile);
        }

    }

    @Override
    public boolean isConnected() {
        return sourceFile != null && sourceFile.exists();
    }

    @Override
    public boolean disconnect() {
        this.sourceFile = null;
        return true;
    }

    @Override
    public boolean upsert(T pojoObject) {
        T existing = index.get(pojoObject.getIdentifierValue());
        if (existing != null) {
            cache.remove(existing);
        }
        cache.add(pojoObject);
        index.put(pojoObject.getIdentifierValue(), pojoObject);
        return writeToFile(cache);
    }

    @Override
    public boolean delete(T pojoObject) {
        T existing = index.get(pojoObject.getIdentifierValue());
        if (existing != null) {
            cache.remove(existing);
            index.remove(pojoObject.getIdentifierValue());
            return writeToFile(cache);
        }
        return false;
    }

    @Override
    public T findOne(T pojoObject) {
        return index.get(pojoObject.getIdentifierValue());
    }

    @Override
    public List<T> findMany(T pojoObject) {
        return cache.stream()
                .filter(obj -> obj.getIdentifierValue().equals(pojoObject.getIdentifierValue()))
                .collect(Collectors.toList());
    }

    @Override
    public Document findDocument(Document document) {
        return null; // todo not implemented
    }

    @Override
    public List<Document> findDocuments(Document document) {
        return Collections.emptyList(); // todo not implemented
    }

    private boolean createSourceFile(File file) {
        try {
            return file.createNewFile();
        } catch (IOException e) {

            LogUtils.warn(
                    LogUtils.LogSource.DATABASE,
                    "Error during createSourceFile: " + e
            );

            return false;
        }
    }

    private List<T> readFromFile() {
        try (Reader reader = new FileReader(sourceFile)) {
            Type listType = TypeToken.getParameterized(List.class, SerializableMapConvertible.class).getType();
            List<T> objects = gson.fromJson(reader, listType);
            return objects != null ? objects : new ArrayList<>();
        } catch (IOException e) {
            LogUtils.warn(
                    LogUtils.LogSource.DATABASE,
                    "Error during readFromFile: " + e
            );
            return new ArrayList<>();
        }
    }

    private boolean writeToFile(List<T> objects) {
        try (Writer writer = new FileWriter(sourceFile)) {
            gson.toJson(objects, writer);
            return true;
        } catch (IOException e) {
            LogUtils.warn(
                    LogUtils.LogSource.DATABASE,
                    "Error during writeToFile: " + e
            );
            return false;
        }
    }

}
