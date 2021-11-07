package de.luzifer.core.api.check;

import java.util.ArrayList;
import java.util.List;

public class CheckManager {
    
    private static final List<Check> checks = new ArrayList<>();
    
    public static void registerCheck(Check check) {
        checks.add(check);
    }
    
    public static void unregisterCheck(Check check) {
        checks.remove(check);
    }
    
    public static void unregisterAll() {
        checks.clear();
    }
    
    public static List<Check> getChecks() {
        return checks;
    }
    
    public static boolean isRegisteredCheck(Check check) {
        return checks.contains(check);
    }
}
