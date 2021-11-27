package de.luzifer.core.api.check;

import de.luzifer.core.Core;
import de.luzifer.core.api.player.User;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

public abstract class Check {
    
    protected final File file;
    
    private final Logger logger;
    
    private final String name;
    
    private boolean activated;
    private boolean loaded;
    
    public Check() {
        
        logger = Core.getInstance().getLogger();
        
        name = this.getClass().getSimpleName();
        
        File path = new File(Core.getInstance().getDataFolder() + File.separator + "checks");
        file = new File(path, name.toLowerCase() + "_config.yml");
        
        createFiles(path, file);
        setupDefaults();
    }
    
    public void load() throws Exception {
        
        if (isLoaded() || !isActivated()) {
            logger.warning("Failed loading " + name);
            return;
        }
        
        loaded = true;
        onLoad();
    }
    
    public void unload() throws Exception {
        
        if (!isLoaded()) {
            logger.warning("Failed unloading " + name);
            return;
        }
        
        loaded = false;
        onUnload();
    }
    
    public boolean isLoaded() {
        return loaded;
    }
    
    public boolean isActivated() {
        return activated;
    }
    
    protected void onLoad() throws Exception {}
    
    protected void onUnload() throws Exception {}
    
    protected FileConfiguration loadConfiguration() {
        return YamlConfiguration.loadConfiguration(file);
    }
    
    protected void saveConfiguration(FileConfiguration fileConfiguration) {
        
        try {
            
            fileConfiguration.options().copyDefaults(true);
            fileConfiguration.save(file);
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void createFiles(File path, File file) {
        
        if (!path.exists()) path.mkdir();
        
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    private void setupDefaults() {
        
        FileConfiguration fileConfiguration = loadConfiguration();
        fileConfiguration.addDefault("Check.Activated", true);
        
        activated = fileConfiguration.getBoolean("Check.Activated");
        
        saveConfiguration(fileConfiguration);
    }
    
    public abstract void onSuccess(User user);
    
    public abstract void onFailure(User user);
    
    public abstract boolean check(User user);
    
}
