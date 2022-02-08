package fileOperations.implementations;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import entities.configurations.Configurations;
import fileOperations.abstracts.IConfigFileOperations;

import java.io.*;

public class ConfigFileOperations implements IConfigFileOperations {

    private static final String defaultConfigPath = "src/main/resources/config.yaml";

    public static Configurations readConfig() {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        mapper.findAndRegisterModules();
        Configurations configurations = null;
        try {
            configurations = mapper.readValue(new File(defaultConfigPath), Configurations.class);
        }
        catch (IOException | NullPointerException e) {
            return null;
        }
        return configurations;
    }
}
