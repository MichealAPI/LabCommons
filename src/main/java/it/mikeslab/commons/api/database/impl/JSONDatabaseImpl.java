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
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class JSONDatabaseImpl<T extends SerializableMapConvertible<T>> implements Database<T> {

    private final URIBuilder uriBuilder;
    private final Gson gson;
    private File sourceFile;
    private T typeInstance;
    private List<T> cache;
    private Map<Object, T> index;

    public JSONDatabaseImpl(URIBuilder uriBuilder) {
        this.uriBuilder = uriBuilder;
        this.gson = new Gson();
        this.cache = new ArrayList<>();
        this.index = new HashMap<>();
    }

    @Override
    public boolean connect(T pojoClass) {
        this.sourceFile = new File(uriBuilder.getUri());
        this.typeInstance = pojoClass;

        LogUtils.debug(LogUtils.LogSource.DATABASE, "Connecting to " + uriBuilder.getUri());

        if (sourceFile.exists()) {
            return loadDataFromFile();
        } else {
            return createSourceFile();
        }
    }

    @Override
    public boolean isConnected() {
        return sourceFile != null && sourceFile.exists();
    }

    @Override
    public boolean disconnect() {
        this.sourceFile = null;
        this.typeInstance = null;
        return true;
    }

    @Override
    public boolean upsert(T pojoObject) {

        T existing = index.get(pojoObject.getUniqueIdentifierValue());
        if (existing != null) {
            cache.remove(existing);
        }

        cache.add(pojoObject);
        index.put(pojoObject.getUniqueIdentifierValue(), pojoObject);
        return saveDataToFile();
    }

    @Override
    public boolean delete(T pojoObject) {
        T existing = index.get(pojoObject.getUniqueIdentifierValue());
        if (existing != null) {
            cache.remove(existing);
            index.remove(pojoObject.getUniqueIdentifierValue());
            return saveDataToFile();
        }
        return false;
    }

    @Override
    public T findOne(T pojoObject) {
        return index.get(pojoObject.getUniqueIdentifierValue());
    }

    @Override
    public List<T> findMany(T pojoObject) {
        return cache.stream()
                .filter(obj -> obj.getUniqueIdentifierValue().equals(pojoObject.getUniqueIdentifierValue()))
                .collect(Collectors.toList());
    }

    @Override
    public Document findDocument(Document document) {
        return null; // Not implemented
    }

    @Override
    public List<Document> findDocuments(Document document) {
        return Collections.emptyList(); // Not implemented
    }

    private boolean createSourceFile() {
        try {
            return sourceFile.createNewFile();
        } catch (IOException e) {
            LogUtils.warn(LogUtils.LogSource.DATABASE, "Error during createSourceFile: " + e);
            return false;
        }
    }

    private boolean loadDataFromFile() {
        try (Reader reader = new InputStreamReader(Files.newInputStream(sourceFile.toPath()), StandardCharsets.UTF_8)) {
            Type listType = TypeToken.getParameterized(List.class, typeInstance.getClass()).getType();
            List<T> objects = gson.fromJson(reader, listType);
            if (objects != null) {
                cache = objects;
                index = cache.stream()
                        .collect(
                                Collectors.toMap(
                                        SerializableMapConvertible::getUniqueIdentifierValue,
                                        Function.identity()
                                )
                        );
            }
            return true;
        } catch (IOException e) {
            LogUtils.warn(LogUtils.LogSource.DATABASE, "Error during loadDataFromFile: " + e);
            return false;
        }
    }

    private boolean saveDataToFile() {

        try (Writer writer = new OutputStreamWriter(Files.newOutputStream(sourceFile.toPath()), StandardCharsets.UTF_8)) {
            gson.toJson(cache, writer);
            return true;
        } catch (IOException e) {
            LogUtils.warn(LogUtils.LogSource.DATABASE, "Error during saveDataToFile: " + e);
            return false;
        }
    }
}