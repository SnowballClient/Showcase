package org.golde.snowball.showcaseplugin.events;

import java.util.ArrayList;

import org.bukkit.DyeColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.EnchantingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Dye;
import org.golde.snowball.showcaseplugin.Showcase;

public class BetterEnchanting extends ShowcaseEvent {

	private ArrayList<EnchantingInventory> inventories = new ArrayList<EnchantingInventory>();
	
	private ItemStack lapis;
	
	@Override
	public void onEnable(Showcase pl) {
		super.onEnable(pl);
		inventories = new ArrayList<EnchantingInventory>();
		Dye dye = new Dye();
		dye.setColor(DyeColor.BLUE);
		lapis = new ItemStack(dye.toItemStack());
		lapis.setAmount(3);
	}
	
	@Override
	public void onDisable() {
		 for (final EnchantingInventory ei : this.inventories) {
	            ei.setItem(1, (ItemStack)null);
	        }
	        this.inventories = null;
	}
	
	@EventHandler
    public void openInventoryEvent(final InventoryOpenEvent e) {
        if (e.getInventory() instanceof EnchantingInventory) {
            e.getInventory().setItem(1, this.lapis);
            inventories.add((EnchantingInventory)e.getInventory());
        }
    }
    
    @EventHandler
    public void closeInventoryEvent(final InventoryCloseEvent e) {
        if (e.getInventory() instanceof EnchantingInventory && inventories.contains(e.getInventory())) {
            e.getInventory().setItem(1, (ItemStack)null);
           inventories.remove(e.getInventory());
        }
    }
    
    @EventHandler
    public void inventoryClickEvent(final InventoryClickEvent e) {
        if (e.getClickedInventory() instanceof EnchantingInventory && inventories.contains(e.getInventory()) && e.getSlot() == 1) {
            e.setCancelled(true);
        }
    }
    
    @EventHandler
    public void enchantItemEvent(final EnchantItemEvent e) {
        if (inventories.contains(e.getInventory())) {
            e.getInventory().setItem(1, this.lapis);
        }
    }


}
