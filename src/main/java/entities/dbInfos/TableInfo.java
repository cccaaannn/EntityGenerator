package entities.dbInfos;

import lombok.Data;
import utilities.StringOperations;

import java.util.List;
import java.util.Set;

@Data
public class TableInfo {
    private String nameSql;
    private String nameJava;
    private List<ColumnInfo> columnInfos;
    private Set<JavaDataType> javaDataTypes;

    /*
     * nameJava is camel case with capital first character
     */
    public void setNameJava(String nameJava) {
        this.nameJava = StringOperations.toCapitalCamelCase(nameJava);
    }

    public ColumnInfo getPrimaryKeyColumn() {
        for (ColumnInfo columnInfo: columnInfos) {
            if(columnInfo.getIsPrimary()) {
                return columnInfo;
            }
        }
        return null;
    }

}
