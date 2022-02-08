package entities.dbInfos;

import lombok.Data;
import utilities.StringOperations;

@Data
public class JavaDataType {
    private String name;
    private String importPath;
    private Boolean isImportRequired;
    private Boolean isJavaDate;

    public JavaDataType(String importPath) {
        this.setName(importPath);
        this.setImportPath(importPath);
        this.setIsImportRequired(importPath);
        this.setIsJavaDate(importPath);
    }

    /*
     * Ex: java.lang.String -> String
     */
    public void setName(String name) {
        this.name = StringOperations.getNameFromImport(name);
    }

    /*
     * To set isImportRequired, check if import path is not java.lang
     */
    public void setIsImportRequired(String javaDataTypeFull) {
        if(StringOperations.isImportRequired(javaDataTypeFull)) {
            this.isImportRequired = true;
        }
        else {
            this.isImportRequired = false;
        }
    }

    /*
     * Set isJavaDate
     */
    public void setIsJavaDate(String javaDataTypeFull) {
        if(javaDataTypeFull.equals("java.util.Date")) {
            this.isJavaDate = true;
        }
        else {
            this.isJavaDate = false;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof JavaDataType)) {
            return false;
        }
        if(((JavaDataType) o).name.equals(this.name)) {
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
