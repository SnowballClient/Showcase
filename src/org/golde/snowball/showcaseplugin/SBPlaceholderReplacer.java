package org.golde.snowball.showcaseplugin;

import java.util.Arrays;
import java.util.List;

import com.gmail.filoghost.holographicdisplays.api.placeholder.PlaceholderReplacer;

public class SBPlaceholderReplacer implements PlaceholderReplacer {

	private int currentIndex = 0;
	private List<String> frames;

	public SBPlaceholderReplacer(String base) {
		this.frames = Arrays.asList(new String[] {base + "1", base + "2"});
	}

	@Override
	public String update() {
		String currentFrame = frames.get(currentIndex);

		if (currentIndex == frames.size() - 1) {
			currentIndex = 0;
		}else{
			currentIndex++;
		}

		return currentFrame;
	}
	
	public String replace(String in, String width) {
		return in.replace(frames.get(0), width).replace(frames.get(1), width);
	}

}
