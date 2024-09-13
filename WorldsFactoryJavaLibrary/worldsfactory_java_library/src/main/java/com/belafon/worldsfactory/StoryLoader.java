package com.belafon.worldsfactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import com.belafon.worldsfactory.api.StoryInitializer;
import com.belafon.worldsfactory.api.WorldsFactoryStory;

public class StoryLoader {
    public WorldsFactoryStory load(StoryInitializer initializer) throws FileNotFoundException, IOException{
        if(initializer.getCode() != null)
            return new Story(initializer, initializer.getCode());
        
        String code = loadCodeFromFile(initializer.getPathToStoryData());
        return new Story(initializer, code);
    }

    private String loadCodeFromFile(String pathToFile) throws FileNotFoundException, IOException {
        File file = new File(pathToFile);
        
        if(!file.exists()){
            throw new FileNotFoundException("File " + pathToFile + " not found");
        }
        
        StringBuilder allLines = new StringBuilder();
        try(BufferedReader br = new BufferedReader(new FileReader(file))){
            String line = "";
            while((line = br.readLine()) != null){
                allLines.append(line + "\n");
            }
        } 
        return allLines.toString();
    }
}