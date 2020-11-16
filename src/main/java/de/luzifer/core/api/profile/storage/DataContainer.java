package de.luzifer.core.api.profile.storage;

import de.luzifer.core.api.player.User;
import de.luzifer.core.api.profile.Profile;
import de.luzifer.core.api.profile.inventory.ProfileGUI;
import de.luzifer.core.utils.Variables;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DataContainer {

    private final User user;

    private Date finishedAt;

    private boolean finish = false;

    private final int storeAsManyData;

    private final List<Integer> clicksList = new ArrayList<>();
    private final List<Double> averagesList = new ArrayList<>();

    public DataContainer(User user, int storeAsManyData) {
        this.user = user;
        this.storeAsManyData = storeAsManyData;
    }

    public void collectData() {

        if(getClicksList().size() <= Variables.storeAsManyData) {
            clicksList.add(user.getClicks());
            averagesList.add(user.getAverage());
        } else {
            finish = true;
            finishedAt = new Date();
            addToProfile(user.getProfile());
        }

    }

    public boolean isFinish() {
        return finish;
    }

    private void addToProfile(Profile profile) {
        profile.addDataContainer(this);

        for(Player all : Bukkit.getOnlinePlayers()) {
            if(all.getOpenInventory().getTopInventory().getHolder() instanceof ProfileGUI) {

                ProfileGUI profileGUI = (ProfileGUI) all.getOpenInventory().getTopInventory().getHolder();
                if(profileGUI.getOwner() == user) {
                    profileGUI.buildGUI();
                    all.openInventory(profileGUI.getInventory());
                }

            }
        }
    }

    public List<Integer> getClicksList() {
        return clicksList;
    }

    public List<Double> getAveragesList() {
        return averagesList;
    }

    public Date getFinishedAt() {
        return finishedAt;
    }
}
