package fileOperations.abstracts;

import entities.generatedClasses.GeneratedClass;
import entities.generatedClasses.GeneratedClassGroup;

public interface IGeneratedClassWriter {
    void writeToFile(GeneratedClassGroup generatedClassGroup);
    void writeToFile(GeneratedClass generatedClass, String containingFolderName);
    void writeToConsole(GeneratedClassGroup generatedClassGroup);
    void writeToConsole(GeneratedClass generatedClasses);
}
