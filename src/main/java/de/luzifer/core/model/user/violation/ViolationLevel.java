package de.luzifer.core.model.user.violation;

import lombok.EqualsAndHashCode;
import lombok.Value;

@Value(staticConstructor = "of")
@EqualsAndHashCode
public class ViolationLevel {
    int level;
}
