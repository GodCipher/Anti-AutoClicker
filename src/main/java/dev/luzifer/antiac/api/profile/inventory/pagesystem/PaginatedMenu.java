package dev.luzifer.antiac.api.profile.inventory.pagesystem;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Objects;

public abstract class PaginatedMenu extends Menu {
    
    protected int page = 0;
    protected int maxItemsPerPage = 42;
    protected int index = 0;
    
    public void build() {
        
        super.build();
        
        setButtons();
        
        fill();
    }
    
    public int getMaxItemsPerPage() {
        return maxItemsPerPage;
    }
    
    private void setButtons() {
        ItemStack back = new ItemStack(Objects.requireNonNull(Material.OAK_BUTTON));
        ItemMeta backMeta = back.getItemMeta();
        assert backMeta != null;
        backMeta.setDisplayName("§bPrevious page");
        back.setItemMeta(backMeta);
        inv.setItem(45, back);
        
        ItemStack forward = new ItemStack(Objects.requireNonNull(Material.OAK_BUTTON));
        ItemMeta forwardMeta = forward.getItemMeta();
        assert forwardMeta != null;
        forwardMeta.setDisplayName("§bNext page");
        forward.setItemMeta(forwardMeta);
        inv.setItem(53, forward);
    }
}
