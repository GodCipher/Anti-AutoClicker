package de.luzifer.core.model.user;

import de.luzifer.core.model.user.cps.Cps;
import de.luzifer.core.model.user.violation.ViolationLevel;
import lombok.Value;

@Value
public class Userdata {
    
    Cps cps;
    
    ViolationLevel violationLevel;
    
}
