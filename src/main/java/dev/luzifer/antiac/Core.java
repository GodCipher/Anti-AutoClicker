package dev.luzifer.antiac;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.EventManager;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import com.github.retrooper.packetevents.util.TimeStampMode;
import dev.luzifer.antiac.api.check.Check;
import dev.luzifer.antiac.api.check.CheckManager;
import dev.luzifer.antiac.api.events.ActionBarMessageEvent;
import dev.luzifer.antiac.api.player.User;
import dev.luzifer.antiac.api.profile.inventory.pagesystem.Menu;
import dev.luzifer.antiac.checks.AverageCheck;
import dev.luzifer.antiac.checks.ClickLimitCheck;
import dev.luzifer.antiac.checks.DoubleClickCheck;
import dev.luzifer.antiac.checks.LevelCheck;
import dev.luzifer.antiac.commands.AntiACCommand;
import dev.luzifer.antiac.commands.AntiACCommandTabCompleter;
import dev.luzifer.antiac.extern.Metrics;
import dev.luzifer.antiac.listener.BlockInteractionPacketListener;
import dev.luzifer.antiac.listener.InteractEntityPacketListener;
import dev.luzifer.antiac.listener.Listeners;
import dev.luzifer.antiac.timer.CheckTimer;
import dev.luzifer.antiac.timer.UpdateTimer;
import dev.luzifer.antiac.utils.InputStreamUtils;
import dev.luzifer.antiac.utils.Variables;
import dev.luzifer.antiac.version.UpdateChecker;
import dev.luzifer.antiac.version.UpdateCheckerResult;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.logging.Logger;

public class Core extends JavaPlugin {
    
    public static String prefix;
    
    public static boolean lowTps = false;
    
    public static double tps = 0;
    
    private static String nmsver;
    private static boolean useOldMethods;
    
    private static Core core;
    
    static {
        Core.useOldMethods = false;
    }
    
    public static Core getInstance() {
        return core;
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

    private static double getBukkitVersion() {
        String version = Bukkit.getBukkitVersion().split("-")[0];
        return Double.parseDouble(version.split("\\.")[1]);
    }
    
    private final CheckManager checkManager = new CheckManager();
    private final Logger logger = getLogger();
    
    public int lowestAllowedTPS;
    
    private byte tick = 0;
    private double lastFinish = 0;
    
    private String pluginVersion;
    
    public void tpsChecker() {
        Bukkit.getScheduler().runTaskTimer(this, () -> {
            
            tick++;
            if (tick == 20) {
                
                tps = tick;
                tick = 0;
                
                if (lastFinish + 1000 < System.currentTimeMillis())
                    tps /= (System.currentTimeMillis() - lastFinish) / 1000;
                
                lastFinish = System.currentTimeMillis();
                lowTps = tps < lowestAllowedTPS;
            }
        }, 1, 1);
    }

    @Override
    public void onDisable() {
        
        saveDefaultConfig();
        
        for (Player all : Bukkit.getOnlinePlayers()) {
            if (all.getOpenInventory().getTopInventory().getHolder() instanceof Menu) all.closeInventory();
        }
    }

    @Override
    public void onLoad() {
        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this));
        PacketEvents.getAPI().load();
    }

    @Override
    public void onEnable() {
        setupPacketEvents();
        
        core = this;
        initialize();
        fetchPluginVersion();
        loadConfig();
        loadChecks();
        loadMessages();
        loadListener();
        registerProtocolListener();
        loadCommands();
        loadActionBar();
        
        if (getConfig().getBoolean("AntiAC.UpdateChecker"))
            checkForUpdate();
    }

    private void setupPacketEvents() {
        PacketEvents.getAPI().getSettings()
                .debug(false)
                .bStats(false)
                .checkForUpdates(false)
                .timeStampMode(TimeStampMode.MILLIS)
                .reEncodeByDefault(true);
        PacketEvents.getAPI().init();
    }

    private void checkForUpdate() {
        UpdateChecker updateChecker = new UpdateChecker(this);
        Bukkit.getScheduler().runTaskAsynchronously(core, () -> {
            UpdateCheckerResult updateCheckerResult = updateChecker.checkUpdate();

            if (updateCheckerResult.isUpdateAvailable()) {
                Bukkit.getScheduler().runTask(core, () -> Bukkit.getConsoleSender().sendMessage("§a[AntiAC] §rThere is an update available. " +
                        "§c[" + updateCheckerResult.getOldVersion() + "] §e-> §a[" + updateCheckerResult.getNewVersion() + "]"));
            }
        });
    }

    private void registerProtocolListener() {
        EventManager eventManager = PacketEvents.getAPI().getEventManager();
        eventManager.registerListener(new InteractEntityPacketListener(), PacketListenerPriority.NORMAL);
        eventManager.registerListener(new BlockInteractionPacketListener(), PacketListenerPriority.NORMAL);
    }
    
    public void initialize() {
        new Metrics(this, 6473);
    }
    
    public void loadMessages() {
        Variables.init();
        logger.info("Loaded messages successful");
    }
    
    public void loadConfig() {
        
        getConfig().options().copyDefaults();
        saveDefaultConfig();

        prefix = getConfig().getString("AntiAC.Prefix"); // Soon moving to Variables.class
        lowestAllowedTPS = getConfig().getInt("AntiAC.LowestAllowedTPS");
        
        if (getConfig().getBoolean("AntiAC.AutoNotification")) setNotified();
        
        if (getConfig().getBoolean("AntiAC.TPSChecker")) tpsChecker();
        
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new CheckTimer(checkManager), 0, 20);

        if (getConfig().getBoolean("AntiAC.UpdateChecker"))
            Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new UpdateTimer(this), 0, 20 * 60 * 5);

        logger.info("Loaded configuration successful");
    }
    
    public void loadCommands() {
        getCommand("antiac").setExecutor(new AntiACCommand(this));
        getCommand("antiac").setTabCompleter(new AntiACCommandTabCompleter());
    }
    
    public void loadActionBar() {
        nmsver = Bukkit.getServer().getClass().getPackage().getName();
        nmsver = nmsver.substring(nmsver.lastIndexOf(".") + 1);
        
        if (nmsver.equalsIgnoreCase("v1_8_R1") || Core.nmsver.startsWith("v1_7_")) Core.useOldMethods = true;
    }
    
    public void loadListener() {
        Bukkit.getPluginManager().registerEvents(new Listeners(this), this);
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
