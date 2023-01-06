package dev.luzifer.antiac;

import dev.luzifer.antiac.api.check.Check;
import dev.luzifer.antiac.api.events.ActionBarMessageEvent;
import dev.luzifer.antiac.api.check.CheckManager;
import dev.luzifer.antiac.api.player.User;
import dev.luzifer.antiac.api.profile.inventory.pagesystem.Menu;
import dev.luzifer.antiac.checks.AverageCheck;
import dev.luzifer.antiac.checks.ClickLimitCheck;
import dev.luzifer.antiac.checks.DoubleClickCheck;
import dev.luzifer.antiac.checks.LevelCheck;
import dev.luzifer.antiac.commands.AntiACCommand;
import dev.luzifer.antiac.commands.AntiACCommandTabCompleter;
import dev.luzifer.antiac.extern.Metrics;
import dev.luzifer.antiac.listener.Listeners;
import dev.luzifer.antiac.timer.CheckTimer;
import dev.luzifer.antiac.timer.UpdateTimer;
import dev.luzifer.antiac.utils.InputStreamUtils;
import dev.luzifer.antiac.utils.Variables;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Objects;
import java.util.logging.Logger;

public class Core extends JavaPlugin {
    
    public static String prefix;
    
    public static boolean lowTps = false;
    
    public static double tps = 0;
    
    private static Plugin plugin;
    private static String nmsver;
    private static boolean useOldMethods;
    
    private static int days = 0;
    private static Core core;
    
    static {
        Core.useOldMethods = false;
    }
    
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
    
    public static void sendActionBar(final Player player, final String message) {
        
        if (!player.isOnline()) return;
        
        ActionBarMessageEvent actionBarMessageEvent = new ActionBarMessageEvent(player, message);
        Bukkit.getPluginManager().callEvent(actionBarMessageEvent);
        if (actionBarMessageEvent.isCancelled()) return;
        
        if (getBukkitVersion() >= 16) {
            
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message));
            return;
        }
        
        try {
            final Class<?> craftPlayerClass = Class.forName("org.bukkit.craftbukkit." + Core.nmsver + ".entity.CraftPlayer");
            final Object craftPlayer = craftPlayerClass.cast(player);
            final Class<?> packetPlayOutChatClass = Class.forName("net.minecraft.server." + Core.nmsver + ".PacketPlayOutChat");
            final Class<?> packetClass = Class.forName("net.minecraft.server." + Core.nmsver + ".Packet");
            Object packet;
            if (Core.useOldMethods) {
                final Class<?> chatSerializerClass = Class.forName("net.minecraft.server." + Core.nmsver + ".ChatSerializer");
                final Class<?> iChatBaseComponentClass = Class.forName("net.minecraft.server." + Core.nmsver + ".IChatBaseComponent");
                final Method m3 = chatSerializerClass.getDeclaredMethod("a", String.class);
                final Object cbc = iChatBaseComponentClass.cast(m3.invoke(chatSerializerClass, "{\"text\": \"" + message + "\"}"));
                packet = packetPlayOutChatClass.getConstructor(iChatBaseComponentClass, Byte.TYPE).newInstance(cbc, 2);
            } else {
                final Class<?> chatComponentTextClass = Class.forName("net.minecraft.server." + Core.nmsver + ".ChatComponentText");
                final Class<?> iChatBaseComponentClass = Class.forName("net.minecraft.server." + Core.nmsver + ".IChatBaseComponent");
                try {
                    final Class<?> chatMessageTypeClass = Class.forName("net.minecraft.server." + Core.nmsver + ".ChatMessageType");
                    final Object[] chatMessageTypes = chatMessageTypeClass.getEnumConstants();
                    Object chatMessageType = null;
                    for (final Object obj : chatMessageTypes) {
                        if (obj.toString().equals("GAME_INFO")) {
                            chatMessageType = obj;
                        }
                    }
                    final Object chatCompontentText = chatComponentTextClass.getConstructor(String.class).newInstance(message);
                    packet = packetPlayOutChatClass.getConstructor(iChatBaseComponentClass, chatMessageTypeClass).newInstance(chatCompontentText, chatMessageType);
                } catch (ClassNotFoundException cnfe) {
                    final Object chatCompontentText2 = chatComponentTextClass.getConstructor(String.class).newInstance(message);
                    packet = packetPlayOutChatClass.getConstructor(iChatBaseComponentClass, Byte.TYPE).newInstance(chatCompontentText2, (byte) 2);
                }
            }
            final Method craftPlayerHandleMethod = craftPlayerClass.getDeclaredMethod("getHandle");
            final Object craftPlayerHandle = craftPlayerHandleMethod.invoke(craftPlayer);
            final Field playerConnectionField = craftPlayerHandle.getClass().getDeclaredField("playerConnection");
            final Object playerConnection = playerConnectionField.get(craftPlayerHandle);
            final Method sendPacketMethod = playerConnection.getClass().getDeclaredMethod("sendPacket", packetClass);
            sendPacketMethod.invoke(playerConnection, packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void sendActionBar(final Player player, final String message, int duration) {
        sendActionBar(player, message);
        if (duration >= 0) {
            new BukkitRunnable() {
                
                public void run() {
                    Core.sendActionBar(player, "");
                }
            }.runTaskLaterAsynchronously(Core.plugin, duration + 1);
        }
        while (duration > 40) {
            duration -= 40;
            new BukkitRunnable() {
                
                public void run() {
                    Core.sendActionBar(player, message);
                }
            }.runTaskLaterAsynchronously(Core.plugin, duration);
        }
    }
    
    private static double getBukkitVersion() {
        
        String version = Bukkit.getBukkitVersion().split("-")[0];
        return Double.parseDouble(version.split("\\.")[1]);
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
                
                tps = Tick;
                Tick = 0;
                
                if (LastFinish + 1000 < System.currentTimeMillis())
                    tps /= (System.currentTimeMillis() - LastFinish) / 1000;
                
                LastFinish = System.currentTimeMillis();
                lowTps = tps < lowestAllowedTPS;
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
        loadActionBar();
    }
    
    public void initialize() {
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

        prefix = getConfig().getString("AntiAC.Prefix"); // Soon moving to Variables.class
        days = getConfig().getInt("AntiAC.DeleteLogsAfterDays");
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
    
    public void loadActionBar() {
        
        plugin = this;
        
        nmsver = Bukkit.getServer().getClass().getPackage().getName();
        nmsver = nmsver.substring(nmsver.lastIndexOf(".") + 1);
        
        if (nmsver.equalsIgnoreCase("v1_8_R1") || Core.nmsver.startsWith("v1_7_")) Core.useOldMethods = true;
        
        logger.info("Loading ActionBarAPI complete");
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
        
        for(Check check : checkManager.getChecks()) {
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
