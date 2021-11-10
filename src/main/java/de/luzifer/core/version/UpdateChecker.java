package de.luzifer.core.version;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class UpdateChecker {
    
    private static final String URL_LINK = "https://raw.githubusercontent.com/Luziferium/Anti-Auto-Clicker/master/pom.xml";
   
    private final Logger logger;
    
    private File versionFile;
    
    public UpdateChecker(Logger logger) {
        
        this.logger = logger;
        
        URL resource = getClass().getClassLoader().getResource("version.txt");
    
        if (resource == null)
            throw new IllegalStateException("Corrupted JAR file, missing version.txt");
    
        instantiateVersionFile(resource);
    }
    
    public UpdateCheckerResult checkUpdate() {
        
        HttpURLConnection connection = null;
        
        String currentVersion;
        String latestVersion;
    
        try {
        
            connection = (HttpURLConnection) new URL(URL_LINK).openConnection();
            connection.connect();
    
            try (Stream<String> stream = Files.lines(Paths.get(versionFile.toURI()))) {
                currentVersion = stream.collect(Collectors.joining());
            }
            
            latestVersion = new BufferedReader(new InputStreamReader(connection.getInputStream())).readLine();
        
        } catch (Exception e) {
            
            logger.warning("Could not resolve connection to GitHub repository");
            return new UpdateCheckerResult(null, null, false);
            
        } finally {
            
            if (connection != null)
                connection.disconnect();
        }
    
        return new UpdateCheckerResult(latestVersion, currentVersion, !latestVersion.equals(currentVersion));
    }
    
    private void instantiateVersionFile(URL resource) {
        
        try {
            this.versionFile = Paths.get(resource.toURI()).toFile();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }
    
}
