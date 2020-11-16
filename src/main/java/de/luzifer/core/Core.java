package de.luzifer.core;

import de.luzifer.core.api.check.Check;
import de.luzifer.core.api.events.ActionBarMessageEvent;
import de.luzifer.core.api.manager.CheckManager;
import de.luzifer.core.api.player.User;
import de.luzifer.core.api.profile.inventory.ProfileGUI;
import de.luzifer.core.checks.AverageCheck;
import de.luzifer.core.checks.ClickCheck;
import de.luzifer.core.commands.AntiACCommand;
import de.luzifer.core.extern.Metrics;
import de.luzifer.core.listener.Listeners;
import de.luzifer.core.timer.Timer;
import de.luzifer.core.timer.UpdateTimer;
import de.luzifer.core.utils.Variables;
import de.luzifer.core.utils.loader.AntiACClassLoader;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.StringUtil;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

public class Core extends JavaPlugin {

    private byte Tick = 0;
    public static double TPS = 0;
    private double LastFinish = 0;

    public static boolean lowTPS = false;

    public static String prefix;

    private static Plugin plugin;
    private static String nmsver;
    private static boolean useOldMethods;

    private static int days = 0;

    public void onEnable() {
        core = this;
        days = Core.getInstance().getConfig().getInt("AntiAC.DeleteLogsAfterDays");
        lowestAllowedTPS = getConfig().getInt("AntiAC.LowestAllowedTPS");
        Bukkit.getLogger().info("[AntiAC]");
        initialize();
        loadConfig();
        loadChecks();
        loadMessages();
        loadListener();
        loadCommands();
        loadActionBar();
        Bukkit.getLogger().info("[AntiAC]");
    }

    static class AntiACCommandTabCompleter implements TabCompleter {

        final String[] ARGS = {"check", "version", "notify", "checkupdate", "profile"};
        final String[] ARGS2 = {"on", "off"};

        @Override
        public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

            final List<String> complete = new ArrayList<>();

            if(args.length == 1) {

                StringUtil.copyPartialMatches(args[0], Arrays.asList(ARGS), complete);

                Collections.sort(complete);

            } else if(args.length == 2) {

                if(args[0].equalsIgnoreCase("check")) {

                    List<String> playerNames = new ArrayList<>();

                    for(Player all : Bukkit.getOnlinePlayers()) {
                        playerNames.add(all.getName());
                    }

                    Collections.sort(playerNames);

                    return playerNames;

                }

                if(args[0].equalsIgnoreCase("profile")) {
                    List<String> playerNames = new ArrayList<>();

                    for(Player all : Bukkit.getOnlinePlayers()) {
                        playerNames.add(all.getName());
                    }

                    Collections.sort(playerNames);

                    return playerNames;
                }

                if(args[0].equalsIgnoreCase("notify")) {

                    StringUtil.copyPartialMatches(args[1], Arrays.asList(ARGS2), complete);

                    Collections.sort(complete);
                }

            }

