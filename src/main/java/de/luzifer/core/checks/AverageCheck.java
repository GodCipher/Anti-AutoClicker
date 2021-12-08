package de.luzifer.core.checks;

import de.luzifer.core.api.check.Check;
import de.luzifer.core.api.enums.ViolationType;
import de.luzifer.core.api.player.User;
import de.luzifer.core.utils.Variables;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

public class AverageCheck extends Check {
    
    private int needed_entries;
    private int needed_clicks;
    private int from_seconds;
    
    @Override
    protected void onLoad() throws Exception {
        
        FileConfiguration configuration = loadConfiguration();
        setupDefaults(configuration);
        
        needed_clicks = configuration.getInt("Check.Needed-Clicks-To-Start");
        needed_entries = configuration.getInt("Check.Start-At-Entries");
        from_seconds = configuration.getInt("Check.Averages-From-Seconds");
    }
    
    @Override
    public void onSuccess(User user) {
        
        if(Variables.sanctionateAtViolations > 0) {
            user.addViolation(ViolationType.HARD);
            return;
        }
        
        cleanup(user);
        user.sanction(false);
    }
    
    @Override
    public void onFailure(User user) {
        cleanup(user);
    }
    
    @Override
    public boolean check(User user) {
        
        if(user.getClicks() >=needed_clicks) {
            
            List<Double> clicksAverageCheckList = user.getClicksAverageCheckList();
            if(clicksAverageCheckList.size() >= needed_entries) {
                
                double d = clicksAverageCheckList.get(0);
                for(double db : clicksAverageCheckList)
                    if(d != db)
                        return false;
            } else {
                
                return false;
            }
            
            return true;
        }
        
        return false;
    }
    
    private void setupDefaults(FileConfiguration fileConfiguration) {
    
        fileConfiguration.addDefault("Check.Needed-Clicks-To-Start", 15);
        fileConfiguration.addDefault("Check.Start-At-Entries", 3);
        fileConfiguration.addDefault("Check.Averages-From-Seconds", 3);
        saveConfiguration(fileConfiguration);
    }
    
    private void cleanup(User user) {
    
        if(user.getClicksAverageCheckList().size() >= from_seconds) {
            user.getClicksAverageCheckList().remove(0);
        }
    
        if(user.getClicksAverageList().size() >= from_seconds) {
            user.getClicksAverageList().remove(0);
        }
    }
}
