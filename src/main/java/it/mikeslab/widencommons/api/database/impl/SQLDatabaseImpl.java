package it.mikeslab.widencommons.api.database.impl;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import it.mikeslab.widencommons.api.database.Database;
import it.mikeslab.widencommons.api.database.SerializableMapConvertible;
import it.mikeslab.widencommons.api.database.pojo.URIBuilder;
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

        if(uriBuilder.getUsername() != null) config.setUsername(uriBuilder.getUsername());
        if(uriBuilder.getPassword() != null) config.setPassword(uriBuilder.getPassword());

        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        dataSource = new HikariDataSource(config);
    }


    @Override
    public boolean connect(Class<T> pojoClass) {
        try {
            this.connection = dataSource.getConnection();

            if (!tableExists()) {
                List<String> fields = Arrays.stream(pojoClass.getDeclaredFields())
                        .map(field -> field.getName())
                        .toList();

                createTable(fields);
            }

            return true;
        } catch (Exception e) {
            LoggerUtil.log(
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
                    Level.SEVERE,
                    LoggerUtil.LogSource.DATABASE,
                    e
            );
            return false;
        }
    }

    @Override
    public T get(T pojoClass) {

        // Map that contains fields and their values of the pojo object
        Map<String, Object> values = new HashMap<>();

        // check if the entry exists
        // if it does, return the pojo object

        try {
            PreparedStatement pst = connection.prepareStatement(
                    "SELECT * FROM " + uriBuilder.getTable()
                            + " WHERE " + pojoClass.getIdentifierName() + "=" + pojoClass.getIdentifierValue()
            );
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                for(int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                    values.put(rs.getMetaData().getColumnName(i), rs.getObject(i));
                }
            }

        } catch (Exception e) {
            LoggerUtil.log(
                    Level.SEVERE,
                    LoggerUtil.LogSource.DATABASE,
                    e
            );
        }

        return pojoClass.fromMap(values);
    }



    @Override
    public boolean upsert(T pojoObject) {

        Map<String, Object> values = pojoObject.toMap();

        // check if the entry exists
        // if it does, update the entry
        // if it doesn't, insert the entry
        try {

            boolean exists = find(pojoObject) != null;

            // If the entry exists, update it, otherwise insert it
            // Passing the parameter pojoObject is needed to get
            // both the identifier name and value
            if (exists) {
                this.update(pojoObject, values); // Returns the ID
            } else {
                this.insert(pojoObject, values); // Returns the ID
            }

        } catch (Exception e) {
            LoggerUtil.log(
                    Level.SEVERE,
                    LoggerUtil.LogSource.DATABASE,
                    e
            );

            return false;
        }


        return true;
    }

    @Override
    public boolean delete(T pojoObject) {

        try {
            PreparedStatement pst = connection.prepareStatement("DELETE FROM " + uriBuilder.getTable()
                    + " WHERE " + pojoObject.getIdentifierName() + " = " + pojoObject.getIdentifierValue()
            );
            return pst.executeUpdate() > 0;
        } catch (Exception e) {
            LoggerUtil.log(
                    Level.SEVERE,
                    LoggerUtil.LogSource.DATABASE,
                    e
            );
        }

        return false;
    }

    @Override
    public T find(T pojoObject) {

        Map<String, Object> values = pojoObject.toMap();

        // check if the entry exists
        // if it does, return the pojo object

        try {
            // Convert the map to a list of "key = value" format
            List<String> keyValuePairs = new ArrayList<>();
            for (Map.Entry<String, Object> entry : values.entrySet()) {
                keyValuePairs.add(entry.getKey() + " = '" + entry.getValue() + "'");
            }

            // Join the list into a single string with an AND separation
            String whereClause = String.join(" AND ", keyValuePairs);

            PreparedStatement pst = connection.prepareStatement("SELECT * FROM " + uriBuilder.getTable() + " WHERE " + whereClause);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                Map<String, Object> resultValues = new HashMap<>();
                for(int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                    resultValues.put(rs.getMetaData().getColumnName(i), rs.getObject(i));
                }

                return pojoObject.fromMap(resultValues);
            }

        } catch (Exception e) {
            LoggerUtil.log(
                    Level.SEVERE,
                    LoggerUtil.LogSource.DATABASE,
                    e
            );
        }

        return null;

    }

    @Deprecated(forRemoval = true)
    private int getNextId() {

        try {
            PreparedStatement pst = connection.prepareStatement("SELECT MAX(id) FROM " + uriBuilder.getTable());
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) + 1;
            }

        } catch (Exception e) {
            LoggerUtil.log(
                    Level.SEVERE,
                    LoggerUtil.LogSource.DATABASE,
                    e
            );
        }

        return -1;

    }

    private int update(T pojoObject, Map<String, Object> values) {

        try {
            // Convert the map to a list of "key = value" format
            List<String> keyValuePairs = new ArrayList<>();
            for (Map.Entry<String, Object> entry : values.entrySet()) {
                keyValuePairs.add(entry.getKey() + " = " + entry.getValue());
            }

            // Join the list into a single string with a comma separation
            String setClause = String.join(", ", keyValuePairs);

            // Prepared statement to update the entry
            PreparedStatement updatePst = connection.prepareStatement("UPDATE " + uriBuilder.getTable() + " SET " + setClause
                    + " WHERE " + pojoObject.getIdentifierName() + " = " + pojoObject.getIdentifierValue()
            );

            return updatePst.executeUpdate();
        } catch (Exception e) {
            LoggerUtil.log(
                    Level.SEVERE,
                    LoggerUtil.LogSource.DATABASE,
                    e
            );
        }

        return -1;
    }


    private boolean insert(T pojoObject, Map<String, Object> values) {

        try {
            // Convert the map to a list of keys
            List<String> keys = new ArrayList<>(values.keySet());

            // Convert the map to a list of values
            List<Object> valuesList = new ArrayList<>(values.values());

            // Prepare the query
            StringBuilder query = new StringBuilder("INSERT INTO " + uriBuilder.getTable() + " (");
            query.append(pojoObject.getIdentifierName());
            query.append(", ");
            query.append(String.join(", ", keys));
            query.append(") VALUES (?");
            query.append(", ?".repeat(valuesList.size()));

            query.append(")");

            // Prepared statement to insert the entry
            PreparedStatement insertPst = connection.prepareStatement(query.toString());
            insertPst.setObject(1, pojoObject.getIdentifierValue());
            for (int i = 0; i < valuesList.size(); i++) {

                // Skips the first value because it's the id
                insertPst.setObject(i + 2, valuesList.get(i));

            }

            return insertPst.executeUpdate() > 0;
        } catch (Exception e) {
            LoggerUtil.log(
                    Level.SEVERE,
                    LoggerUtil.LogSource.DATABASE,
                    e
            );
        }

        return false;
    }


    private boolean tableExists() {
        try {
            PreparedStatement pst = connection.prepareStatement("SELECT * FROM " + uriBuilder.getTable());
            pst.executeQuery();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean createTable(List<String> fields) {
        try {

            StringBuilder query = new StringBuilder("CREATE TABLE " + uriBuilder.getTable() + " (id INT PRIMARY KEY, ");

            for(int i = 0; i < fields.size(); i++) {
                query.append(fields.get(i));
                query.append(" TEXT");
                if(i != fields.size() - 1) query.append(", ");
            }


            query.append(")");

            PreparedStatement pst = connection.prepareStatement(query.toString());
            pst.executeUpdate();
            return true;
        } catch (Exception e) {
            LoggerUtil.log(
                    Level.SEVERE,
                    LoggerUtil.LogSource.DATABASE,
                    e
            );
            return false;
        }
    }


}
