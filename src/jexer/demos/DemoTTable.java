package jexer.demos;

import java.util.Arrays;
import java.util.List;

import jexer.TApplication;
import jexer.TTable;
import jexer.TTableOld;
import jexer.TWindow;

public class DemoTTable extends TWindow {
	public DemoTTable(TApplication application) {
		super(application, "Add/Remove widgets", 60, 20);

		List<Object> headers = Arrays
				.asList((Object) "COL_1", (Object) "COL_2");

		List<List<String>> rows = Arrays.asList(
				Arrays.asList("val 1", "val 2"), //
				Arrays.asList("seval 1", "v 2"), //
				Arrays.asList("col value 1", "value 2"), //
				Arrays.asList("v1", "vavavava 2") //
				);

		TTableOld tab1 = new TTableOld(this, 2, 2, 56, 16, null, null);
		tab1.setHeaders((List<String>) ((Object) headers), true);
		for (List<String> row : rows) {
			tab1.addRow(row);
		}

		TTable tab2 = new TTable(this, 2, 10, 56, 16, null, null, true);
		tab2.setRowData(rows, headers);
	}
}
