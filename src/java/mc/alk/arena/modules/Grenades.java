package mc.alk.arena.modules;

import mc.alk.arena.objects.events.ArenaEventHandler;
import mc.alk.arena.objects.modules.ArenaModule;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * 
 * 
 * @author Nikolai
 */
public class Grenades extends ArenaModule {
    
    String name = "Grenades";
    String version = "1.0";
    EntityType grenades;
    double damage;
    
    public Grenades() {
        this(EntityType.EGG);
    }
    
    public Grenades(EntityType grenadeType) {
        this(grenadeType, 20.0);
    }
    public Grenades(EntityType grenadeType, double damageAmount) {
        this.grenades = grenadeType;
        this.damage = damageAmount;
    }
    
    @ArenaEventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        e.getPlayer().sendMessage("Module: Grenades");
    }
    
    @ArenaEventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        System.out.println("ArenaListener: Grenades called.");
        EntityType type = event.getDamager().getType();
        if (type == grenades) {
            event.setDamage(damage);
            System.out.println("" + damage + " damage has been applied.");
        }        
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getVersion() {
        return this.version;
    }
    
}
