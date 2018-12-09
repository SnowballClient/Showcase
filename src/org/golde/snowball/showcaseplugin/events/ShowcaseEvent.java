package org.golde.snowball.showcaseplugin.events;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.golde.snowball.showcaseplugin.Showcase;

public abstract class ShowcaseEvent implements Listener {
	
	protected Showcase pl;
	
	public void onEnable(Showcase pl) {
		this.pl = pl;
		Bukkit.getPluginManager().registerEvents(this, pl);
	}
	
	public void onDisable() {
		
	}
	
}
