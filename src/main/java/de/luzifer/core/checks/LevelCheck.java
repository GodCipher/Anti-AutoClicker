package de.luzifer.core.checks;

import de.luzifer.core.api.check.Check;
import de.luzifer.core.api.enums.ViolationType;
import de.luzifer.core.api.player.User;
import de.luzifer.core.utils.Variables;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;

public class LevelCheck extends Check {
    
    private final HashMap<User, Integer> lastClick = new HashMap<>();
    
    private int max_difference;
    
    @Override
    protected void onLoad() throws Exception {
        
        FileConfiguration configuration = loadConfiguration();
        setupDefaults(configuration);
        
        max_difference = configuration.getInt("Check.Max-Difference");
    }
    
    @Override
    public void onSuccess(User user) {
        
        if (Variables.sanctionateAtViolations > 0) {
            user.addViolation(ViolationType.NORMAL);
            return;
        }
        
        user.sanction(this);
    }
    
    @Override
    public void onFailure(User user) {}
    
    @Override
    public boolean check(User user) {
        
        if (!lastClick.containsKey(user)) lastClick.put(user, user.getClicks());
        
        lastClick.put(user, user.getClicks());
        
        return user.getClicks() - lastClick.get(user) >= max_difference;
    }
    
    private void setupDefaults(FileConfiguration fileConfiguration) {
        
        fileConfiguration.addDefault("Check.Max-Difference", 25);
        saveConfiguration(fileConfiguration);
    }
    
}
