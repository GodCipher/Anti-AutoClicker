package dev.luzifer.antiac.api.profile;

import dev.luzifer.antiac.api.player.User;
import dev.luzifer.antiac.api.profile.storage.DataContainer;
import dev.luzifer.antiac.utils.Variables;

import java.util.ArrayList;
import java.util.List;

public class Profile {

    private final List<DataContainer> dataContainers = new ArrayList<>();
    
    private final User user;
    
    public Profile(User user) {this.user = user;}
    
    public List<DataContainer> getDataContainers() {
        return dataContainers;
    }
    
    public void addDataContainer(DataContainer dataContainer) {
        dataContainers.add(dataContainer);
    }
    
    public void removeDataContainer(DataContainer dataContainer) {
        dataContainers.remove(dataContainer);
    }
    
    public void checkForContainer() {
        if (dataContainers.size() > Variables.removeAfterExist) {
            dataContainers.remove(0);
        }
    }
    
    public User getOwner() {
        return user;
    }
    
}
