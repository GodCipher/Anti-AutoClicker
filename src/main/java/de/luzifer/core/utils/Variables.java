package de.luzifer.core.utils;

import de.luzifer.core.Core;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class Variables {
    
    private static final File MESSAGES_FILE = new File("plugins/AntiAC", "messages.yml");
    
    public static final List<String> PLAYER_OFFLINE = new ArrayList<>();
    public static final List<String> NOTIFY_ACTIVATED = new ArrayList<>();
    public static final List<String> NOTIFY_DEACTIVATED = new ArrayList<>();
    public static final List<String> NOTIFY_ALREADY_ACTIVATED = new ArrayList<>();
    public static final List<String> NOTIFY_ALREADY_DEACTIVATED = new ArrayList<>();
    public static final List<String> ON_CLICK_CHECK = new ArrayList<>();
    public static final List<String> ON_CLICK_CHECK_OFF = new ArrayList<>();
    public static final List<String> NOT_CHECKING_ANYONE = new ArrayList<>();
    public static final List<String> BAN_REASON = new ArrayList<>();
    public static final List<String> KICK_REASON = new ArrayList<>();
    public static final List<String> PLAYER_NOW_OFFLINE = new ArrayList<>();
    public static final List<String> PUNISHED = new ArrayList<>();
    public static final List<String> SHOUTOUT_PUNISHMENT = new ArrayList<>();
    public static final List<String> TEAM_NOTIFY = new ArrayList<>();
    public static final List<String> PUNISHED_PLAYER = new ArrayList<>();
    
    public static String executeBanCommand, executeKickCommand, perms;
    
    public static boolean excludeBedrockPlayers, clicksOnGround, consoleNotify, log, playerBan, shoutOutPunishment, informTeam, playerKick, playerKill, playerFreeze, restrictPlayer, bypass, pingChecker, autoNotify, doNotStoreNothing;
    
    public static int restrictForSeconds, unbanAfterHours, freezeTimeInSeconds, highestAllowedPing, clearVLMinutes, storeAsManyData, removeAfterExist, sanctionateAtViolations;
    
    public static void init() {
        
        PLAYER_OFFLINE.clear();
        NOTIFY_ACTIVATED.clear();
        NOTIFY_DEACTIVATED.clear();
        NOTIFY_ALREADY_ACTIVATED.clear();
        NOTIFY_ALREADY_DEACTIVATED.clear();
        ON_CLICK_CHECK.clear();
        ON_CLICK_CHECK_OFF.clear();
        NOT_CHECKING_ANYONE.clear();
        BAN_REASON.clear();
        KICK_REASON.clear();
        PLAYER_NOW_OFFLINE.clear();
        PUNISHED.clear();
        SHOUTOUT_PUNISHMENT.clear();
        TEAM_NOTIFY.clear();
        PUNISHED_PLAYER.clear();
        
        if (!MESSAGES_FILE.exists()) {
            try {
                copyInputStreamToFile(Core.getInstance().getResource("messages.yml"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(MESSAGES_FILE);
        
        try {
            cfg.load(MESSAGES_FILE);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
        
        configStrings(cfg);
        configBooleans();
        configInts();
    }
    
    private static void configStrings(FileConfiguration cfg) {
        PLAYER_OFFLINE.addAll(cfg.getStringList("Player-Not-Online"));
        NOTIFY_ACTIVATED.addAll(cfg.getStringList("Activate-Notify"));
        NOTIFY_DEACTIVATED.addAll(cfg.getStringList("Deactivate-Notify"));
        NOTIFY_ALREADY_ACTIVATED.addAll(cfg.getStringList("Notify-Already-Activated"));
        NOTIFY_ALREADY_DEACTIVATED.addAll(cfg.getStringList("Notify-Already-Deactivated"));
        ON_CLICK_CHECK.addAll(cfg.getStringList("On-Click-Check"));
        ON_CLICK_CHECK_OFF.addAll(cfg.getStringList("On-Click-Check-Off"));
        NOT_CHECKING_ANYONE.addAll(cfg.getStringList("Not-Checking-Anyone"));
        BAN_REASON.addAll(cfg.getStringList("Ban-Reason"));
        KICK_REASON.addAll(cfg.getStringList("Kick-Reason"));
        PLAYER_NOW_OFFLINE.addAll(cfg.getStringList("Player-Now-Offline"));
        PUNISHED.addAll(cfg.getStringList("Punished"));
        SHOUTOUT_PUNISHMENT.addAll(cfg.getStringList("ShoutOut-Punishment"));
        TEAM_NOTIFY.addAll(cfg.getStringList("Team-Notify"));
        PUNISHED_PLAYER.addAll(cfg.getStringList("Punished-Player"));
        
        executeBanCommand = Core.getInstance().getConfig().getString("AntiAC.ExecuteBanCommand");
        executeKickCommand = Core.getInstance().getConfig().getString("AntiAC.ExecuteKickCommand");
        perms = Core.getInstance().getConfig().getString("AntiAC.NeededPermission");
    }
    
    private static void configBooleans() {
        excludeBedrockPlayers = Core.getInstance().getConfig().getBoolean("AntiAC.ExcludeBedrockPlayers");
        clicksOnGround = Core.getInstance().getConfig().getBoolean("AntiAC.CountClicksOnBlocks");
        informTeam = Core.getInstance().getConfig().getBoolean("AntiAC.InformTeam");
        consoleNotify = Core.getInstance().getConfig().getBoolean("AntiAC.ConsoleNotification");
        log = Core.getInstance().getConfig().getBoolean("AntiAC.Log");
        playerBan = Core.getInstance().getConfig().getBoolean("AntiAC.PlayerBan");
        playerKick = Core.getInstance().getConfig().getBoolean("AntiAC.PlayerKick");
        playerKill = Core.getInstance().getConfig().getBoolean("AntiAC.PlayerKill");
        playerFreeze = Core.getInstance().getConfig().getBoolean("AntiAC.PlayerFreeze");
        restrictPlayer = Core.getInstance().getConfig().getBoolean("AntiAC.RestrictPlayer");
        shoutOutPunishment = Core.getInstance().getConfig().getBoolean("AntiAC.ShoutOutPunishment");
        bypass = Core.getInstance().getConfig().getBoolean("AntiAC.Bypass");
        pingChecker = Core.getInstance().getConfig().getBoolean("AntiAC.PingChecker");
        autoNotify = Core.getInstance().getConfig().getBoolean("AntiAC.AutoNotification");
        doNotStoreNothing = Core.getInstance().getConfig().getBoolean("AntiAC.Profile-Do-Not-Store-Nothing");
    }
    
    private static void configInts() {
        restrictForSeconds = Core.getInstance().getConfig().getInt("AntiAC.RestrictForSeconds");
        unbanAfterHours = Core.getInstance().getConfig().getInt("AntiAC.UnbanAfterHours");
        freezeTimeInSeconds = Core.getInstance().getConfig().getInt("AntiAC.FreezeTimeInSeconds");
        highestAllowedPing = Core.getInstance().getConfig().getInt("AntiAC.HighestAllowedPing");
        clearVLMinutes = Core.getInstance().getConfig().getInt("AntiAC.Clear-Violations-After-Minutes-Of-Non-Adding");
        storeAsManyData = Core.getInstance().getConfig().getInt("AntiAC.Profile-Store-As-Much-Data-In-One-DataContainer");
        removeAfterExist = Core.getInstance().getConfig().getInt("AntiAC.Remove-First-DataContainer-After-X-Exist");
        sanctionateAtViolations = Core.getInstance().getConfig().getInt("AntiAC.Just-Sanction-If-At-Violations");
    }

    private static void copyInputStreamToFile(InputStream in) throws IOException {

        if(in == null)
            return;

        try (OutputStream out = Files.newOutputStream(Variables.MESSAGES_FILE.toPath())) {
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
        }
    }
    
}