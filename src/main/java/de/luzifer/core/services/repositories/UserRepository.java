package de.luzifer.core.services.repositories;

import de.luzifer.core.model.player.User;
import de.luzifer.core.model.exceptions.UserNotFoundException;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class UserRepository {

    private final Set<User> userSet = new HashSet<>();

    public User create(User user) {
         userSet.add(user);
         return user;
    }

    public User read(UUID id) throws UserNotFoundException {

        for (User user : userSet)
            if (user.getPlayer().getUniqueId().equals(id))
                return user;

        throw new UserNotFoundException("User could not be found.");
    }

    /**
     * @deprecated since 3.0
     */
    @Deprecated
    public User update(UUID id, User user) {
        throw new UnsupportedOperationException("This method is not yet implemented.");
    }

    public void delete(UUID id) throws UserNotFoundException {

        for (User user : userSet) {
            if (user.getPlayer().getUniqueId().equals(id)) {
                userSet.remove(user);
                return;
            }
        }
        throw new UserNotFoundException("User could not be found.");
    }
}
