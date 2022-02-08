package fileOperations.implementations;

import entities.configurations.GeneratedClassWriterConfig;
import entities.generatedClasses.GeneratedClass;
import fileOperations.abstracts.IGeneratedClassWriter;
import org.apache.commons.io.FileUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class GeneratedGeneratedClassWriter implements IGeneratedClassWriter {

    private GeneratedClassWriterConfig generatedClassWriterConfig;

    public GeneratedGeneratedClassWriter(GeneratedClassWriterConfig generatedClassWriterConfig) {
        this.generatedClassWriterConfig = generatedClassWriterConfig;
    }

    @Override
    public void writeToFile(List<GeneratedClass> generatedClasses) {
        if(generatedClassWriterConfig.getDeleteBeforeStart()) {
            this.deleteSaveDir();
        }
        for (GeneratedClass generatedClass: generatedClasses) {
            writeToFile(generatedClass);
        }
    }

    @Override
    public void writeToFile(GeneratedClass generatedClass){

        this.createDirsIfNotExists();

        File file = new File(this.generatedClassWriterConfig.getOutputDir() + "/" + generatedClass.getFileName());
        try{
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(generatedClass.getGeneratedClassStr());
            bw.close();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }


    @Override
    public void writeToConsole(List<GeneratedClass> generatedClasses) {
        for (GeneratedClass generatedClass: generatedClasses) {
            writeToConsole(generatedClass);
        }
    }

    @Override
    public void writeToConsole(GeneratedClass generatedClasses) {
        System.out.println("---------- ---------- " + generatedClasses.getFileName() + " ---------- ----------");
        System.out.println(generatedClasses.getGeneratedClassStr());
        System.out.println("---------- ---------- ---------- ---------- ----------\n");
    }

    private void deleteSaveDir() {
        try {
            FileUtils.deleteDirectory(new File(generatedClassWriterConfig.getOutputDir()));
        } catch (IOException e) {
            // e.printStackTrace();
        }
    }

    private void createDirsIfNotExists() {
        File directory = new File(this.generatedClassWriterConfig.getOutputDir());
        if (!directory.exists()){
            directory.mkdirs();
        }
    }

}
