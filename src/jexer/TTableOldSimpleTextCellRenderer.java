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

import jexer.bits.CellAttributes;
import jexer.bits.ColorTheme;

/**
 * A simple {@link TTableCellRenderer} that display the values within a
 * {@link TLabel}.
 * <p>
 * It supports a few different modes, see
 * {@link TTableOldSimpleTextCellRenderer.CellRendererMode}.
 * 
 * @author niki
 */
public class TTableOldSimpleTextCellRenderer implements TTableOldCellRenderer {
	private boolean separator;
	private boolean header;
	private boolean rightAlign;

	/**
	 * The simple renderer mode.
	 * 
	 * @author niki
	 */
	public enum CellRendererMode {
		/** Normal text mode */
		NORMAL,
		/** Only display a separator */
		SEPARATOR,
		/** Header text mode */
		HEADER,
		/** Both HEADER and SEPARATOR at once */
		HEADER_SEPARATOR;
	}

	/**
	 * Create a new renderer for normal text mode.
	 */
	public TTableOldSimpleTextCellRenderer() {
		this(CellRendererMode.NORMAL);
	}

	/**
	 * Create a new renderer of the given mode.
	 * 
	 * @param mode
	 *            the renderer mode
	 */
	public TTableOldSimpleTextCellRenderer(CellRendererMode mode) {
		this(mode, false);
	}

	/**
	 * Create a new renderer of the given mode.
	 * 
	 * @param mode
	 *            the renderer mode
	 */
	public TTableOldSimpleTextCellRenderer(CellRendererMode mode,
			boolean rightAlign) {
		separator = mode == CellRendererMode.SEPARATOR
				|| mode == CellRendererMode.HEADER_SEPARATOR;
		header = mode == CellRendererMode.HEADER
				|| mode == CellRendererMode.HEADER_SEPARATOR;
		this.rightAlign = rightAlign;
	}

	@Override
	public void renderTableCell(TTableOld table, Object value, int rowIndex,
			int colIndex, int y) {
		int xOffset = table.getHorizontalValue();
		for (int i = 0; i <= colIndex; i++) {
			TTableColumn tcol = table.getColumns().get(i);
			xOffset += tcol.getWidth();
			if (i > 0) {
				xOffset += table.getSeparatorRenderer().getWidthOf(null);
			}
		}

		TTableColumn tcol = table.getColumns().get(colIndex);
		if (!separator) {
			xOffset -= tcol.getWidth();
		}

		String data = getText(value, tcol.getWidth());
		if (!data.isEmpty()) {
			boolean isSelected = table.getSelectedRow() == rowIndex;
			boolean hasFocus = table.isAbsoluteActive();
			CellAttributes color = getCellAttributes(table.getTheme(),
					isSelected, hasFocus);
			table.getScreen().putStringXY(xOffset, y, data, color);
		}
	}

	/**
	 * Return the text to use (usually the converted-to-text value, except for
	 * the special separator mode).
	 * 
	 * @param value
	 *            the value to get the text of
	 * @param width
	 *            the width we should tale
	 * 
	 * @return the {@link String} to display
	 */
	protected String getText(Object value, int width) {
		if (separator) {
			// some nice characters for the separator: ┃ │ |
			return " │ ";
		}

		if (width <= 0) {
			return "";
		}

		String format;
		if (!rightAlign) {
			// Left align
			format = "%-" + width + "s";
		} else {
			// right align
			format = "%" + width + "s";
		}

		return String.format(format, value);
	}

	@Override
	public CellAttributes getCellAttributes(ColorTheme theme,
			boolean isSelected, boolean hasFocus) {
		return theme.getColor(getColorKey(isSelected, hasFocus));
	}

	@Override
	public int getWidthOf(Object value) {
		if (separator) {
			return getText(null, 0).length();
		}
		return ("" + value).length();
	}

	/**
	 * The colour to use for the given state, specified as a Jexer colour key.
	 * 
	 * @param isSelected
	 *            TRUE if the cell is selected
	 * @param hasFocus
	 *            TRUE if the cell has focus
	 * 
	 * @return the colour key
	 */
	protected String getColorKey(boolean isSelected, boolean hasFocus) {
		if (header) {
			return "tlabel";
		}

		String colorKey = "tlist";
		if (isSelected) {
			colorKey += ".selected";
		} else if (!hasFocus) {
			colorKey += ".inactive";
		}

		return colorKey;
	}
}
