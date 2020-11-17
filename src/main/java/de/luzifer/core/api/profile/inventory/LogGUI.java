package de.luzifer.core.api.profile.inventory;

import de.luzifer.core.api.profile.inventory.pagesystem.PaginatedMenu;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public class LogGUI extends PaginatedMenu {

//todo
    @Override
    public String getTitle() {
        return "§8[" + "§6" + (page+1) + "§8] §9Choose a File to inspect";
    }

    @Override
    public int getSlots() {
        return 54;
    }

    @Override
    public void handleEvent(InventoryClickEvent e) {

    }

    @Override
    public Inventory getInventory() {
        return null;
    }
}
