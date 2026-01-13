package mc.alk.arena.listeners;

import mc.alk.arena.BattleArena;
import mc.alk.arena.Defaults;
import mc.alk.arena.competition.match.Match;
import mc.alk.arena.controllers.PlayerController;
import mc.alk.arena.objects.ArenaClass;
import mc.alk.arena.objects.signs.ArenaCommandSign;
import mc.alk.arena.objects.signs.ArenaStatusSign;
import mc.alk.arena.util.Log;
import mc.alk.arena.util.MessageUtil;
import mc.alk.arena.util.PermissionsUtil;
import mc.alk.arena.util.SignUtil;
import mc.euro.bukkitadapter.material.BattleMaterial;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class BASignListener implements Listener{
    SignUpdateListener sul;
    Map<String, ArenaCommandSign> signLocs = new HashMap<String, ArenaCommandSign>();

    public BASignListener(SignUpdateListener sul){
        this.sul = sul;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        /// If this is an uninteresting block get out of here as quickly as we can
        if (event.getClickedBlock() == null || !(event.getClickedBlock().getState() instanceof Sign)) {
            return;
        }

        if (PermissionsUtil.isAdmin(event.getPlayer()) && (event.getAction() == Action.LEFT_CLICK_BLOCK)){
            return;
        }

        ArenaCommandSign acs = signLocs.get(getKey(event.getClickedBlock().getLocation()));
        if (acs == null) {
            acs = SignUtil.getArenaCommandSign(((Sign) event.getClickedBlock().getState()),
                    ((Sign) event.getClickedBlock().getState()).getLines());
            if (acs != null){
                signLocs.put(getKey(event.getClickedBlock().getLocation()), acs);}
        }
        if (acs == null) {
            return;
        }
        event.setCancelled(true);
        sul.addSign(acs);
        acs.performAction(PlayerController.toArenaPlayer(event.getPlayer()));
    }

    @EventHandler
    public void onSignChange(SignChangeEvent event){
        // System.out.println("SignChangeEvent called");
        if (Defaults.DEBUG_TRACE) System.out.println("onSignChange Event");
        final Block block = event.getBlock();

        if (!(block.getState() instanceof Sign)) {
            // System.out.println("SignChangeEvent exiting for " + type.toString());
            return;}

        Player p = event.getPlayer();

        /// Is the sign a arena class sign?
        final boolean admin = PermissionsUtil.isAdmin(p);
        String lines[] = event.getLines();
        ArenaClass ac = SignUtil.getArenaClassSign(lines);
        if (ac != null){
            if (!admin){
                cancelSignPlace(event,block);
                return;
            }
            makeArenaClassSign(event, ac, lines);
            return;
        }
        /// is the sign a command sign
        ArenaCommandSign acs = SignUtil.getArenaCommandSign((Sign)block.getState(), lines);
        if (acs != null){
            if (!admin){
                cancelSignPlace(event,block);
                return;
            }
            makeArenaCommandSign(event, acs, lines);
            return;
        }
        /// is the sign a command sign
        ArenaStatusSign ass = SignUtil.getArenaStatusSign(lines);
        if (ass != null){
            if (!admin){
                cancelSignPlace(event,block);
                return;
            }
            makeArenaStatusSign(event, ass, lines);
        }
    }

    private void makeArenaClassSign(SignChangeEvent event, ArenaClass ac, String[] lines) {
        if (ac == null)
            return;
        final Block block = event.getBlock();
        for (int i=1;i<lines.length;i++){
            if (!lines[i].isEmpty()) /// other text, not our sign
                return;
        }

        try{
            event.setLine(0, MessageUtil.colorChat("["+ac.getDisplayName()+"&0]"));
            MessageUtil.sendMessage(event.getPlayer(), "&2Arena class sign created");
        } catch (Exception e){
            MessageUtil.sendMessage(event.getPlayer(), "&cError creating Arena Class Sign");
            Log.printStackTrace(e);
            cancelSignPlace(event,block);
        }
    }

    private void makeArenaCommandSign(SignChangeEvent event, ArenaCommandSign acs, String[] lines) {
        if (acs == null)
            return;
        final Block block = event.getBlock();
        for (int i=3;i<lines.length;i++){
            if (!lines[i].isEmpty()) /// other text, not our sign
                return;
        }

        try {
            String arenaName = "";
            if (acs.getArena() != null)
                arenaName = acs.getArena().getName();

            if (SignUtil.isJoinSign(lines)) {
                String[] formattedLines = SignUtil.getFormattedLines(acs.getMatchParams(), "Open", arenaName, BattleArena.getSelf().getBASignSerializer().getJoinSignFormat("open"));
                for (int i = 0; i < formattedLines.length; i++) {
                    event.setLine(i, formattedLines[i]);
                }
            }

            if (SignUtil.isLeaveSign(lines)) {
                String[] formattedLines = SignUtil.getFormattedLines(acs.getMatchParams(), "Open", arenaName, BattleArena.getSelf().getBASignSerializer().getLeaveSignFormat("open"));
                for (int i = 0; i < formattedLines.length; i++) {
                    event.setLine(i, formattedLines[i]);
                }
            }

            MessageUtil.sendMessage(event.getPlayer(), "&2Arena command sign created");
            sul.addSign(acs);
            signLocs.put(getKey(acs.getLocation()), acs);
        } catch (Exception e){
            MessageUtil.sendMessage(event.getPlayer(), "&cError creating Arena Command Sign");
            Log.printStackTrace(e);
            cancelSignPlace(event,block);
        }
    }

    private String getKey(Location location) {
        return location.getWorld().getName()+":"+location.getBlockX()+":"+
                location.getBlockY()+":"+location.getBlockZ();
    }

    private void makeArenaStatusSign(SignChangeEvent event, ArenaStatusSign acs, String[] lines) {
        if (acs == null)
            return;
        final Block block = event.getBlock();
        for (int i=3;i<lines.length;i++){
            if (!lines[i].isEmpty()) /// other text, not our sign
                return;
        }

        try{
            String match = acs.getType().toLowerCase();
            match = Character.toUpperCase(match.charAt(0)) + match.substring(1);
            event.setLine(0, MessageUtil.colorChat( ChatColor.GOLD+Defaults.SIGN_PREFIX+
                    acs.getMatchParams().getColor()+match));
            event.setLine(1, MessageUtil.colorChat( ""));
            acs.setLocation(event.getBlock().getLocation());

//			signController.addStatusSign(acs);
            MessageUtil.sendMessage(event.getPlayer(), "&2Arena status sign created");
        } catch (Exception e){
            MessageUtil.sendMessage(event.getPlayer(), "&cError creating Arena Status Sign");
            Log.printStackTrace(e);
            cancelSignPlace(event,block);
        }
    }

    public static void cancelSignPlace(SignChangeEvent event, Block block){
        event.setCancelled(true);
        block.setType(Material.AIR);
        block.getWorld().dropItemNaturally(block.getLocation(), new ItemStack(BattleMaterial.OAK_SIGN.parseMaterial(), 1));
    }
}
