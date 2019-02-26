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

/**
 * A simple {@link TTableCellRenderer} that display the values within a
 * {@link TLabel}.
 * <p>
 * It supports a few different modes, see
 * {@link TTableSimpleTextCellRenderer.CellRendererMode}.
 * 
 * @author niki
 */
public class TTableSimpleTextCellRenderer implements TTableCellRenderer {
	private boolean separator;
	private boolean header;

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
		HEADER;
	}

	/**
	 * Create a new renderer for normal text mode.
	 */
	public TTableSimpleTextCellRenderer() {
		this(CellRendererMode.NORMAL);
	}

	/**
	 * Create a new renderer of the given mode.
	 * 
	 * @param mode
	 *            the renderer mode
	 */
	public TTableSimpleTextCellRenderer(CellRendererMode mode) {
		separator = mode == CellRendererMode.SEPARATOR;
		header = mode == CellRendererMode.HEADER;
	}

	@Override
	public TWidget getTableCellRendererComponent(TTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		return new TLabel(table, getText(value), 0, 0, getColorKey(isSelected,
				hasFocus), false);
	}

	@Override
	public boolean updateTableCellRendererComponent(TTable table,
			TWidget component, Object value, boolean isSelected,
			boolean hasFocus, int row, int column) {

		if (component instanceof TLabel) {
			TLabel widget = (TLabel) component;
			widget.setLabel(getText(value));
			widget.setColorKey(getColorKey(isSelected, hasFocus));
			return true;
		}

		return false;
	}

	/**
	 * Return the text to use (usually the converted-to-text value, except for
	 * the special separator mode).
	 * 
	 * @param value
	 *            the value to get the text of
	 * 
	 * @return the {@link String} to display
	 */
	protected String getText(Object value) {
		if (separator) {
			// some nice characters: ┃ │ |
			return " │ ";
		}

		return "" + value;
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
		}

		return colorKey;
	}
}
