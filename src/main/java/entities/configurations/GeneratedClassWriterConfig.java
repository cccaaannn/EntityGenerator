package entities.configurations;

import lombok.Data;

@Data
public class GeneratedClassWriterConfig {
    private String outputDir;
    private Boolean deleteBeforeStart;
}
