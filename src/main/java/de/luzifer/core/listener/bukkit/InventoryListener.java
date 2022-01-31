package de.luzifer.core.listener.bukkit;

import de.luzifer.core.model.profile.inventory.pagesystem.Menu;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class InventoryListener implements Listener {
    
    @EventHandler
    public void onInvClick(InventoryClickEvent event) {
        
        if (event.getView().getTopInventory().getHolder() instanceof Menu) {
            
            Menu menu = (Menu) event.getView().getTopInventory().getHolder();
            menu.handleEvent(event);
        }
    }
    
}
