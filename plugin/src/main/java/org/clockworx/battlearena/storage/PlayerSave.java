package org.clockworx.battlearena.storage;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Represents saved player data for persistence.
 * Contains all data from PlayerStorage for long-term storage.
 */
public class PlayerSave {
    private final UUID uuid;
    private final String name;
    
    // Core player data
    private ItemStack[] inventory;
    private GameMode gamemode;
    private Double health;
    private Integer hunger;
    
    // Experience
    private Integer totalExp;
    private Float exp;
    private Integer expLevels;
    
    // Attributes and speeds
    private Map<Attribute, Double> attributes;
    private Float walkSpeed;
    private Float flySpeed;
    
    // Flight
    private Boolean flight;
    private Boolean allowFlight;
    
    // Effects and location
    private Collection<PotionEffect> effects;
    private Location location;
    
    // Legacy fields (for backward compatibility)
    private Integer experience; // Legacy - use totalExp instead
    private Double healthp; // Legacy
    private Integer magic; // Legacy
    private Double magicp; // Legacy
    private Boolean godmode; // Legacy
    private String arenaClass; // Legacy
    private String oldTeam; // Legacy
    private Double money; // Legacy
    private Map<String, Object> items; // Legacy
    private Map<String, Object> matchItems; // Legacy
    
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
        
        // Core data
        if (inventory != null) {
            map.put("inventory", StorageAdapter.serializeInventory(inventory));
        }
        if (gamemode != null) map.put("gamemode", gamemode.name());
        if (health != null) map.put("health", health);
        if (hunger != null) map.put("hunger", hunger);
        
        // Experience
        if (totalExp != null) map.put("totalExp", totalExp);
        if (exp != null) map.put("exp", exp);
        if (expLevels != null) map.put("expLevels", expLevels);
        
        // Attributes and speeds
        if (attributes != null && !attributes.isEmpty()) {
            Map<String, Double> attrMap = new HashMap<>();
            for (Map.Entry<Attribute, Double> entry : attributes.entrySet()) {
                attrMap.put(entry.getKey().name(), entry.getValue());
            }
            map.put("attributes", attrMap);
        }
        if (walkSpeed != null) map.put("walkSpeed", walkSpeed);
        if (flySpeed != null) map.put("flySpeed", flySpeed);
        
        // Flight
        if (flight != null) map.put("flight", flight);
        if (allowFlight != null) map.put("allowFlight", allowFlight);
        
        // Effects and location
        if (effects != null && !effects.isEmpty()) {
            List<Map<String, Object>> effectsList = new ArrayList<>();
            for (PotionEffect effect : effects) {
                Map<String, Object> effectMap = new HashMap<>();
                effectMap.put("type", effect.getType().getName());
                effectMap.put("amplifier", effect.getAmplifier());
                effectMap.put("duration", effect.getDuration());
                effectMap.put("ambient", effect.isAmbient());
                effectMap.put("particles", effect.hasParticles());
                effectMap.put("icon", effect.hasIcon());
                effectsList.add(effectMap);
            }
            map.put("effects", effectsList);
        }
        if (location != null) map.put("location", locationToString(location));
        
        // Legacy fields (for backward compatibility)
        if (experience != null) map.put("experience", experience);
        if (healthp != null) map.put("healthp", healthp);
        if (magic != null) map.put("magic", magic);
        if (magicp != null) map.put("magicp", magicp);
        if (godmode != null) map.put("godmode", godmode);
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
        
        // Core data
        if (map.containsKey("inventory")) {
            String serialized = (String) map.get("inventory");
            inventory = StorageAdapter.deserializeInventory(serialized);
        }
        if (map.containsKey("gamemode")) {
            try {
                gamemode = GameMode.valueOf((String) map.get("gamemode"));
            } catch (IllegalArgumentException e) {
                // Ignore invalid gamemode
            }
        }
        if (map.containsKey("health")) health = ((Number) map.get("health")).doubleValue();
        if (map.containsKey("hunger")) hunger = ((Number) map.get("hunger")).intValue();
        
        // Experience
        if (map.containsKey("totalExp")) totalExp = ((Number) map.get("totalExp")).intValue();
        if (map.containsKey("exp")) exp = ((Number) map.get("exp")).floatValue();
        if (map.containsKey("expLevels")) expLevels = ((Number) map.get("expLevels")).intValue();
        
        // Attributes and speeds
        if (map.containsKey("attributes")) {
            Map<String, Object> attrMap = (Map<String, Object>) map.get("attributes");
            attributes = new HashMap<>();
            for (Map.Entry<String, Object> entry : attrMap.entrySet()) {
                try {
                    Attribute attr = Attribute.valueOf(entry.getKey());
                    attributes.put(attr, ((Number) entry.getValue()).doubleValue());
                } catch (IllegalArgumentException e) {
                    // Ignore invalid attribute
                }
            }
        }
        if (map.containsKey("walkSpeed")) walkSpeed = ((Number) map.get("walkSpeed")).floatValue();
        if (map.containsKey("flySpeed")) flySpeed = ((Number) map.get("flySpeed")).floatValue();
        
        // Flight
        if (map.containsKey("flight")) flight = (Boolean) map.get("flight");
        if (map.containsKey("allowFlight")) allowFlight = (Boolean) map.get("allowFlight");
        
