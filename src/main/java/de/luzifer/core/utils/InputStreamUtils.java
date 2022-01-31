package de.luzifer.core.utils;

import de.luzifer.core.Core;
import lombok.experimental.UtilityClass;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Logger;

@UtilityClass
public class InputStreamUtils {
    
    public InputStream getInputStream(String fileName) {
        
        InputStream resource = Core.class.getResourceAsStream("/" + fileName);
        
        if (resource == null)
            throw new IllegalStateException("Probably corrupted JAR file, missing " + fileName);
        
        return resource;
    }
    
    public String readLineFromInputStream(InputStream inputStream, Logger logger) {
        
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
            return bufferedReader.readLine();
        } catch (IOException e) {
            logger.warning("Something exploded, reload/restart and try again");
            e.printStackTrace();
        }
        
        return "INVALID";
    }
    
}
