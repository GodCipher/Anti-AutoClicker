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

    private final Plugin plugin;

    public UpdateChecker(Plugin plugin) {
        this.plugin = plugin;
    }

    private String last;
    public boolean check() {
        try {

            HttpURLConnection connection = (HttpURLConnection) new URL("https://raw.githubusercontent.com/Luziferium/Anti-Auto-Clicker/master/pom.xml").openConnection();
            connection.connect();
            Stream<String> list = new BufferedReader(new InputStreamReader(connection.getInputStream())).lines();
            for(String s : list.collect(Collectors.toList())) {
                if(s.toLowerCase().contains("<version>") && s.toLowerCase().contains("</version>")) {
                    last = s.replace("<version>", "").replace("</version>", "").replaceAll(" " ,"");
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return last.equalsIgnoreCase(plugin.getDescription().getVersion());
    }

    public String getLast() {
        return last;
    }
}
