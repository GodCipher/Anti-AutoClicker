package de.luzifer.core.checks;

import de.luzifer.core.Core;
import de.luzifer.core.api.check.Check;
import de.luzifer.core.api.enums.ViolationType;
import de.luzifer.core.api.log.Log;
import de.luzifer.core.api.player.User;
import de.luzifer.core.utils.Variables;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.text.SimpleDateFormat;
import java.util.*;

public class AverageCheck extends Check {

    @Override
    public void execute(User user) {

        if(Variables.averageCheck) {

            user.getClicksAverageCheckList().add(user.getAverage());

            if(user.getClicks() >= Variables.averageCheckAtNeededClicks) {

                boolean averageCheck = false;
                if(user.getClicksAverageCheckList().size() >= Variables.averageCheckAtEntries) {

                    averageCheck = true;

                }

                double d;
                boolean isSame = true;
                if(averageCheck) {
                    d = user.getClicksAverageCheckList().get(0);
                    for(double db : user.getClicksAverageCheckList()) {

                        if(d != db) {
                            isSame = false;
                            break;
                        }

                    }

                }

                if(isSame) {

                    user.addViolation(ViolationType.HARD);

                    if(Variables.consoleNotify) {

                        Variables.TEAM_NOTIFY.forEach(var -> Bukkit.getConsoleSender().sendMessage(Core.prefix + var.replace("&", "§").replaceAll("%player%", user.getPlayer().getName())
                                .replaceAll("%clicks%", String.valueOf(user.getClicks()))
                                .replaceAll("%average%", String.valueOf(user.getAverage())).replaceAll("%VL%", String.valueOf(user.getViolations()))));
                    }

                    if(Variables.log) {
                        Log.log(user.getPlayer(), user.getClicks(), user.getAverage(), Variables.allowedClicks, "too equal average");
                    }

                    if(Variables.playerBan) {
                        if(Variables.shoutOutPunishment) {
                            Objects.requireNonNull(user.getPlayer().getLocation().getWorld()).strikeLightningEffect(user.getPlayer().getLocation());
                            Bukkit.broadcastMessage("");
                            Variables.SHOUTOUT_PUNISHMENT.forEach(var -> Bukkit.broadcastMessage(Core.prefix + var.replace("&", "§").replaceAll("%player%", user.getPlayer().getName())));
                            Bukkit.broadcastMessage("");

                            for(Player others : Bukkit.getOnlinePlayers()) {
                                Objects.requireNonNull(others.getLocation().getWorld()).spawnEntity(others.getLocation(), EntityType.FIREWORK);
                            }

                        }

                        if(Variables.executeBanCommand.equals("") ||
                                Variables.executeBanCommand == null) {

                            SimpleDateFormat format = new SimpleDateFormat("dd-MM-YYYY HH:mm:ss");
                            Date date = new Date();
                            Calendar calendar = new GregorianCalendar();
                            calendar.setTime(date);
                            calendar.add(Calendar.HOUR_OF_DAY, + Variables.unbanAfterHours);
                            String date1 = format.format(calendar.getTime());
                            String bumper = org.apache.commons.lang.StringUtils.repeat("\n", 35);
                            ArrayList<String> reasonList = new ArrayList<>(Variables.BAN_REASON);
                            String reason = bumper + "§cAnti§4AC \n " + String.join("\n ", reasonList).replace("&", "§").replaceAll("%date%", date1) + bumper;
                            user.getPlayer().kickPlayer(reason);
                            Bukkit.getBanList(BanList.Type.NAME).addBan(user.getPlayer().getName(), reason, calendar.getTime() , null);

                        } else {

                            String execute = Variables.executeBanCommand;
                            assert execute != null;
                            execute = execute.replaceAll("%player%", user.getPlayer().getName()).replace("&", "§");

                            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), execute);

                        }

                        if(Variables.informTeam) {
                            for(Player team : Bukkit.getOnlinePlayers()) {
                                if(team.hasPermission(Objects.requireNonNull(Variables.perms))) {
                                    if(User.get(team.getUniqueId()).isNotified()) {
                                        team.sendMessage(" ");
                                        Variables.TEAM_NOTIFY.forEach(var -> team.sendMessage(Core.prefix + var.replace("&", "§").replaceAll("%player%", user.getPlayer().getName())
                                                .replaceAll("%clicks%", String.valueOf(user.getClicks()))
                                                .replaceAll("%average%", String.valueOf(user.getAverage())).replaceAll("%VL%", String.valueOf(user.getViolations()))));
                                        team.sendMessage(" ");
                                    }
                                }
                            }
                        }
                    }
                    else
                    if(Variables.playerKick) {

                        if(Variables.executeKickCommand.equals("") ||
                                Variables.executeKickCommand == null) {

                            ArrayList<String> reasonList = new ArrayList<>(Variables.KICK_REASON);
                            user.getPlayer().kickPlayer("§cAnti§4AC \n " + String.join("\n ", reasonList).replace("&", "§"));

                        } else {

                            String execute = Variables.executeKickCommand;
                            assert execute != null;
                            execute = execute.replace("%player%", user.getPlayer().getName()).replace("&", "§");

                            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), execute);

                        }

                        if(Variables.shoutOutPunishment) {
                            Objects.requireNonNull(user.getPlayer().getLocation().getWorld()).strikeLightningEffect(user.getPlayer().getLocation());
                            Bukkit.broadcastMessage("");
                            Variables.SHOUTOUT_PUNISHMENT.forEach(var -> Bukkit.broadcastMessage(Core.prefix + var.replace("&", "§").replaceAll("%player%", user.getPlayer().getName())));
                            Bukkit.broadcastMessage("");

                            for(Player others : Bukkit.getOnlinePlayers()) {
                                Objects.requireNonNull(others.getLocation().getWorld()).spawnEntity(others.getLocation(), EntityType.FIREWORK);
                            }

                        }
                        if(Variables.informTeam) {
                            for(Player team : Bukkit.getOnlinePlayers()) {
                                if(team.hasPermission(Objects.requireNonNull(Variables.perms))) {
                                    if(User.get(team.getUniqueId()).isNotified()) {
                                        team.sendMessage(" ");
                                        Variables.TEAM_NOTIFY.forEach(var -> team.sendMessage(Core.prefix + var.replace("&", "§").replaceAll("%player%", user.getPlayer().getName())
                                                .replaceAll("%clicks%", String.valueOf(user.getClicks()))
                                                .replaceAll("%average%", String.valueOf(user.getAverage())).replaceAll("%VL%", String.valueOf(user.getViolations()))));
                                        team.sendMessage(" ");
                                    }
                                }
                            }
                        }
                    }
                    else
                    if(Variables.playerKill) {

                        user.getPlayer().setHealth(0);
                        Variables.PUNISHED.forEach(var -> user.getPlayer().sendMessage(Core.prefix + var.replace("&", "§")));

                        if(Variables.shoutOutPunishment) {
                            Objects.requireNonNull(user.getPlayer().getLocation().getWorld()).strikeLightningEffect(user.getPlayer().getLocation());
                            Bukkit.broadcastMessage("");
                            Variables.SHOUTOUT_PUNISHMENT.forEach(var -> Bukkit.broadcastMessage(Core.prefix + var.replace("&", "§").replaceAll("%player%", user.getPlayer().getName())));
                            Bukkit.broadcastMessage("");

                            for(Player others : Bukkit.getOnlinePlayers()) {
                                Objects.requireNonNull(others.getLocation().getWorld()).spawnEntity(others.getLocation(), EntityType.FIREWORK);
                            }

                        }
                        if(Variables.informTeam) {
                            for(Player team : Bukkit.getOnlinePlayers()) {
                                if(team.hasPermission(Objects.requireNonNull(Variables.perms))) {
                                    if(User.get(team.getUniqueId()).isNotified()) {
                                        team.sendMessage(" ");
                                        Variables.TEAM_NOTIFY.forEach(var -> team.sendMessage(Core.prefix + var.replace("&", "§").replaceAll("%player%", user.getPlayer().getName())
                                                .replaceAll("%clicks%", String.valueOf(user.getClicks()))
                                                .replaceAll("%average%", String.valueOf(user.getAverage())).replaceAll("%VL%", String.valueOf(user.getViolations()))));
                                        team.sendMessage(" ");
                                    }
                                }
                            }
                        }
                    }
                    else
                    if(Variables.playerFreeze) {
                        if(!user.isFrozen()) {
                            user.setFrozen(true);

                            Variables.PUNISHED.forEach(var -> user.getPlayer().sendMessage(Core.prefix + var.replace("&", "§")));

                            if(Variables.shoutOutPunishment) {
                                Objects.requireNonNull(user.getPlayer().getLocation().getWorld()).strikeLightningEffect(user.getPlayer().getLocation());
                                Bukkit.broadcastMessage("");
                                Variables.SHOUTOUT_PUNISHMENT.forEach(var -> Bukkit.broadcastMessage(Core.prefix + var.replace("&", "§").replaceAll("%player%", user.getPlayer().getName())));
                                Bukkit.broadcastMessage("");

                                for(Player others : Bukkit.getOnlinePlayers()) {
                                    Objects.requireNonNull(others.getLocation().getWorld()).spawnEntity(others.getLocation(), EntityType.FIREWORK);
                                }

                            }

                            Bukkit.getScheduler().runTaskLater(Core.getInstance(), () -> user.setFrozen(false), 20*Variables.freezeTimeInSeconds);
                        }
                        if(Variables.informTeam) {
                            for(Player team : Bukkit.getOnlinePlayers()) {
                                if(team.hasPermission(Objects.requireNonNull(Variables.perms))) {
                                    if(User.get(team.getUniqueId()).isNotified()) {
                                        team.sendMessage(" ");
                                        Variables.TEAM_NOTIFY.forEach(var -> team.sendMessage(Core.prefix + var.replace("&", "§").replaceAll("%player%", user.getPlayer().getName())
                                                .replaceAll("%clicks%", String.valueOf(user.getClicks()))
                                                .replaceAll("%average%", String.valueOf(user.getAverage())).replaceAll("%VL%", String.valueOf(user.getViolations()))));
                                        team.sendMessage(" ");
                                    }
                                }
                            }
                        }
                    }
                    else
                    if(Variables.restrictPlayer) {
                        if(!user.isRestricted()) {
                            user.setRestricted(true);

                            Variables.PUNISHED.forEach(var -> user.getPlayer().sendMessage(Core.prefix + var.replace("&", "§")));

                            if(Variables.shoutOutPunishment) {
                                Objects.requireNonNull(user.getPlayer().getLocation().getWorld()).strikeLightningEffect(user.getPlayer().getLocation());
                                Bukkit.broadcastMessage("");
                                Variables.SHOUTOUT_PUNISHMENT.forEach(var -> Bukkit.broadcastMessage(Core.prefix + var.replace("&", "§").replaceAll("%player%", user.getPlayer().getName())));
                                Bukkit.broadcastMessage("");

                                for(Player others : Bukkit.getOnlinePlayers()) {
                                    Objects.requireNonNull(others.getLocation().getWorld()).spawnEntity(others.getLocation(), EntityType.FIREWORK);
                                }

                            }
                        }
                        if(Variables.informTeam) {
                            for(Player team : Bukkit.getOnlinePlayers()) {
                                if(team.hasPermission(Objects.requireNonNull(Variables.perms))) {
                                    if(User.get(team.getUniqueId()).isNotified()) {

                                        team.sendMessage(" ");
                                        Variables.TEAM_NOTIFY.forEach(var -> team.sendMessage(Core.prefix + var.replace("&", "§").replaceAll("%player%", user.getPlayer().getName())
                                                .replaceAll("%clicks%", String.valueOf(user.getClicks()))
                                                .replaceAll("%average%", String.valueOf(user.getAverage())).replaceAll("%VL%", String.valueOf(user.getViolations()))));
                                        team.sendMessage(" ");
                                    }
                                }
                            }
                        }
                    } else {
                        if(Variables.informTeam) {
                            for(Player team : Bukkit.getOnlinePlayers()) {
                                if(team.hasPermission(Objects.requireNonNull(Variables.perms))) {
                                    if(User.get(team.getUniqueId()).isNotified()) {
                                        team.sendMessage(" ");
                                        Variables.TEAM_NOTIFY.forEach(var -> team.sendMessage(Core.prefix + var.replace("&", "§").replaceAll("%player%", user.getPlayer().getName())
                                                .replaceAll("%clicks%", String.valueOf(user.getClicks()))
                                                .replaceAll("%average%", String.valueOf(user.getAverage())).replaceAll("%VL%", String.valueOf(user.getViolations()))));
                                        team.sendMessage(" ");
                                    }
                                }
                            }
                        }
                    }
                }
            }

        }

    }
}
