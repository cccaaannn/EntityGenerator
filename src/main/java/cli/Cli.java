package cli;

import connectors.implementations.Connection;
import entities.configurations.Configurations;
import entities.dbInfos.DbInfo;
import entities.dbInfos.TableInfo;
import entities.generatedClasses.GeneratedClass;
import fileOperations.implementations.ConfigFileOperations;
import fileOperations.implementations.GeneratedGeneratedClassWriter;
import generators.implementations.EntityClassGenerator;
import mappers.implementations.DbToEntityMapper;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;


@Command(name = "Generator", mixinStandardHelpOptions = true, version = "EntityGenerator 0.1",
        description = "Generates Entity classes from database tables for Spring Boot applications.")
public class Cli implements Callable<Integer>{


    @Option(names = {"-t", "--table"}, description = "Table name to generate class")
    private String tableName = "";



    private void allDbOption(Configurations configurations) {

        System.out.println("\nPhase 1 started (fetching metadata)\n");

        long dbStartTime = System.nanoTime();
        DbInfo dbInfo = null;
        try (Connection connection = new Connection(configurations.getConnectionConfig())) {
            DbToEntityMapper dbToEntityMapper = new DbToEntityMapper(connection, configurations.getDbToEntityMapperConfig());
            dbInfo = dbToEntityMapper.mapDb();
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(0);
        }
        long dbEndTime = System.nanoTime();
        System.out.printf("Db data fetched, took %s second/s%n\n", (dbEndTime - dbStartTime) / 1000000000);

        System.out.println("Phase 2 started (Class generation)\n");

        long generationStartTime = System.nanoTime();
        EntityClassGenerator entityClassGenerator = new EntityClassGenerator(configurations.getEntityClassGeneratorConfig());
        List<GeneratedClass> generatedClasses = entityClassGenerator.generateJavaEntityClasses(dbInfo);
        long generationEndTime = System.nanoTime();
        System.out.printf("Class generation completed, %s classes generated took %s second/s%n\n", generatedClasses.size(), (generationEndTime - generationStartTime) / 1000000000);

        System.out.println("Phase 3 started (Saving generated classes)\n");

        GeneratedGeneratedClassWriter generatedClassWriter = new GeneratedGeneratedClassWriter(configurations.getGeneratedClassWriterConfig());
        generatedClassWriter.writeToFile(generatedClasses);
        System.out.println("Process completed");
    }

    private void singleTableOption(Configurations configurations, String tableName) {
        TableInfo tableInfo = null;
        try (Connection connection = new Connection(configurations.getConnectionConfig())) {
            DbToEntityMapper dbToEntityMapper = new DbToEntityMapper(connection, configurations.getDbToEntityMapperConfig());
            tableInfo = dbToEntityMapper.mapTable(tableName);
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }

        EntityClassGenerator entityClassGenerator = new EntityClassGenerator(configurations.getEntityClassGeneratorConfig());
        GeneratedClass generatedClass = entityClassGenerator.generateJavaEntityClass(tableInfo);


        GeneratedGeneratedClassWriter generatedClassWriter = new GeneratedGeneratedClassWriter(configurations.getGeneratedClassWriterConfig());
        generatedClassWriter.writeToConsole(generatedClass);
    }


    @Override
    public Integer call() throws Exception {

        Configurations configurations = ConfigFileOperations.readConfig();

        if(Objects.isNull(configurations)) {
            System.out.println("Can not read config file");
            return 0;
        }

        if(tableName.equals("")) {
            allDbOption(configurations);
        }
        else{
            singleTableOption(configurations, tableName);
        }

        return 0;
    }


    public static void main(String[] args) {
        int exitCode = new CommandLine(new Cli()).execute(args);
        System.exit(exitCode);
    }
}