package generators.abstracts;

import entities.dbInfos.DbInfo;
import entities.dbInfos.TableInfo;
import entities.generatedClasses.GeneratedClass;
import entities.generatedClasses.GeneratedClassGroup;

public interface IRepositoryClassGenerator {
    GeneratedClassGroup generateJavaRepositoryClasses(DbInfo dbInfo);

    GeneratedClass generateJavaRepositoryClass(TableInfo tableInfo);
}
