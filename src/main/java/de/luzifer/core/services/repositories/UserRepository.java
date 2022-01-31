package de.luzifer.core.services.repositories;

import de.luzifer.core.model.user.User;

import java.util.UUID;

public interface UserRepository {

    User create(User user);

    User read(UUID uuid);

    /**
     * @deprecated since 3.0
     */
    @Deprecated
    User update(UUID uuid, User user);

    void delete(UUID uuid);
}
