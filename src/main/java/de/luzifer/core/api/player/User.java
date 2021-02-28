package de.luzifer.core.api.player;

import de.luzifer.core.Core;
import de.luzifer.core.api.enums.ViolationType;
import de.luzifer.core.api.log.Log;
import de.luzifer.core.api.profile.Profile;
import de.luzifer.core.checks.DoubleClickCheck;
import de.luzifer.core.utils.Variables;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.*;

public class User {

    private int clicks = 0;

    private Long lastRightClick;

    private Profile profile;

    private final List<Integer> clicksAverage = new ArrayList<>();
    private final List<Double> clicksAverageCheck = new ArrayList<>();

    private int violations = 0;

    private boolean frozen = false;
    private boolean restricted = false;
    private boolean notified = false;

    public int clearViolations = 0;

    private UUID uuid;
    private User check;

    private static List<User> allUser = new ArrayList<>();

    public static User get(UUID uuid) {

        if(!allUser.isEmpty()) {

            for(User user : allUser) {

                if(user.getUniqueID().equals(uuid)) {

                    return user;

                }

            }

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

    private User(UUID uuid) {

        this.uuid = uuid;

    }

    private UUID getUniqueID() {

        return uuid;

    }

    public Profile getProfile() {
        return profile;
    }

    private void setProfile(Profile profile) {
        this.profile = profile;
    }

    public void pluginBan() {

        if(Variables.executeBanCommand.equals("") ||
                Variables.executeBanCommand == null) {

            SimpleDateFormat format = new SimpleDateFormat("dd-MM-YYYY HH:mm:ss");
            Date date = new Date();
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(date);
            calendar.add(Calendar.HOUR_OF_DAY, +Variables.unbanAfterHours);
            String date1 = format.format(calendar.getTime());
            String bumper = org.apache.commons.lang.StringUtils.repeat("\n", 35);
            ArrayList<String> reasonList = new ArrayList<>(Variables.BAN_REASON);
            String reason = bumper + "§cAnti§4AC \n " + String.join("\n ", reasonList).replace("&", "§").replaceAll("%date%", date1) + bumper;
            getPlayer().kickPlayer(reason);
            Bukkit.getBanList(BanList.Type.NAME).addBan(getPlayer().getName(), reason, calendar.getTime() , null);

        } else {

            String execute = Variables.executeBanCommand;
            assert execute != null;
            execute = execute.replaceAll("%player%", getPlayer().getName()).replace("&", "§");

            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), execute);

        }
    }

