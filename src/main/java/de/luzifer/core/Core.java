package de.luzifer.core;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import de.luzifer.core.checks.AverageCheck;
import de.luzifer.core.checks.ClickLimitCheck;
import de.luzifer.core.checks.DoubleClickCheck;
import de.luzifer.core.checks.LevelCheck;
import de.luzifer.core.commands.AntiACCommand;
import de.luzifer.core.commands.AntiACCommandTabCompleter;
import de.luzifer.core.extern.Metrics;
import de.luzifer.core.listener.packet.ArmAnimationListener;
import de.luzifer.core.listener.bukkit.Listeners;
import de.luzifer.core.model.check.Check;
import de.luzifer.core.model.check.CheckManager;
import de.luzifer.core.model.log.Log;
import de.luzifer.core.model.profile.inventory.pagesystem.Menu;
import de.luzifer.core.model.user.User;
import de.luzifer.core.timer.CheckTimer;
import de.luzifer.core.timer.UpdateTimer;
import de.luzifer.core.utils.ActionBarUtil;
import de.luzifer.core.utils.InputStreamUtils;
import de.luzifer.core.utils.Variables;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.InputStream;
import java.util.Objects;
import java.util.logging.Logger;

public class Core extends JavaPlugin {
    
    public static String prefix;
    public static boolean lowTPS = false;
    public static double TPS = 0;
    
    private static Core core;
    
    public static Core getInstance() {
        return core;
    }
    
    private final CheckManager checkManager = new CheckManager();
    private final Logger logger = getLogger();
    
    public int lowestAllowedTPS;
    
    private byte Tick = 0;
    private double LastFinish = 0;
    
    private String pluginVersion;
    
    private ProtocolManager protocolManager;
    
    @Override
    public void onLoad() {
        protocolManager = ProtocolLibrary.getProtocolManager();
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
    
    public String getPluginVersion() {
        return pluginVersion;
    }
    
    private void tpsChecker() {
        
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
    
    private void initialize() {
        
        prefix = "§cAnti§4AC §8» ";
        new Metrics(this, 6473);
        logger.info("Initialize complete");
    }
    
    private void loadMessages() {
        
        Variables.init();
        logger.info("Loading messages.yml complete");
    }
    
    private void loadConfig() {
        
        getConfig().options().copyDefaults();
        saveDefaultConfig();
        
        Log.load();
        lowestAllowedTPS = getConfig().getInt("AntiAC.LowestAllowedTPS");
        
        if (getConfig().getBoolean("AntiAC.AutoNotification")) setNotified();
        
        if (getConfig().getBoolean("AntiAC.TPSChecker")) tpsChecker();
        
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new CheckTimer(checkManager), 0, 20);
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, Log::deleteLogs, 0, 20 * 60 * 60 * 12 /* 12 hours */);
        
        if (getConfig().getBoolean("AntiAC.UpdateChecker"))
            Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new UpdateTimer(this), 0, 20 * 60 * 5);
        
        logger.info("Loading config.yml complete");
    }
    
    private void loadCommands() {
        
        getCommand("antiac").setExecutor(new AntiACCommand(this));
        getCommand("antiac").setTabCompleter(new AntiACCommandTabCompleter());
        
        logger.info("Loading Command(s) complete");
    }
    
    private void loadListener() {
        
        protocolManager.addPacketListener(new ArmAnimationListener(this, PacketType.Play.Client.ARM_ANIMATION));
        
        Bukkit.getPluginManager().registerEvents(new Listeners(this), this);
        logger.info("Loading Listener(s) complete");
    }
    
    private void setNotified() {
        for (Player all : Bukkit.getOnlinePlayers()) {
            Bukkit.getScheduler().runTaskLater(this, () -> {
                if (all.hasPermission(Objects.requireNonNull(getConfig().getString("AntiAC.NeededPermission"))) || all.isOp()) {
                    User.get(all.getUniqueId()).setNotified(true);
                    Variables.NOTIFY_ACTIVATED.forEach(var -> all.sendMessage(prefix + var.replace("&", "§")));
                }
            }, 15);
        }
    }
    
    private void fetchPluginVersion() {
        
        InputStream inputStream = InputStreamUtils.getInputStream("version.txt");
        this.pluginVersion = InputStreamUtils.readLineFromInputStream(inputStream, logger);
    }
}
