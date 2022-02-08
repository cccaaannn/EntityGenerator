import connectors.implementations.Connection;
import entities.configurations.Configurations;
import entities.dbInfos.DbInfo;
import entities.generatedClasses.GeneratedClass;
import fileOperations.implementations.ConfigFileOperations;
import fileOperations.implementations.GeneratedGeneratedClassWriter;
import mappers.implementations.DbToEntityMapper;
import generators.implementations.EntityClassGenerator;

import java.util.List;


public class Main {

    public static void main(String[] args) {

        Configurations configurations = ConfigFileOperations.readConfig();

        DbInfo dbInfo = null;
        try (Connection connection = new Connection(configurations.getConnectionConfig())) {
            DbToEntityMapper dbToEntityMapper = new DbToEntityMapper(connection, configurations.getDbToEntityMapperConfig());
            dbInfo = dbToEntityMapper.mapDb();
        }

        EntityClassGenerator entityClassGenerator = new EntityClassGenerator(configurations.getEntityClassGeneratorConfig());
        List<GeneratedClass> generatedClasses = entityClassGenerator.generateJavaEntityClasses(dbInfo);


        GeneratedGeneratedClassWriter generatedClassWriter = new GeneratedGeneratedClassWriter(configurations.getGeneratedClassWriterConfig());
        // entityWriter.writeToFile(generatedClasses);
        generatedClassWriter.writeToConsole(generatedClasses);

    }
}
