package de.luzifer.core.model.user.data.cps;

import lombok.EqualsAndHashCode;
import lombok.Value;

@Value(staticConstructor = "of")
@EqualsAndHashCode
public class Cps {
    int value;
}
