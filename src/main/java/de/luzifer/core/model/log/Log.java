package de.luzifer.core.model.log;

import de.luzifer.core.Core;
import de.luzifer.core.model.check.Check;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.io.File;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Log {
    
    private static int days;
    
    private static final Date dateFile = new Date();
    private static final SimpleDateFormat formatFile = new SimpleDateFormat("dd MMMM yyyy");
    
    private static final File file = new File("plugins/AntiAC/Logs", formatFile.format(dateFile) + ".yml");
    private static final FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
    
    public static void load() {
        days = Core.getInstance().getConfig().getInt("AntiAC.DeleteLogsAfterDays");
    }
    
    public static void log(Player player, Integer clicks, Double average, @Nullable Check check) {
        
        try {
            cfg.load(file);
        } catch (Exception ignored) {}
        
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
        cfg.set("LogDetailed." + player.getName() + ".logMessage", "Logged by " + (check != null ? check.getClass().getSimpleName() : "System"));
        
        try {
            cfg.save(file);
        } catch (Exception ignored) {}
        
    }
    
    public static void deleteLogs() {
        Date xDaysAgo = Date.from(Instant.now().minus(Duration.ofDays(days)));
        SimpleDateFormat formatFile = new SimpleDateFormat("dd MMMM yyyy");
        
        if (new Date().after(xDaysAgo)) {
            File file = new File("plugins/AntiAC/Logs", formatFile.format(xDaysAgo) + ".yml");
            
            if (file.exists()) {
                file.delete();
                Core.getInstance().getLogger().info(" Deleted log of ///| " + formatFile.format(xDaysAgo) + " |///");
            }
        }
    }
    
    public static boolean isLogged(Player player) {
        try {
            cfg.load(file);
        } catch (Exception ignored) {}
        
        return cfg.getString("LogDetailed." + player.getName()) != null;
    }
    
}
