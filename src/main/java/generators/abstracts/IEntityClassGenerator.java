package generators.abstracts;

import entities.dbInfos.DbInfo;
import entities.dbInfos.TableInfo;
import entities.generatedClasses.GeneratedClass;

import java.util.List;

public interface IEntityClassGenerator {
    List<GeneratedClass> generateJavaEntityClasses(DbInfo dbInfo);
    GeneratedClass generateJavaEntityClass(TableInfo tableInfo);
}
