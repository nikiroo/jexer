package jexer.demos;

import jexer.TApplication;
import jexer.TWindow;
import jexer.event.TResizeEvent;

public class DemoFullscreenWindow extends TWindow {
	public DemoFullscreenWindow(TApplication parent) {
		super(parent, "Fullscreen window", 0, 0, 44, 20);
		setFullscreen(true);
		addLabel("Exit: ALT + X", 4, 4);
		maximize();
	}
}
