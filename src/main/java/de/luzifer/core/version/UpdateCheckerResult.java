package de.luzifer.core.version;

public class UpdateCheckerResult {
    
    private final String newVersion;
    private final String oldVersion;
    
    private final boolean updateAvailable;
    
    public UpdateCheckerResult(String newVersion, String oldVersion, boolean updateAvailable) {
        
        this.newVersion = newVersion;
        this.oldVersion = oldVersion;
        this.updateAvailable = updateAvailable;
    }
    
    public String getNewVersion() {
        return newVersion;
    }
    
    public String getOldVersion() {
        return oldVersion;
    }
    
    public boolean isUpdateAvailable() {
        return updateAvailable;
    }
}
