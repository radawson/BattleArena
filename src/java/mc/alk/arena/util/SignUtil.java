package mc.alk.arena.util;

import mc.alk.arena.BattleArena;
import mc.alk.arena.Defaults;
import mc.alk.arena.controllers.ArenaClassController;
import mc.alk.arena.controllers.ParamController;
import mc.alk.arena.objects.ArenaClass;
import mc.alk.arena.objects.MatchParams;
import mc.alk.arena.objects.arenas.Arena;
import mc.alk.arena.objects.exceptions.InvalidOptionException;
import mc.alk.arena.objects.options.JoinOptions;
import mc.alk.arena.objects.signs.ArenaCommandSign;
import mc.alk.arena.objects.signs.ArenaStatusSign;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;

import java.util.Collection;

public class SignUtil {

    public static ArenaCommandSign getArenaCommandSign(Sign sign, String[] lines) {
        MatchParams mp = getParamsFromLines(lines);
        if (mp == null)
            return null;

        try {
            return ArenaCommandSign.create(sign.getLocation(), mp, lines);
        } catch (InvalidOptionException ex) {
            return null;
        }
    }

    public static ArenaClass getArenaClassSign(String[] lines) {
        return ArenaClassController.getClass(
                MessageUtil.decolorChat(lines[0]).replaceAll("[\\[\\"+Defaults.SIGN_PREFIX+"\\]]", ""));
    }

    public static ArenaStatusSign getArenaStatusSign(String[] lines) {
        if (lines.length < 2)
            return null;
        String param = MessageUtil.decolorChat(lines[0]).replaceAll("\\"+Defaults.SIGN_PREFIX, "").trim();
        MatchParams mp = ParamController.getMatchParamCopy(param);
        if (mp == null)
            return null;
        if (lines[1].contains("status"))
            return new ArenaStatusSign(mp);

        return null;
    }

    // TODO: Find a way to "hide" some of these numbers (or lines) if an arena is not specified
    public static String[] getFormattedLines(MatchParams mp, String state, String arenaName, String[] lines) {
        String[] formattedLines = new String[lines.length];
        Arena arena = BattleArena.getArena(arenaName);

        for (int i = 0; i < formattedLines.length; i++) {
            String line = lines[i];
            line = line.replace("{signprefix}", mp.getSignDisplayName());
            line = line.replace("{minplayers}", String.valueOf(mp.getMinPlayers()));
            line = line.replace("{maxplayers}", String.valueOf(mp.getMaxPlayers()));
            line = line.replace("{state}", state);

            if (arena != null) {
                line = line.replace("{arena}", arena.getName());
                line = line.replace("{queuedplayers}", String.valueOf(arena.getQueueCount()));

                int players = 0;
                if (arena.getMatch() != null)
                    players = arena.getMatch().getPlayers().size();

                line = line.replace("{players}", String.valueOf(players));
            } else {
                line = line.replace("{arena}", "");
                line = line.replace("{queuedplayers}", "0");
                line = line.replace("{players}", "0");
            }
            formattedLines[i] = MessageUtil.colorChat(line);
        }

        return formattedLines;
    }

    public static boolean isJoinSign(String[] lines) {
        for (int i = 0; i < lines.length; i++) {
            if (MessageUtil.decolorChat(lines[i]).contains("join"))
                return true;
        }

        return false;
    }

    public static boolean isLeaveSign(String[] lines) {
        for (int i = 0; i < lines.length; i++) {
            if (MessageUtil.decolorChat(lines[i]).contains("leave"))
                return true;
        }

        return false;
    }

    public static JoinOptions getJoinOptionsFromLines(MatchParams mp, String[] lines) throws InvalidOptionException {
        JoinOptions options = JoinOptions.parseOptions(mp, null, new String[]{""});

        for (String line : lines) {
            String[] split = MessageUtil.decolorChat(line).split(" ");

            // Needs to be at least one word long
            if (split.length < 1)
                continue;

            // JoinOptions must be after arena name
            Arena arena = BattleArena.getArena(split[0]);
            if (arena == null)
                continue;

            try {
                options = JoinOptions.parseOptions(mp, null, split);
                break;
            } catch (Exception ex) {
                continue;
            }
        }

        return options;
    }

    public static MatchParams getParamsFromLines(String[] lines) {
        Collection<MatchParams> params = ParamController.getAllParams();
        for (String line : lines) {
            String formattedParam = MessageUtil.decolorChat(line).replaceAll("[\\[\\" + Defaults.SIGN_PREFIX + "\\]]", "").trim();

            for (MatchParams param : params) {
                if (param.getName().equalsIgnoreCase(formattedParam))
                    return param;

                if (param.getSignDisplayName() != null && line.contains(param.getSignDisplayName()))
                    return param;

                if (param.getCommand().equalsIgnoreCase(formattedParam))
                    return param;
            }
        }

        return null;
    }

    public static Sign getSign(World w, int x, int y, int z) {
        Block b = w.getBlockAt(x, y, z);
        Material t = b.getType();
        return t.name().contains("SIGN") ? (Sign)b.getState(): null;
    }

    public static Sign getSign(Location l) {
        if (l == null)
            return null;
        Material t = l.getBlock().getType();
        return t.name().contains("SIGN") ? (Sign)l.getBlock().getState(): null;
    }

}
