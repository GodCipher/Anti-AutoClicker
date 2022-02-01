package de.luzifer.core.model.punishment.implementations;

import de.luzifer.core.model.punishment.Punishment;
import de.luzifer.core.model.user.User;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

public class RestrictionPunishment implements Punishment {

    private final Set<UUID> map = new LinkedHashSet<>();

    @Override
    public void punish(User user) {
        map.add(user.getUuid());
    }

    public boolean isPunished(User user) {
        return map.contains(user.getUuid());
    }
}
