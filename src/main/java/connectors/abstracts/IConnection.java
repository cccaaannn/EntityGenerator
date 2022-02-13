package connectors.abstracts;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

public interface IConnection {
    void connect();
    void disconnect();
    void closeResultSet(ResultSet resultSet);
    Integer getResultSetSize(ResultSet resultSet);
    DatabaseMetaData getDbMetadata();
    ResultSetMetaData getTableMetadata(String tableName);
    String getPrimaryKey(String tableName);
}
