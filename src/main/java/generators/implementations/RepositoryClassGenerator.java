package generators.implementations;

import entities.configurations.EntityClassGeneratorConfig;
import entities.configurations.RepositoryClassGeneratorConfig;
import entities.dbInfos.ColumnInfo;
import entities.dbInfos.DbInfo;
import entities.dbInfos.TableInfo;
import entities.generatedClasses.GeneratedClass;
import entities.generatedClasses.GeneratedClassGroup;
import generators.abstracts.IRepositoryClassGenerator;
import utilities.StringOperations;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RepositoryClassGenerator implements IRepositoryClassGenerator {

    private RepositoryClassGeneratorConfig repositoryClassGeneratorConfig;
    private EntityClassGeneratorConfig entityClassGeneratorConfig;

    public RepositoryClassGenerator(RepositoryClassGeneratorConfig repositoryClassGeneratorConfig, EntityClassGeneratorConfig entityClassGeneratorConfig) {
        this.repositoryClassGeneratorConfig = repositoryClassGeneratorConfig;
        this.entityClassGeneratorConfig = entityClassGeneratorConfig;
    }

    @Override
    public GeneratedClassGroup generateJavaRepositoryClasses(DbInfo dbInfo) {
        GeneratedClassGroup generatedClassGroup = new GeneratedClassGroup();
        List<GeneratedClass> generatedRepositories = new ArrayList<>();

        for (TableInfo tableInfo: dbInfo.getTableInfos()) {
            GeneratedClass generatedRepository = generateJavaRepositoryClass(tableInfo);
            generatedRepositories.add(generatedRepository);
        }

        generatedClassGroup.setPackageName(repositoryClassGeneratorConfig.getPackagePath());
        generatedClassGroup.setContainingFolderName(StringOperations.getNameFromImport(repositoryClassGeneratorConfig.getPackagePath()));
        generatedClassGroup.setGeneratedClasses(generatedRepositories);

        return generatedClassGroup;
    }


    @Override
    public GeneratedClass generateJavaRepositoryClass(TableInfo tableInfo) {

        GeneratedClass generatedClass = new GeneratedClass();

        String fileName = tableInfo.getNameJava() + "Repository.java";
        generatedClass.setFileName(fileName);

        String generatedClassStr = "";
        generatedClassStr += generatePackage();
        generatedClassStr += generateImports(tableInfo);
        generatedClassStr += generateClassAnnotations(tableInfo);
        generatedClassStr += generateClassHeader(tableInfo);

        generatedClass.setGeneratedClassStr(generatedClassStr);

        return generatedClass;
    }

    private String generatePackage() {
        String packageStr = "package " + this.repositoryClassGeneratorConfig.getPackagePath() + ";\n\n";
        return packageStr;
    }

    private String generateImports(TableInfo tableInfo) {
        String importsStr = "" +
                "import org.springframework.data.jpa.repository.JpaRepository;\n" +
                "import org.springframework.stereotype.Repository;\n\n";

        // Entity import
        importsStr += "import " + entityClassGeneratorConfig.getPackagePath() + "." + tableInfo.getNameJava() + ";\n\n";

        // Generate extra imports if needed,
        ColumnInfo primaryKeyColumn = tableInfo.getPrimaryKeyColumn();
        boolean isExtraClassImported = false;
        if (primaryKeyColumn.getJavaDataType().getIsImportRequired()) {
            importsStr += "import " + primaryKeyColumn.getJavaDataType().getImportPath() + ";\n";
            isExtraClassImported = true;
        }
        if (isExtraClassImported) {
            importsStr += "\n";
        }

        return importsStr;
    }

    private String generateClassAnnotations(TableInfo tableInfo) {
        String classAnnotationsStr = "@Repository\n";
        return classAnnotationsStr;
    }

    private String generateClassHeader(TableInfo tableInfo) {
        String classStr = "";

        classStr += "public interface " + tableInfo.getNameJava() + "Repository extends JpaRepository<" + tableInfo.getNameJava();

        ColumnInfo primaryKeyColumn = tableInfo.getPrimaryKeyColumn();
        if(Objects.nonNull(primaryKeyColumn)) {
            classStr += ", " + primaryKeyColumn.getJavaDataType().getName() + ">" + " {\n\n";
        }
        else {
            classStr += ", Object>" + " {\n\n";
        }

        classStr += "}\n";

        return classStr;
    }

}