            return complete;
        }


    }

    private static Core core;
    public static Core getInstance() {
        return core;
    }

    public int lowestAllowedTPS;

    public void tpsChecker() {
        Bukkit.getLogger().info("[AntiAC] Booting up TPSChecker");
        Bukkit.getScheduler().runTaskTimer(this, () -> {
            Tick++;
            if (Tick == 20) {
                TPS = Tick;
                Tick = 0;
                if (LastFinish + 1000 < System.currentTimeMillis()) TPS /= (System.currentTimeMillis() - LastFinish) / 1000;
                LastFinish = System.currentTimeMillis();
                if(TPS < lowestAllowedTPS) {
                    if(!lowTPS) {
                        lowTPS = true;
                    }
                } else {
                    if(lowTPS) {
                        lowTPS = false;
                    }
                }
            }
        }, 1, 1);
    }

    public void onDisable() {
        saveDefaultConfig();

        for(Player all : Bukkit.getOnlinePlayers()) {
            if(all.getOpenInventory().getTopInventory().getHolder() instanceof ProfileGUI) {
                all.closeInventory();
            }
        }

    }

    public void initialize() {
        prefix = "§cAnti§4AC §8» ";
        new Metrics(this, 6473);
        try {
            Bukkit.getLogger().info("[AntiAC] Initialize complete");
            Thread.sleep(50);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadMessages() {

        Variables.init();

        try {
            Bukkit.getLogger().info("[AntiAC] Loading messages.yml complete");
            Thread.sleep(50);
        } catch (InterruptedException ignored) {}
    }

    public void loadConfig() {

        getConfig().options().copyDefaults();
        saveDefaultConfig();

        if(getConfig().getBoolean("AntiAC.AutoNotification")) {
            setNotified();
        }
        if(getConfig().getBoolean("AntiAC.TPSChecker")) {
            tpsChecker();
        }
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Timer(), 0, 20);
        if(getConfig().getBoolean("AntiAC.UpdateChecker")) {
            Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new UpdateTimer(this), 0, 20*(1800*3));
        }
        try {
            Bukkit.getLogger().info("[AntiAC] Loading config.yml complete");
            Thread.sleep(50);
        } catch (InterruptedException ignored) {}
    }

    public void loadCommands() {
        Objects.requireNonNull(getCommand("antiac")).setExecutor(new AntiACCommand());
        Objects.requireNonNull(getCommand("antiac")).setTabCompleter(new AntiACCommandTabCompleter());

        try {
            Bukkit.getLogger().info("[AntiAC] Loading Commands complete");
            Thread.sleep(50);
        } catch (InterruptedException ignored) {}
    }

    public static void deleteLogs() {
        Date xDaysAgo = Date.from(Instant.now().minus(Duration.ofDays(days)));
        SimpleDateFormat formatFile = new SimpleDateFormat("dd MMMM yyyy");

        if(new Date().after(xDaysAgo)) {
            File file = new File("plugins/AntiAC/Logs", formatFile.format(xDaysAgo) + ".yml");

            if(file.exists()) {
                file.delete();
                Bukkit.getLogger().info("[AntiAC] Deleted log of ///| " + formatFile.format(xDaysAgo) + " |///");
            }
        }
    }

    public void loadActionBar() {
        Core.plugin = this;
        Core.nmsver = Bukkit.getServer().getClass().getPackage().getName();
        Core.nmsver = Core.nmsver.substring(Core.nmsver.lastIndexOf(".") + 1);
        if (Core.nmsver.equalsIgnoreCase("v1_8_R1") || Core.nmsver.startsWith("v1_7_")) {
            Core.useOldMethods = true;
        }

        try {
            Bukkit.getLogger().info("[AntiAC] Loading ActionBarAPI complete");
            Thread.sleep(50);
        } catch (InterruptedException ignored) {}
    }

    public void loadListener() {
        Bukkit.getPluginManager().registerEvents(new Listeners(this), this);

        try {
            Bukkit.getLogger().info("[AntiAC] Loading Listeners complete");
            Thread.sleep(50);
        } catch (InterruptedException ignored) {}
    }

    private void addFromDir(File dir) {

        AntiACClassLoader loader = new AntiACClassLoader();

        Arrays.stream(Objects.requireNonNull(dir.listFiles())).forEach(jar -> {
            if(jar.getName().endsWith(".jar")) {
                if(!loader.findClasses(getInstance().getClassLoader(), jar, Check.class).isEmpty()) {
                    for(Class<? extends Check> clazz : loader.findClasses(getInstance().getClassLoader(), jar, Check.class)) {

                        try {
                            Check checkInstance = clazz.getConstructor().newInstance();
                            checkInstance.onEnable(this);

                            CheckManager.registerCheck(checkInstance);
                        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                            if(!(e instanceof InstantiationException)) {
                                e.printStackTrace();
                            }
                        }

                    }
                }
            }
        });

    }

    public void loadChecks() {

        File file = new File("plugins/AntiAC/Checks");

        if(!file.exists())
            file.mkdirs();

        addFromDir(file);
        addFromDir(new File("plugins"));

        Bukkit.getLogger().info("[AntiAC] Registered " + CheckManager.getChecks().size() + " Checks");

    }

    public void setNotified() {
        for(Player all : Bukkit.getOnlinePlayers()) {
            Bukkit.getScheduler().runTaskLater(this, () -> {
                if(all.hasPermission(Objects.requireNonNull(getConfig().getString("AntiAC.NeededPermission"))) || all.isOp()) {
                    User.get(all.getUniqueId()).setNotified(true);
                    Variables.NOTIFY_ACTIVATED.forEach(var -> all.sendMessage(prefix + var.replace("&", "§")));
                }
            }, 15);
        }
    }

    public static void sendActionBar(final Player player, final String message) {
        if (!player.isOnline()) {
            return;
        }
        final ActionBarMessageEvent actionBarMessageEvent = new ActionBarMessageEvent(player, message);
        Bukkit.getPluginManager().callEvent(actionBarMessageEvent);
        if (actionBarMessageEvent.isCancelled()) {
            return;
        }

        if(Bukkit.getVersion().contains("1.16")) {

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
            }
            else {
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
                }
                catch (ClassNotFoundException cnfe) {
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
        }
        catch (Exception e) {
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

    public static void sendActionBarToAllPlayers(final String message) {
        sendActionBarToAllPlayers(message, -1);
    }

    public static void sendActionBarToAllPlayers(final String message, final int duration) {
        for (final Player p : Bukkit.getOnlinePlayers()) {
            sendActionBar(p, message, duration);
        }
    }

    static {
        Core.useOldMethods = false;
    }
}
