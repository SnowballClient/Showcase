package org.golde.snowball.showcaseplugin.events;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.event.world.WorldSaveEvent;
import org.golde.snowball.showcaseplugin.Showcase;

public class CommandButtons implements ShowcaseEvent {

	File data;
	Map<Location, String> commandButtons = new HashMap<Location, String>();
	String prefix = "Showcase > ";

	Showcase plugin;

	private boolean isButton(final Block block) {
		return block.getType() == Material.STONE_BUTTON || block.getType() == Material.WOOD_BUTTON;
	}

	private String joinArray(final String[] stringArray, final String seperator, final int offsetBegining) {
		String string = "";
		for (int i = offsetBegining; i < stringArray.length; ++i) {
			string = String.valueOf(string) + stringArray[i] + " ";
		}
		return string;
	}

	public void onEnable(Showcase pl) {
		this.plugin = pl;

		pl.getDataFolder().mkdirs();

		Bukkit.getServer().getPluginManager().registerEvents(this, pl);

		this.data = new File(pl.getDataFolder(), "data.txt");
		if (!this.data.exists()) {
			try {
				this.data.createNewFile();
			}
			catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		else {
			try {
				final BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(this.data)));
				String line;
				while ((line = reader.readLine()) != null) {
					final String[] lineSegment = line.split(" - ");
					final String[] cords = lineSegment[1].split(",");
					this.commandButtons.put(new Location(Bukkit.getWorld(lineSegment[0]), Double.parseDouble(cords[0]), Double.parseDouble(cords[1]), Double.parseDouble(cords[2])), lineSegment[2]);
				}
				reader.close();
			}
			catch (FileNotFoundException e2) {
				e2.printStackTrace();
			}
			catch (IOException e3) {
				e3.printStackTrace();
			}
		}
	}

	public void onDisable() {
		saveData();
		this.commandButtons.clear();
	}

	private void saveData() {
		final Location[] locations = new Location[this.commandButtons.size()];
		this.commandButtons.keySet().toArray(locations);
		try {
			(this.data = new File(plugin.getDataFolder(), "data.txt")).delete();
			this.data.createNewFile();
			final BufferedWriter writer = new BufferedWriter(new FileWriter(this.data, true));
			for (int i = 0; i < locations.length; ++i) {
				writer.write(String.valueOf(locations[i].getWorld().getName()) + " - " + locations[i].getBlockX() + "," + locations[i].getBlockY() + "," + locations[i].getBlockZ() + " - " + this.commandButtons.get(locations[i]).toString());
				writer.newLine();
			}
			writer.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	@EventHandler
	public void onBlockBreak(final BlockBreakEvent event) {
		final Block block = event.getBlock();
		if (commandButtons.containsKey(block.getLocation())) {
			commandButtons.remove(block.getLocation());
		}
	}

	@EventHandler
	public void onWorldSave(final WorldSaveEvent event) {
		saveData();
	}

	@EventHandler
	public void onServerCommand(final ServerCommandEvent event) {
		if (event.getCommand().equalsIgnoreCase("save-all")) {
			saveData();
		}
	}

	@EventHandler
	public void onPlayerInteract(final PlayerInteractEvent event) {
		final Block clickedBlock = event.getClickedBlock();
		final Player player = event.getPlayer();
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK && isButton(clickedBlock) && commandButtons.containsKey(clickedBlock.getLocation())) {
			final String[] commands = commandButtons.get(clickedBlock.getLocation()).replace("@p", player.getName()).split(" & ");
			for (int i = 0; i < commands.length; ++i) {
				String cmd = commands[i];
				if(cmd.startsWith("CONSOLE:")) {
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), commands[i].replace("CONSOLE:", ""));
				}
				else {
					Bukkit.dispatchCommand(player, commands[i]);
				}
				
			}
			final CraftPlayer cPlayer = (CraftPlayer)player;
			cPlayer.getHandle().updateInventory(cPlayer.getHandle().activeContainer);
		}
	}


	public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {
		if (label.equalsIgnoreCase("cob") && sender instanceof Player) {
			final Player player = (Player)sender;
			final Block targetBlock = player.getTargetBlock((HashSet)null, 16);
			if (this.isButton(targetBlock)) {
				if (args[0].equalsIgnoreCase("set")) {
					if (player.hasPermission("cob.edit")) {
						this.commandButtons.put(targetBlock.getLocation(), this.joinArray(args, " ", 1));
						player.sendMessage(String.valueOf(this.prefix) + ChatColor.YELLOW + "Command set to: " + ChatColor.WHITE + this.joinArray(args, " ", 1));
					}
					else {
						player.sendMessage(String.valueOf(this.prefix) + ChatColor.RED + "You don't have the permission to set and edit button commands.");
					}
				}
				else if (args[0].equalsIgnoreCase("clear")) {
					if (player.hasPermission("cob.edit")) {
						this.commandButtons.remove(targetBlock.getLocation());
						player.sendMessage(String.valueOf(this.prefix) + ChatColor.YELLOW + "Command cleared for this button");
					}
					else {
						player.sendMessage(String.valueOf(this.prefix) + ChatColor.RED + "You don't have the permission to set and edit button commands.");
					}
				}
				else if (args[0].equalsIgnoreCase("show")) {
					if (player.hasPermission("cob.view")) {
						if (this.commandButtons.containsKey(targetBlock.getLocation())) {
							player.sendMessage(String.valueOf(this.prefix) + ChatColor.YELLOW + "Current command for this button: " + ChatColor.WHITE + this.commandButtons.get(targetBlock.getLocation()));
						}
						else {
							player.sendMessage(String.valueOf(this.prefix) + ChatColor.YELLOW + "No command currently set");
						}
					}
					else {
						player.sendMessage(String.valueOf(this.prefix) + ChatColor.RED + "You don't have the permission to view button commands");
					}
				}
				else {
					player.sendMessage(String.valueOf(this.prefix) + ChatColor.GRAY + "Wrong Usage! /cob [set/show/clear]");
				}
			}
		}
		return false;
	}

}
