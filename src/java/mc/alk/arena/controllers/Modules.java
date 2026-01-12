package mc.alk.arena.controllers;

import java.util.HashMap;
import java.util.Map;
import mc.alk.arena.objects.modules.ArenaModule;

/**
 * 
 * 
 * @author Nikolai
 */
public class Modules {
    
    private static final Map<String, ArenaModule> modules = new HashMap<String, ArenaModule>();
    
    public static void addModule(ArenaModule mod) {
        modules.put(mod.getName().toUpperCase(), mod);
    }
    
    public static ArenaModule getModule(String name) {
        return modules.get(name.toUpperCase());
    }
    
}
