package de.luzifer.core.commands;

import de.luzifer.core.Core;
import de.luzifer.core.api.player.User;
import de.luzifer.core.api.profile.inventory.LogGUI;
import de.luzifer.core.api.profile.inventory.ProfileGUI;
import de.luzifer.core.utils.UpdateChecker;
import de.luzifer.core.utils.Variables;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Objects;

public class AntiACCommand implements CommandExecutor {

    String prefix = Core.prefix;

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(command.getName().equalsIgnoreCase("antiac")) {
            if(!(sender instanceof Player)) {
                sender.sendMessage("Whups.. that didn't worked.");
                return true;
            }
            Player p = (Player) sender;

            String perms = Objects.requireNonNull(Core.getInstance().getConfig().getString("AntiAC.NeededPermission")).toLowerCase();
            if(!p.hasPermission(perms) && !p.isOp()) {
                
                p.sendMessage(Core.prefix + "§7Current plugin version : " + Core.getInstance().getDescription().getVersion());
                p.sendMessage("§6https://www.spigotmc.org/resources/anti-autoclicker-1-8-x-1-16-2.74933");
                
                return true;
            }

            if(args.length == 0) {
                sendCommands(p);
                return true;
            }
            else if(args.length == 1) {

                if(args[0].equalsIgnoreCase("reload")) {
                    Core.getInstance().reloadConfig();
                    Variables.init();

                    p.sendMessage(prefix + "§7Config reloaded!");
                    
                    return true;
                } else

                if(args[0].equalsIgnoreCase("logs")) {

                    LogGUI logGUI = new LogGUI();
                    logGUI.buildGUI();
                    p.openInventory(logGUI.getInventory());
                    return true;

                } else

                if(args[0].equalsIgnoreCase("version")) {
                    
                    p.sendMessage(Core.prefix + "§7Current plugin version : " + Core.getInstance().getDescription().getVersion());
                    
                    return true;
                }

                else if(args[0].equalsIgnoreCase("checkupdate")) {
                    Bukkit.getScheduler().runTaskAsynchronously(Core.getInstance(), () -> {
                        UpdateChecker updateChecker = new UpdateChecker(Core.getInstance());
                        if(!updateChecker.check()) {
                            Bukkit.getScheduler().runTask(Core.getInstance(), () -> {
                                p.sendMessage(Core.prefix + "§aAn update is available!");
                                p.sendMessage(Core.prefix + "§c" + Core.getInstance().getDescription().getVersion() + " §e-> §a" + updateChecker.getLatestVersion());
                            });
                        } else {
                            Bukkit.getScheduler().runTask(Core.getInstance(), () -> {
                                p.sendMessage(Core.prefix + "§aYou have the latest version!");
                            });
                        }
                    });
                    return true;
                } else {

                    sendCommands(p);
                    return true;

                }

            } else if(args.length == 2) {
                if (args[0].equalsIgnoreCase("profile")) {

                    Player target = Bukkit.getPlayer(args[1]);

                    if(target == null) {
                        Variables.PLAYER_OFFLINE.forEach(var -> p.sendMessage(Core.prefix + var.replace("&", "§")));
                        return true;
                    }

                    User targetUser = User.get(target.getUniqueId());

                    ProfileGUI profileGUI = new ProfileGUI();
                    profileGUI.setOwner(targetUser);
                    profileGUI.buildGUI();

                    p.openInventory(profileGUI.getInventory());

                } else
                if(args[0].equalsIgnoreCase("notify")) {
                    if(args[1].equalsIgnoreCase("on")) {
                        if(!User.get(p.getUniqueId()).isNotified()) {
                            User.get(p.getUniqueId()).setNotified(true);
                            Variables.NOTIFY_ACTIVATED.forEach(var -> p.sendMessage(Core.prefix + var.replace("&", "§")));
                        } else {
                            Variables.NOTIFY_ALREADY_ACTIVATED.forEach(var -> p.sendMessage(Core.prefix + var.replace("&", "§")));
                        }
                    } else if(args[1].equalsIgnoreCase("off")) {
                        if(User.get(p.getUniqueId()).isNotified()) {
                            User.get(p.getUniqueId()).setNotified(false);
                            Variables.NOTIFY_DEACTIVATED.forEach(var -> p.sendMessage(Core.prefix + var.replace("&", "§")));
                        } else {
                            Variables.NOTIFY_ALREADY_DEACTIVATED.forEach(var -> p.sendMessage(Core.prefix + var.replace("&", "§")));
                        }
                    } else {
                        p.sendMessage(prefix + "§6/antiac notify <ON/OFF>");
                    }
                    return true;
                }
                else if(args[0].equalsIgnoreCase("check")) {
                    Player t = Bukkit.getPlayer(args[1]);
                    if(t != null) {
                        User.get(p.getUniqueId()).setChecked(User.get(t.getUniqueId()));
                        
                        Variables.ON_CLICK_CHECK.forEach(var -> p.sendMessage(Core.prefix + var.replace("&", "§").replaceAll("%player%", t.getName())));
                    } else {
                        if(args[1].equalsIgnoreCase("off")) {
                            if(User.get(p.getUniqueId()).getChecked() == null) {
                                
                                Variables.NOT_CHECKING_ANYONE.forEach(var -> p.sendMessage(Core.prefix + var.replace("&", "§")));
                                
                                return true;
                            }
                            User.get(p.getUniqueId()).setChecked(null);
                            
                            Variables.ON_CLICK_CHECK_OFF.forEach(var -> p.sendMessage(Core.prefix + var.replace("&", "§")));
                            
                            return true;
                        }
                        
                        Variables.PLAYER_OFFLINE.forEach(var -> p.sendMessage(Core.prefix + var.replace("&", "§")));
                    }
                    
                } else {

                    sendCommands(p);
                    return true;

                }
            }
        }

        return false;
    }

    private void sendCommands(Player p) {
        
        p.sendMessage(prefix + "§6/antiac version");
        p.sendMessage(prefix + "§6/antiac checkupdate");
        p.sendMessage(prefix + "§6/antiac logs");
        p.sendMessage(prefix + "§6/antiac reload");
        p.sendMessage(prefix + "§6/antiac profile <PLAYER>");
        p.sendMessage(prefix + "§6/antiac check <PLAYER>/off");
        p.sendMessage(prefix + "§6/antiac notify <ON/OFF>");
        
    }
}
