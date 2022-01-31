package de.luzifer.core.model.user.data.violation;

import lombok.EqualsAndHashCode;
import lombok.Value;

@Value(staticConstructor = "of")
@EqualsAndHashCode
public class ViolationLevel {
    int level;
}
