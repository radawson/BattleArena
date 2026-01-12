package mc.alk.arena.objects.signs;

import mc.alk.arena.objects.ArenaPlayer;
import mc.alk.arena.objects.MatchParams;
import mc.alk.arena.objects.arenas.Arena;
import mc.alk.arena.objects.exceptions.InvalidOptionException;
import mc.alk.arena.objects.options.JoinOptions;
import mc.alk.arena.util.MessageUtil;
import mc.alk.arena.util.SerializerUtil;
import mc.alk.arena.util.SignUtil;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public abstract class ArenaCommandSign implements ConfigurationSerializable{

    final Location location;
    final MatchParams mp;
    final JoinOptions joinOptions;
    Arena arena;

    ArenaCommandSign(Location location, MatchParams mp, JoinOptions joinOptions) {
        this.mp = mp;
        this.location = location;
        this.joinOptions = joinOptions;
        this.arena = joinOptions.getArena();
    }

    public abstract void performAction(ArenaPlayer player);

    public MatchParams getMatchParams() {
        return mp;
    }

    public JoinOptions getJoinOptions() {
        return joinOptions;
    }

    @Override
    public Map<String, Object> serialize() {
        HashMap<String,Object> map = new HashMap<String,Object>();
        map.put("location", SerializerUtil.getBlockLocString(location));
        return map;
    }

    public static ArenaCommandSign deserialize(Map<String, Object> map) throws IllegalArgumentException{
        Location location = SerializerUtil.getLocation((String) map.get("location"));
        if (location == null)
            return null;
        Sign s = SignUtil.getSign(location);
        if (s == null)
            return null;
        return SignUtil.getArenaCommandSign(s, s.getLines());
    }

    public Location getLocation() {
        return location;
    }

    public Sign getSign() {
        return SignUtil.getSign(location);
    }

    public static ArenaCommandSign create(Location location, MatchParams mp, String[] lines) throws InvalidOptionException {
        if (SignUtil.isJoinSign(lines)) {
            return new ArenaJoinSign(location, mp, SignUtil.getJoinOptionsFromLines(mp, lines));
        }

        if (SignUtil.isLeaveSign(lines)) {
            return new ArenaLeaveSign(location, mp, SignUtil.getJoinOptionsFromLines(mp, lines));
        }

        throw new InvalidOptionException("This is not a known sign type");
    }

    public abstract String getCommand();

    public Arena getArena(){
        return arena;
    }
}
