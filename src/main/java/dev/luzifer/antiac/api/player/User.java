package dev.luzifer.antiac.api.player;

import dev.luzifer.antiac.Core;
import dev.luzifer.antiac.api.enums.ViolationType;
import dev.luzifer.antiac.api.events.FlagEvent;
import dev.luzifer.antiac.api.profile.Profile;
import dev.luzifer.antiac.checks.DoubleClickCheck;
import dev.luzifer.antiac.utils.Variables;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class User {
    
    private static final List<User> allUser = new ArrayList<>();
    private final List<Integer> clicksAverage = new ArrayList<>();
    private final List<Double> clicksAverageCheck = new ArrayList<>();
    
    private final UUID uuid;
    
    public int clearViolations = 0;
    
    private int clicks = 0;
    private int violations = 0;
    
    private Long lastRightClick;
    private Long lastBlockClick;
    private int blockClicksInARow;
    private Long lastItemDrop;
    private User check;
    private Profile profile;
    
    private boolean frozen = false;
    private boolean restricted = false;
    private boolean notified = false;
    
    private User(UUID uuid) {
        this.uuid = uuid;
    }
    
    public static User get(UUID uuid) {
        
        if (!allUser.isEmpty()) {
            
            for (User user : allUser)
                if (user.getUniqueID().equals(uuid))
                    return user;
            
            User user = new User(uuid);
            
            Profile profile = new Profile(user);
            user.setProfile(profile);
            
            allUser.add(user);
            
            return user;
        } else {
            
            User user = new User(uuid);
            
            Profile profile = new Profile(user);
            user.setProfile(profile);
            
            allUser.add(user);
            
            return user;
        }
        
    }
    
    public static List<User> getAllUser() {
        return allUser;
    }
    
    public Profile getProfile() {
        return profile;
    }
    
    public void pluginBan() {
        
        if (Variables.executeBanCommand == null || Variables.executeBanCommand.isEmpty()) {
            
            SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            Date date = new Date();
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(date);
            calendar.add(Calendar.HOUR_OF_DAY, +Variables.unbanAfterHours);
            String date1 = format.format(calendar.getTime());
            ArrayList<String> reasonList = new ArrayList<>(Variables.BAN_REASON);
            String reason = "§cAnti§4AC \n " + String.join("\n ", reasonList).replace("&", "§").replaceAll("%date%", date1);
            Bukkit.getBanList(BanList.Type.NAME).addBan(getPlayer().getName(), reason, calendar.getTime(), null);
            getPlayer().kickPlayer(reason);
        } else {
            
            String execute = Variables.executeBanCommand;
            execute = execute.replaceAll("%player%", getPlayer().getName()).replace("&", "§");
            
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), execute);
        }
    }
    
    public void pluginKick() {
        
        if (Variables.executeKickCommand == null || Variables.executeKickCommand.isEmpty()) {
            
            ArrayList<String> reasonList = new ArrayList<>(Variables.KICK_REASON);
            getPlayer().kickPlayer("§cAnti§4AC \n " + String.join("\n ", reasonList).replace("&", "§"));
        } else {
            
            String execute = Variables.executeKickCommand;
            execute = execute.replace("%player%", getPlayer().getName()).replace("&", "§");
    
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), execute);
        }
    }
    
    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    public Long getLastRightClick() {
        return lastRightClick;
    }

    public Long getLastItemDrop() {
        return lastItemDrop;
    }

    public void setLastRightClick(Long lastRightClick) {
        this.lastRightClick = lastRightClick;
    }

    public void setLastItemDrop(Long lastItemDrop) {
        this.lastItemDrop = lastItemDrop;
    }

    @Deprecated
    public void setViolations(int violations) {
        this.violations = violations;
    }
    
    public void addViolation(ViolationType violationType) {
        this.violations = violations + violationType.getViolations();
        this.clearViolations = 0;
    }

    public int getBlockClicksInARow() {
        return blockClicksInARow;
    }

    public void setBlockClicksInARow(int blockClicksInARow) {
        this.blockClicksInARow = blockClicksInARow;
    }

    public void setLastBlockClick(Long lastBlockClick) {
        this.lastBlockClick = lastBlockClick;
    }

    public Long getLastBlockClick() {
        return lastBlockClick;
    }

    public void clearViolations() {
        this.violations = 0;
    }
    
    public int getViolations() {
        return violations;
    }
    
    public int getClicks() {
        return clicks;
    }
    
    public void setNotified(boolean notified) {
        this.notified = notified;
    }
    
    public String getName() {
        return getPlayer().getName();
    }
    
    public boolean isNotified() {
        return notified;
    }
    
    public void setFrozen(boolean frozen) {
        this.frozen = frozen;
    }
    
    public void setFrozen(boolean frozen, int duration) {
        this.setFrozen(frozen);
        Bukkit.getScheduler().runTaskLater(Core.getInstance(), () -> this.setFrozen(!frozen), 20 * duration);
    }
    
    public boolean isFrozen() {
        return frozen;
    }
    
    public void setRestricted(boolean restricted) {
        this.restricted = restricted;
    }

    public void setRestricted(boolean restricted, int duration) {
        this.setRestricted(restricted);
        Bukkit.getScheduler().runTaskLater(Core.getInstance(), () -> this.setRestricted(!restricted), 20 * duration);
    }
    
    public boolean isRestricted() {
        return restricted;
    }
    
    @Deprecated
    public void setClicks(int amount) {
        clicks = amount;
    }
    
    public void addClicks(int amount) {
        setClicks(getClicks() + amount);
        
        // Needed for DoubleClickCheck
        if (!DoubleClickCheck.latestClicks.containsKey(this))
            DoubleClickCheck.latestClicks.put(this, new ArrayList<>());
        
        List<Long> millis = DoubleClickCheck.latestClicks.get(this);
        millis.add(System.currentTimeMillis());
        
        DoubleClickCheck.latestClicks.put(this, millis);
    }

    public double getAverage() {
        return round(calculateAverage(clicksAverage));
    }
    
    public List<Double> getClicksAverageCheckList() {
        return clicksAverageCheck;
    }
    
    public void setChecked(User user) {
        this.check = user;
    }
    
    public User getChecked() {
        return check;
    }
    
    public List<Integer> getClicksAverageList() {
        return clicksAverage;
    }
    
    public int getPing() {
        
        if (getBukkitVersion() >= 17) return getPlayer().getPing();
        
        int ping = -1;
        try {
            
            if (getPlayer() == null) return ping;
            
            Object entityPlayer = getPlayer().getClass().getMethod("getHandle").invoke(getPlayer());
            ping = (int) entityPlayer.getClass().getField("ping").get(entityPlayer);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException | NoSuchFieldException e) {
            e.printStackTrace();
        }
        return ping;
    }
    
    public boolean isBypassed() {
        return getPlayer().hasPermission(Objects.requireNonNull(Core.getInstance().getConfig().getString("AntiAC.BypassPermission"))) || getPlayer().isOp() || getPlayer().hasPermission(Objects.requireNonNull(Core.getInstance().getConfig().getString("AntiAC.BypassPermission"))) && getPlayer().isOp();
    }
    
    public void sanction() {
        
        if (Variables.consoleNotify)
            Variables.TEAM_NOTIFY.forEach(var -> Bukkit.getConsoleSender().sendMessage(Core.prefix + var.replace("&", "§").replaceAll("%player%", getPlayer().getName()).replaceAll("%clicks%", String.valueOf(getClicks())).replaceAll("%average%", String.valueOf(getAverage())).replaceAll("%VL%", String.valueOf(getViolations()))));

        Bukkit.getPluginManager().callEvent(new FlagEvent(getPlayer(), getClicks(), getAverage(), getViolations()));

        shoutOutPunishment();
        informTeam();

        if (Variables.playerBan) {
            pluginBan();
        } else if (Variables.playerKick) {
            pluginKick();
        } else if (Variables.playerKill) {
            getPlayer().setHealth(0);
            Variables.PUNISHED.forEach(var -> getPlayer().sendMessage(Core.prefix + var.replace("&", "§")));
        } else if (Variables.playerFreeze) {
            if (!isFrozen()) {
                setFrozen(true, Variables.freezeTimeInSeconds);
                Variables.PUNISHED.forEach(var -> getPlayer().sendMessage(Core.prefix + var.replace("&", "§")));
            }
        } else if (Variables.restrictPlayer) {
            if (!isRestricted()) {
                setRestricted(true, Variables.restrictForSeconds);
                Variables.PUNISHED.forEach(var -> getPlayer().sendMessage(Core.prefix + var.replace("&", "§")));
            }
        }
        
        clearViolations();
    }
    
    private UUID getUniqueID() {
        return uuid;
    }
    
    private void setProfile(Profile profile) {
        this.profile = profile;
    }
    
    private void shoutOutPunishment() {
        if (Variables.shoutOutPunishment) {
            Objects.requireNonNull(getPlayer().getLocation().getWorld()).strikeLightningEffect(getPlayer().getLocation());
            Variables.SHOUTOUT_PUNISHMENT.forEach(var -> Bukkit.broadcastMessage(Core.prefix + var.replace("&", "§").replaceAll("%player%", getPlayer().getName())));

            for (Player others : Bukkit.getOnlinePlayers()) {
                Objects.requireNonNull(others.getLocation().getWorld()).spawnEntity(others.getLocation(), EntityType.FIREWORK_ROCKET);
            }
        }
    }
    
    private void informTeam() {
        if (Variables.informTeam) {
            for (Player team : Bukkit.getOnlinePlayers())
                informPlayerIfNotified(team);
        }
    }
    
    private void informPlayerIfNotified(Player player) {
        if (get(player.getUniqueId()).notified) {
            String name = getPlayer() == null ? Bukkit.getOfflinePlayer(uuid).getName() : getPlayer().getName();
            Variables.TEAM_NOTIFY.forEach(variable ->
                    player.sendMessage(Core.prefix + variable.replace("&", "§")
                            .replaceAll("%player%", name)
                            .replaceAll("%clicks%", String.valueOf(getClicks()))
                            .replaceAll("%average%", String.valueOf(getAverage()))
                            .replaceAll("%VL%", String.valueOf(getViolations()))));
        }
    }
    
    private double calculateAverage(List<Integer> marks) {
        Integer sum = 0;
        if (!marks.isEmpty()) {
            for (Integer mark : marks) {
                sum += mark;
            }
            return sum.doubleValue() / marks.size();
        }
        return sum;
    }
    
    private double round(double value) {
        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
    
    private double getBukkitVersion() {
        String version = Bukkit.getBukkitVersion().split("-")[0];
        return Double.parseDouble(version.split("\\.")[1]);
    }
}
