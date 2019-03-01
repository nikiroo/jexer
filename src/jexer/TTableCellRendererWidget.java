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
package jexer;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link TTableCellRenderer} that display the values within a
 * {@link TLabel}.
 * <p>
 * It supports a few different modes, see
 * {@link TTableSimpleTextCellRenderer.CellRendererMode}.
 * 
 * @author niki
 */
public class TTableCellRendererWidget extends TTableCellRenderer {
	private boolean rightAlign;
	private Map<String, TWidget> widgets = new HashMap<String, TWidget>();

	/**
	 * Create a new renderer for normal text mode.
	 */
	public TTableCellRendererWidget() {
		this(CellRendererMode.NORMAL);
	}

	/**
	 * Create a new renderer of the given mode.
	 * 
	 * @param mode
	 *            the renderer mode
	 */
	public TTableCellRendererWidget(CellRendererMode mode) {
		this(mode, false);
	}

	/**
	 * Create a new renderer of the given mode.
	 * 
	 * @param mode
	 *            the renderer mode, cannot be NULL
	 */
	public TTableCellRendererWidget(CellRendererMode mode,
			boolean rightAlign) {
		super(mode);

		this.rightAlign = rightAlign;
	}

	@Override
	public void renderTableCell(TTable table, Object value, int rowIndex,
			int colIndex, int y) {

		String wkey = "[Row " + y + " " + getMode() + "]";
		TWidget widget = widgets.get(wkey);

		TTableColumn tcol = table.getColumns().get(colIndex);
		boolean isSelected = table.getSelectedRow() == rowIndex;
		boolean hasFocus = table.isAbsoluteActive();
		int width = tcol.getWidth();

		int xOffset = getXOffset(table, colIndex);

		if (widget != null
				&& !updateTableCellRendererComponent(widget, value, isSelected,
						hasFocus, xOffset, y, width)) {
			table.removeChild(widget);
			widget = null;
		}

		if (widget == null) {
			widget = getTableCellRendererComponent(table, value, isSelected,
					hasFocus, y, xOffset, width);
		}

		widgets.put(wkey, widget);
	}

	private TWidget getTableCellRendererComponent(TTable table,
			Object value, boolean isSelected, boolean hasFocus, int row,
			int column, int width) {
		return new TLabel(table, asText(value, width, rightAlign), column, row,
				getColorKey(isSelected, hasFocus), false);
	}

	private boolean updateTableCellRendererComponent(TWidget component,
			Object value, boolean isSelected, boolean hasFocus, int x, int y,
			int width) {

		if (component instanceof TLabel) {
			TLabel widget = (TLabel) component;
			widget.setLabel(asText(value, width, rightAlign));
			widget.setColorKey(getColorKey(isSelected, hasFocus));
			widget.setWidth(width);
			widget.setX(x);
			widget.setY(y);
			return true;
		}

		return false;
	}
}
