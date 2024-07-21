package it.mikeslab.commons.api.database.util;

import it.mikeslab.commons.api.database.pojo.URIBuilder;
import lombok.experimental.UtilityClass;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Utility class for {@link it.mikeslab.commons.api.database.impl.SQLDatabaseImpl} operations
 */
@UtilityClass
public class SQLUtil {

    /**
     * Prepare a PreparedStatement with the given values
     * @param connection the connection
     * @param sql the SQL query
     * @param values the values to set in the PreparedStatement
     * @return the PreparedStatement
     * @throws SQLException if an error occurs
     */
    public PreparedStatement prepareStatement(Connection connection, String sql, Map<String, Object> values) throws SQLException {
        PreparedStatement pst = connection.prepareStatement(sql);
        int index = 1;
        for (Object value : values.values()) {
            pst.setObject(index++, value);
        }

        // System.out.println(sql);

        return pst;
    }

    /**
     * Get the result values from a ResultSet
     * @param rs the ResultSet
     * @return a map of the result values
     * @throws SQLException if an error occurs
     */
    public Map<String, Object> getResultValues(ResultSet rs) throws SQLException {
        Map<String, Object> resultValues = new LinkedHashMap<>();
        for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
            resultValues.put(rs.getMetaData().getColumnName(i), rs.getObject(i));
        }
        return resultValues;
    }


    public String getTableCreationQuery(String tableName, String identifier, Set<String> columns) {
        StringBuilder sb = new StringBuilder(); // todo needs a more robust logging system
        sb.append("CREATE TABLE IF NOT EXISTS ")
                .append(tableName)
                .append(" (")
                .append(identifier)
                .append(" VARCHAR(255) UNIQUE, ");
        for (String column : columns) {

            if(column == identifier) continue;

            sb.append(column)
                    .append(" ")
                    .append("VARCHAR(255)")
                    .append(", ");
        }
        sb.delete(sb.length() - 2, sb.length());
        sb.append(");");
        return sb.toString();
    }

//    public String getIndexCreationQuery(String indexName, String table, Set<String> columns) {
//        StringBuilder sb = new StringBuilder();
//        sb.append("CREATE INDEX ")
//                .append(indexName)
//                .append(" ON ")
//                .append(table)
//                .append(" (");
//        for (String column : columns) {
//
//            column = replaceUnsupportedCharacters(column);
//
//            sb.append(column)
//                    .append(", ");
//        }
//        sb.delete(sb.length() - 2, sb.length());
//        sb.append(");");
//
//        return sb.toString();
//    }

    public String getFindStatement(URIBuilder uriBuilder, Map<String, Object> values) {

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


    public String getUpsertStatement(URIBuilder uriBuilder, Map<String, Object> values, Map<String, Object> updateQueryMap, String identifierFieldName) {
        StringBuilder sb = new StringBuilder();

        sb.append(buildInsertStatement(uriBuilder.getTable(), values));
        if (!updateQueryMap.isEmpty()) {
            sb.append(buildUpdateStatement(
                    updateQueryMap,
                    identifierFieldName,
                    uriBuilder.isSqlite()
            ));
        }

        return sb.toString();
    }

    private String buildInsertStatement(String table, Map<String, Object> values) {
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO ").append(table).append(" (");
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


    private String buildUpdateStatement(Map<String, Object> updateQueryMap, String identifierFieldName, boolean isSqlite) {
        StringBuilder sb = new StringBuilder();
        sb.append(" ON ");
        sb.append(isSqlite ? " CONFLICT(" + identifierFieldName + ") DO UPDATE SET " :
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


}
