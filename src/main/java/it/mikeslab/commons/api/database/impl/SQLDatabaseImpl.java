package it.mikeslab.commons.api.database.impl;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import it.mikeslab.commons.api.database.Database;
import it.mikeslab.commons.api.database.SerializableMapConvertible;
import it.mikeslab.commons.api.database.pojo.URIBuilder;
import it.mikeslab.commons.api.database.util.SQLUtil;
import it.mikeslab.commons.api.logger.LogUtils;
import org.bson.Document;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;
import java.util.stream.Collectors;

public class SQLDatabaseImpl<T extends SerializableMapConvertible<T>> implements Database<T> {

    private final URIBuilder uriBuilder;
    private final HikariDataSource dataSource;
    private Connection connection;

    private List<String> fields;

    public SQLDatabaseImpl(URIBuilder uriBuilder) {
        this.uriBuilder = uriBuilder;

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(uriBuilder.getUri());

        if (uriBuilder.getUsername() != null) config.setUsername(uriBuilder.getUsername());
        if (uriBuilder.getPassword() != null) config.setPassword(uriBuilder.getPassword());

        // Databases are handled exclusively through the properties usage

        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        dataSource = new HikariDataSource(config);
    }


    @Override
    public boolean connect(T pojoObject) {
        try {
            this.connection = dataSource.getConnection();

            Class<T> pojoClass = (Class<T>) pojoObject.getClass();

            if (!tableExists()) {
                this.fields = Arrays.stream(pojoClass.getDeclaredFields())
                        .map(Field::getName)
                        .collect(Collectors.toList());

                this.createTableIfNotExists();

                this.createIndexesIfNotExists(
                        pojoObject.getIdentifierName()
                );
            }

            return true;
        } catch (Exception e) {
            LogUtils.severe(
                    LogUtils.LogSource.DATABASE,
                    e
            );
            return false;
        }

    }

    @Override
    public boolean isConnected() {
        return dataSource.isRunning();
    }

    @Override
    public boolean disconnect() {

        try {
            dataSource.close();
            return true;
        } catch (Exception e) {
            LogUtils.severe(
                    LogUtils.LogSource.DATABASE,
                    e
            );
            return false;
        }
    }

    /**
     * Upsert an object into the database
     * @param pojoObject the object to insert
     * @return true if the object is upserted, false otherwise
     */
    @Override
    public boolean upsert(T pojoObject) {
        Map<String, Object> values = pojoObject.toMap();

        // A Map object for Updating, without Identifier
        Map<String, Object> updateQueryMap = new HashMap<>(values);
        updateQueryMap.remove(pojoObject.getIdentifierName());

        try (PreparedStatement pst = SQLUtil.prepareStatement(connection, getUpsertStatement(values, updateQueryMap, pojoObject.getIdentifierName()), values)) {
            int index = values.size() + 1;
            if (!updateQueryMap.isEmpty()) {
                for (Object value : updateQueryMap.values()) {
                    pst.setObject(index++, value);
                }
            }

            return pst.executeUpdate() > 0;
        } catch (Exception e) {
            LogUtils.severe(
                    LogUtils.LogSource.DATABASE,
                    e
            );
        }

        return false;
    }


    /**
     * Delete an object from the database
     * @param pojoObject the object to delete
     * @return true if the object is deleted, false otherwise
     */
    @Override
    public boolean delete(T pojoObject) {
        String sql = "DELETE FROM " + uriBuilder.getTable()
                + " WHERE " + pojoObject.getIdentifierName() + " = ?";

        Map<String, Object> values = Collections.singletonMap(
                pojoObject.getIdentifierName(),
                pojoObject.getIdentifierValue()
        );

        try (PreparedStatement pst = SQLUtil.prepareStatement(connection, sql, values)) {
            return pst.executeUpdate() > 0;
        } catch (Exception e) {
            LogUtils.severe(
                    LogUtils.LogSource.DATABASE,
                    e
            );
        }

        return false;
    }

