package de.luzifer.core.version;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
public class UpdateCheckerResult {
    
    String newVersion;
    String oldVersion;
    
    boolean updateAvailable;
}
