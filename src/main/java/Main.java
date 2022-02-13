import connectors.implementations.Connection;
import entities.configurations.Configurations;
import entities.dbInfos.DbInfo;
import entities.generatedClasses.GeneratedClassGroup;
import fileOperations.implementations.ConfigFileOperations;
import fileOperations.implementations.GeneratedGeneratedClassWriter;
import generators.implementations.EntityClassGenerator;
import generators.implementations.RepositoryClassGenerator;
import mappers.implementations.DbToEntityMapper;

public class Main {
    public static void main(String[] args) {

        Configurations configurations = ConfigFileOperations.readConfig();

        DbInfo dbInfo = null;
        try (Connection connection = new Connection(configurations.getConnectionConfig())) {
            DbToEntityMapper dbToEntityMapper = new DbToEntityMapper(connection, configurations.getDbToEntityMapperConfig());
            dbInfo = dbToEntityMapper.mapDb();
        }
        catch (Exception e) {
            System.exit(0);
        }

        EntityClassGenerator entityClassGenerator = new EntityClassGenerator(configurations.getEntityClassGeneratorConfig());
        GeneratedClassGroup generatedEntityClassGroup = entityClassGenerator.generateJavaEntityClasses(dbInfo);

        RepositoryClassGenerator repositoryClassGenerator = new RepositoryClassGenerator(configurations.getRepositoryClassGeneratorConfig(), configurations.getEntityClassGeneratorConfig());
        GeneratedClassGroup generatedRepositoryClassGroup = repositoryClassGenerator.generateJavaRepositoryClasses(dbInfo);

        GeneratedGeneratedClassWriter generatedClassWriter = new GeneratedGeneratedClassWriter(configurations.getGeneratedClassWriterConfig());
        generatedClassWriter.writeToConsole(generatedEntityClassGroup);
        generatedClassWriter.writeToConsole(generatedRepositoryClassGroup);
//        generatedClassWriter.writeToFile(generatedEntityClassGroup);
//        generatedClassWriter.writeToFile(generatedRepositoryClassGroup);

    }
}
