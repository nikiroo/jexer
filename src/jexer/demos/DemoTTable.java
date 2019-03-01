/*
 * Jexer - Java Text User Interface
 *
 * The MIT License (MIT)
 *
 * Copyright (C) 2019 David "Niki" ROULET
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 *
 * @author David ROULET [niki@nikiroo.be]
 * @version 1
 */
package jexer.demos;

import java.util.Arrays;
import java.util.List;

import jexer.TApplication;
import jexer.TScrollableWidget;
import jexer.TTable;
import jexer.TTableCellRendererWidget;
import jexer.TTableColumn;
import jexer.TWindow;
import jexer.event.TResizeEvent;

public class DemoTTable extends TWindow {
	private TScrollableWidget widget;

	public DemoTTable(TApplication application) {
		super(application, "Text Table", 30, 10);

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

		TTable tab1 = new TTable(this, 0, 0, getWidth(), getHeight(), null,
				null);
		tab1.setRowData(rows);
		tab1.setHeaders(headers, true);
		widget = tab1;
	}

	@Override
	public void onResize(TResizeEvent resize) {
		super.onResize(resize);
		widget.setWidth(this.getWidth());
		widget.setHeight(this.getHeight());
		widget.reflowData();
	}
}
