package org.golde.snowball.showcaseplugin.objs;

import org.golde.snowball.api.event.SnowballPlayerKeypressEvent;

public class KeyGui {

	public final int key;
	public final boolean inGui;
	
	public KeyGui(SnowballPlayerKeypressEvent e) {
		this.key = e.getKey();
		this.inGui = e.isInGuiWindow();
	}
	
}
