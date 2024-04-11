package it.mikeslab.widencommons.api.database.impl;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import it.mikeslab.widencommons.api.database.Database;
import it.mikeslab.widencommons.api.database.pojo.URIBuilder;
import it.mikeslab.widencommons.api.database.util.PojoMapper;
import it.mikeslab.widencommons.api.logger.LoggerUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;
import java.util.logging.Level;

public class SQLDatabaseImpl<T> implements Database<T> {

    private final URIBuilder uriBuilder;
    private HikariConfig config = new HikariConfig();
    private HikariDataSource dataSource;
    private Connection connection;

    public SQLDatabaseImpl(URIBuilder uriBuilder) {
        this.uriBuilder = uriBuilder;

        config.setJdbcUrl(uriBuilder.getUri());

        if(uriBuilder.getUsername() != null) config.setUsername(uriBuilder.getUsername());
        if(uriBuilder.getPassword() != null) config.setPassword(uriBuilder.getPassword());

        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        dataSource = new HikariDataSource(config);
    }


    @Override
    public boolean connect(Class<?> pojoClass) {
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
    public T get(int id, Class<T> pojoClass) {

        // Map that contains fields and their values of the pojo object
        Map<String, Object> values = new HashMap<>();

        // check if the entry exists
        // if it does, return the pojo object

        try {
            PreparedStatement pst = connection.prepareStatement("SELECT * FROM " + uriBuilder.getTable() + " WHERE id = " + id);
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

        values.remove("id");
        return PojoMapper.fromMap(values, pojoClass);
    }



    @Override
    public int upsert(Optional<Integer> id, Object pojoObject) {

        Map<String, Object> values = PojoMapper.toMap(pojoObject);

        // check if the entry exists
        // if it does, update the entry
        // if it doesn't, insert the entry
        try {

            final int finalId;

            if (id.isPresent()) {
                finalId = id.get();
                this.update(finalId, values); // Returns the ID

            } else {
                finalId = getNextId();
                this.insert(finalId, values); // Returns the ID
            }

            return finalId;

        } catch (Exception e) {
            LoggerUtil.log(
                    Level.SEVERE,
                    LoggerUtil.LogSource.DATABASE,
                    e
            );
        }


        return -1;
    }

    @Override
    public boolean delete(int id) {

        try {
            PreparedStatement pst = connection.prepareStatement("DELETE FROM " + uriBuilder.getTable() + " WHERE id = " + id);
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

    private int update(int id, Map<String, Object> values) {

        try {
            // Convert the map to a list of "key = value" format
            List<String> keyValuePairs = new ArrayList<>();
            for (Map.Entry<String, Object> entry : values.entrySet()) {
                keyValuePairs.add(entry.getKey() + " = " + entry.getValue());
            }

            // Join the list into a single string with a comma separation
            String setClause = String.join(", ", keyValuePairs);

            // Prepared statement to update the entry
            PreparedStatement updatePst = connection.prepareStatement("UPDATE " + uriBuilder.getTable() + " SET " + setClause + " WHERE id = " + id);

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


    private int insert(int id, Map<String, Object> values) {

        try {
            // Convert the map to a list of keys
            List<String> keys = new ArrayList<>(values.keySet());

            // Convert the map to a list of values
            List<Object> valuesList = new ArrayList<>(values.values());

            // Prepare the query
            StringBuilder query = new StringBuilder("INSERT INTO " + uriBuilder.getTable() + " (id, ");
            query.append(String.join(", ", keys));
            query.append(") VALUES (?");
            query.append(", ?".repeat(valuesList.size()));

            query.append(")");

            // Prepared statement to insert the entry
            PreparedStatement insertPst = connection.prepareStatement(query.toString());
            insertPst.setInt(1, id);
            for (int i = 0; i < valuesList.size(); i++) {

                // Skips the first value because it's the id
                insertPst.setObject(i + 2, valuesList.get(i));

            }

            return insertPst.executeUpdate();
        } catch (Exception e) {
            LoggerUtil.log(
                    Level.SEVERE,
                    LoggerUtil.LogSource.DATABASE,
                    e
            );
        }

        return 0;
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
            query.append(String.join(", ", fields));
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
