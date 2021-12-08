package de.luzifer.core.commands;

import de.luzifer.core.Core;
import de.luzifer.core.api.check.CheckManager;
import de.luzifer.core.api.player.User;
import de.luzifer.core.api.profile.inventory.LogGUI;
import de.luzifer.core.api.profile.inventory.ProfileGUI;
import de.luzifer.core.version.UpdateChecker;
import de.luzifer.core.utils.Variables;
import de.luzifer.core.version.UpdateCheckerResult;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AntiACCommand implements CommandExecutor {
    
    private final String[] subCommands = {"version", "checkupdate", "logs", "reload", "profile", "check", "punish", "notify"};
    private final String prefix = Core.prefix;
    
    private final Core core;
    private final UpdateChecker updateChecker;
    
    public AntiACCommand(Core core) {
        this.core = core;
        this.updateChecker = new UpdateChecker(core);
    }
    
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        
        if (command.getName().equalsIgnoreCase("antiac")) {
            
            boolean isPlayer = sender instanceof Player;
            
            if (args.length == 0) {
                sendCommands(sender);
                return true;
            } else if (args.length == 1) {
                
                if (args[0].equalsIgnoreCase("reload")) {
                    
                    if (!hasSubPermission(sender, "reload")) {
                        sender.sendMessage(Core.prefix + "§7Current plugin version : " + core.getPluginVersion());
                        return true;
                    }
                    
                    core.reloadConfig();
                    core.reloadChecks();
                    
                    Variables.init();
                    
                    sender.sendMessage(prefix + "§7Config, Checks & Messages reloaded!");
                    return true;
                } else if (args[0].equalsIgnoreCase("logs")) {
                    
                    if (!isPlayer) {
                        sender.sendMessage("Whups.. that didn't worked.");
                        return true;
                    }
                    
                    Player player = (Player) sender;
                    
                    if (!hasSubPermission(player, "logs")) {
                        player.sendMessage(Core.prefix + "§7Current plugin version : " + core.getPluginVersion());
                        return true;
                    }
                    
                    LogGUI logGUI = new LogGUI();
                    logGUI.buildGUI();
                    
                    player.openInventory(logGUI.getInventory());
                    return true;
                } else if (args[0].equalsIgnoreCase("version")) {
                    sender.sendMessage(Core.prefix + "§7Current plugin version : " + core.getPluginVersion());
                    return true;
                } else if (args[0].equalsIgnoreCase("checkupdate")) {
                    
                    if (!hasSubPermission(sender, "checkupdate")) {
                        sender.sendMessage(Core.prefix + "§7Current plugin version : " + core.getPluginVersion());
                        return true;
                    }
                    
                    Bukkit.getScheduler().runTaskAsynchronously(core, () -> {
                        
                        UpdateCheckerResult updateCheckerResult = updateChecker.checkUpdate();
                        
                        if (updateCheckerResult.isUpdateAvailable()) {
                            
                            Bukkit.getScheduler().runTask(core, () -> {
                                sender.sendMessage(Core.prefix + "§aAn update is available!");
                                sender.sendMessage(Core.prefix + "§c" + updateCheckerResult.getOldVersion() + " §e-> §a" + updateCheckerResult.getNewVersion());
                            });
                        } else {
                            
                            Bukkit.getScheduler().runTask(core, () -> {
                                sender.sendMessage(Core.prefix + "§aYou have the latest version!");
                            });
                        }
                    });
                    
                    return true;
                } else {
                    sendCommands(sender);
                    return true;
                }
            } else if (args.length == 2) {
                if (args[0].equalsIgnoreCase("profile")) {
                    
                    if (!isPlayer) {
                        sender.sendMessage("Whups.. that didn't worked.");
                        return true;
                    }
                    
                    Player player = (Player) sender;
                    
                    if (!hasSubPermission(player, "profile")) {
                        player.sendMessage(Core.prefix + "§7Current plugin version : " + core.getPluginVersion());
                        return true;
                    }
                    
                    Player target = Bukkit.getPlayer(args[1]);
                    
                    if (target == null) {
                        Variables.PLAYER_OFFLINE.forEach(var -> player.sendMessage(Core.prefix + var.replace("&", "§")));
                        return true;
                    }
                    
                    User targetUser = User.get(target.getUniqueId());
                    
                    ProfileGUI profileGUI = new ProfileGUI();
                    profileGUI.setOwner(targetUser);
                    profileGUI.buildGUI();
                    
                    player.openInventory(profileGUI.getInventory());
                } else if (args[0].equalsIgnoreCase("notify")) {
                    
                    if (!isPlayer) {
                        sender.sendMessage("Whups.. that didn't worked.");
                        return true;
                    }
                    
                    Player player = (Player) sender;
                    
                    if (!hasSubPermission(player, "notify")) {
                        player.sendMessage(Core.prefix + "§7Current plugin version : " + core.getPluginVersion());
                        return true;
                    }
                    
                    if (args[1].equalsIgnoreCase("on")) {
                        if (!User.get(player.getUniqueId()).isNotified()) {
                            User.get(player.getUniqueId()).setNotified(true);
                            Variables.NOTIFY_ACTIVATED.forEach(var -> player.sendMessage(Core.prefix + var.replace("&", "§")));
                        } else {
                            Variables.NOTIFY_ALREADY_ACTIVATED.forEach(var -> player.sendMessage(Core.prefix + var.replace("&", "§")));
                        }
                    } else if (args[1].equalsIgnoreCase("off")) {
                        if (User.get(player.getUniqueId()).isNotified()) {
                            User.get(player.getUniqueId()).setNotified(false);
                            Variables.NOTIFY_DEACTIVATED.forEach(var -> player.sendMessage(Core.prefix + var.replace("&", "§")));
                        } else {
                            Variables.NOTIFY_ALREADY_DEACTIVATED.forEach(var -> player.sendMessage(Core.prefix + var.replace("&", "§")));
                        }
                    } else {
                        player.sendMessage(prefix + "§6/antiac notify <ON/OFF>");
                    }
                    return true;
                } else if (args[0].equalsIgnoreCase("check")) {
                    
                    if (!isPlayer) {
                        sender.sendMessage("Whups.. that didn't worked.");
                        return true;
                    }
                    
                    Player player = (Player) sender;
                    
                    if (!hasSubPermission(player, "check")) {
                        player.sendMessage(Core.prefix + "§7Current plugin version : " + core.getPluginVersion());
                        return true;
                    }
                    
                    Player t = Bukkit.getPlayer(args[1]);
                    if (t != null) {
                        User.get(player.getUniqueId()).setChecked(User.get(t.getUniqueId()));
                        Variables.ON_CLICK_CHECK.forEach(var -> player.sendMessage(Core.prefix + var.replace("&", "§").replaceAll("%player%", t.getName())));
                    } else {
                        if (args[1].equalsIgnoreCase("off")) {
                            if (User.get(player.getUniqueId()).getChecked() == null) {
                                
                                Variables.NOT_CHECKING_ANYONE.forEach(var -> player.sendMessage(Core.prefix + var.replace("&", "§")));
                                return true;
                            }
                            User.get(player.getUniqueId()).setChecked(null);
                            
                            Variables.ON_CLICK_CHECK_OFF.forEach(var -> player.sendMessage(Core.prefix + var.replace("&", "§")));
                            return true;
                        }
                        
                        Variables.PLAYER_OFFLINE.forEach(var -> player.sendMessage(Core.prefix + var.replace("&", "§")));
                    }
                    
                } else if(args[0].equalsIgnoreCase("punish")) {
    
                    if (!hasSubPermission(sender, "punish")) {
                        sender.sendMessage(Core.prefix + "§7Current plugin version : " + core.getPluginVersion());
                        return true;
                    }
    
                    Player target = Bukkit.getPlayer(args[1]);
                    
                    if (target == null) {
                        Variables.PLAYER_OFFLINE.forEach(var -> sender.sendMessage(Core.prefix + var.replace("&", "§")));
                        return true;
                    }
    
                    User targetUser = User.get(target.getUniqueId());
                    targetUser.sanction(null);
                    
                    Variables.PUNISHED_PLAYER.forEach(var -> sender.sendMessage(Core.prefix + var.replace("&", "§").replace("%player%", target.getName())));
                    
                } else {
                    sendCommands(sender);
                    return true;
                }
            }
        }
        
        return false;
    }
    
    private void sendCommands(CommandSender p) {
        
        int count = 0;
        for (String s : subCommands) {
            
            if (hasSubPermission(p, s)) {
                
                switch (s) {
                    case "profile":
                    case "check":
                    case "punish":
                        s += " <player>";
                        break;
                    case "notify":
                        s += " <on/off>";
                        break;
                }
                
                p.sendMessage(prefix + "§6/antiac " + s);
                count++;
            }
        }
        
        if (count == 0)
            p.sendMessage(Core.prefix + "§7Current plugin version : " + core.getPluginVersion());
    }
    
    private boolean hasSubPermission(CommandSender sender, String perms) {
        return sender.hasPermission(Variables.perms + "." + perms) || hasPermission(sender);
    }
    
    private boolean hasPermission(CommandSender sender) {
        return sender.hasPermission(Variables.perms + ".*") || sender.isOp();
    }
}
