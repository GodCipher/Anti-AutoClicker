package de.luzifer.core.utils;

import de.luzifer.core.Core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Logger;

public class InputStreamUtils {
    
    public static InputStream getInputStream(String fileName) {
        
        InputStream resource = Core.class.getResourceAsStream("/" + fileName);
        
        if (resource == null)
            throw new IllegalStateException("Probably corrupted JAR file, missing " + fileName);
        
        return resource;
    }
    
    public static String readLineFromInputStream(InputStream inputStream, Logger logger) {
        
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
            return bufferedReader.readLine();
        } catch (IOException e) {
            logger.warning("Something exploded, reload/restart and try again");
            e.printStackTrace();
        }
        
        return "INVALID";
    }
    
}
