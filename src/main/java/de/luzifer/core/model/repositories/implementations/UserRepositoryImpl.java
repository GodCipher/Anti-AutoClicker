package de.luzifer.core.model.repositories.implementations;

import de.luzifer.core.model.exceptions.UserNotFoundException;
import de.luzifer.core.model.repositories.UserRepository;
import de.luzifer.core.model.user.User;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class UserRepositoryImpl implements UserRepository {

    private final Set<User> userSet = new HashSet<>();

    @Override
    public User create(User user) {
         userSet.add(user);
         return user;
    }

    @Override
    public User read(UUID id) throws UserNotFoundException {

        for (User user : userSet)
            if (user.getPlayer().getUniqueId().equals(id))
                return user;

        throw new UserNotFoundException("User could not be found.");
    }

    @Override
    public User update(UUID uuid, User user) {
        throw new UnsupportedOperationException("This method is not yet implemented.");
    }

    @Override
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
