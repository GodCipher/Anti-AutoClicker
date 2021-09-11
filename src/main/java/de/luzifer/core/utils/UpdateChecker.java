package de.luzifer.core.utils;

import org.bukkit.plugin.Plugin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class UpdateChecker {
    
    private static final String URL_LINK = "https://raw.githubusercontent.com/Luziferium/Anti-Auto-Clicker/master/pom.xml";
    private final Plugin PLUGIN;
    
    private String latestVersion = "NOT_ASSIGNED";
    
    public UpdateChecker(Plugin plugin) {
        this.PLUGIN = plugin;
    }
    
    public boolean check() {
        
        HttpURLConnection connection = null;
        try {
            
            connection = (HttpURLConnection) new URL(URL_LINK).openConnection();
            connection.connect();
            
            latestVersion = convertLatestVersionFromGitHub(connection);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(connection != null)
                connection.disconnect();
        }
        
        return latestVersion.equalsIgnoreCase(PLUGIN.getDescription().getVersion());
    }
    
    /**
     * Will just be assigned if {@link #check()} ran at least 1 time
     */
    public String getLatestVersion() {
        return latestVersion;
    }
    
    private String convertLatestVersionFromGitHub(HttpURLConnection connection) throws IOException {
        
        Stream<String> list = new BufferedReader(new InputStreamReader(connection.getInputStream())).lines();
        for(String s : list.collect(Collectors.toList())) {
            if(s.toLowerCase().contains("<version>") && s.toLowerCase().contains("</version>"))
                return s.replace("<version>", "").replace("</version>", "").replaceAll(" ", "");
        }
        return "NOT_FOUND";
    }
    
}
