package org.golde.snowball.showcaseplugin;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.golde.snowball.api.SnowballAPI;
import org.golde.snowball.api.object.SnowballPlayer;
import org.golde.snowball.shared.enums.EnumCosmetic;
import org.golde.snowball.showcaseplugin.events.CommandButtons;
import org.golde.snowball.showcaseplugin.events.GenericEvents;
import org.golde.snowball.showcaseplugin.objs.KeyGui;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;

public class Showcase extends JavaPlugin implements Listener, TabCompleter {

	private HashMap<UUID, KeyGui> playerLastPressedKeys = new HashMap<UUID, KeyGui>();
	private Location loc_spawn;

	private SBPlaceholderReplacer hd_key;
	private SBPlaceholderReplacer hd_key_gui;
	
	private CommandButtons cmdButtons = new CommandButtons();
	private GenericEvents genericEvents = new GenericEvents();
	
	public void onEnable() {
		getCommand("spawn").setExecutor(this);
		getCommand("setname").setExecutor(this);
		getCommand("setskin").setExecutor(this);
		
		getCommand("addcosmetic").setExecutor(this);
		getCommand("addcosmetic").setTabCompleter(this);
		
		getCommand("removecosmetic").setExecutor(this);
		getCommand("removecosmetic").setTabCompleter(this);
		
		getCommand("toastexample").setExecutor(this);
		
		getCommand("help").setExecutor(this);
		
		
		Bukkit.getPluginManager().registerEvents(this, this);
		
		
		loc_spawn = new Location(Bukkit.getWorld("world"), -181.5, 74.5, -7.5, -90, 0);
		
		PacketAdapter.AdapterParameteters params = PacketAdapter
                .params()
                .plugin(this)
                .types(	PacketType.Play.Server.SPAWN_ENTITY_LIVING,
                        PacketType.Play.Server.SPAWN_ENTITY,
                        PacketType.Play.Server.ENTITY_METADATA)
                .serverSide()
                .listenerPriority(ListenerPriority.NORMAL);

        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketPlaceholderListener(this, params));
        
        HologramsAPI.registerPlaceholder(this, "sb_lastkey", 0.1, hd_key = new SBPlaceholderReplacer("sb_key"));
        HologramsAPI.registerPlaceholder(this, "sb_lastgui", 0.1, hd_key_gui = new SBPlaceholderReplacer("sb_gui"));
        
        cmdButtons.onEnable(this);
        genericEvents.onEnable(this);
	}
	
	public HashMap<UUID, KeyGui> getPlayerLastPressedKeys() {
		return playerLastPressedKeys;
	}
	
	public SBPlaceholderReplacer getHd_key() {
		return hd_key;
	}
	public SBPlaceholderReplacer getHd_key_gui() {
		return hd_key_gui;
	}
	
	public Location getLoc_spawn() {
		return loc_spawn;
	}
	
	@Override
	public void onDisable() {
		 ProtocolLibrary.getProtocolManager().removePacketListeners(this);
		 cmdButtons.onDisable();
		 genericEvents.onDisable();
		 
		 for(OfflinePlayer p : Bukkit.getOperators()) {
			 p.setOp(false);
		 }
	}
	
	
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		cmdButtons.onCommand(sender, command, label, args);
		
		Player player = (Player)sender;
		SnowballPlayer snowballPlayer = SnowballAPI.getInstance(this).getSnowballPlayer(player);
		
		if(command.getName().equalsIgnoreCase("spawn")) {
			player.teleport(loc_spawn);
			player.sendMessage(ChatColor.GRAY + "You have been teleported to spawn.");
		}
		else if(command.getName().equalsIgnoreCase("setskin")) {
			snowballPlayer.setCustomSkin(args[0]);
			player.sendMessage(ChatColor.GRAY + "Your skin has been set to: " + ChatColor.GREEN + args[0]);
		}
		else if(command.getName().equalsIgnoreCase("setname")) {
			String username = ChatColor.translateAlternateColorCodes('&', args[0]);
			snowballPlayer.setCustomUsername(username);
			player.sendMessage(ChatColor.GRAY + "Your name has been set to: " + ChatColor.RESET + args[0]);
		}
		else if(command.getName().equalsIgnoreCase("addcosmetic")) {
			try {
				EnumCosmetic cos = EnumCosmetic.valueOf(args[0]);
				snowballPlayer.addCosmetic(cos);
				player.sendMessage(ChatColor.GRAY + "Added cosmetic: " + ChatColor.AQUA + cos.name());
			}
			catch(Exception e) {
				player.sendMessage(ChatColor.GRAY + "Invallid cosmetic: " + ChatColor.AQUA + args[0]);
			}
		}
		else if(command.getName().equalsIgnoreCase("removecosmetic")) {
			try {
				EnumCosmetic cos = EnumCosmetic.valueOf(args[0]);
				snowballPlayer.removeCosmetic(cos);
				player.sendMessage(ChatColor.GRAY + "Removed cosmetic: " + ChatColor.AQUA + cos.name());
			}
			catch(Exception e) {
				player.sendMessage(ChatColor.GRAY + "Invallid cosmetic: " + ChatColor.AQUA + args[0]);
			}
		}
		else if(command.getName().equalsIgnoreCase("toastexample")) {
			snowballPlayer.sendToast("Hello World!", "This is a example toast!", new ItemStack(Material.BLAZE_ROD));
		}
		else if(command.getName().equalsIgnoreCase("help")) {
			player.sendMessage("I should write a better help message here");
		}
		
		return true;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		
		if(command.getName().equalsIgnoreCase("addcosmetic") || command.getName().equalsIgnoreCase("removecosmetic")) {
			return Arrays.asList(getNames(EnumCosmetic.class));
		}
		
		return super.onTabComplete(sender, command, alias, args);
	}
	
	private String[] getNames(Class<? extends Enum<?>> e) {
	    return Arrays.stream(e.getEnumConstants()).map(Enum::name).toArray(String[]::new);
	}
	
}
