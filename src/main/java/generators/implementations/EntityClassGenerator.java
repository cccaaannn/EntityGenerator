package generators.implementations;

import entities.dbInfos.JavaDataType;
import entities.dbInfos.ColumnInfo;
import entities.dbInfos.DbInfo;
import entities.dbInfos.TableInfo;
import entities.configurations.EntityClassGeneratorConfig;
import entities.generatedClasses.GeneratedClass;
import entities.generatedClasses.GeneratedClassGroup;
import generators.abstracts.IEntityClassGenerator;
import utilities.StringOperations;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class EntityClassGenerator implements IEntityClassGenerator {

    private EntityClassGeneratorConfig entityClassGeneratorConfig;

    public EntityClassGenerator(EntityClassGeneratorConfig entityClassGeneratorConfig) {
        this.entityClassGeneratorConfig = entityClassGeneratorConfig;
    }

    @Override
    public GeneratedClassGroup generateJavaEntityClasses(DbInfo dbInfo) {
        GeneratedClassGroup generatedClassGroup = new GeneratedClassGroup();
        List<GeneratedClass> generatedEntities = new ArrayList<>();

        for (TableInfo tableInfo: dbInfo.getTableInfos()) {
            // Skip tables that does not have a primary key
            if(entityClassGeneratorConfig.getSkipNonPrimaryKeyTables() && Objects.isNull(tableInfo.getPrimaryKeyColumn())) {
                continue;
            }
            GeneratedClass generatedEntity = generateJavaEntityClass(tableInfo);
            generatedEntities.add(generatedEntity);
        }

        generatedClassGroup.setPackageName(entityClassGeneratorConfig.getPackagePath());
        generatedClassGroup.setContainingFolderName(StringOperations.getNameFromImport(entityClassGeneratorConfig.getPackagePath()));
        generatedClassGroup.setGeneratedClasses(generatedEntities);

        return generatedClassGroup;
    }


    @Override
    public GeneratedClass generateJavaEntityClass(TableInfo tableInfo) {

        GeneratedClass generatedClass = new GeneratedClass();

        String fileName = tableInfo.getNameJava() + ".java";
        generatedClass.setFileName(fileName);

        String generatedClassStr = "";
        generatedClassStr += generatePackage();
        generatedClassStr += generateImports(tableInfo);
        generatedClassStr += generateClassAnnotations(tableInfo);
        generatedClassStr += generateEntities(tableInfo);

        generatedClass.setGeneratedClassStr(generatedClassStr);

        return generatedClass;
    }

    private String generatePackage() {
        String packageStr = "package " + this.entityClassGeneratorConfig.getPackagePath() + ";\n\n";
        return packageStr;
    }

    private String generateImports(TableInfo tableInfo) {
        String importsStr = "" +
                "import javax.persistence.*;\n\n";

        if (this.entityClassGeneratorConfig.getUseLombok()) {
            importsStr += "" +
                    "import lombok.Data;\n" +
                    "import lombok.AllArgsConstructor;\n" +
                    "import lombok.NoArgsConstructor;\n" +
                    "\n";
        }

        // Generate extra imports if needed
        boolean isExtraClassImported = false;
        for (JavaDataType javaDataType : tableInfo.getJavaDataTypes()) {
            if (javaDataType.getIsImportRequired()) {
                importsStr += "import " + javaDataType.getImportPath() + ";\n";
                isExtraClassImported = true;
            }
        }
        if (isExtraClassImported) {
            importsStr += "\n";
        }

        return importsStr;
    }

    private String generateClassAnnotations(TableInfo tableInfo) {
        String classAnnotationsStr = "";
        if (this.entityClassGeneratorConfig.getUseLombok()) {
            classAnnotationsStr += "" +
                    "@Data\n" +
                    "@AllArgsConstructor\n" +
                    "@NoArgsConstructor\n";
        }
        classAnnotationsStr += "" +
                "@Entity\n" +
                "@Table(name = \"" + tableInfo.getNameSql() + "\")\n";

        return classAnnotationsStr;
    }

    private String generateEntities(TableInfo tableInfo) {
        String classStr = "";

        classStr += "public class " + tableInfo.getNameJava() + " {\n\n";
        for (ColumnInfo columnInfo : tableInfo.getColumnInfos()) {

            // add id primary key
            if (columnInfo.getIsPrimary()) {
                classStr += "\t@Id\n";
            }

            // add GeneratedValue if auto increment
            if (columnInfo.getIsAutoIncrement()) {
                if(!this.entityClassGeneratorConfig.getGeneratedValueStrategy().trim().isEmpty()) {
                    classStr += "\t@GeneratedValue(strategy = " + this.entityClassGeneratorConfig.getGeneratedValueStrategy() + ")\n";
                }
                else {
                    classStr += "\t@GeneratedValue\n";
                }

            }

            // add Temporal annotation if java Date
            if (columnInfo.getJavaDataType().getIsJavaDate()) {
                classStr += "\t@Temporal(TemporalType." + columnInfo.getSqlDataType() + ")\n";
            }

            // add not nullable to column if not nullable and config is true
            if (!columnInfo.getIsNullable() && this.entityClassGeneratorConfig.getAddNotNullable()) {
                classStr += "\t@Column(name = \"" + columnInfo.getColumnNameSql() + "\", nullable = \"false\")\n";
            } else {
                classStr += "\t@Column(name = \"" + columnInfo.getColumnNameSql() + "\")\n";
            }

            classStr += "\tprivate " + columnInfo.getJavaDataType().getName() + " " + columnInfo.getColumnNameJava() + ";";
            classStr += "\n\n";

        }

        if(entityClassGeneratorConfig.getGenerateConstructorsGettersSetters()){
            classStr += "\n\t// ---------- Constructors ----------\n";
            classStr += generateAllArgsConstructor(tableInfo);
            classStr += generateNoArgsConstructor(tableInfo);

            classStr += "\n\t// ---------- Getter - Setter ----------\n";
            for (ColumnInfo columnInfo : tableInfo.getColumnInfos()) {
                classStr += generateGetter(columnInfo);
                classStr += generateSetter(columnInfo);
            }
        }

        classStr += "}\n";

        return classStr;
    }

    private String generateAllArgsConstructor(TableInfo tableInfo) {
        String constructorStr = "";
        constructorStr += "\n\tpublic " + tableInfo.getNameJava() + "(";
        for (int i = 0; i < tableInfo.getColumnInfos().size(); i++) {
            constructorStr += tableInfo.getColumnInfos().get(i).getJavaDataType().getName() + " " + tableInfo.getColumnInfos().get(i).getColumnNameJava();
            if(i != tableInfo.getColumnInfos().size() - 1) {
                constructorStr += ", ";
            }
        }
        constructorStr += ") {\n";
        for (ColumnInfo columnInfo : tableInfo.getColumnInfos()) {
            constructorStr += "\t\tthis." + columnInfo.getColumnNameJava() + " = " + columnInfo.getColumnNameJava() + ";\n";
        }
        constructorStr += "\t}\n";

        return constructorStr;
    }

    private String generateNoArgsConstructor(TableInfo tableInfo) {
        String constructorStr = "";
        constructorStr += "\n\tpublic " + tableInfo.getNameJava() + "() { }\n";
        return constructorStr;
    }

    private String generateGetter(ColumnInfo columnInfo) {
        String getterStr = "";
        getterStr += "\n\tpublic " + columnInfo.getJavaDataType().getName() + " get" + columnInfo.getColumnNameJavaFunction() + "() {\n";
        getterStr += "\t\treturn " + columnInfo.getColumnNameJava() + ";\n";
        getterStr += "\t}\n";

        return getterStr;
    }

    private String generateSetter(ColumnInfo columnInfo) {
        String setterStr = "";
        setterStr += "\n\tpublic void set" + columnInfo.getColumnNameJavaFunction() + "(" + columnInfo.getJavaDataType().getName() + " " + columnInfo.getColumnNameJava() + ") {\n";
        setterStr += "\t\tthis." + columnInfo.getColumnNameJava() + " = " + columnInfo.getColumnNameJava() + ";\n";
        setterStr += "\t}\n";

        return setterStr;
    }

}
