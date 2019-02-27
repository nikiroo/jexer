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
 * A {@link TTableOld} cell renderer allows you to customize the way a single
 * cell will be displayed on screen.
 * 
 * @author niki
 */
public interface TTableOldCellRenderer {
	/**
	 * Render the given value.
	 * 
	 * @param table
	 *            the table to write on
	 * @param value
	 *            the value to write
	 * @param rowIndex
	 *            the row index in the table
	 * @param colIndex
	 *            the column index in the table
	 * @param y
	 *            the Y position at which to draw this row
	 */
	public void renderTableCell(TTableOld table, Object value, int rowIndex,
			int colIndex, int y);

	/**
	 * The cell attributes to use for the given state.
	 * 
	 * @param theme
	 *            the color theme to use
	 * @param isSelected
	 *            TRUE if the cell is selected
	 * @param hasFocus
	 *            TRUE if the cell has focus
	 * 
	 * @return the attributes
	 */
	public CellAttributes getCellAttributes(ColorTheme theme,
			boolean isSelected, boolean hasFocus);

	public int getWidthOf(Object value);
}