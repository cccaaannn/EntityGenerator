package entities.configurations;

import lombok.Data;

@Data
public class RepositoryClassGeneratorConfig {
    private String packagePath;
    private Boolean skipNonPrimaryKeyTables;
}
