package jexer.demos;

import java.util.Arrays;
import java.util.List;

import jexer.TApplication;
import jexer.TScrollableWidget;
import jexer.TTableColumn;
import jexer.TTable;
import jexer.TTableCellRendererWidget;
import jexer.TWindow;
import jexer.event.TResizeEvent;

public class DemoTTable extends TWindow {
	private TScrollableWidget widget;

	public DemoTTable(TApplication application, boolean old) {
		super(application, old ? "Old Table" : "New Table", 30, 10);

		List<String> headers = Arrays.asList("COL_1", "COL_2");

		@SuppressWarnings("unchecked")
		List<List<String>> rows = Arrays.asList(Arrays.asList(
				"First row, first column", "Some value for row1 col2"), //
				Arrays.asList("This is a key", "This is a value"), //
				Arrays.asList("Testy", "Toasty"), //
				Arrays.asList("v1", "Vavakung"), //
				Arrays.asList("1", "un"), //
				Arrays.asList("2", "deux"), //
				Arrays.asList("3", "trois"), //
				Arrays.asList("4", "quatre"), //
				Arrays.asList("5", "cinq"), //
				Arrays.asList("6", "six"), //
				Arrays.asList("7", "sept"), //
				Arrays.asList("8", "huit"), //
				Arrays.asList("9", "neuf") //
				);

		if (old) {
			TTable tab1 = new TTable(this, 0, 0, getWidth(), getHeight(),
					null, null);
			tab1.setRowData(rows, headers);
			tab1.setHeaders(headers, true);
			widget = tab1;
		} else {
			/*
			 * TTable tab2 = new TTable(this, 0, 0, getWidth(), getHeight(),
			 * null, null, true); tab2.setRowData(rows, headers); widget = tab2;
			 */
			TTable tab1 = new TTable(this, 0, 0, getWidth(), getHeight(),
					null, null);
			tab1.setRowData(rows, headers);
			tab1.setHeaders(headers, true);
			for (TTableColumn tcol : tab1.getColumns()) {
				tcol.setCellRenderer(new TTableCellRendererWidget());
			}
			widget = tab1;
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
