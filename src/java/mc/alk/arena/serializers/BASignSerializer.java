package mc.alk.arena.serializers;

import mc.alk.arena.util.Log;
import mc.euro.bukkitadapter.material.BattleMaterial;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

public class BASignSerializer extends BaseConfig {

    public void load() {
        try {
            config.load(file);
        } catch (Exception e) {
            Log.printStackTrace(e);
        }
    }

    public String[] getJoinSignFormat(String state) {
        return getSignFormat("join." + state + ".lines");
    }

    public String[] getLeaveSignFormat(String state) {
        return getSignFormat("leave." + state + ".lines");
    }

    public BattleMaterial getAttachedBlock(String type, String state) {
        ConfigurationSection section = config.getConfigurationSection("signs." + type + "." + state);
        if (!section.contains("blockAttached"))
            return null;

        BattleMaterial battleMat = BattleMaterial.fromString(section.getString("blockAttached"));
        if (battleMat == null)
            return null;

        return battleMat;
    }

    public String[] getSignFormat(String path) {
        ConfigurationSection section = config.getConfigurationSection("signs." + path);
        String[] lines = new String[section.getKeys(false).size()];
        int i = 0;
        for (String str : section.getKeys(false)) {
            lines[i] = section.getString(str);
            i++;
        }

        return lines;
    }
}