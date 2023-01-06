package dev.luzifer.antiac.api.enums;

import dev.luzifer.antiac.Core;

public enum ViolationType {
    
    EASY(Core.getInstance().getConfig().getInt("AntiAC.Easy-Violation")),
    NORMAL(Core.getInstance().getConfig().getInt("AntiAC.Normal-Violation")),
    HARD(Core.getInstance().getConfig().getInt("AntiAC.Hard-Violation"));
    
    private final int i;
    
    ViolationType(int i) {
        this.i = i;
    }
    
    public int getViolations() {
        return i;
    }
    
}
