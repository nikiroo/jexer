package jexer.demos;

import java.util.Arrays;
import java.util.List;

import jexer.TApplication;
import jexer.TScrollableWidget;
import jexer.TTable;
import jexer.TTableOld;
import jexer.TWindow;
import jexer.event.TResizeEvent;

public class DemoTTable extends TWindow {
	private TScrollableWidget widget;

	public DemoTTable(TApplication application, boolean old) {
		super(application, old ? "Old Table" : "New Table", 60, 20);

		List<String> headers = Arrays.asList("COL_1", "COL_2");

		@SuppressWarnings("unchecked")
		List<List<String>> rows = Arrays.asList(
				Arrays.asList("val 1", "val 2"), //
				Arrays.asList("seval 1", "v 2"), //
				Arrays.asList("col value 1", "value 2"), //
				Arrays.asList("v1", "vavavava 2") //
				);

		if (old) {
			TTableOld tab1 = new TTableOld(this, 0, 0, getWidth(), getHeight(),
					null, null);
			tab1.setRowData(rows, headers);
			tab1.setHeaders(headers, true);
			widget = tab1;
		} else {
			TTable tab2 = new TTable(this, 0, 0, getWidth(), getHeight(), null,
					null, true);
			tab2.setRowData(rows, headers);
			widget = tab2;
		}
	}

	@Override
	public void onResize(TResizeEvent resize) {
		super.onResize(resize);
		widget.setWidth(this.getWidth());
		widget.setHeight(this.getHeight());
		widget.reflowData();
	}
}
