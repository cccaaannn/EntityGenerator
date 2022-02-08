package connectors.implementations;

import connectors.abstracts.IConnection;
import entities.configurations.ConnectionConfig;

import java.sql.*;

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
        }
        catch (Exception e) {
            System.err.println(e);
        }
    }

    @Override
    public void disconnect() {
        try {
            conn.close();
//            System.out.println("--- dc ---\n");
        }
        catch (Exception e){
            System.err.println(e);
        }
    }

    @Override
    public void closeResultSet(ResultSet resultSet) {
        try {
            resultSet.close();
        }
        catch (Exception e) {

        }
    }

    @Override
    public DatabaseMetaData getDbMetadata(){
        try {
            DatabaseMetaData databaseMetaData = this.conn.getMetaData();
            // ResultSet resultSet = databaseMetaData.getTables(null, null, null, new String[]{"TABLE"});
            return databaseMetaData;
        }
        catch (Exception e){
            System.out.println(e);
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
            System.err.println(e);
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
        catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        finally {
            this.closeResultSet(resultSet);
        }
        return primaryKey;
    }

}
