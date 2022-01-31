package de.luzifer.core.model.check;

import de.luzifer.core.Core;
import de.luzifer.core.model.user.User;
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
    
    /**
     * Will try to load the check according to the configurations.
     * <p>
     * If the check is already loaded or can't be activated because it got disabled,
     * it will skip the loading and NOT invoke the {@link #onLoad()} method.
     */
    public void load() throws Exception {
        
        if (isLoaded() || !isActivated()) {
            logger.warning("Failed loading " + name);
            return;
        }
        
        loaded = true;
        onLoad();
    }
    
    /**
     * Will try to unload the check.
     * <p>
     * If the check is already unloaded, it will skip the unloading process incl.
     * the invoking of {@link #onUnload()}.
     */
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
    
    /**
     * If the check detects nothing, in other words {@link #check(User)} returns false,
     * this method will be invoked.
     *
     * @param user the user on which the check got executed on
     */
    public void onFailure(User user) {}
    
    /**
     * This method will be invoked if the check gets loaded.
     */
    protected void onLoad() throws Exception {}
    
    /**
     * This method will be invoked if the check gets unloaded.
     */
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
    
    /**
     * If the check detects something, in other words {@link #check(User)} returns true,
     * this method will be invoked.
     *
     * @param user the user on which the check got executed on
     */
    public abstract void onSuccess(User user);
    
    /**
     * Core functionality of the check. This method alone represents the check and will be executed
     * every second on the player.
     *
     * @param user the user on which the check gets executed on
     */
    public abstract boolean check(User user);
    
}
