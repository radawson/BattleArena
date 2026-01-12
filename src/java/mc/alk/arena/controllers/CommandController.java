package mc.alk.arena.controllers;

import java.lang.reflect.Field;

import mc.alk.arena.util.Log;
import mc.euro.version.VersionFactory;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;

public class CommandController {

    public static CommandMap getCommandMap() {
        String NMS = VersionFactory.getNmsPackage();
        Class<?> clazz;
        try {
            clazz = Class.forName("org.bukkit.craftbukkit.CraftServer");
        } catch (ClassNotFoundException handled) {
            try {
                clazz = Class.forName("org.bukkit.craftbukkit." + NMS + ".CraftServer");
            } catch (ClassNotFoundException ex) {
                Log.printStackTrace(ex);
                return null;
            }
        }
        return getCommandMapFromServer(clazz);
    }

    private static CommandMap getCommandMapFromServer(Class<?> serverClass) {
        try {
            if (serverClass.isAssignableFrom(Bukkit.getServer().getClass())) {
                final Field f = serverClass.getDeclaredField("commandMap");
                f.setAccessible(true);
                return (CommandMap) f.get(Bukkit.getServer());
            }
        } catch (final SecurityException e) {
            System.out.println("You will need to disable the security manager to use dynamic commands");
        } catch (final Exception e) {
            Log.printStackTrace(e);
        }
        return null;
    }

    public static void registerCommand(final Command command) {
        CommandMap commandMap = getCommandMap();
        if (commandMap != null) {
            commandMap.register("/", command);
        }
    }
}
