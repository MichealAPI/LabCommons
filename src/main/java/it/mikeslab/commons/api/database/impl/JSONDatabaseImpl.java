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
        private T typeInstance;  // To store the instance of T

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
            this.typeInstance = pojoClass;  // Store the instance of T

            LogUtils.debug(
                    LogUtils.LogSource.DATABASE,
                    "Connecting to " + uriBuilder.getUri()
            );

            if (sourceFile.exists()) {
                this.cache = readFromFile();
                this.index = cache.stream()
                        .collect(Collectors.toMap(SerializableMapConvertible::getUniqueIdentifierName, Function.identity()));
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
            this.typeInstance = null;  // Clear the instance
            return true;
        }

        @Override
        public boolean upsert(T pojoObject) {
            // Read existing data
            this.cache = readFromFile();
            this.index = cache.stream()
                    .collect(Collectors.toMap(SerializableMapConvertible::getUniqueIdentifierName, Function.identity()));

            T existing = index.get(pojoObject.getUniqueIdentifierName());
            if (existing != null) {
                cache.remove(existing);
            }
            cache.add(pojoObject);
            index.put(pojoObject.getUniqueIdentifierName(), pojoObject);
            return writeToFile(cache);
        }

        @Override
        public boolean delete(T pojoObject) {
            // Read existing data
            this.cache = readFromFile();
            this.index = cache.stream()
                    .collect(Collectors.toMap(SerializableMapConvertible::getUniqueIdentifierName, Function.identity()));

            T existing = index.get(pojoObject.getUniqueIdentifierName());
            if (existing != null) {
                cache.remove(existing);
                index.remove(pojoObject.getUniqueIdentifierName());
                return writeToFile(cache);
            }
            return false;
        }

        @Override
        public T findOne(T pojoObject) {
            return index.get(pojoObject.getUniqueIdentifierName());
        }

        @Override
        public List<T> findMany(T pojoObject) {
            return cache.stream()
                    .filter(obj -> obj.getUniqueIdentifierValue().equals(pojoObject.getUniqueIdentifierValue()))
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
            try (Reader reader = new InputStreamReader(Files.newInputStream(sourceFile.toPath()), StandardCharsets.UTF_8)) {
                Type listType = TypeToken.getParameterized(List.class, typeInstance.getClass()).getType();  // Use the instance class
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
            try (Writer writer = new OutputStreamWriter(Files.newOutputStream(sourceFile.toPath()), StandardCharsets.UTF_8)) {
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