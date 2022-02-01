package de.luzifer.core.model.punishment.implementations;

import de.luzifer.core.model.punishment.Punishment;
import de.luzifer.core.model.user.User;

import java.util.*;

public class FrozenPunishment implements Punishment {

    private static final int DEFAULT_DURATION = 20 * 10;

    private final HashMap<UUID, Integer> map = new HashMap<>();

    @Override
    public void punish(User user) {
        map.put(user.getUuid(), DEFAULT_DURATION);
    }
    
    public void punish(User user, int duration) {
        map.put(user.getUuid(), duration);
    }

    public boolean isPunished(User user) {
        return map.containsKey(user.getUuid());
    }
}
