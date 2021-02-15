package de.luzifer.core.api.profile.inventory;

import com.cryptomorin.xseries.XMaterial;
import de.luzifer.core.api.player.User;
import de.luzifer.core.api.profile.inventory.pagesystem.PaginatedMenu;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class LogGUI extends PaginatedMenu {

    @Override
    public String getTitle() {
        return "§8[" + "§6" + (page+1) + "§8] §9Choose a File";
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

        File folder = new File("plugins/AntiAC/Logs");

        if(!folder.exists())
            folder.mkdirs();

        if(folder.listFiles() != null && Objects.requireNonNull(folder.listFiles()).length > 0) {
            List<File> files = Arrays.asList(folder.listFiles());

            if(e.getSlot() != 45 && e.getSlot() != 53) {
                if(e.getCurrentItem() != null) {
                    ItemStack item = e.getCurrentItem();

                    if(!item.hasItemMeta() || !item.getItemMeta().hasDisplayName() || item.getType() != XMaterial.BOOK.parseMaterial()) return;

                    InsideLogGUI insideLogGUI =
                            new InsideLogGUI(new File(folder, e.getCurrentItem().getItemMeta().getDisplayName().replaceAll("§c", "") + ".yml"));

                    insideLogGUI.buildGUI();
                    player.openInventory(insideLogGUI.getInventory());

                }
            } else {
                switch (e.getSlot()) {

                    case 45:
                        if(page != 0){
                            page = page-1;
                            buildGUI();
                            player.openInventory(inv);
                        }
                        break;
                    case 53:
                        if(!((index + 1) >= files.size())) {
                            page = page+1;
                            buildGUI();
                            player.openInventory(inv);
                        }
                        break;
                }
            }
        }

    }

    @Override
    public Inventory getInventory() {
        return inv;
    }

    public void buildGUI() {

        build();

        File folder = new File("plugins/AntiAC/Logs");

        if(!folder.exists())
            folder.mkdirs();

        if(folder.listFiles() != null && Objects.requireNonNull(folder.listFiles()).length > 0) {
            List<File> files = Arrays.asList(Objects.requireNonNull(folder.listFiles()));

            for(int i = 0; i < getMaxItemsPerPage(); i++) {
                index = getMaxItemsPerPage()*page+i;

                if(index >= files.size()) break;

                if(files.get(index) != null) {
                    addLog(files);
                }

            }

            changeButtons(files);
        }

    }

    private void addLog(List<File> files) {
        String name = files.get(index).getName().replaceAll(".yml", "");

        ItemStack item = new ItemStack(XMaterial.BOOK.parseMaterial());
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName("§c" + name);
        ArrayList<String> lore = new ArrayList<>();
        lore.add("§7Contains the Logs of the day");
        meta.setLore(lore);
        item.setItemMeta(meta);

        inv.addItem(item);
    }

    private void changeButtons(List<File> files) {
        if(page == 0) {
            ItemStack backward = new ItemStack(Objects.requireNonNull(XMaterial.STONE_BUTTON.parseMaterial()));
            ItemMeta backwardMeta = backward.getItemMeta();

            assert backwardMeta != null;
            backwardMeta.setDisplayName("§cThere is no previous page");
            backward.setItemMeta(backwardMeta);

            inv.setItem(45, backward);
        }

        if(!((index + 1) >= files.size())) {
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
