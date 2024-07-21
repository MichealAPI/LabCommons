package it.mikeslab.commons.api.database.impl;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import it.mikeslab.commons.api.database.Database;
import it.mikeslab.commons.api.database.SerializableMapConvertible;
import it.mikeslab.commons.api.database.pojo.URIBuilder;
import it.mikeslab.commons.api.database.util.SQLUtil;
import it.mikeslab.commons.api.logger.LogUtils;
import org.bson.Document;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;
import java.util.stream.Collectors;

public class SQLDatabaseImpl<T extends SerializableMapConvertible<T>> implements Database<T> {

    private final URIBuilder uriBuilder;
    private final HikariDataSource dataSource;
    private Connection connection;

    private Set<String> fields;

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

        config.setDriverClassName("com.mysql.cj.jdbc.Driver");

        dataSource = new HikariDataSource(config);
    }


    @Override
    public boolean connect(T pojoObject) {
        try {
            this.connection = dataSource.getConnection();

            if (!tableExists()) {
                this.fields = pojoObject.identifiers();

                String uniqueIdentifier = pojoObject.getUniqueIdentifierName();

                this.createTableIfNotExists(uniqueIdentifier);
                this.createIndexesIfNotExists(uniqueIdentifier);

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
        updateQueryMap.remove(pojoObject.getUniqueIdentifierName());

        try (PreparedStatement pst = SQLUtil.prepareStatement(connection, SQLUtil.getUpsertStatement(uriBuilder, values, updateQueryMap, pojoObject.getUniqueIdentifierName()), values)) {
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
                + " WHERE " + pojoObject.getUniqueIdentifierName() + " = ?";

        Map<String, Object> values = Collections.singletonMap(
                pojoObject.getUniqueIdentifierName(),
                pojoObject.getUniqueIdentifierValue()
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

        try (PreparedStatement pst = SQLUtil.prepareStatement(connection, SQLUtil.getFindStatement(uriBuilder, document), document)) {
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
    private void createTableIfNotExists(String uniqueIdentifier) {

        String sql = SQLUtil.getTableCreationQuery(
                uriBuilder.getTable(),
                uniqueIdentifier,
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
            System.out.println(pst.toString());
            pst.executeUpdate();
        } catch (Exception e) {
            LogUtils.severe(
                    LogUtils.LogSource.DATABASE,
                    e
            );
        }
    }


}
