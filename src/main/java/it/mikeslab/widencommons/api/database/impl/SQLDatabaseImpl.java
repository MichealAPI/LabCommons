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
import java.sql.SQLException;
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

    /**
     * Get a pojo object from the database
     * @param pojoClass the pojoObject
     * @return the object if it exists, null otherwise
     */
    @Override
    public T get(T pojoClass) {

        // Map that contains fields and their values of the pojo object
        Map<String, Object> values = new HashMap<>();

        // check if the entry exists
        // if it does, return the pojo object

        PreparedStatement pst = null;

        try {
            pst = connection.prepareStatement(
                    "SELECT * FROM " + uriBuilder.getTable()
                            + " WHERE " + pojoClass.getIdentifierName() + " = '" + pojoClass.getIdentifierValue() + "'"
            );
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                    values.put(rs.getMetaData().getColumnName(i), rs.getObject(i));
                }
            }

        } catch (Exception e) {
            LoggerUtil.log(
                    Level.SEVERE,
                    LoggerUtil.LogSource.DATABASE,
                    e
            );
        } finally {

            try {

                if(pst != null) {
                    pst.close();
                }

            } catch (SQLException ex) {
                LoggerUtil.log(
                        Level.SEVERE,
                        LoggerUtil.LogSource.DATABASE,
                        ex
                );
            }

        }

        return pojoClass.fromMap(values);
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

        PreparedStatement pst = null;

        try {


            pst = connection.prepareStatement(
                    getUpsertStatement(values, updateQueryMap)
            );

            int index = 1;
            for (Object value : values.values()) {
                pst.setObject(index++, value);
            }

            if (!updateQueryMap.isEmpty()) {
                for (Object value : updateQueryMap.values()) {
                    pst.setObject(index++, value);
                }
            }

            return pst.executeUpdate() > 0;
        } catch (Exception e) {
            LoggerUtil.log(
                    Level.SEVERE,
                    LoggerUtil.LogSource.DATABASE,
                    e
            );
        } finally {

            try {

                if(pst != null) {
                    pst.close();
                }

            } catch (SQLException ex) {
                LoggerUtil.log(
                        Level.SEVERE,
                        LoggerUtil.LogSource.DATABASE,
                        ex
                );
            }


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

        PreparedStatement pst = null;

        try {
            pst = connection.prepareStatement("DELETE FROM " + uriBuilder.getTable()
                    + " WHERE " + pojoObject.getIdentifierName() + " = ?");
            pst.setObject(1, pojoObject.getIdentifierValue());
            return pst.executeUpdate() > 0;
        } catch (Exception e) {
            LoggerUtil.log(
                    Level.SEVERE,
                    LoggerUtil.LogSource.DATABASE,
                    e
            );
        } finally {

            try {
                if(pst != null) {
                    pst.close();
                }
            } catch (SQLException ex) {
                LoggerUtil.log(
                        Level.SEVERE,
                        LoggerUtil.LogSource.DATABASE,
                        ex
                );
            }

        }

        return false;
    }

    /**
     * Find an object in the database
     * @param pojoObject similar to the object to find
     * @return the object if it exists, null otherwise
     */
    @Override
    public T find(T pojoObject) {

        Map<String, Object> values = pojoObject.toMap();
        PreparedStatement pst = null;
        ResultSet rs = null;

        try {

            pst = connection.prepareStatement(
                    getFindStatement(values)
            );

            // Set the values in the prepared statement
            int index = 1;
            for (Object value : values.values()) {
                pst.setObject(index++, value);
            }

            rs = pst.executeQuery();

            if (rs.next()) {
                Map<String, Object> resultValues = new LinkedHashMap<>();
                for (int i = 1; i < rs.getMetaData().getColumnCount() + 1; i++) {
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
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    LoggerUtil.log(
                            Level.SEVERE,
                            LoggerUtil.LogSource.DATABASE,
                            e
                    );
                }
            }
            if (pst != null) {
                try {
                    pst.close();
                } catch (SQLException e) {
                    LoggerUtil.log(
                            Level.SEVERE,
                            LoggerUtil.LogSource.DATABASE,
                            e
                    );
                }
            }
        }

        return null;

    }

    /**
     * Check if the table exists in the database
     * @return true if the table exists, false otherwise
     */
    private boolean tableExists() {

        PreparedStatement pst = null;

        try {
            pst = connection.prepareStatement("SELECT * FROM " + uriBuilder.getTable());
            pst.executeQuery();
            return true;
        } catch (Exception e) {
            return false;

        } finally {

            try {
                if (pst != null) {
                    pst.close();
                }
            } catch (SQLException ex) {
                LoggerUtil.log(
                        Level.SEVERE,
                        LoggerUtil.LogSource.DATABASE,
                        ex
                );

            }

        }
    }


    /**
     * Create a table in the database
     * @param fields List of fields
     * @param pojoObject Pojo object from which fields are taken
     * @return true if the table is created, false otherwise
     */
    private boolean createTable(List<String> fields, T pojoObject) {

        PreparedStatement pst = null;

        try {
            // Limit the length of the fixed id field
            // MySQL doesn't support unique indexes with variable length
            final int fixedIdFieldLengthLimit = 100;

            StringBuilder query = new StringBuilder("CREATE TABLE " + uriBuilder.getTable() + " (" + pojoObject.getIdentifierName() + " VARCHAR(" + fixedIdFieldLengthLimit + ") PRIMARY KEY, ");
            List<String> fieldsClone = new ArrayList<>(fields);
            fieldsClone.remove(pojoObject.getIdentifierName());

            for (int i = 0; i < fieldsClone.size(); i++) {
                query.append(fieldsClone.get(i));
                query.append(" TEXT");
                if (i != fieldsClone.size() - 1) query.append(", ");
            }

            query.append(")");
            pst = connection.prepareStatement(query.toString());
            pst.executeUpdate();
            return true;
        } catch (Exception e) {
            LoggerUtil.log(
                    Level.SEVERE,
                    LoggerUtil.LogSource.DATABASE,
                    e
            );
            return false;
        } finally {

            try {
                if(pst != null) {
                    pst.close();
                }
            } catch (Exception e) {
                LoggerUtil.log(
                        Level.SEVERE,
                        LoggerUtil.LogSource.DATABASE,
                        e
                );
            }



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
