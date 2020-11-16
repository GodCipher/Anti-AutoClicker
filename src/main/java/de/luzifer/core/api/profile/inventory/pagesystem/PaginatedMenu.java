package de.luzifer.core.api.profile.inventory.pagesystem;

import com.cryptomorin.xseries.XMaterial;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Objects;

public abstract class PaginatedMenu extends Menu {

    protected int page = 0;
    protected int maxItemsPerPage = 42;
    protected int index = 0;

    public void build() {

        super.build();

        ItemStack back = new ItemStack(Objects.requireNonNull(XMaterial.OAK_BUTTON.parseMaterial()));
        ItemMeta backMeta = back.getItemMeta();
        assert backMeta != null;
        backMeta.setDisplayName("§bPrevious page");
        back.setItemMeta(backMeta);
        inv.setItem(45, back);

        ItemStack forward = new ItemStack(Objects.requireNonNull(XMaterial.OAK_BUTTON.parseMaterial()));
        ItemMeta forwardMeta = forward.getItemMeta();
        assert forwardMeta != null;
        forwardMeta.setDisplayName("§bNext page");
        forward.setItemMeta(forwardMeta);
        inv.setItem(53, forward);

        fill();
    }

    public int getMaxItemsPerPage() {
        return maxItemsPerPage;
    }
}
