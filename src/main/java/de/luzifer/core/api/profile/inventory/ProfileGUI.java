package de.luzifer.core.api.profile.inventory;

import com.cryptomorin.xseries.XMaterial;
import com.google.common.collect.Lists;
import de.luzifer.core.api.player.User;
import de.luzifer.core.api.profile.inventory.pagesystem.PaginatedMenu;
import de.luzifer.core.api.profile.storage.DataContainer;
import de.luzifer.core.utils.Variables;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.ocpsoft.prettytime.PrettyTime;

import java.util.*;

public class ProfileGUI extends PaginatedMenu {

    private User user;

    @Override
    public String getTitle() {
        return "§8[" + "§6" + (page+1) + "§8] §b" + user.getPlayer().getName();
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

        List<DataContainer> dataContainers = user.getProfile().getDataContainers();

        switch (e.getSlot()) {

            case 45:
                if(page != 0){
                    page = page-1;
                    buildGUI();
                    player.openInventory(inv);
                }
                break;
            case 53:
                if(!((index + 1) >= dataContainers.size())) {
                    page = page+1;
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

    public void setOwner(User user) {
        this.user = user;
    }

    public void buildGUI() {

        build();

        List<DataContainer> dC = user.getProfile().getDataContainers();
        List<DataContainer> dataContainers = Lists.reverse(dC);

        for(int i = 0; i < getMaxItemsPerPage(); i++) {
            index = getMaxItemsPerPage()*page+i;

            if(index >= dataContainers.size()) break;

            if(dataContainers.get(index) != null) {

                ItemStack item = new ItemStack(Objects.requireNonNull(XMaterial.CHEST.parseMaterial()));
                ItemMeta meta = item.getItemMeta();
                assert meta != null;
                meta.setDisplayName("§cDataContainer §8" + UUID.randomUUID().toString().split("(?<=\\G.{5})")[0]);
                ArrayList<String> lore = new ArrayList<>();
                PrettyTime pr = new PrettyTime();
                lore.add("§6Data from: §e" + pr.format(dataContainers.get(index).getFinishedAt()));
                lore.add("§6Data collected: §e" + Variables.storeAsManyData);
                lore.add("");
                lore.add("§6Clicks                  §6Averages");
                for(int i1 = 0; i1 < dataContainers.get(index).getClicksList().size(); i1++) {
                    if(i1 >= 20) {
                        lore.add("§8+" + (dataContainers.get(index).getClicksList().size()-i1) + " more...");
                        break;
                    } else {

                        if(Variables.allowedClicks - dataContainers.get(index).getClicksList().get(i1) <= 8) {
                            if(!(Variables.allowedClicks - dataContainers.get(index).getClicksList().get(i1) <= 0)) {

                                if (dataContainers.get(index).getClicksList().get(i1) >= 100 && dataContainers.get(index).getClicksList().get(i1) <= 999) {
                                    lore.add("§c" + dataContainers.get(index).getClicksList().get(i1)
                                            + "                         §6" + dataContainers.get(index).getAveragesList().get(i1));
                                } else if(dataContainers.get(index).getClicksList().get(i1) >= 10 && dataContainers.get(index).getClicksList().get(i1) <= 99) {
                                    lore.add("§c" + dataContainers.get(index).getClicksList().get(i1)
                                            + "                           §6" + dataContainers.get(index).getAveragesList().get(i1));
                                } else {
                                    lore.add("§c" + dataContainers.get(index).getClicksList().get(i1)
                                            + "                            §6" + dataContainers.get(index).getAveragesList().get(i1));
                                }
                            } else {

                                if (dataContainers.get(index).getClicksList().get(i1) >= 100 && dataContainers.get(index).getClicksList().get(i1) <= 999) {
                                    lore.add("§4" + dataContainers.get(index).getClicksList().get(i1)
                                            + "                         §6" + dataContainers.get(index).getAveragesList().get(i1));
                                } else if(dataContainers.get(index).getClicksList().get(i1) >= 10 && dataContainers.get(index).getClicksList().get(i1) <= 99) {
                                    lore.add("§4" + dataContainers.get(index).getClicksList().get(i1)
                                            + "                           §6" + dataContainers.get(index).getAveragesList().get(i1));
                                } else {
                                    lore.add("§4" + dataContainers.get(index).getClicksList().get(i1)
                                            + "                            §6" + dataContainers.get(index).getAveragesList().get(i1));
                                }
                            }
                        } else {

                            if (dataContainers.get(index).getClicksList().get(i1) >= 100 && dataContainers.get(index).getClicksList().get(i1) <= 999) {
                                lore.add("§a" + dataContainers.get(index).getClicksList().get(i1)
                                        + "                         §6" + dataContainers.get(index).getAveragesList().get(i1));
                            } else if(dataContainers.get(index).getClicksList().get(i1) >= 10 && dataContainers.get(index).getClicksList().get(i1) <= 99) {
                                lore.add("§a" + dataContainers.get(index).getClicksList().get(i1)
                                        + "                           §6" + dataContainers.get(index).getAveragesList().get(i1));
                            } else {
                                lore.add("§a" + dataContainers.get(index).getClicksList().get(i1)
                                        + "                            §6" + dataContainers.get(index).getAveragesList().get(i1));
                            }
                        }
                    }
                }
                meta.setLore(lore);
                item.setItemMeta(meta);

                inv.addItem(item);
            }

        }

        if(page == 0) {
            ItemStack backward = new ItemStack(Objects.requireNonNull(XMaterial.STONE_BUTTON.parseMaterial()));
            ItemMeta backwardMeta = backward.getItemMeta();

            assert backwardMeta != null;
            backwardMeta.setDisplayName("§cThere is no previous page");
            backward.setItemMeta(backwardMeta);

            inv.setItem(45, backward);
        }

        if(!((index + 1) >= dataContainers.size())) {
        } else {
            ItemStack forward = new ItemStack(Objects.requireNonNull(XMaterial.STONE_BUTTON.parseMaterial()));
            ItemMeta forwardMeta = forward.getItemMeta();

            assert forwardMeta != null;
            forwardMeta.setDisplayName("§cThere is no next page");
            forward.setItemMeta(forwardMeta);

            inv.setItem(53, forward);
        }

    }

    public User getOwner() {
        return user;
    }

    public void setStartPage(int page) {
        this.page = page;
    }

    public int getPage() {
        return page;
    }

    public boolean isNextPage(int i) {
        return !((index + 1) >= i);
    }

    public boolean isPreviousPage() {
        return !(page == 0);
    }

}
