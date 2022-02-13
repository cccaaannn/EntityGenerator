package fileOperations.implementations;

import entities.configurations.GeneratedClassWriterConfig;
import entities.generatedClasses.GeneratedClass;
import entities.generatedClasses.GeneratedClassGroup;
import fileOperations.abstracts.IGeneratedClassWriter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

@Slf4j
public class GeneratedGeneratedClassWriter implements IGeneratedClassWriter {

    private GeneratedClassWriterConfig generatedClassWriterConfig;

    public GeneratedGeneratedClassWriter(GeneratedClassWriterConfig generatedClassWriterConfig) {
        this.generatedClassWriterConfig = generatedClassWriterConfig;
    }

    @Override
    public void writeToFile(GeneratedClassGroup generatedClassGroup) {
        if(generatedClassWriterConfig.getDeleteBeforeStart()) {
            this.deleteDirIfNotExists(generatedClassGroup.getContainingFolderName());
        }
        for (GeneratedClass generatedClass: generatedClassGroup.getGeneratedClasses()) {
            writeToFile(generatedClass, generatedClassGroup.getContainingFolderName());
        }
    }

    @Override
    public void writeToFile(GeneratedClass generatedClass, String containingFolderName){

        this.createDirsIfNotExists(containingFolderName);

        File file = new File(this.generatedClassWriterConfig.getOutputDir() + File.separator + containingFolderName + File.separator + generatedClass.getFileName());
        try{
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(generatedClass.getGeneratedClassStr());
            bw.close();
        }
        catch (IOException e){
            log.error(e.getMessage());
        }
    }


    @Override
    public void writeToConsole(GeneratedClassGroup generatedClassGroup) {
        for (GeneratedClass generatedClass: generatedClassGroup.getGeneratedClasses()) {
            writeToConsole(generatedClass);
        }
    }

    @Override
    public void writeToConsole(GeneratedClass generatedClasses) {
        System.out.println("---------- ---------- " + generatedClasses.getFileName() + " ---------- ----------");
        System.out.println(generatedClasses.getGeneratedClassStr());
        System.out.println("---------- ---------- ---------- ---------- ----------\n");
    }

    private void createDirsIfNotExists(String containingFolderName) {
        File directory = new File(this.generatedClassWriterConfig.getOutputDir() + File.separator + containingFolderName);
        if (!directory.exists()){
            directory.mkdirs();
        }
    }

    private void deleteDirIfNotExists(String containingFolderName) {
        File directory = new File(this.generatedClassWriterConfig.getOutputDir() + File.separator + containingFolderName);
        if (directory.exists()){
            try {
                FileUtils.deleteDirectory(new File(generatedClassWriterConfig.getOutputDir()));
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }
    }

}
