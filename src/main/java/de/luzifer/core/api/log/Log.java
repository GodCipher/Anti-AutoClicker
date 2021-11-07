package de.luzifer.core.api.log;

import de.luzifer.core.api.check.Check;
import de.luzifer.core.utils.Variables;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Log {
    
    static Date dateFile = new Date();
    static SimpleDateFormat formatFile = new SimpleDateFormat("dd MMMM yyyy");
    
    static File file = new File("plugins/AntiAC/Logs", formatFile.format(dateFile) + ".yml");
    static FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
    
    public static void log(Player player, Integer clicks, Double average, Check check) {
        
        try {
            cfg.load(file);
        } catch (Exception e) {
        }
        
        List<String> logList;
        if (file.exists()) {
            logList = cfg.getStringList("LogList");
        } else {
            logList = new ArrayList<>();
        }
        
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-YYYY HH;mm;ss");
        
        if (isLogged(player)) {
            if (cfg.getInt("LogDetailed." + player.getName() + ".clicks") >= clicks) {
                return;
            }
            
            for (String s : cfg.getStringList("LogList")) {
                if (s.startsWith(player.getName())) {
                    logList.remove(s);
                    cfg.set("LogList", logList);
                    break;
                }
            }
            
        }
        
        String c = "" + player.getName() + " ||| [" + clicks + "] Clicks - [" + average + "] ||| " + format.format(date);
        logList.add(c);
        
        cfg.set("LogList", logList);
        
        cfg.set("LogDetailed." + player.getName() + ".name", player.getName());
        cfg.set("LogDetailed." + player.getName() + ".uuid", player.getUniqueId().toString());
        cfg.set("LogDetailed." + player.getName() + ".date", format.format(date));
        cfg.set("LogDetailed." + player.getName() + ".clicks", clicks);
        cfg.set("LogDetailed." + player.getName() + ".average", average);
        cfg.set("LogDetailed." + player.getName() + ".clicksToMuch", clicks - Variables.allowedClicks);
        cfg.set("LogDetailed." + player.getName() + ".logMessage", "Logged by " + check != null ? check.getClass().getSimpleName() : "System");
        
        try {
            cfg.save(file);
        } catch (Exception ignored) {}
        
    }
    
    public static boolean isLogged(Player player) {
        try {
            cfg.load(file);
        } catch (Exception ignored) {}
        
        return cfg.getString("LogDetailed." + player.getName()) != null;
    }
    
}
