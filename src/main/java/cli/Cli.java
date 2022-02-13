package cli;

import connectors.implementations.Connection;
import entities.configurations.Configurations;
import entities.dbInfos.DbInfo;
import entities.dbInfos.TableInfo;
import entities.generatedClasses.GeneratedClass;
import entities.generatedClasses.GeneratedClassGroup;
import fileOperations.implementations.ConfigFileOperations;
import fileOperations.implementations.GeneratedGeneratedClassWriter;
import generators.implementations.EntityClassGenerator;
import generators.implementations.RepositoryClassGenerator;
import lombok.extern.slf4j.Slf4j;
import mappers.implementations.DbToEntityMapper;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.util.Objects;
import java.util.concurrent.Callable;


@Slf4j
@Command(name = "Generator", mixinStandardHelpOptions = true, version = "EntityGenerator 0.1", description = "Generates Entity classes from database tables for Spring Boot applications.")
public class Cli implements Callable<Integer>{

    @Option(names = {"-t", "--table"}, description = "Table name to generate class")
    private String tableName = "";

    private void cliAllDbOption(Configurations configurations) {

        log.info("Phase 1 started (fetching-mapping metadata)");

        long dbStartTime = System.nanoTime();
        DbInfo dbInfo = null;
        try (Connection connection = new Connection(configurations.getConnectionConfig())) {
            DbToEntityMapper dbToEntityMapper = new DbToEntityMapper(connection, configurations.getDbToEntityMapperConfig());
            dbInfo = dbToEntityMapper.mapDb();
        }
        catch (Exception e) {
            log.error(e.getMessage());
            System.exit(0);
        }
        long dbEndTime = System.nanoTime();
        log.info("Data mapped, took {} second/s", (dbEndTime - dbStartTime) / 1000000000);

        log.info("Phase 2 started (Class generation)");

        long generationStartTime = System.nanoTime();
        EntityClassGenerator entityClassGenerator = new EntityClassGenerator(configurations.getEntityClassGeneratorConfig());
        GeneratedClassGroup generatedEntityClassGroup = entityClassGenerator.generateJavaEntityClasses(dbInfo);

        RepositoryClassGenerator repositoryClassGenerator = new RepositoryClassGenerator(configurations.getRepositoryClassGeneratorConfig(), configurations.getEntityClassGeneratorConfig());
        GeneratedClassGroup generatedRepositoryClassGroup = repositoryClassGenerator.generateJavaRepositoryClasses(dbInfo);

        long generationEndTime = System.nanoTime();
        log.info("Class generation completed, {} classes generated took {} milli second/s",
                generatedEntityClassGroup.getGeneratedClasses().size() + generatedRepositoryClassGroup.getGeneratedClasses().size(), (generationEndTime - generationStartTime) / 1000000);

        log.info("Phase 3 started (Saving generated classes)");

        GeneratedGeneratedClassWriter generatedClassWriter = new GeneratedGeneratedClassWriter(configurations.getGeneratedClassWriterConfig());
        generatedClassWriter.writeToFile(generatedEntityClassGroup);
        generatedClassWriter.writeToFile(generatedRepositoryClassGroup);
        log.info("Process completed");
    }

    private void cliSingleTableOption(Configurations configurations, String tableName) {
        TableInfo tableInfo = null;
        try (Connection connection = new Connection(configurations.getConnectionConfig())) {
            DbToEntityMapper dbToEntityMapper = new DbToEntityMapper(connection, configurations.getDbToEntityMapperConfig());
            tableInfo = dbToEntityMapper.mapTable(tableName);
        }
        catch (Exception e) {
            log.error(e.getMessage());
            System.exit(0);
        }

        EntityClassGenerator entityClassGenerator = new EntityClassGenerator(configurations.getEntityClassGeneratorConfig());
        GeneratedClass generatedEntityClass = entityClassGenerator.generateJavaEntityClass(tableInfo);

        RepositoryClassGenerator repositoryClassGenerator = new RepositoryClassGenerator(configurations.getRepositoryClassGeneratorConfig(), configurations.getEntityClassGeneratorConfig());
        GeneratedClass generatedRepositoryClass = repositoryClassGenerator.generateJavaRepositoryClass(tableInfo);

        GeneratedGeneratedClassWriter generatedClassWriter = new GeneratedGeneratedClassWriter(configurations.getGeneratedClassWriterConfig());
        generatedClassWriter.writeToConsole(generatedEntityClass);
        generatedClassWriter.writeToConsole(generatedRepositoryClass);
    }


    @Override
    public Integer call() {

        Configurations configurations = ConfigFileOperations.readConfig();

        if(Objects.isNull(configurations)) {
            log.error("Can not read config file");
            return 0;
        }

        if(tableName.equals("")) {
            cliAllDbOption(configurations);
        }
        else{
            cliSingleTableOption(configurations, tableName);
        }

        return 0;
    }


    public static void main(String[] args) {
        int exitCode = new CommandLine(new Cli()).execute(args);
        System.exit(exitCode);
    }
}