    public void pluginKick() {

        if(Variables.executeKickCommand.equals("") ||
                Variables.executeKickCommand == null) {

            ArrayList<String> reasonList = new ArrayList<>(Variables.KICK_REASON);
            getPlayer().kickPlayer("§cAnti§4AC \n " + String.join("\n ", reasonList).replace("&", "§"));

        } else {

            String execute = Variables.executeKickCommand;
            assert execute != null;
            execute = execute.replace("%player%", getPlayer().getName()).replace("&", "§");

            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), execute);

        }
    }

    public Player getPlayer() {

        return Bukkit.getPlayer(uuid);

    }

    public OfflinePlayer getOfflinePlayer() {

        return Bukkit.getOfflinePlayer(uuid);

    }

    public Long getLastRightClick() {
        return lastRightClick;
    }

    public void setLastRightClick(Long lastRightClick) {
        this.lastRightClick = lastRightClick;
    }

    @Deprecated
    public void setViolations(int violations) {
        this.violations = violations;
    }

    public void addViolation(ViolationType violationType) {
        this.violations = violations+violationType.getViolations();
        this.clearViolations = 0;
    }

    public void removeViolation(ViolationType violationType) {
        this.violations = violations-violationType.getViolations();
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
        Bukkit.getScheduler().runTaskLater(Core.getInstance(), () -> this.setFrozen(!frozen), 20*duration);

    }

    public boolean isFrozen() {

        return frozen;

    }

    public void setRestricted(boolean restricted) {

        this.restricted = restricted;

    }

    public boolean isRestricted() {

        return restricted;

    }

    @Deprecated
    public void setClicks(int amount) {
        clicks = amount;
    }

    public void addClicks(int amount) {

        setClicks(getClicks()+amount);

        // Needed for DoubleClickCheck
        if(!DoubleClickCheck.latestClicks.containsKey(this)) {
            DoubleClickCheck.latestClicks.put(this, new ArrayList<>());
        }
        List<Long> millis = DoubleClickCheck.latestClicks.get(this);
        millis.add(System.currentTimeMillis());
        DoubleClickCheck.latestClicks.put(this, millis);
    }

    public void removeClicks(int amount) {
        setClicks(getClicks()-amount);
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

        int ping = -1;
        try {
            Object entityPlayer = getPlayer().getClass().getMethod("getHandle").invoke(getPlayer());
            ping = (int) entityPlayer.getClass().getField("ping").get(entityPlayer);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException | NoSuchFieldException e) {
            e.printStackTrace();
        }
        return ping;

    }

    public boolean isBypassed() {
        return (getPlayer().hasPermission(Objects.requireNonNull(Core.getInstance().getConfig().getString("AntiAC.BypassPermission"))) || getPlayer().isOp())
                || getPlayer().hasPermission(Objects.requireNonNull(Core.getInstance().getConfig().getString("AntiAC.BypassPermission"))) && getPlayer().isOp();
    }

    private void shoutOutPunishment() {
        if(Variables.shoutOutPunishment) {
            Objects.requireNonNull(getPlayer().getLocation().getWorld()).strikeLightningEffect(getPlayer().getLocation());
            Bukkit.broadcastMessage("");
            Variables.SHOUTOUT_PUNISHMENT.forEach(var -> Bukkit.broadcastMessage(Core.prefix + var.replace("&", "§").replaceAll("%player%", getPlayer().getName())));
            Bukkit.broadcastMessage("");

            for(Player others : Bukkit.getOnlinePlayers()) {
                Objects.requireNonNull(others.getLocation().getWorld()).spawnEntity(others.getLocation(), EntityType.FIREWORK);
            }

        }
    }

    private void informTeam() {
        if(Variables.informTeam) {
            for(Player team : Bukkit.getOnlinePlayers()) {
                if(team.hasPermission(Objects.requireNonNull(Variables.perms))) {
                    if(User.get(team.getUniqueId()).isNotified()) {
                        team.sendMessage(" ");
                        Variables.TEAM_NOTIFY.forEach(var -> team.sendMessage(Core.prefix + var.replace("&", "§").replaceAll("%player%", getPlayer().getName())
                                .replaceAll("%clicks%", String.valueOf(getClicks()))
                                .replaceAll("%average%", String.valueOf(getAverage())).replaceAll("%VL%", String.valueOf(getViolations()))));
                        team.sendMessage(" ");
                    }
                }
            }
        }
    }

    public enum CheckType {
        CLICK,
        AVERAGE,
        DOUBLE_CLICK,
        LEVEL;
    }

    public void sanction(boolean b, CheckType checkType) {
        if(Variables.consoleNotify) {

            Variables.TEAM_NOTIFY.forEach(var -> Bukkit.getConsoleSender().sendMessage(Core.prefix + var.replace("&", "§").replaceAll("%player%", getPlayer().getName())
                    .replaceAll("%clicks%", String.valueOf(getClicks()))
                    .replaceAll("%average%", String.valueOf(getAverage())).replaceAll("%VL%", String.valueOf(getViolations()))));
        }
        if(Variables.log) {
            if(checkType == null) {
                Log.log(getPlayer(), getClicks(), getAverage(), Variables.allowedClicks, "got detected/too many violations");
            } else if(checkType == CheckType.CLICK) {
                Log.log(getPlayer(), getClicks(), getAverage(), Variables.allowedClicks, "many clicks/too many violations");
            } else if(checkType == CheckType.AVERAGE) {
                Log.log(getPlayer(), getClicks(), getAverage(), Variables.allowedClicks, "equal averages/too many violations");
            } else if(checkType == CheckType.DOUBLE_CLICK) {
                Log.log(getPlayer(), getClicks(), getAverage(), Variables.allowedClicks, "double clicking/too many violations");
            } else if(checkType == CheckType.LEVEL) {
                Log.log(getPlayer(), getClicks(), getAverage(), Variables.allowedClicks, "too fast increasing clicks/too many violations");
            } else {
                Log.log(getPlayer(), getClicks(), getAverage(), Variables.allowedClicks, "got detected/too many violations");

            }
        }

        boolean ban = false;
        boolean kick = false;
        boolean kill = false;
        boolean freeze = false;

        if(b) {
            if(Variables.playerBan) {
                if(getClicks() >= Variables.banAtClicks) {
                    ban = true;
                }
            }
            if(Variables.playerKick) {
                if(getClicks() >= Variables.kickAtClicks) {
                    kick = true;
                }
            }
            if(Variables.playerKill) {
                if(getClicks() >= Variables.killAtClicks) {
                    kill = true;
                }
            }
            if(Variables.playerFreeze) {
                if(getClicks() >= Variables.freezeAtClicks) {
                    freeze = true;
                }
            }
        } else {
            if(Variables.playerBan)
                ban = true;

            if(Variables.playerKick)
                kick = true;

            if(Variables.playerKill)
                kill = true;

            if(Variables.playerFreeze)
                freeze = true;
        }

        if(ban) {
            shoutOutPunishment();
            pluginBan();
            informTeam();
        } else if(kick) {
            shoutOutPunishment();
            pluginKick();
            informTeam();
        } else
        if(kill) {
            getPlayer().setHealth(0);
            Variables.PUNISHED.forEach(var -> getPlayer().sendMessage(Core.prefix + var.replace("&", "§")));

            shoutOutPunishment();
            informTeam();
        } else
        if(freeze) {
            if(!isFrozen()) {
                setFrozen(true);

                Variables.PUNISHED.forEach(var -> getPlayer().sendMessage(Core.prefix + var.replace("&", "§")));

                shoutOutPunishment();

                Bukkit.getScheduler().runTaskLater(Core.getInstance(), () -> setFrozen(false), 20L * Variables.freezeTimeInSeconds);
            }
            informTeam();
        } else {
            if(Variables.restrictPlayer) {
                if(!isRestricted()) {
                    setRestricted(true);

                    Variables.PUNISHED.forEach(var -> getPlayer().sendMessage(Core.prefix + var.replace("&", "§")));

                    shoutOutPunishment();
                }
            }
            informTeam();
        }
        clearViolations();
    }

    /**
     * Sanctions the user according to the set settings
     *
     * If true: Involves the cl
     *
     * icks of the player in the decision
     * If false: Just pays attention to the set settings in the config.yml
     *
     * @param b Specifies whether the user's click count is specifically considered when sanctioning
     */
    public void sanction(boolean b) {
        sanction(b, null);
    }

    private double calculateAverage(List<Integer> marks) {
        Integer sum = 0;
        if(!marks.isEmpty()) {
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

}
