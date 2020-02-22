package me.scifi.hcf.combatlog.event;

import me.scifi.hcf.combatlog.type.LoggerEntity;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class LoggerDeathEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final LoggerEntity loggerEntity;

    public LoggerDeathEvent(LoggerEntity loggerEntity) {
        this.loggerEntity = loggerEntity;
    }

    public LoggerEntity getLoggerEntity() {
        return loggerEntity;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
