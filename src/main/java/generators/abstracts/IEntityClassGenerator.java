package generators.abstracts;

import entities.dbInfos.DbInfo;
import entities.dbInfos.TableInfo;
import entities.generatedClasses.GeneratedClass;
import entities.generatedClasses.GeneratedClassGroup;

import java.util.List;

public interface IEntityClassGenerator {
    GeneratedClassGroup generateJavaEntityClasses(DbInfo dbInfo);
    GeneratedClass generateJavaEntityClass(TableInfo tableInfo);
}
