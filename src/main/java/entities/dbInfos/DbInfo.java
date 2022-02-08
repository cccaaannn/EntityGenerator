package entities.dbInfos;

import lombok.Data;

import java.util.List;

@Data
public class DbInfo {
    List<TableInfo> tableInfos;
}