    /**
     * Find an object in the database
     * @param pojoObject similar to the object to find
     * @return the object if it exists, null otherwise
     */
    @Override
    public T findOne(T pojoObject) {
        return findMany(pojoObject)
                .stream()
                .findFirst()
                .orElse(null);
    }



    @Override
    public List<T> findMany(T pojoObject) {

        Document filterDocument = new Document(pojoObject.toMap());

        return this.findDocuments(filterDocument)
                .stream()
                .map(pojoObject::fromMap)
                .collect(Collectors.toList());
    }

    @Override
    public Document findDocument(Document document) {
        return findDocuments(document)
                .stream()
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Document> findDocuments(Document document) {

        List<Document> foundDocuments = new ArrayList<>();

        try (PreparedStatement pst = SQLUtil.prepareStatement(connection, getFindStatement(document), document)) {
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    foundDocuments.add(new Document(SQLUtil.getResultValues(rs)));
                }
            }
        } catch (Exception e) {
            LogUtils.severe(
                    LogUtils.LogSource.DATABASE,
                    e
            );
        }

        return foundDocuments;

    }


    /**
     * Check if the table exists in the database
     * @return true if the table exists, false otherwise
     */
    private boolean tableExists() {

        String sql = "SELECT * FROM " + uriBuilder.getTable();

        try(PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.executeQuery();
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    /**
     * Create a table in the database
     */
    private void createTableIfNotExists() {

        String sql = SQLUtil.getTableCreationQuery(
                uriBuilder.getTable(),
                fields
        );

        try (PreparedStatement pst = SQLUtil.prepareStatement(connection, sql, Collections.emptyMap())) {
            pst.executeUpdate();
        } catch (Exception e) {
            LogUtils.severe(
                    LogUtils.LogSource.DATABASE,
                    e
            );
        }
    }


    private String getFindStatement(Map<String, Object> values) {

        // Convert the map to a list of "key = ?" format
        StringBuilder whereClause = new StringBuilder();
        for (String key : values.keySet()) {
            if (whereClause.length() > 0) {
                whereClause.append(" AND ");
            }
            whereClause.append(key).append(" = ?");

        }

        return "SELECT * FROM " + uriBuilder.getTable() + " WHERE " + whereClause;

    }


    private String getUpsertStatement(Map<String, Object> values, Map<String, Object> updateQueryMap, String identifierFieldName) {
        StringBuilder sb = new StringBuilder();

        sb.append(buildInsertStatement(values));
        if (!updateQueryMap.isEmpty()) {
            sb.append(buildUpdateStatement(updateQueryMap, identifierFieldName));
        }

        return sb.toString();
    }

    private String buildInsertStatement(Map<String, Object> values) {
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO ").append(uriBuilder.getTable()).append(" (");
        sb.append(String.join(", ", values.keySet()));
        sb.append(") VALUES (");

        sb.append("?");

        if (values.size() > 1) {
            for(int i = 1; i < values.size(); i++) {
                sb.append(", ?");
            }
        }

        sb.append(")");
        return sb.toString();
    }

    private String buildUpdateStatement(Map<String, Object> updateQueryMap, String identifierFieldName) {
        StringBuilder sb = new StringBuilder();
        sb.append(" ON ");
        sb.append(uriBuilder.isSqlite() ? " CONFLICT(" + identifierFieldName + ") DO UPDATE SET " :
                " DUPLICATE KEY UPDATE ");

        int i = 0;
        for (Map.Entry<String, Object> entry : updateQueryMap.entrySet()) {
            sb.append(entry.getKey()).append(" = ?");
            if (i++ < updateQueryMap.size() - 1) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }


    private void createIndexesIfNotExists(String identifierName) {

        if(fields == null || fields.isEmpty()) {
            return;
        }

        String sql = SQLUtil.getIndexCreationQuery(
                identifierName,
                uriBuilder.getTable(),
                this.fields
        );

        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.executeUpdate();
        } catch (Exception e) {
            LogUtils.severe(
                    LogUtils.LogSource.DATABASE,
                    e
            );
        }
    }


}
