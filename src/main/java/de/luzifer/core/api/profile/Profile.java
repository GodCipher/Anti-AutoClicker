package de.luzifer.core.api.profile;

import de.luzifer.core.api.profile.storage.DataContainer;
import de.luzifer.core.utils.Variables;

import java.util.ArrayList;
import java.util.List;

public class Profile {
    
    private final List<DataContainer> dataContainers = new ArrayList<>();
    
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
    
}
