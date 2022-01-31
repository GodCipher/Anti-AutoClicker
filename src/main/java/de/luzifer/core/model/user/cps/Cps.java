package de.luzifer.core.model.user.cps;

import lombok.EqualsAndHashCode;
import lombok.Value;

@Value(staticConstructor = "of")
@EqualsAndHashCode
public class Cps {
    int value;
}
