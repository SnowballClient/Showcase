package org.golde.snowball.showcaseplugin.events;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;

public class UnbreakingAnvils extends ShowcaseEvent {

	private Map<UUID, Block> anvilMap = new HashMap<UUID, Block>();
    
	@EventHandler
    public void anvilClick(final PlayerInteractEvent e) {
        final Player p = e.getPlayer();
        final Action a = e.getAction();
        if (a == Action.RIGHT_CLICK_BLOCK) {
            final Block b = e.getClickedBlock();
            
            if(b.getType() == Material.ANVIL || b.getType() == Material.ENCHANTMENT_TABLE) {
            	p.setLevel(50);
            }
            
            if (b.getType() == Material.ANVIL) {
            	this.anvilMap.put(p.getUniqueId(), b);
            }
        }
    }
	
    @EventHandler
    public void anvilClose(final InventoryCloseEvent e) {
    	final Inventory inv = e.getInventory();
    	final UUID puuid = e.getPlayer().getUniqueId();
    	if (inv.getType() == InventoryType.ANVIL) {
    		final Block b = this.anvilMap.get(puuid);
    		b.setData((byte)1);
            this.anvilMap.remove(puuid);
    	}
    }
	
}
