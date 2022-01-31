package de.luzifer.core.model.check;

import java.util.ArrayList;
import java.util.List;

public class CheckManager {
    
    private final List<Check> checks = new ArrayList<>();
    
    public void registerCheck(Check check) {
        checks.add(check);
    }
    
    public void unregisterCheck(Check check) {
        checks.remove(check);
    }
    
    public void unregisterAll() {
        checks.clear();
    }
    
    public List<Check> getChecks() {
        return checks;
    }
    
    public boolean isRegisteredCheck(Check check) {
        return checks.contains(check);
    }
}
