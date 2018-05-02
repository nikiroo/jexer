package jexer.demos;

import jexer.TAction;
import jexer.TApplication;
import jexer.TButton;
import jexer.TLabel;
import jexer.TWindow;

public class DemoAddRemoveWindow extends TWindow {
	private int current_y;
	private TLabel current_label;
	TButton add;

	public DemoAddRemoveWindow(TApplication application) {
		super(application, "Add/Remove widgets", 60, 20);

		final TLabel label = addLabel("Some label", 4, y());

		addButton("Remove 'Some label'", 4, y(), new TAction() {
			@Override
			public void DO() {
				removeChild(label);
			}
		});

		add = addButton("Create random label", 4, y(), new TAction() {
			@Override
			public void DO() {
				if (current_label == null) {
					String text = Integer.toString((int) ((10000 * Math
							.random()) % 10000));
					current_label = new TLabel(null, text, 4, y());
					add.setText("Add previously created label");
				} else {
					addChild(current_label);
					current_label = null;
					add.setText("Create random label");
				}
			}
		});
	}

	private int y() {
		current_y += 2;
		return current_y;
	}
}
