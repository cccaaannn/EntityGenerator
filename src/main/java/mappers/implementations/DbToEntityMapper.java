package mappers.implementations;

import connectors.abstracts.IConnection;
import entities.dbInfos.JavaDataType;
import entities.dbInfos.ColumnInfo;
import entities.dbInfos.DbInfo;
import entities.dbInfos.TableInfo;
import entities.configurations.DbToEntityMapperConfig;
import mappers.abstracts.IDbToEntityMapper;
import utilities.StringOperations;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DbToEntityMapper implements IDbToEntityMapper {

    private IConnection connection;
    private DbToEntityMapperConfig dbToEntityMapperConfig;


    public DbToEntityMapper(IConnection connection, DbToEntityMapperConfig dbToEntityMapperConfig) {
        this.connection = connection;
        this.dbToEntityMapperConfig = dbToEntityMapperConfig;
    }


    /*
     * Maps raw table information to TableInfo entity
     */
    public TableInfo mapTable(String tableName) {

        TableInfo tableInfo = new TableInfo();
        tableInfo.setNameSql(tableName);
        tableInfo.setNameJava(tableName);

        List<ColumnInfo> columnInfos = new ArrayList<>();
        Set<JavaDataType> javaDataTypes = new HashSet<>();

        ResultSetMetaData resultSetMetaData = this.connection.getTableMetadata(tableName);
        String primaryKey = this.connection.getPrimaryKey(tableName);
        try {
            int colCount = resultSetMetaData.getColumnCount();
            for (int i = 1; i <= colCount; i++) {
                ColumnInfo columnInfo = new ColumnInfo();

                // Replace current import path with preferred import
                // Ex: "Timestamp": "java.util.Date"
                // Ex: "Date": "java.util.Date"
                String columnClassName = resultSetMetaData.getColumnClassName(i);
                if(this.dbToEntityMapperConfig.getPreferredImports().containsKey(StringOperations.getNameFromImport(columnClassName))){
                    columnClassName = this.dbToEntityMapperConfig.getPreferredImports().get(StringOperations.getNameFromImport(columnClassName));
                }

                JavaDataType javaDataType = new JavaDataType(columnClassName);
                javaDataTypes.add(javaDataType);

                // Check if this key is the primary key
                Boolean isPrimaryKey = false;
                if(resultSetMetaData.getColumnName(i).equals(primaryKey)) {
                    isPrimaryKey = true;
                }

                // Set fields
                columnInfo.setColumnPosition(i);
                columnInfo.setColumnNameSql(resultSetMetaData.getColumnName(i));
                columnInfo.setColumnNameJava(resultSetMetaData.getColumnName(i));

                columnInfo.setSqlDataType(resultSetMetaData.getColumnTypeName(i));

                columnInfo.setJavaDataType(javaDataType);

                columnInfo.setIsAutoIncrement(resultSetMetaData.isAutoIncrement(i));
                columnInfo.setIsNullable(resultSetMetaData.isNullable(i));
                columnInfo.setIsPrimary(isPrimaryKey);


                columnInfos.add(columnInfo);
            }

            tableInfo.setJavaDataTypes(javaDataTypes);
            tableInfo.setColumnInfos(columnInfos);
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return tableInfo;
    }


    /*
     * Maps raw db information to DbInfo entity
     */
    public DbInfo mapDb() {

        DatabaseMetaData metadata = this.connection.getDbMetadata();
        ResultSet resultSet = null;

        DbInfo dbInfo = new DbInfo();
        List<TableInfo> tableInfos = new ArrayList<>();
        try {
            resultSet = metadata.getTables(dbToEntityMapperConfig.getCatalog(), null, null, new String[]{"TABLE"});
            while(resultSet.next()){
                String tableName = resultSet.getString("TABLE_NAME");
                System.out.printf("Table: %s\n", tableName);
                // pass ignored tables
                if(dbToEntityMapperConfig.getTablesToIgnore().contains(tableName)){ continue;}

                // add table info
                TableInfo tableInfo = this.mapTable(tableName);
                tableInfos.add(tableInfo);
            }

            dbInfo.setTableInfos(tableInfos);
        }
        catch (Exception e){
            System.err.println(e);
        }
        // Try to close result set
        finally {
            connection.closeResultSet(resultSet);
        }

        return dbInfo;
    }

}
