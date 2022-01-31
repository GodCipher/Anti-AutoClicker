package de.luzifer.core.model.user.violation;

import lombok.Value;

@Value(staticConstructor = "of")
public class ViolationLevel {
    int level;
}
