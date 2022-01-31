package de.luzifer.core;

import de.luzifer.core.api.check.Check;
import de.luzifer.core.api.check.CheckManager;
import de.luzifer.core.api.player.User;
import de.luzifer.core.api.profile.inventory.pagesystem.Menu;
import de.luzifer.core.checks.AverageCheck;
import de.luzifer.core.checks.ClickLimitCheck;
import de.luzifer.core.checks.DoubleClickCheck;
import de.luzifer.core.checks.LevelCheck;
import de.luzifer.core.commands.AntiACCommand;
import de.luzifer.core.commands.AntiACCommandTabCompleter;
import de.luzifer.core.extern.Metrics;
import de.luzifer.core.listener.Listeners;
import de.luzifer.core.timer.CheckTimer;
import de.luzifer.core.timer.UpdateTimer;
import de.luzifer.core.utils.ActionBarUtil;
import de.luzifer.core.utils.InputStreamUtils;
import de.luzifer.core.utils.Variables;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Objects;
import java.util.logging.Logger;

public class Core extends JavaPlugin {
    
    public static String prefix;
    public static boolean lowTPS = false;
    public static double TPS = 0;
    
    private static int days = 0;
    
    private static Core core;
    
    public static Core getInstance() {
        return core;
    }
    
    public static void deleteLogs() {
        Date xDaysAgo = Date.from(Instant.now().minus(Duration.ofDays(days)));
        SimpleDateFormat formatFile = new SimpleDateFormat("dd MMMM yyyy");
        
        if (new Date().after(xDaysAgo)) {
            File file = new File("plugins/AntiAC/Logs", formatFile.format(xDaysAgo) + ".yml");
            
            if (file.exists()) {
                file.delete();
                getInstance().logger.info(" Deleted log of ///| " + formatFile.format(xDaysAgo) + " |///");
            }
        }
    }
    
    private final CheckManager checkManager = new CheckManager();
    private final Logger logger = getLogger();
    
    public int lowestAllowedTPS;
    
    private byte Tick = 0;
    private double LastFinish = 0;
    
    private String pluginVersion;
    
    public void tpsChecker() {
        logger.info("Booting up TPSChecker");
        Bukkit.getScheduler().runTaskTimer(this, () -> {
            
            Tick++;
            if (Tick == 20) {
                
                TPS = Tick;
                Tick = 0;
                
                if (LastFinish + 1000 < System.currentTimeMillis())
                    TPS /= (System.currentTimeMillis() - LastFinish) / 1000;
                
                LastFinish = System.currentTimeMillis();
                lowTPS = TPS < lowestAllowedTPS;
            }
        }, 1, 1);
    }
    
    public void onDisable() {
        
        saveDefaultConfig();
        
        for (Player all : Bukkit.getOnlinePlayers()) {
            if (all.getOpenInventory().getTopInventory().getHolder() instanceof Menu) all.closeInventory();
        }
    }
    
    public void onEnable() {
        
        core = this;
        initialize();
        fetchPluginVersion();
        loadConfig();
        loadChecks();
        loadMessages();
        loadListener();
        loadCommands();
        
        ActionBarUtil.load();
        logger.info("Loading ActionBarAPI complete");
    }
    
    public void initialize() {
        prefix = "§cAnti§4AC §8» ";
        new Metrics(this, 6473);
        logger.info("Initialize complete");
    }
    
    public void loadMessages() {
        
        Variables.init();
        logger.info("Loading messages.yml complete");
    }
    
    public void loadConfig() {
        
        getConfig().options().copyDefaults();
        saveDefaultConfig();
        
        days = Core.getInstance().getConfig().getInt("AntiAC.DeleteLogsAfterDays");
        lowestAllowedTPS = getConfig().getInt("AntiAC.LowestAllowedTPS");
        
        if (getConfig().getBoolean("AntiAC.AutoNotification")) setNotified();
        
        if (getConfig().getBoolean("AntiAC.TPSChecker")) tpsChecker();
        
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new CheckTimer(checkManager), 0, 20);
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, Core::deleteLogs, 0, 20 * 60 * 60 * 12 /* 12 hours */);
        
        if (getConfig().getBoolean("AntiAC.UpdateChecker"))
            Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new UpdateTimer(this), 0, 20 * 60 * 5);
        
        logger.info("Loading config.yml complete");
    }
    
    public void loadCommands() {
        
        getCommand("antiac").setExecutor(new AntiACCommand(this));
        getCommand("antiac").setTabCompleter(new AntiACCommandTabCompleter());
        
        logger.info("Loading Command(s) complete");
    }
    
    public void loadListener() {
        
        Bukkit.getPluginManager().registerEvents(new Listeners(this), this);
        logger.info("Loading Listener(s) complete");
    }
    
    public void loadChecks() {
        
        checkManager.registerCheck(new AverageCheck());
        checkManager.registerCheck(new ClickLimitCheck());
        checkManager.registerCheck(new DoubleClickCheck());
        checkManager.registerCheck(new LevelCheck());
        
        Bukkit.getScheduler().runTaskLater(this, () -> {
            
            for (Check check : checkManager.getChecks()) {
                try {
                    check.load();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            
            logger.info("Loaded " + (int) checkManager.getChecks().stream().filter(Check::isLoaded).count() + " Check(s)");
        }, 1);
        
    }
    
    public void reloadChecks() {
        
        for (Check check : checkManager.getChecks()) {
            try {
                check.unload();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        checkManager.unregisterAll();
        loadChecks();
    }
    
    public void setNotified() {
        for (Player all : Bukkit.getOnlinePlayers()) {
            Bukkit.getScheduler().runTaskLater(this, () -> {
                if (all.hasPermission(Objects.requireNonNull(getConfig().getString("AntiAC.NeededPermission"))) || all.isOp()) {
                    User.get(all.getUniqueId()).setNotified(true);
                    Variables.NOTIFY_ACTIVATED.forEach(var -> all.sendMessage(prefix + var.replace("&", "§")));
                }
            }, 15);
        }
    }
    
    public String getPluginVersion() {
        return pluginVersion;
    }
    
    private void fetchPluginVersion() {
        
        InputStream inputStream = InputStreamUtils.getInputStream("version.txt");
        this.pluginVersion = InputStreamUtils.readLineFromInputStream(inputStream, logger);
    }
}
