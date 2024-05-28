package it.mikeslab.widencommons.api.database.impl;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import it.mikeslab.widencommons.WidenCommons;
import it.mikeslab.widencommons.api.database.Database;
import it.mikeslab.widencommons.api.database.SerializableMapConvertible;
import it.mikeslab.widencommons.api.database.pojo.URIBuilder;
import it.mikeslab.widencommons.api.database.util.SQLUtil;
import it.mikeslab.widencommons.api.logger.LoggerUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;
import java.util.logging.Level;

public class SQLDatabaseImpl<T extends SerializableMapConvertible<T>> implements Database<T> {

    private final URIBuilder uriBuilder;
    private final HikariDataSource dataSource;
    private Connection connection;

    public SQLDatabaseImpl(URIBuilder uriBuilder) {
        this.uriBuilder = uriBuilder;

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(uriBuilder.getUri());

        if (uriBuilder.getUsername() != null) config.setUsername(uriBuilder.getUsername());
        if (uriBuilder.getPassword() != null) config.setPassword(uriBuilder.getPassword());

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
                List<String> fields = Arrays.stream(pojoClass.getDeclaredFields())
                        .map(field -> field.getName())
                        .toList();

                createTable(
                        fields,
                        pojoObject
                );
            }

            return true;
        } catch (Exception e) {
            LoggerUtil.log(
                    WidenCommons.PLUGIN_NAME,
                    Level.SEVERE,
                    LoggerUtil.LogSource.DATABASE,
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
            LoggerUtil.log(
                    WidenCommons.PLUGIN_NAME,
                    Level.SEVERE,
                    LoggerUtil.LogSource.DATABASE,
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

        try (PreparedStatement pst = SQLUtil.prepareStatement(connection, getUpsertStatement(values, updateQueryMap), values)) {
            int index = values.size() + 1;
            if (!updateQueryMap.isEmpty()) {
                for (Object value : updateQueryMap.values()) {
                    pst.setObject(index++, value);
                }
            }

            return pst.executeUpdate() > 0;
        } catch (Exception e) {
            LoggerUtil.log(
                    WidenCommons.PLUGIN_NAME,
                    Level.SEVERE,
                    LoggerUtil.LogSource.DATABASE,
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
            LoggerUtil.log(
                    WidenCommons.PLUGIN_NAME,
                    Level.SEVERE,
                    LoggerUtil.LogSource.DATABASE,
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

        Map<String, Object> values = pojoObject.toMap();
        List<T> foundPojoObjects = new ArrayList<>();

        try (PreparedStatement pst = SQLUtil.prepareStatement(connection, getFindStatement(values), values)) {
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    foundPojoObjects.add(
                            pojoObject.fromMap(SQLUtil.getResultValues(rs))
                    );
                }
            }
        } catch (Exception e) {
            LoggerUtil.log(
                    WidenCommons.PLUGIN_NAME,
                    Level.SEVERE,
                    LoggerUtil.LogSource.DATABASE,
                    e
            );
        }

        return foundPojoObjects;
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
     * @param fields List of fields
     * @param pojoObject Pojo object from which fields are taken
     * @return true if the table is created, false otherwise
     */
    private boolean createTable(List<String> fields, T pojoObject) {
        // Limit the length of the fixed id field
        // MySQL doesn't support unique indexes with variable length
        final int fixedIdFieldLengthLimit = 100;

        StringBuilder query = new StringBuilder(
                "CREATE TABLE " + uriBuilder.getTable()
                        + " (" + pojoObject.getIdentifierName() + " VARCHAR("
                        + fixedIdFieldLengthLimit + ") PRIMARY KEY, "
        );
        List<String> fieldsClone = new ArrayList<>(fields);
        fieldsClone.remove(pojoObject.getIdentifierName());

        for (int i = 0; i < fieldsClone.size(); i++) {
            query.append(fieldsClone.get(i));
            query.append(" TEXT");
            if (i != fieldsClone.size() - 1) query.append(", ");
        }

        query.append(")");

        try (PreparedStatement pst = SQLUtil.prepareStatement(connection, query.toString(), Collections.emptyMap())) {
            return pst.executeUpdate() > 0;
        } catch (Exception e) {
            LoggerUtil.log(
                    WidenCommons.PLUGIN_NAME,
                    Level.SEVERE,
                    LoggerUtil.LogSource.DATABASE,
                    e
            );
            return false;
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


    private String getUpsertStatement(Map<String, Object> values, Map<String, Object> updateQueryMap) {

        StringBuilder sb = new StringBuilder();

        sb.append("INSERT INTO ").append(uriBuilder.getTable()).append(" (");
        sb.append(String.join(", ", values.keySet()));
        sb.append(") VALUES (");

        sb.append("?");

        if (values.size() > 1) {
            sb.append(", ?".repeat(values.size() - 1));
        }

        sb.append(")");

        if (updateQueryMap.size() > 0) {
            sb.append(" ON DUPLICATE KEY UPDATE ");
            int i = 0;
            for (Map.Entry<String, Object> entry : updateQueryMap.entrySet()) {
                sb.append(entry.getKey()).append(" = ?");
                if (i++ < updateQueryMap.size() - 1) {
                    sb.append(", ");
                }
            }
        }

        return sb.toString();
    }


}
