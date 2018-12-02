package org.golde.snowball.showcaseplugin.events;

import org.bukkit.event.Listener;
import org.golde.snowball.showcaseplugin.Showcase;

public interface ShowcaseEvent extends Listener {

	public void onEnable(Showcase pl);
	public void onDisable();
	
}
