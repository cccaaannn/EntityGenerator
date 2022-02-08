package generators.implementations;

import entities.dbInfos.JavaDataType;
import entities.dbInfos.ColumnInfo;
import entities.dbInfos.DbInfo;
import entities.dbInfos.TableInfo;
import entities.configurations.EntityClassGeneratorConfig;
import entities.generatedClasses.GeneratedClass;
import generators.abstracts.IEntityClassGenerator;

import java.util.ArrayList;
import java.util.List;

public class EntityClassGenerator implements IEntityClassGenerator {

    private EntityClassGeneratorConfig entityClassGeneratorConfig;

    public EntityClassGenerator(EntityClassGeneratorConfig entityClassGeneratorConfig) {
        this.entityClassGeneratorConfig = entityClassGeneratorConfig;
    }

    @Override
    public List<GeneratedClass> generateJavaEntityClasses(DbInfo dbInfo) {
        List<GeneratedClass> generatedEntities = new ArrayList<>();

        for (TableInfo tableInfo: dbInfo.getTableInfos()) {
            GeneratedClass generatedEntity = generateJavaEntityClass(tableInfo);
            generatedEntities.add(generatedEntity);
        }

        return generatedEntities;
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
        if(this.entityClassGeneratorConfig.getGenerateGettersSetters()) {
            generatedClassStr += generateGettersSetters(tableInfo);
        }

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
        classStr += "}\n";

        return classStr;
    }

    private String generateGettersSetters(TableInfo tableInfo) {
        return null;
    }

}
