package entities.configurations;

import lombok.Data;

@Data
public class Configurations {
    private ConnectionConfig connectionConfig;
    private DbToEntityMapperConfig dbToEntityMapperConfig;
    private EntityClassGeneratorConfig entityClassGeneratorConfig;
    private RepositoryClassGeneratorConfig repositoryClassGeneratorConfig;
    private GeneratedClassWriterConfig generatedClassWriterConfig;
}
