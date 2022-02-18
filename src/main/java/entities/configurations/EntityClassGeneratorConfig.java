package entities.configurations;

import lombok.Data;

@Data
public class EntityClassGeneratorConfig {
    private String packagePath;
    private String generatedValueStrategy;
    private Boolean addNotNullable;
    private Boolean useLombok;
    private Boolean generateConstructorsGettersSetters;
    private Boolean skipNonPrimaryKeyTables;
}
