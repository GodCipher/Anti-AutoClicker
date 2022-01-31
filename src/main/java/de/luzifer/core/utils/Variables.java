package de.luzifer.core.utils;

import de.luzifer.core.Core;
import lombok.experimental.UtilityClass;
import org.apache.commons.io.FileUtils;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

@UtilityClass
public class Variables {
    
    private static final File file = new File("plugins/AntiAC", "messages.yml");
    
    public static ArrayList<String> PLAYER_OFFLINE = new ArrayList<>();
    public static ArrayList<String> NOTIFY_ACTIVATED = new ArrayList<>();
    public static ArrayList<String> NOTIFY_DEACTIVATED = new ArrayList<>();
    public static ArrayList<String> NOTIFY_ALREADY_ACTIVATED = new ArrayList<>();
    public static ArrayList<String> NOTIFY_ALREADY_DEACTIVATED = new ArrayList<>();
    public static ArrayList<String> ON_CLICK_CHECK = new ArrayList<>();
    public static ArrayList<String> ON_CLICK_CHECK_OFF = new ArrayList<>();
    public static ArrayList<String> NOT_CHECKING_ANYONE = new ArrayList<>();
    public static ArrayList<String> BAN_REASON = new ArrayList<>();
    public static ArrayList<String> KICK_REASON = new ArrayList<>();
    public static ArrayList<String> PLAYER_NOW_OFFLINE = new ArrayList<>();
    public static ArrayList<String> PUNISHED = new ArrayList<>();
    public static ArrayList<String> SHOUTOUT_PUNISHMENT = new ArrayList<>();
    public static ArrayList<String> TEAM_NOTIFY = new ArrayList<>();
    public static ArrayList<String> PUNISHED_PLAYER = new ArrayList<>();
    
    public static String executeBanCommand, executeKickCommand, perms;
    
    public static boolean consoleNotify, log, playerBan, shoutOutPunishment, informTeam, playerKick, playerKill, playerFreeze, restrictPlayer, bypass, pingChecker, autoNotify, doNotStoreNothing;
    
    public static int unbanAfterHours, freezeTimeInSeconds, highestAllowedPing, clearVLMinutes, storeAsManyData, removeAfterExist, sanctionateAtViolations;
    
    public static void init() {
        
        PLAYER_OFFLINE = new ArrayList<>();
        NOTIFY_ACTIVATED = new ArrayList<>();
        NOTIFY_DEACTIVATED = new ArrayList<>();
        NOTIFY_ALREADY_ACTIVATED = new ArrayList<>();
        NOTIFY_ALREADY_DEACTIVATED = new ArrayList<>();
        ON_CLICK_CHECK = new ArrayList<>();
        ON_CLICK_CHECK_OFF = new ArrayList<>();
        NOT_CHECKING_ANYONE = new ArrayList<>();
        BAN_REASON = new ArrayList<>();
        KICK_REASON = new ArrayList<>();
        PLAYER_NOW_OFFLINE = new ArrayList<>();
        PUNISHED = new ArrayList<>();
        SHOUTOUT_PUNISHMENT = new ArrayList<>();
        TEAM_NOTIFY = new ArrayList<>();
        PUNISHED_PLAYER = new ArrayList<>();
        
        if (!file.exists()) {
            try {
                FileUtils.copyInputStreamToFile(Core.getInstance().getResource("messages.yml"), file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        
        try {
            cfg.load(file);
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
        unbanAfterHours = Core.getInstance().getConfig().getInt("AntiAC.UnbanAfterHours");
        freezeTimeInSeconds = Core.getInstance().getConfig().getInt("AntiAC.FreezeTimeInSeconds");
        highestAllowedPing = Core.getInstance().getConfig().getInt("AntiAC.HighestAllowedPing");
        clearVLMinutes = Core.getInstance().getConfig().getInt("AntiAC.Clear-Violations-After-Minutes-Of-Non-Adding");
        storeAsManyData = Core.getInstance().getConfig().getInt("AntiAC.Profile-Store-As-Much-Data-In-One-DataContainer");
        removeAfterExist = Core.getInstance().getConfig().getInt("AntiAC.Remove-First-DataContainer-After-X-Exist");
        sanctionateAtViolations = Core.getInstance().getConfig().getInt("AntiAC.Just-Sanction-If-At-Violations");
    }
    
}