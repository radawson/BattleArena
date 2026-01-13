package org.clockworx.battlearena.storage;

import org.bukkit.GameMode;
import org.bukkit.Location;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Represents saved player data for persistence.
 * This is a simplified version adapted from the old codebase.
 * TODO: Properly integrate with new codebase's PlayerStorage system.
 */
public class PlayerSave {
    private final UUID uuid;
    private final String name;
    
    private Integer experience;
    private Double health;
    private Double healthp;
    private Integer hunger;
    private Integer magic;
    private Double magicp;
    private GameMode gamemode;
    private Boolean godmode;
    private Location location;
    private Collection<org.bukkit.potion.PotionEffect> effects;
    private Boolean flight;
    private String arenaClass;
    private String oldTeam;
    private Double money;
    
    // Inventory data - simplified for now
    private Map<String, Object> items;
    private Map<String, Object> matchItems;
    
    public PlayerSave(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
    }
    
    public UUID getID() {
        return uuid;
    }
    
    public String getName() {
        return name;
    }
    
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("uuid", uuid.toString());
        map.put("name", name);
        if (experience != null) map.put("experience", experience);
        if (health != null) map.put("health", health);
        if (healthp != null) map.put("healthp", healthp);
        if (hunger != null) map.put("hunger", hunger);
        if (magic != null) map.put("magic", magic);
        if (magicp != null) map.put("magicp", magicp);
        if (gamemode != null) map.put("gamemode", gamemode.name());
        if (godmode != null) map.put("godmode", godmode);
        if (location != null) map.put("location", locationToString(location));
        if (flight != null) map.put("flight", flight);
        if (arenaClass != null) map.put("arenaClass", arenaClass);
        if (oldTeam != null) map.put("oldTeam", oldTeam);
        if (money != null) map.put("money", money);
        if (items != null) map.put("items", items);
        if (matchItems != null) map.put("matchItems", matchItems);
        return map;
    }
    
    @SuppressWarnings("unchecked")
    public void fromMap(Map<String, Object> map) {
        if (map == null) return;
        if (map.containsKey("experience")) experience = ((Number) map.get("experience")).intValue();
        if (map.containsKey("health")) health = ((Number) map.get("health")).doubleValue();
        if (map.containsKey("healthp")) healthp = ((Number) map.get("healthp")).doubleValue();
        if (map.containsKey("hunger")) hunger = ((Number) map.get("hunger")).intValue();
        if (map.containsKey("magic")) magic = ((Number) map.get("magic")).intValue();
        if (map.containsKey("magicp")) magicp = ((Number) map.get("magicp")).doubleValue();
        if (map.containsKey("gamemode")) {
            try {
                gamemode = GameMode.valueOf((String) map.get("gamemode"));
            } catch (IllegalArgumentException e) {
                // Ignore invalid gamemode
            }
        }
        if (map.containsKey("godmode")) godmode = (Boolean) map.get("godmode");
        if (map.containsKey("flight")) flight = (Boolean) map.get("flight");
        if (map.containsKey("arenaClass")) arenaClass = (String) map.get("arenaClass");
        if (map.containsKey("oldTeam")) oldTeam = (String) map.get("oldTeam");
        if (map.containsKey("money")) money = ((Number) map.get("money")).doubleValue();
        if (map.containsKey("items")) items = (Map<String, Object>) map.get("items");
        if (map.containsKey("matchItems")) matchItems = (Map<String, Object>) map.get("matchItems");
        // Location and effects need proper parsing - simplified for now
    }
    
    private String locationToString(Location loc) {
        return loc.getWorld().getName() + "," + loc.getX() + "," + loc.getY() + "," + loc.getZ() + "," + loc.getYaw() + "," + loc.getPitch();
    }
    
    // Getters and setters
    public Integer getExperience() { return experience; }
    public void setExperience(Integer experience) { this.experience = experience; }
    public Double getHealth() { return health; }
    public void setHealth(Double health) { this.health = health; }
    public Double getHealthp() { return healthp; }
    public void setHealthp(Double healthp) { this.healthp = healthp; }
    public Integer getHunger() { return hunger; }
    public void setHunger(Integer hunger) { this.hunger = hunger; }
    public GameMode getGamemode() { return gamemode; }
    public void setGamemode(GameMode gamemode) { this.gamemode = gamemode; }
    public Location getLocation() { return location; }
    public void setLocation(Location location) { this.location = location; }
    public Double getMoney() { return money; }
    public void setMoney(Double money) { this.money = money; }
}
