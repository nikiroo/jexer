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
 * A {@link TTable} cell renderer allows you to customize the way a single cell
 * will be displayed on screen.
 * 
 * @author niki
 */
public interface TTableCellRenderer {
	/**
	 * Create a new {@link TWidget} component to render the specified cell, or
	 * simply return NULL if no cell has to exist here.
	 * 
	 * @param table
	 *            the {@link TTable} this cell is used on
	 * @param value
	 *            the new data value for it
	 * @param isSelected
	 *            TRUE if the cell is selected
	 * @param hasFocus
	 *            TRUE if the cell has focus
	 * @param row
	 *            the row index in the table
	 * @param column
	 *            the column index in the table
	 * @param width
	 *            the width we are supposed to take
	 * 
	 * @return
	 */
	public TWidget getTableCellRendererComponent(TTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column, int width);

	/**
	 * Update the given {@link TWidget} component for the given cell if
	 * possible, or return FALSE to cancel it (the component will be removed --
	 * a new one will be created via
	 * {@link TTableCellRenderer#getTableCellRendererComponent(TTable, Object, boolean, boolean, int, int)}
	 * ).
	 * 
	 * @param table
	 *            the {@link TTable} this cell is used on
	 * @param component
	 *            the actual component to update
	 * @param value
	 *            the new data value for it
	 * @param isSelected
	 *            TRUE if the cell is selected
	 * @param hasFocus
	 *            TRUE if the cell has focus
	 * @param row
	 *            the row index in the table
	 * @param column
	 *            the column index in the table
	 * @param width
	 *            the width we are supposed to take
	 * 
	 * @return TRUE if the component has been updated and can be kept, FALSE if
	 *         it must be discarded (a new one will be created via
	 *         {@link TTableCellRenderer#getTableCellRendererComponent(TTable, Object, boolean, boolean, int, int)}
	 *         ).
	 */
	public boolean updateTableCellRendererComponent(TTable table,
			TWidget component, Object value, boolean isSelected,
			boolean hasFocus, int row, int column, int width);
}