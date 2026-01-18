package org.clockworx.battlearena.util;

/**
 * Categories for debug logging in BattleArena.
 * Each category can be independently enabled/disabled in config.
 * 
 * @author Clockworx
 * @since 5.0.3
 */
public enum LogCategory {
    /**
     * General plugin operations.
     */
    GENERAL("General"),
    
    /**
     * Arena operations (loading, creation, management).
     */
    ARENA("Arena"),
    
    /**
     * Competition lifecycle (start, end, phases).
     */
    COMPETITION("Competition"),
    
    /**
     * Event system (event triggers, actions, handlers).
     */
    EVENT("Event"),
    
    /**
     * Storage operations (saves, loads, queries).
     */
    STORAGE("Storage"),
    
    /**
     * Module operations (loading, enabling, disabling).
     */
    MODULE("Module"),
    
    /**
     * Command execution.
     */
    COMMAND("Command"),
    
    /**
     * WorldEdit/FAWE operations.
     */
    WORLDEDIT("WorldEdit");
    
    private final String displayName;
    
    LogCategory(String displayName) {
        this.displayName = displayName;
    }
    
    /**
     * Gets the display name for log output.
     * 
     * @return The display name
     */
    public String getDisplayName() {
        return displayName;
    }
    
    @Override
    public String toString() {
        return displayName;
    }
}
