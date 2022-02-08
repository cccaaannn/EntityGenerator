package entities.configurations;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class DbToEntityMapperConfig {
    private String catalog;
    private List<String> tablesToIgnore;
    private Map<String, String> preferredImports;
}
