package de.luzifer.core.version;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Logger;

public class UpdateChecker {
    
    private static final String URL_LINK = "https://raw.githubusercontent.com/Luziferium/Anti-Auto-Clicker/master/src/main/resources/version.txt";
    
    private final Logger logger;
    
    public UpdateChecker(Logger logger) {
        this.logger = logger;
    }
    
    public UpdateCheckerResult checkUpdate() {
        
        HttpURLConnection connection;
        InputStream inputStream = getInputStream("version.txt");
        
        String currentVersion;
        String latestVersion;
        
        try {
            
            connection = (HttpURLConnection) new URL(URL_LINK).openConnection();
            connection.connect();
            
            currentVersion = readLineFromInputStream(inputStream);
            latestVersion = readLineFromInputStream(connection.getInputStream());
            
        } catch (Exception e) {
            
            logger.warning("This seems wrong, either GitHub is down, the repository private or something else happened");
            return new UpdateCheckerResult(null, null, false);
        }
        
        return new UpdateCheckerResult(latestVersion, currentVersion, !latestVersion.equals(currentVersion));
    }
    
    private InputStream getInputStream(String fileName) {
        
        InputStream resource = getClass().getResourceAsStream("/" + fileName);
        
        if (resource == null)
            throw new IllegalStateException("Corrupted JAR file, missing version.txt");
        
        return resource;
    }
    
    private String readLineFromInputStream(InputStream inputStream) {
        
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
            return bufferedReader.readLine();
        } catch (IOException e) {
            logger.warning("Something exploded, reload/restart and try again");
            e.printStackTrace();
        }
        
        return "INVALID";
    }
    
}
