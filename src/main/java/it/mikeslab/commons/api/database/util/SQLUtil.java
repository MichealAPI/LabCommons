package it.mikeslab.commons.api.database.util;

import lombok.experimental.UtilityClass;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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


    public String getTableCreationQuery(String tableName, List<String> columns) {
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE IF NOT EXISTS ")
                .append(tableName)
                .append(" (");
        for (String column : columns) {
            sb.append(column)
                    .append(" ")
                    .append("VARCHAR(255)")
                    .append(", ");
        }
        sb.delete(sb.length() - 2, sb.length());
        sb.append(");");
        return sb.toString();
    }

    public String getIndexCreationQuery(String indexName, String table, List<String> columns) {
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE INDEX IF NOT EXISTS ")
                .append(indexName)
                .append(" ON ")
                .append(table)
                .append(" (");
        for (String column : columns) {
            sb.append(column)
                    .append(", ");
        }
        sb.delete(sb.length() - 2, sb.length());
        sb.append(");");
        return sb.toString();
    }

}
