package connectors.implementations;

import connectors.abstracts.IConnection;
import entities.configurations.ConnectionConfig;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;

@Slf4j
public class Connection implements IConnection, AutoCloseable {

    private java.sql.Connection conn = null;
    ConnectionConfig connectionConfig;

    public Connection(ConnectionConfig connectionConfig) {
        this.connectionConfig = connectionConfig;
        this.connect();
    }

    /*
     * Implemented for try with resources
     */
    @Override
    public void close() {
        this.disconnect();
    }

    @Override
    public void connect() {
        try {
            this.conn = DriverManager.getConnection(this.connectionConfig.getConnectionString());
            log.debug("Connected\n");
        }
        catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    @Override
    public void disconnect() {
        try {
            conn.close();
            log.debug("Disconnected\n");
        }
        catch (Exception e){
            log.error(e.getMessage());
        }
    }

    @Override
    public void closeResultSet(ResultSet resultSet) {
        try {
            resultSet.close();
            log.debug("ResultSet closed\n");
        }
        catch (Exception e) {
            log.debug("Can not closed ResultSet\n");
        }
    }

    @Override
    public Integer getResultSetSize(ResultSet resultSet) {
        int counter = 0;
        try {
            while (resultSet.next()) {
                counter++;
            }
            resultSet.beforeFirst();
        }
        catch (Exception e) {
            log.debug("Can not get ResultSet size\n");
        }
        return counter;
    }

    @Override
    public DatabaseMetaData getDbMetadata(){
        try {
            DatabaseMetaData databaseMetaData = this.conn.getMetaData();
            // ResultSet resultSet = databaseMetaData.getTables(null, null, null, new String[]{"TABLE"});
            return databaseMetaData;
        }
        catch (Exception e){
            log.error(e.getMessage());
        }
        return null;
    }

    @Override
    public ResultSetMetaData getTableMetadata(String tableName) {
        String query = String.format(this.connectionConfig.getTableFetchQuery(), tableName);
        try {
            Statement st = this.conn.createStatement();
            return st.executeQuery(query).getMetaData();
        }
        catch (Exception e){
            log.error(e.getMessage());
        }
        return null;
    }

    @Override
    public String getPrimaryKey(String tableName) {
        ResultSet resultSet = null;
        String primaryKey = null;
        try {
            DatabaseMetaData databaseMetaData = this.conn.getMetaData();
            resultSet = databaseMetaData.getPrimaryKeys(null, null, tableName);
            while(resultSet.next()){
                primaryKey = resultSet.getString("COLUMN_NAME");
            }
        }
        catch (SQLException e) {
            log.error(e.getMessage());
        }
        finally {
            this.closeResultSet(resultSet);
        }
        return primaryKey;
    }

}
