package de.luzifer.core.model.events;

import de.luzifer.core.model.user.User;

public class PlayerClickEvent extends AntiACEvent {
    
    private final User user;
    
    public PlayerClickEvent(User user) {
        this.user = user;
    }
    
    public User getUser() {
        return user;
    }
}
