package mappers.abstracts;

import entities.dbInfos.DbInfo;
import entities.dbInfos.TableInfo;

public interface IDbToEntityMapper {
    TableInfo mapTable(String tableName);
    DbInfo mapDb();
}
