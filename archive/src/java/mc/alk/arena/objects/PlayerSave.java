package mc.alk.arena.objects;

import mc.alk.arena.Defaults;
import mc.alk.arena.controllers.MoneyController;
import mc.alk.arena.controllers.plugins.EssentialsController;
import mc.alk.arena.controllers.plugins.HeroesController;
import mc.alk.arena.listeners.BAPlayerListener;
import mc.alk.arena.serializers.ArenaControllerSerializer;
import mc.alk.arena.serializers.InventorySerializer;
import mc.alk.arena.util.Log;
import mc.alk.arena.util.PermissionsUtil;
import mc.alk.arena.util.PlayerUtil;
import mc.alk.arena.util.SerializerUtil;
import mc.alk.battlebukkitlib.EffectUtil;
import mc.alk.battlebukkitlib.ExpUtil;
import mc.alk.battlebukkitlib.InventoryUtil;
import mc.alk.battlebukkitlib.InventoryUtil.PInv;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
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
 * @author alkarin
 */
public class PlayerSave {
    final ArenaPlayer player;

    Integer experience;

    Double health;
    Double healthp;
    Integer hunger;
    Integer magic;
    Double magicp;
    PInv items;
    PInv matchItems;
    GameMode gamemode;

    Boolean godmode;

    Location location;

    Collection<PotionEffect> effects;

    Boolean flight;
    String arenaClass;
    String oldTeam;
    private Object scoreboard;
    Double money;

    public PlayerSave(ArenaPlayer player) {
        this.player = player;
    }

    public String getName() {
        return player.getName();
    }

    public Integer getExp() {
        return experience;
    }

    public void setExp(Integer exp) {
        this.experience = exp;
    }

    public Double getHealth() {
        return health;
    }

    public void setHealth(Double health) {
        this.health = health;
    }

    public Double getHealthp() {
        return healthp;
    }

    public void setHealthp(Double healthp) {
        this.healthp = healthp;
    }

    public Integer getHunger() {
        return hunger;
    }

    public void setHunger(Integer hunger) {
        this.hunger = hunger;
    }

    public Integer getMagic() {
        return magic;
    }

    public void setMagic(Integer magic) {
        this.magic = magic;
    }

    public Double getMagicp() {
        return magicp;
    }

    public void setMagicp(Double magicp) {
        this.magicp = magicp;
    }

    public PInv getItems() {
        return items;
    }

    public void setItems(PInv items) {
        this.items = items;
    }

    public GameMode getGamemode() {
        return gamemode;
    }

    public void setGamemode(GameMode gamemode) {
        this.gamemode = gamemode;
    }

    public Boolean getGodmode() {
        return godmode;
    }

