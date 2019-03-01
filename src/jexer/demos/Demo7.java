package jexer.demos;

import jexer.TApplication;
import jexer.TApplication.BackendType;

public class Demo7 {
	public static void main(String[] args) {
		try {
			TApplication app = new TApplication(backend(null)) {
				{
					new DemoTTable(this, true);
					new DemoTTable(this, false);
					addFileMenu();
					addWindowMenu();
				}
			};
			(new Thread(app)).start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Select the most appropriate backend.
	 * 
	 * @param textMode
	 *            NULL for auto-detection
	 * @return the backend type to use
	 */
	private static BackendType backend(Boolean textMode) {
		if (textMode == null) {
			boolean isMsWindows = System.getProperty("os.name", "")
					.toLowerCase().startsWith("windows");
			boolean forceSwing = System.getProperty("jexer.Swing", "false")
					.equals("true");
			boolean noConsole = System.console() == null;
			if (isMsWindows || forceSwing || noConsole) {
				return BackendType.SWING;
			}

			return BackendType.XTERM;
		}

		if (textMode) {
			return BackendType.XTERM;
		}

		return BackendType.SWING;
	}
}
