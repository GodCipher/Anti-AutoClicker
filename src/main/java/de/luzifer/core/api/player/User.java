package de.luzifer.core.api.player;

import de.luzifer.core.Core;
import de.luzifer.core.api.enums.ViolationType;
import de.luzifer.core.api.profile.Profile;
import de.luzifer.core.utils.Variables;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
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

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public void pluginBan() {

        SimpleDateFormat format = new SimpleDateFormat("dd-MM-YYYY HH:mm:ss");
        Date date = new Date();
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.add(Calendar.HOUR_OF_DAY, + Core.getInstance().getConfig().getInt("AntiAC.UnbanAfterHours"));
        String date1 = format.format(calendar.getTime());
        String bumper = org.apache.commons.lang.StringUtils.repeat("\n", 35);
        ArrayList<String> reasonList = new ArrayList<>(Variables.BAN_REASON);
        String reason = bumper + "§cAnti§4AC \n " + String.join("\n ", reasonList).replace("&", "§").replaceAll("%date%", date1) + bumper;
        getPlayer().kickPlayer(reason);
        Bukkit.getBanList(BanList.Type.NAME).addBan(getPlayer().getName(), reason, calendar.getTime() , null);
    }

    public void pluginKick() {

        ArrayList<String> reasonList = new ArrayList<>(Variables.KICK_REASON);
        getPlayer().kickPlayer("§cAnti§4AC \n " + String.join("\n ", reasonList).replace("&", "§"));
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

    public void setClicks(int amount) {

        clicks = amount;

    }

    public void addClicks(int amount) {

        clicks = clicks+amount;

    }

    public void removeClicks(int amount) {

        clicks = clicks-amount;

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
