package entities.dbInfos;

import lombok.Data;
import utilities.StringOperations;

@Data
public class ColumnInfo {
    private Integer columnPosition;
    private String columnNameSql;
    private String columnNameJava;
    private String columnNameJavaFunction;

    private String sqlDataType;
    private JavaDataType javaDataType;

    private Boolean isPrimary;
    private Boolean isNullable;
    private Boolean isAutoIncrement;

    /*
     * javaColumnName is camel case
     */
    public void setColumnNameJava(String columnNameJava) {
        this.columnNameJava = StringOperations.toCamelCase(columnNameJava);
    }

    public void setColumnNameJavaFunction(String columnNameJavaFunction) {
        this.columnNameJavaFunction = StringOperations.toCapitalCamelCase(columnNameJavaFunction);
    }

    /*
     * 0- not nullable
     * 1- nullable
     * 2- unknown
     */
    public void setIsNullable(int nullable) {
        if(nullable == 1) {
            this.isNullable = true;
        }
        else {
            this.isNullable = false;
        }
    }

}
