package jexer.demos;

import jexer.TApplication;

public class Demo7 {
	public static void main(String[] args) {
		try {
			// Swing is the default backend on Windows unless explicitly
			// overridden by jexer.Swing.
			TApplication.BackendType backendType = TApplication.BackendType.XTERM;
			if (System.getProperty("os.name").startsWith("Windows")) {
				backendType = TApplication.BackendType.SWING;
			}
			if (System.getProperty("os.name").startsWith("Mac")) {
				backendType = TApplication.BackendType.SWING;
			}
			if (System.getProperty("jexer.Swing") != null) {
				if (System.getProperty("jexer.Swing", "false").equals("true")) {
					backendType = TApplication.BackendType.SWING;
				} else {
					backendType = TApplication.BackendType.XTERM;
				}
			}
			backendType = TApplication.BackendType.SWING;
			TApplication app = new TApplication(backendType) {
				{
					new DemoFullscreenWindow(this);
					new DemoMainWindow(this);
					new DemoMainWindow(this);
					addFileMenu();
					addWindowMenu();
				}
			};
			(new Thread(app)).start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
