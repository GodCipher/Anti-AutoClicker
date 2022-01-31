package de.luzifer.core.model.profile.inventory;

import com.cryptomorin.xseries.XMaterial;
import de.luzifer.core.model.user.User;
import de.luzifer.core.model.profile.inventory.pagesystem.PaginatedMenu;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

public class InsideLogGUI extends PaginatedMenu {
    
    private File file;
    
    public InsideLogGUI(File file) {
        this.file = file;
    }
    
    @Override
    public String getTitle() {
        return "§8[" + "§6" + (page + 1) + "§8] §b" + file.getName().replaceAll(".yml", "");
    }
    
    @Override
    public int getSlots() {
        return 54;
    }
    
    @Override
    public void handleEvent(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();
        User user = User.get(player.getUniqueId());
        
        e.setCancelled(true);
        
        switch (e.getSlot()) {
            
            case 45:
                if (page != 0) {
                    page = page - 1;
                    buildGUI();
                    player.openInventory(inv);
                }
                break;
            case 53:
                List<String> playerNames = new ArrayList<>();
                
                FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
                
                for (String s : cfg.getStringList("LogList")) {
                    s = s.replaceAll(" ", "");
                    String[] sArray = s.split(Pattern.quote("|||"));
                    String playerName = sArray[0];
                    playerNames.add(playerName);
                }
                
                if (!((index + 1) >= playerNames.size())) {
                    page = page + 1;
                    buildGUI();
                    player.openInventory(inv);
                }
                break;
        }
    }
    
    @Override
    public Inventory getInventory() {
        return inv;
    }
    
    public void buildGUI() {
        
        build();
        
        List<String> playerNames = new ArrayList<>();
        
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        
        for (String s : cfg.getStringList("LogList")) {
            s = s.replaceAll(" ", "");
            String[] sArray = s.split(Pattern.quote("|||"));
            String playerName = sArray[0];
            playerNames.add(playerName);
        }
        
        for (int i = 0; i < getMaxItemsPerPage(); i++) {
            index = getMaxItemsPerPage() * page + i;
            
            if (index >= playerNames.size()) break;
            
            if (playerNames.get(index) != null) {
                
                addLogEntry(cfg, playerNames);
                
            }
            
        }
        
        changeButtons(playerNames);
        
    }
    
    private void addLogEntry(FileConfiguration cfg, List<String> playerNames) {
        String name = cfg.getString("LogDetailed." + playerNames.get(index) + ".name");
        String uuid = cfg.getString("LogDetailed." + playerNames.get(index) + ".uuid");
        String date = cfg.getString("LogDetailed." + playerNames.get(index) + ".date");
        Integer clicks = cfg.getInt("LogDetailed." + playerNames.get(index) + ".clicks");
        Double average = cfg.getDouble("LogDetailed." + playerNames.get(index) + ".average");
        Integer clicksToMuch = cfg.getInt("LogDetailed." + playerNames.get(index) + ".clicksToMuch");
        String logMessage = cfg.getString("LogDetailed." + playerNames.get(index) + ".logMessage");
        
        ItemStack itemStack = new ItemStack(XMaterial.PAPER.parseMaterial());
        ItemMeta itemMeta = itemStack.getItemMeta();
        
        itemMeta.setDisplayName("§cIndex: " + (index + 1));
        ArrayList<String> lore = new ArrayList<>();
        lore.add("§6Name: §e" + name);
        lore.add("§6UUID: §e" + uuid);
        lore.add("§6Date: §e" + date);
        lore.add("§6Clicks: §e" + clicks);
        lore.add("§6average: §e" + average);
        lore.add("§6Clicks to much: §e" + clicksToMuch);
        lore.add("§6Log message: §e" + logMessage);
        itemMeta.setLore(lore);
        itemStack.setItemMeta(itemMeta);
        
        inv.addItem(itemStack);
    }
    
    private void changeButtons(List<String> playerNames) {
        if (page == 0) {
            ItemStack backward = new ItemStack(Objects.requireNonNull(XMaterial.STONE_BUTTON.parseMaterial()));
            ItemMeta backwardMeta = backward.getItemMeta();
            
            assert backwardMeta != null;
            backwardMeta.setDisplayName("§cThere is no previous page");
            backward.setItemMeta(backwardMeta);
            
            inv.setItem(45, backward);
        }
        
        if (!((index + 1) >= playerNames.size())) {
        } else {
            ItemStack forward = new ItemStack(Objects.requireNonNull(XMaterial.STONE_BUTTON.parseMaterial()));
            ItemMeta forwardMeta = forward.getItemMeta();
            
            assert forwardMeta != null;
            forwardMeta.setDisplayName("§cThere is no next page");
            forward.setItemMeta(forwardMeta);
            
            inv.setItem(53, forward);
        }
    }
    
}
