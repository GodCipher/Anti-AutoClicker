package de.luzifer.core.api.profile.inventory.pagesystem;

import de.luzifer.core.xseries.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Objects;

public abstract class Menu implements InventoryHolder {
    
    protected Inventory inv;
    
    public abstract String getTitle();
    
    public abstract int getSlots();
    
    public abstract void handleEvent(InventoryClickEvent e);
    
    public void fill() {
        ItemStack fill = new ItemStack(Objects.requireNonNull(XMaterial.WHITE_STAINED_GLASS_PANE.parseMaterial()), 1, (byte) 15);
        ItemMeta fillMeta = fill.getItemMeta();
        fillMeta.setDisplayName("§9");
        fill.setItemMeta(fillMeta);
        fill(inv, fill);
    }
    
    public void build() {
        
        inv = Bukkit.createInventory(this, getSlots(), getTitle());
        
        this.fill();
        
    }
    
    private void fill(Inventory inv, ItemStack item) {
        for (int i = 0; i != 53; i++) {
            
            if (i == 0) {
                inv.setItem(i, item);
                for (int i1 = 1; i1 != 5; i1++) {
                    inv.setItem(i + (9 * i1), item);
                }
            }
            
            if (i == 8) {
                inv.setItem(i, item);
                for (int i1 = 1; i1 != 5; i1++) {
                    inv.setItem(i + (9 * i1), item);
                }
            }
            
        }
    }
}
