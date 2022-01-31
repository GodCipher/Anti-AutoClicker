package de.luzifer.core.checks;

import de.luzifer.core.model.check.Check;
import de.luzifer.core.model.enums.ViolationType;
import de.luzifer.core.model.player.User;
import de.luzifer.core.utils.Variables;
import org.bukkit.configuration.file.FileConfiguration;

public class ClickLimitCheck extends Check {
    
    private int click_limit;
    
    @Override
    protected void onLoad() throws Exception {
        
        FileConfiguration configuration = loadConfiguration();
        setupDefaults(configuration);
        
        click_limit = configuration.getInt("Check.Click-Limit");
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
    public boolean check(User user) {
        return user.getClicks() >= click_limit;
    }
    
    private void setupDefaults(FileConfiguration fileConfiguration) {
        
        fileConfiguration.addDefault("Check.Click-Limit", 40);
        saveConfiguration(fileConfiguration);
    }
}
