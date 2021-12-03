package de.luzifer.core.commands;

import de.luzifer.core.utils.Variables;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class AntiACCommandTabCompleter implements TabCompleter {
    
    private final String[] ARGS = {"check", "version", "notify", "checkupdate", "profile", "logs", "reload"};
    private final String[] ARGS2 = {"on", "off"};
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        
        final List<String> complete = new ArrayList<>();
        
        if (args.length == 1) {
            
            StringUtil.copyPartialMatches(args[0], Arrays.asList(ARGS), complete);
            Collections.sort(complete);
            
            complete.removeIf(s -> !hasSubPermission(sender, s));
            return complete;
            
        } else if (args.length == 2) {
            
            if (!hasSubPermission(sender, args[0])) return Collections.emptyList();
            
            if (args[0].equalsIgnoreCase("check") || args[0].equalsIgnoreCase("profile")) {
                
                List<String> playerNames = new ArrayList<>();
                
                for (Player all : Bukkit.getOnlinePlayers()) {
                    playerNames.add(all.getName());
                }
                
                Collections.sort(playerNames);
                return playerNames;
            }
            
            if (args[0].equalsIgnoreCase("notify")) {
                
                StringUtil.copyPartialMatches(args[1], Arrays.asList(ARGS2), complete);
                Collections.sort(complete);
                
                return complete;
            }
            
        }
        
        return Collections.emptyList();
    }
    
    private boolean hasSubPermission(CommandSender player, String perms) {
        return player.hasPermission(Variables.perms + "." + perms) || hasPermission(player);
    }
    
    private boolean hasPermission(CommandSender player) {
        return player.hasPermission(Variables.perms + ".*") || player.isOp();
    }
    
    
}
