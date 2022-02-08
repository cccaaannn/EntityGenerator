package fileOperations.abstracts;

import entities.generatedClasses.GeneratedClass;

import java.util.List;

public interface IGeneratedClassWriter {
    void writeToFile(List<GeneratedClass> generatedClasses);
    void writeToFile(GeneratedClass generatedClass);
    void writeToConsole(List<GeneratedClass> generatedClasses);
    void writeToConsole(GeneratedClass generatedClasses);
}