    public void setGodmode(Boolean godmode) {
        this.godmode = godmode;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Collection<PotionEffect> getEffects() {
        return effects;
    }

    public void setEffects(Collection<PotionEffect> effects) {
        this.effects = effects;
    }

    public Boolean getFlight() {
        return flight;
    }

    public void setFlight(Boolean flight) {
        this.flight = flight;
    }

    public String getArenaClass() {
        return arenaClass;
    }

    public void setArenaClass(String arenaClass) {
        this.arenaClass = arenaClass;
    }

    public String getOldTeam() {
        return oldTeam;
    }

    public void setOldTeam(String oldTeam) {
        this.oldTeam = oldTeam;
    }

    public Double getMoney() {
        return money;
    }

    public void setMoney(Double money) {
        this.money = money;
    }


    public PInv getMatchItems() {
        return matchItems;
    }

    public int storeExperience() {
        Player p = player.getPlayer();
        int exp = ExpUtil.getTotalExperience(p);
        if (exp == 0)
            return 0;
        experience = experience == null ? exp : experience + exp;
        ExpUtil.setTotalExperience(p, 0);
        try {
            //noinspection deprecation
            p.updateInventory();
        } catch (Exception e) {/* do nothing */}
        return exp;
    }

    public void restoreExperience() {
        if (experience==null)
            return;
        Player p = player.getPlayer();
        ExpUtil.setTotalExperience(p.getPlayer(), experience);
        experience=null;
    }

    public Integer removeExperience() {
        Integer exp = experience;
        experience = null;
        return exp;
    }

    public void storeHealth() {
        if (health!=null)
            return;

        health = player.getHealth();
        if (Defaults.DEBUG_STORAGE) Log.info("storing health=" + health + " for player=" + player.getName());
    }

    public void restoreHealth() {
        if (health == null || health <= 0)
            return;
        if (Defaults.DEBUG_STORAGE) Log.info("restoring health=" + health+" for player=" + player.getName());
        PlayerUtil.setHealth(player.getPlayer(),health);
        health=null;
    }

    public Double removeHealth() {
        Double rhealth = health;
        health = null;
        return rhealth;
    }

    public void storeHunger() {
        if (hunger !=null)
            return;
        hunger = player.getFoodLevel();
    }

    public void restoreHunger() {
        if (hunger == null || hunger <= 0)
            return;
        PlayerUtil.setHunger(player.getPlayer(), hunger);
        hunger = null;
    }
    public Integer removeHunger(){
        Integer ret = hunger;
        hunger = null;
        return ret;
    }

    public void storeEffects() {
        if (effects !=null)
            return;
        effects = new ArrayList<PotionEffect>(player.getPlayer().getActivePotionEffects());
    }

    public void restoreEffects() {
        if (effects == null)
            return;
        EffectUtil.enchantPlayer(player.getPlayer(), effects);
        effects = null;
    }

    public Collection<PotionEffect> removeEffects() {
        Collection<PotionEffect> ret = effects;
        effects = null;
        return ret;
    }

    public void storeMagic() {
        if (!HeroesController.enabled() || magic != null)
            return;
        magic = HeroesController.getMagicLevel(player.getPlayer());
    }

    public void restoreMagic() {
        if (!HeroesController.enabled() || magic ==null)
            return;
        HeroesController.setMagicLevel(player.getPlayer(), magic);
        magic = null;
    }

    public Integer removeMagic() {
        Integer ret = magic;
        magic = null;
        return ret;
    }

    public void storeItems() {
        if (items != null)
            return;
//        if (Defaults.DEBUG_STORAGE) Log.info("storing items for = " + name +" contains=" + itemmap.containsKey(name));
        InventoryUtil.closeInventory(player.getPlayer());
        items = new PInv(player.getInventory());
        InventorySerializer.saveInventory(player.getID(), items);
    }

    public void restoreItems() {
        //        if (Defaults.DEBUG_STORAGE)  Log.info("   "+p.getName()+" psc contains=" + itemmap.containsKey(p.getName()) +"  dead=" + p.isDead()+" online=" + p.isOnline());
        if (items ==null)
            return;
        InventoryUtil.addToInventory(player.getPlayer(), items);
        items = null;
    }

    public PInv removeItems() {
        PInv ret = items;
        items = null;
        return ret;
    }

    public void storeMatchItems() {
        final UUID id = player.getID();
//        if (Defaults.DEBUG_STORAGE) Log.info("storing in match items for = " + name +" contains=" + matchitemmap.containsKey(name));
        InventoryUtil.closeInventory(player.getPlayer());
        final PInv pinv = new PInv(player.getInventory());
        if (matchItems == null) {
            /// on the first entry, lets log that to disk
            InventorySerializer.saveInventory(id, pinv);
        }
        matchItems = pinv;
        BAPlayerListener.restoreMatchItemsOnReenter(player, pinv);
    }

    public void restoreMatchItems() {
        if (matchItems==null)
            return;
        InventoryUtil.addToInventory(player.getPlayer(), matchItems);
        matchItems = null;
    }

    public PInv removeMatchItems() {
        PInv ret = matchItems;
        matchItems = null;
        return ret;
    }

    public void storeGamemode() {
//        if (Defaults.DEBUG_STORAGE)  Log.info("storing gamemode " + p.getName() +" " + p.getPlayer().getGameMode());
        if (gamemode !=null)
            return;
        PermissionsUtil.givePlayerInventoryPerms(player.getPlayer());
        gamemode = player.getPlayer().getGameMode();
    }


    public void storeFlight() {
        if (!EssentialsController.enabled() || flight != null){
            return;}
//        if (Defaults.DEBUG_STORAGE)  Log.info("storing flight " + p.getName() +" " + p.getPlayer().getGameMode());
        Boolean b = EssentialsController.isFlying(player);
        if (b)
            flight = true;
    }

    public void restoreFlight() {
        if (flight == null)
            return;
        EssentialsController.setFlight(player.getPlayer(), flight);
        flight = null;
    }


    public void storeGodmode() {
        if (!EssentialsController.enabled() || godmode != null){
            return;
        }
//        if (Defaults.DEBUG_STORAGE)  Log.info("storing godmode " + p.getName() +" " + p.getPlayer().getGameMode());
        Boolean b = EssentialsController.isGod(player);
        if (b)
            godmode = true;
    }

    public void restoreGodmode() {
        if (godmode == null)
            return;
        EssentialsController.setGod(player.getPlayer(), godmode);
        godmode = null;
    }

    public void restoreGamemode() {
        if (gamemode == null)
            return;
        PlayerUtil.setGameMode(player.getPlayer(), gamemode);
        gamemode = null;
    }

    public GameMode removeGamemode() {
        GameMode ret = gamemode;
        gamemode = null;
        return ret;
    }

    public void storeArenaClass() {
        if (!HeroesController.enabled())
            return;
        arenaClass = HeroesController.getHeroClassName(player.getPlayer());
    }

    public void restoreArenaClass() {
        if (!HeroesController.enabled() || arenaClass==null)
            return;
        HeroesController.setHeroClass(player.getPlayer(), arenaClass);
    }

    public void storeScoreboard() {
        if (scoreboard != null)
            return;
        scoreboard = PlayerUtil.getScoreboard(player.getPlayer());
    }

    public Object getScoreboard() {
        return scoreboard;
    }

    public void restoreScoreboard() {
        if (scoreboard==null)
            return;
        PlayerUtil.setScoreboard(player.getPlayer(), scoreboard);
    }

    public void restoreMoney() {
        if (money == null)
            return;
        MoneyController.add(player.getName(), money);
        money = null;
    }

    public UUID getID() {
        return player.getID();
    }
    
    /**
     * Serializes this PlayerSave to a Map for storage.
     * 
     * @return Map containing all serialized player data
     */
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<String, Object>();
        
        // Basic fields
        if (experience != null) map.put("experience", experience);
        if (health != null) map.put("health", health);
        if (healthp != null) map.put("healthp", healthp);
        if (hunger != null) map.put("hunger", hunger);
        if (magic != null) map.put("magic", magic);
        if (magicp != null) map.put("magicp", magicp);
        if (gamemode != null) map.put("gamemode", gamemode.name());
        if (godmode != null) map.put("godmode", godmode);
        if (location != null) map.put("location", SerializerUtil.getLocString(location));
        if (flight != null) map.put("flight", flight);
        if (arenaClass != null) map.put("arenaClass", arenaClass);
        if (oldTeam != null) map.put("oldTeam", oldTeam);
        if (money != null) map.put("money", money);
        
        // Inventory (PInv)
        if (items != null) {
            Map<String, Object> itemsMap = new HashMap<String, Object>();
            List<String> armorList = new ArrayList<String>();
            List<String> contentsList = new ArrayList<String>();
            
            for (ItemStack is : items.armor) {
                if (is != null && is.getType() != Material.AIR) {
                    armorList.add(InventoryUtil.getItemString(is));
                }
            }
            for (ItemStack is : items.contents) {
                if (is != null && is.getType() != Material.AIR) {
                    contentsList.add(InventoryUtil.getItemString(is));
                }
            }
            
            itemsMap.put("armor", armorList);
            itemsMap.put("contents", contentsList);
            map.put("items", itemsMap);
        }
        
        // Match items (PInv)
        if (matchItems != null) {
            Map<String, Object> matchItemsMap = new HashMap<String, Object>();
            List<String> armorList = new ArrayList<String>();
            List<String> contentsList = new ArrayList<String>();
            
            for (ItemStack is : matchItems.armor) {
                if (is != null && is.getType() != Material.AIR) {
                    armorList.add(InventoryUtil.getItemString(is));
                }
            }
            for (ItemStack is : matchItems.contents) {
                if (is != null && is.getType() != Material.AIR) {
                    contentsList.add(InventoryUtil.getItemString(is));
                }
            }
            
            matchItemsMap.put("armor", armorList);
            matchItemsMap.put("contents", contentsList);
            map.put("matchItems", matchItemsMap);
        }
        
        // Potion effects
        if (effects != null && !effects.isEmpty()) {
            List<String> effectsList = new ArrayList<String>();
            for (PotionEffect effect : effects) {
                effectsList.add(EffectUtil.getEnchantString(effect));
            }
            map.put("effects", effectsList);
        }
        
        // Scoreboard - stored as null for now since it's an Object type
        // This would need custom serialization if needed
        // map.put("scoreboard", scoreboard);
        
        return map;
    }
    
