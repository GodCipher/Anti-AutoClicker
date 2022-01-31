package de.luzifer.core.model.user.data;

import de.luzifer.core.model.user.data.cps.Cps;
import de.luzifer.core.model.user.data.violation.ViolationLevel;
import lombok.Value;

@Value
public class Userdata {
    
    Cps cps;
    
    ViolationLevel violationLevel;
    
}