        // Effects
        if (map.containsKey("effects")) {
            List<Map<String, Object>> effectsList = (List<Map<String, Object>>) map.get("effects");
            effects = new ArrayList<>();
            for (Map<String, Object> effectMap : effectsList) {
                try {
                    PotionEffectType type = PotionEffectType.getByName((String) effectMap.get("type"));
                    if (type != null) {
                        int amplifier = ((Number) effectMap.get("amplifier")).intValue();
                        int duration = ((Number) effectMap.get("duration")).intValue();
                        boolean ambient = (Boolean) effectMap.getOrDefault("ambient", false);
                        boolean particles = (Boolean) effectMap.getOrDefault("particles", true);
                        boolean icon = (Boolean) effectMap.getOrDefault("icon", true);
                        effects.add(new PotionEffect(type, duration, amplifier, ambient, particles, icon));
                    }
                } catch (Exception e) {
                    // Ignore invalid effect
                }
            }
        }
        
        // Location
        if (map.containsKey("location")) {
            String locStr = (String) map.get("location");
            if (locStr != null) {
                try {
                    location = stringToLocation(locStr);
                } catch (Exception e) {
                    // Ignore invalid location
                }
            }
        }
        
        // Legacy fields (for backward compatibility)
        if (map.containsKey("experience")) experience = ((Number) map.get("experience")).intValue();
        if (map.containsKey("healthp")) healthp = ((Number) map.get("healthp")).doubleValue();
        if (map.containsKey("magic")) magic = ((Number) map.get("magic")).intValue();
        if (map.containsKey("magicp")) magicp = ((Number) map.get("magicp")).doubleValue();
        if (map.containsKey("godmode")) godmode = (Boolean) map.get("godmode");
        if (map.containsKey("arenaClass")) arenaClass = (String) map.get("arenaClass");
        if (map.containsKey("oldTeam")) oldTeam = (String) map.get("oldTeam");
        if (map.containsKey("money")) money = ((Number) map.get("money")).doubleValue();
        if (map.containsKey("items")) items = (Map<String, Object>) map.get("items");
        if (map.containsKey("matchItems")) matchItems = (Map<String, Object>) map.get("matchItems");
    }
    
    private String locationToString(Location loc) {
        return loc.getWorld().getName() + "," + loc.getX() + "," + loc.getY() + "," + loc.getZ() + "," + loc.getYaw() + "," + loc.getPitch();
    }
    
    private Location stringToLocation(String locStr) {
        String[] parts = locStr.split(",");
        if (parts.length != 6) {
            throw new IllegalArgumentException("Invalid location string: " + locStr);
        }
        return new Location(
            org.bukkit.Bukkit.getServer().getWorld(parts[0]),
            Double.parseDouble(parts[1]),
            Double.parseDouble(parts[2]),
            Double.parseDouble(parts[3]),
            Float.parseFloat(parts[4]),
            Float.parseFloat(parts[5])
        );
    }
    
    // Getters and setters for new fields
    public ItemStack[] getInventory() { return inventory; }
    public void setInventory(ItemStack[] inventory) { this.inventory = inventory; }
    
    public GameMode getGamemode() { return gamemode; }
    public void setGamemode(GameMode gamemode) { this.gamemode = gamemode; }
    
    public Double getHealth() { return health; }
    public void setHealth(Double health) { this.health = health; }
    
    public Integer getHunger() { return hunger; }
    public void setHunger(Integer hunger) { this.hunger = hunger; }
    
    public Integer getTotalExp() { return totalExp; }
    public void setTotalExp(Integer totalExp) { this.totalExp = totalExp; }
    
    public Float getExp() { return exp; }
    public void setExp(Float exp) { this.exp = exp; }
    
    public Integer getExpLevels() { return expLevels; }
    public void setExpLevels(Integer expLevels) { this.expLevels = expLevels; }
    
    public Map<Attribute, Double> getAttributes() { return attributes; }
    public void setAttributes(Map<Attribute, Double> attributes) { this.attributes = attributes; }
    
    public Float getWalkSpeed() { return walkSpeed; }
    public void setWalkSpeed(Float walkSpeed) { this.walkSpeed = walkSpeed; }
    
    public Float getFlySpeed() { return flySpeed; }
    public void setFlySpeed(Float flySpeed) { this.flySpeed = flySpeed; }
    
    public Boolean isFlight() { return flight; }
    public void setFlight(Boolean flight) { this.flight = flight; }
    
    public Boolean isAllowFlight() { return allowFlight; }
    public void setAllowFlight(Boolean allowFlight) { this.allowFlight = allowFlight; }
    
    public Collection<PotionEffect> getEffects() { return effects; }
    public void setEffects(Collection<PotionEffect> effects) { this.effects = effects; }
    
    public Location getLocation() { return location; }
    public void setLocation(Location location) { this.location = location; }
    
    // Legacy getters and setters (for backward compatibility)
    public Integer getExperience() { return experience; }
    public void setExperience(Integer experience) { this.experience = experience; }
    public Double getHealthp() { return healthp; }
    public void setHealthp(Double healthp) { this.healthp = healthp; }
    public Integer getMagic() { return magic; }
    public void setMagic(Integer magic) { this.magic = magic; }
    public Double getMagicp() { return magicp; }
    public void setMagicp(Double magicp) { this.magicp = magicp; }
    public Boolean getGodmode() { return godmode; }
    public void setGodmode(Boolean godmode) { this.godmode = godmode; }
    public String getArenaClass() { return arenaClass; }
    public void setArenaClass(String arenaClass) { this.arenaClass = arenaClass; }
    public String getOldTeam() { return oldTeam; }
    public void setOldTeam(String oldTeam) { this.oldTeam = oldTeam; }
    public Double getMoney() { return money; }
    public void setMoney(Double money) { this.money = money; }
    public Map<String, Object> getItems() { return items; }
    public void setItems(Map<String, Object> items) { this.items = items; }
    public Map<String, Object> getMatchItems() { return matchItems; }
    public void setMatchItems(Map<String, Object> matchItems) { this.matchItems = matchItems; }
}
