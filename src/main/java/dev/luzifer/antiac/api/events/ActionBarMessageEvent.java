package dev.luzifer.antiac.api.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ActionBarMessageEvent extends Event implements Cancellable {
    
    private static final HandlerList handlers = new HandlerList();;
    
    private final Player player;
    private String message;
    private boolean cancelled;
    
    public ActionBarMessageEvent(final Player player, final String message) {
        this.cancelled = false;
        this.player = player;
        this.message = message;
    }
    
    public static HandlerList getHandlerList() {
        return ActionBarMessageEvent.handlers;
    }
    
    public HandlerList getHandlers() {
        return ActionBarMessageEvent.handlers;
    }
    
    public Player getPlayer() {
        return this.player;
    }
    
    public String getMessage() {
        return this.message;
    }
    
    public void setMessage(final String message) {
        this.message = message;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}