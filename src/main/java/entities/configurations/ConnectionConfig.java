package entities.configurations;

import lombok.Data;

@Data
public class ConnectionConfig {
    private String connectionString;
    private String tableFetchQuery;
}
