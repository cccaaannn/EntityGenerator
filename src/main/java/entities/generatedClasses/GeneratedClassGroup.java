package entities.generatedClasses;

import lombok.Data;

import java.util.List;

@Data
public class GeneratedClassGroup {
    String packageName;
    String containingFolderName;
    List<GeneratedClass> generatedClasses;
}
