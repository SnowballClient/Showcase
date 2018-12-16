package org.golde.snowball.showcaseplugin.events;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.golde.snowball.api.event.SnowballPlayerKeypressEvent;
import org.golde.snowball.showcaseplugin.Showcase;
import org.golde.snowball.showcaseplugin.objs.KeyGui;

public class GenericEvents extends ShowcaseEvent {

	@EventHandler
    public void onLeavesDecay(final LeavesDecayEvent event) {
        event.setCancelled(true);
    }
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		e.getPlayer().teleport(pl.getLoc_spawn().clone());
		e.getPlayer().setGameMode(GameMode.CREATIVE);
	}
	
	@EventHandler
	public void onLeave(PlayerQuitEvent e) {
		final UUID puuid = e.getPlayer().getUniqueId();
		if(pl.getPlayerLastPressedKeys().containsKey(puuid)) {
			pl.getPlayerLastPressedKeys().remove(puuid);
		}
	}
	
	@EventHandler
    public void onWeatherChange(final WeatherChangeEvent e) {
		e.setCancelled(true);
    }
	
	@EventHandler
	public void onPlayerInteractEntity(PlayerInteractEntityEvent event){
	    if(event.getRightClicked().getType() != EntityType.VILLAGER) return;
	    event.setCancelled(true);
	}
	
	@EventHandler
	public void onEntityHurt(EntityDamageByEntityEvent e) {
		e.setCancelled(true);
		e.setDamage(0);
	}
	
	@EventHandler
	public void onEntityHurt(EntityDamageByBlockEvent e) {
		e.setCancelled(true);
		e.setDamage(0);
	}
	
	@EventHandler
	public void onSnowballKeyPress(SnowballPlayerKeypressEvent e) {
		pl.getPlayerLastPressedKeys().put(e.getPlayer().getBukkitPlayer().getUniqueId(), new KeyGui(e));
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
		e.setCancelled(!canDo(e.getPlayer()));
	}
	
	@EventHandler
	public void onBlockBreak(BlockPlaceEvent e) {
		e.setCancelled(!canDo(e.getPlayer()));
	}
	
	@EventHandler
	public void onMove(PlayerMoveEvent e) {
		if(!canDo(e.getPlayer())) {
			e.getPlayer().setFlying(false);
			e.getPlayer().setAllowFlight(false);
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
    public void onTrample(final PlayerInteractEvent event) {
        if (event.isCancelled()) {
            return;
        }
        if (event.getAction() == Action.PHYSICAL) {
            final Block block = event.getClickedBlock();
            if (block == null) {
                return;
            }
            final int blockType = block.getTypeId();
            if (blockType == Material.getMaterial(59).getId()) {
                event.setUseInteractedBlock(Event.Result.DENY);
                event.setCancelled(true);
                block.setTypeId(blockType);
                block.setData(block.getData());
            }
        }
        if (event.getAction() == Action.PHYSICAL) {
            final Block block = event.getClickedBlock();
            if (block == null) {
                return;
            }
            final int blockType = block.getTypeId();
            if (blockType == Material.getMaterial(60).getId()) {
                event.setUseInteractedBlock(Event.Result.DENY);
                event.setCancelled(true);
                block.setType(Material.getMaterial(60));
                block.setData(block.getData());
            }
        }
    }

    @EventHandler
    public void onPlayerBucketFill(final PlayerBucketFillEvent e) {
    	e.setCancelled(true);
    }
    @EventHandler
    public void onPlayerBucketEmpty(final PlayerBucketEmptyEvent e) {
    	e.setCancelled(true);
    }
	
	private boolean canDo(Player player) {
		return player.isOp();
	}

}