    /**
     * Deserializes a PlayerSave from a Map.
     * This method populates the existing PlayerSave instance with data from the map.
     * 
     * @param map Map containing serialized player data
     */
    public void fromMap(Map<String, Object> map) {
        if (map == null) return;
        
        // Basic fields
        if (map.containsKey("experience")) {
            Object exp = map.get("experience");
            if (exp instanceof Number) {
                this.experience = ((Number) exp).intValue();
            }
        }
        if (map.containsKey("health")) {
            Object h = map.get("health");
            if (h instanceof Number) {
                this.health = ((Number) h).doubleValue();
            }
        }
        if (map.containsKey("healthp")) {
            Object hp = map.get("healthp");
            if (hp instanceof Number) {
                this.healthp = ((Number) hp).doubleValue();
            }
        }
        if (map.containsKey("hunger")) {
            Object h = map.get("hunger");
            if (h instanceof Number) {
                this.hunger = ((Number) h).intValue();
            }
        }
        if (map.containsKey("magic")) {
            Object m = map.get("magic");
            if (m instanceof Number) {
                this.magic = ((Number) m).intValue();
            }
        }
        if (map.containsKey("magicp")) {
            Object mp = map.get("magicp");
            if (mp instanceof Number) {
                this.magicp = ((Number) mp).doubleValue();
            }
        }
        if (map.containsKey("gamemode")) {
            Object gm = map.get("gamemode");
            if (gm instanceof String) {
                try {
                    this.gamemode = GameMode.valueOf((String) gm);
                } catch (IllegalArgumentException e) {
                    Log.err("[BattleArena] Invalid gamemode: " + gm);
                }
            }
        }
        if (map.containsKey("godmode")) {
            Object gd = map.get("godmode");
            if (gd instanceof Boolean) {
                this.godmode = (Boolean) gd;
            }
        }
        if (map.containsKey("location")) {
            Object loc = map.get("location");
            if (loc instanceof String) {
                try {
                    this.location = SerializerUtil.getLocation((String) loc);
                } catch (IllegalArgumentException e) {
                    Log.err("[BattleArena] Invalid location: " + loc);
                }
            }
        }
        if (map.containsKey("flight")) {
            Object f = map.get("flight");
            if (f instanceof Boolean) {
                this.flight = (Boolean) f;
            }
        }
        if (map.containsKey("arenaClass")) {
            Object ac = map.get("arenaClass");
            if (ac instanceof String) {
                this.arenaClass = (String) ac;
            }
        }
        if (map.containsKey("oldTeam")) {
            Object ot = map.get("oldTeam");
            if (ot instanceof String) {
                this.oldTeam = (String) ot;
            }
        }
        if (map.containsKey("money")) {
            Object m = map.get("money");
            if (m instanceof Number) {
                this.money = ((Number) m).doubleValue();
            }
        }
        
        // Inventory (PInv)
        if (map.containsKey("items")) {
            Object itemsObj = map.get("items");
            if (itemsObj instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> itemsMap = (Map<String, Object>) itemsObj;
                this.items = deserializePInv(itemsMap);
            }
        }
        
        // Match items (PInv)
        if (map.containsKey("matchItems")) {
            Object matchItemsObj = map.get("matchItems");
            if (matchItemsObj instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> matchItemsMap = (Map<String, Object>) matchItemsObj;
                this.matchItems = deserializePInv(matchItemsMap);
            }
        }
        
        // Potion effects
        if (map.containsKey("effects")) {
            Object effectsObj = map.get("effects");
            if (effectsObj instanceof List) {
                @SuppressWarnings("unchecked")
                List<String> effectsList = (List<String>) effectsObj;
                this.effects = new ArrayList<PotionEffect>();
                for (String effectStr : effectsList) {
                    try {
                        PotionEffect effect = EffectUtil.parseArg(effectStr, 0, 120);
                        if (effect != null) {
                            this.effects.add(effect);
                        }
                    } catch (Exception e) {
                        Log.err("[BattleArena] Failed to parse potion effect: " + effectStr);
                    }
                }
            }
        }
    }
    
    /**
     * Helper method to deserialize a PInv from a Map.
     */
    private PInv deserializePInv(Map<String, Object> pinvMap) {
        PInv pinv = new PInv();
        List<ItemStack> armorList = new ArrayList<ItemStack>();
        List<ItemStack> contentsList = new ArrayList<ItemStack>();
        
        if (pinvMap.containsKey("armor")) {
            Object armorObj = pinvMap.get("armor");
            if (armorObj instanceof List) {
                @SuppressWarnings("unchecked")
                List<String> armorStrs = (List<String>) armorObj;
                for (String armorStr : armorStrs) {
                    try {
                        ItemStack is = InventoryUtil.parseItem(armorStr);
                        if (is != null) {
                            armorList.add(is);
                        }
                    } catch (Exception e) {
                        Log.err("[BattleArena] Failed to parse armor item: " + armorStr);
                    }
                }
            }
        }
        
        if (pinvMap.containsKey("contents")) {
            Object contentsObj = pinvMap.get("contents");
            if (contentsObj instanceof List) {
                @SuppressWarnings("unchecked")
                List<String> contentsStrs = (List<String>) contentsObj;
                for (String contentsStr : contentsStrs) {
                    try {
                        ItemStack is = InventoryUtil.parseItem(contentsStr);
                        if (is != null) {
                            contentsList.add(is);
                        }
                    } catch (Exception e) {
                        Log.err("[BattleArena] Failed to parse contents item: " + contentsStr);
                    }
                }
            }
        }
        
        pinv.armor = armorList.toArray(new ItemStack[armorList.size()]);
        pinv.contents = contentsList.toArray(new ItemStack[contentsList.size()]);
        return pinv;
    }
}
