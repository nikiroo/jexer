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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.swing.table.TableModel;

import jexer.TTableSimpleTextCellRenderer.CellRendererMode;
import jexer.event.TResizeEvent;

/**
 * A {@link TTable} is a table you can navigate into; you can select an item
 * then 'execute' it by pressing ENTER with the keyboard or with a mouse click.
 * <p>
 * It consists of rows of items, with an optional header row.
 * 
 * @author niki
 */
public class TTable extends TBrowsableWidget {
	static private TTableCellRenderer defaultCellRenderer = new TTableSimpleTextCellRenderer(
			CellRendererMode.NORMAL);
	static private TTableCellRenderer defaultSeparatorRenderer = new TTableSimpleTextCellRenderer(
			CellRendererMode.SEPARATOR);
	static private TTableCellRenderer defaultHeaderRenderer = new TTableSimpleTextCellRenderer(
			CellRendererMode.HEADER);

	/**
	 * The action to perform when the user selects an item (mouse click or
	 * enter).
	 */
	private TAction enterAction = null;

	/**
	 * The action to perform when the user navigates with keyboard.
	 */
	private TAction moveAction = null;

	private TableModel model = null;

	private List<TTableColumn> columns = new ArrayList<TTableColumn>();

	private List<List<TWidget>> headers = new ArrayList<List<TWidget>>();
	private List<List<TWidget>> rows = new ArrayList<List<TWidget>>();
	private List<List<TWidget>> separators = new ArrayList<List<TWidget>>();

	/** The size of the header, in rows (default is 2). */
	private int headerSize = 2;

	/**
	 * Create a new {@link TTable}.
	 * 
	 * @param parent
	 *            the parent widget
	 * @param x
	 *            the X position
	 * @param y
	 *            the Y position
	 * @param width
	 *            the width of the {@link TTable}
	 * @param height
	 *            the height of the {@link TTable}
	 */
	public TTable(TWidget parent, int x, int y, int width, int height) {
		this(parent, x, y, width, height, null, null, false);
	}

	/**
	 * Create a new {@link TTable}.
	 * 
	 * @param parent
	 *            the parent widget
	 * @param x
	 *            the X position
	 * @param y
	 *            the Y position
	 * @param width
	 *            the width of the {@link TTable}
	 * @param height
	 *            the height of the {@link TTable}
	 * @param enterAction
	 *            an action to call when a cell is selected
	 * @param moveAction
	 *            an action to call when the currently active cell is changed
	 */
	public TTable(TWidget parent, int x, int y, int width, int height,
			final TAction enterAction, final TAction moveAction) {
		this(parent, x, y, width, height, enterAction, moveAction, false);
	}

	/**
	 * Create a new {@link TTable}.
	 * 
	 * @param parent
	 *            the parent widget
	 * @param x
	 *            the X position
	 * @param y
	 *            the Y position
	 * @param width
	 *            the width of the {@link TTable}
	 * @param height
	 *            the height of the {@link TTable}
	 * @param enterAction
	 *            an action to call when a cell is selected
	 * @param moveAction
	 *            an action to call when the currently active cell is changed
	 * @param showHeaders
	 *            TRUE to show the headers on screen
	 */
	public TTable(TWidget parent, int x, int y, int width, int height,
			final TAction enterAction, final TAction moveAction,
			boolean showHeaders) {
		super(parent, x, y, width, height);

		this.enterAction = enterAction;
		this.moveAction = moveAction;

		if (showHeaders) {
			headerSize = 2;
		} else {
			headerSize = 0;
		}
	}

	/**
	 * The number of rows.
	 * 
	 * @return the number of rows
	 */
	@Override
	public int getNumberOfRows() {
		return rows.size();
	}

	/**
	 * The number of columns.
	 * 
	 * @return the number of columns
	 */
	public int getNumberOfColumns() {
		return headers.size();
	}

	/**
	 * Perform user selection action.
	 */
	@Override
	public void dispatchEnter(int selectedRow) {
		super.dispatchEnter(selectedRow);
		if (enterAction != null) {
			enterAction.DO();
		}
	}

	/**
	 * Perform list movement action.
	 */
	@Override
	public void dispatchMove(int fromRow, int toRow) {
		super.dispatchMove(fromRow, toRow);
		reflow(fromRow, fromRow);
		reflow(toRow, toRow);
		if (moveAction != null) {
			moveAction.DO();
		}
	}

	/**
	 * The data model behind this {@link TTable}, as with the usual Swing
	 * tables.
	 * 
	 * @return the model
	 */
	public TableModel getModel() {
		return model;
	}

	/**
	 * The data model behind this {@link TTable}, as with the usual Swing
	 * tables.
	 * <p>
	 * Will reset all the rendering cells.
	 * 
	 * @param model
	 *            the new model
	 */
	public void setModel(TableModel model) {
		this.model = model;
		resetData();
	}

	/**
	 * Set the data and create a new {@link TTableSimpleTextModel} for them.
	 * 
	 * @param data
	 *            the data to set into this table, as an array of rows, that is,
	 *            an array of arrays of values
	 * @param names
	 *            the optional names of the column (can be NULL)
	 */

	public void setRowData(Object[][] data, Object[] names) {
		setRowData(TTableSimpleTextModel.convert(data), Arrays.asList(names));
	}

	/**
	 * Set the data and create a new {@link TTableSimpleTextModel} for them.
	 * 
	 * @param data
	 *            the data to set into this table, as a collection of rows, that
	 *            is, a collection of collections of values
	 * @param names
	 *            the optional names of the column (can be NULL)
	 */
	public void setRowData(
			final Collection<? extends Collection<? extends Object>> data,
			final Collection<? extends Object> names) {

		TableModel model = new TTableSimpleTextModel(data);

		String[] onames;
		if (names != null) {
			onames = names.toArray(new String[] {});
		} else {
			onames = new String[model.getColumnCount()];
		}

		columns.clear();
		for (int i = 0; i < onames.length; i++) {
			columns.add(new TTableColumn(i, onames[i], model));
		}

		setModel(model);
	}

	/**
	 * Delete all the cells and force their recreation if visible (note: some
	 * cells can be NULL after creation, this is allowed; we can render NULL
	 * cells).
	 */
	private void resetData() {
		for (List<TWidget> row : rows) {
			for (TWidget col : row) {
				this.removeChild(col);
			}
		}
		for (List<TWidget> sepRow : separators) {
			for (TWidget sep : sepRow) {
				this.removeChild(sep);
			}
		}

		headers = new ArrayList<List<TWidget>>(1);
		rows = new ArrayList<List<TWidget>>(model.getRowCount());
		separators = new ArrayList<List<TWidget>>(model.getRowCount());

		List<TWidget> headerCols = Arrays.asList(new TWidget[model
				.getColumnCount()]);
		List<TWidget> headerSepCols = Arrays.asList(new TWidget[model
				.getColumnCount()]);
		headers.add(headerCols);
		headers.add(headerSepCols);

		for (int i = 0; i < model.getRowCount(); i++) {
			List<TWidget> cols = Arrays.asList(new TWidget[model
					.getColumnCount()]);
			rows.add(cols);
			List<TWidget> seps = Arrays.asList(new TWidget[model
					.getColumnCount()]);
			separators.add(seps);
		}

		reflowData();
	}

	/**
	 * Loop through all the cells and update their values (which can result in
	 * new {@link TWidget}s being created and old one discarded).
	 */
	@Override
	public void reflowData() {
		super.reflowData();
		// TODO: only reflow the visible rows
		reflow(0, rows.size() - 1);
	}

	/**
	 * Loop through all the cells of the given rows and update their values
	 * (which can result in new {@link TWidget}s being created and old one
	 * discarded).
	 */
	private void reflow(int fromRow, int toRow) {
		int numOfCols = columns.size();
		int selectedRow = getSelectedRow();
		for (int rowIndex = fromRow - headerSize; rowIndex <= toRow; rowIndex++) {
			int currentX = 0;
			for (int displayColIndex = 0; displayColIndex < numOfCols; displayColIndex++) {
				TTableColumn tcol = columns.get(displayColIndex);
				int colIndex = tcol.getModelIndex();

				TTableCellRenderer headerRenderer = tcol.getHeaderRenderer();
				if (headerRenderer == null) {
					headerRenderer = defaultHeaderRenderer;
				}
				TTableCellRenderer cellRenderer = tcol.getCellRenderer();
				if (cellRenderer == null) {
					cellRenderer = defaultCellRenderer;
				}
				TTableCellRenderer separatorRenderer = tcol
						.getSeparatorRenderer();
				if (separatorRenderer == null) {
					separatorRenderer = defaultSeparatorRenderer;
				}

				if (rowIndex == -1) {
					Object value = columns.get(colIndex).getHeaderValue();
					updateData(headers, 0, colIndex, headerRenderer, currentX,
							0, value, false, false, tcol.getWidth());
					currentX += tcol.getWidth();

					if (displayColIndex + 1 < numOfCols) {
						TWidget sep = updateData(headers, 1, colIndex,
								separatorRenderer, currentX, -1, value, false,
								false, 0);
						if (sep != null) {
							currentX += sep.getWidth();
						}
					}
				} else if (rowIndex < 0) {
					// Skip (empty row for headers)
				} else {
					Object value = model.getValueAt(rowIndex, colIndex);
					boolean isSelected = (rowIndex == selectedRow);
					boolean hasFocus = this.isAbsoluteActive();

					updateData(rows, rowIndex, colIndex, cellRenderer,
							currentX, headerSize, value, isSelected, hasFocus,
							tcol.getWidth());
					currentX += tcol.getWidth();

					if (displayColIndex + 1 < numOfCols) {
						TWidget sep = updateData(separators, rowIndex,
								colIndex, separatorRenderer, currentX,
								headerSize, value, isSelected, hasFocus, 0);
						if (sep != null) {
							currentX += sep.getWidth();
						}
					}
				}
			}
		}
	}

	/**
	 * Update and/or (re)create the cell with the new data.
	 * <p>
	 * The resulting {@link TWidget}, if not NULL, will always be a child of
	 * 'this' (i.e., we will add it to 'this' if needed).
	 * 
	 * @param widgets
	 *            the list of widgets
	 * @param rowIndex
	 *            the row index
	 * @param colIndex
	 *            the column index
	 * @param renderer
	 *            the renderer to use to display the value
	 * @param currentX
	 *            the current X position
	 * @param yOffset
	 *            the Y offset
	 * @param value
	 *            the new value to display
	 * @param isSelected
	 *            TRUE if the cell is selected
	 * @param hasFocus
	 *            TRUE if the cell has focus
	 * @param width
	 *            the width we can take
	 * 
	 * @return the resulting {@link TWidget} (can be the same as before, can be
	 *         NULL, can be a new one; will always be a child of 'this')
	 */
	private TWidget updateData(List<List<TWidget>> widgets, int rowIndex,
			int colIndex, TTableCellRenderer renderer, int currentX,
			int yOffset, Object value, boolean isSelected, boolean hasFocus,
			int width) {

		TWidget widget = widgets.get(rowIndex).get(colIndex);
		if (widget != null) {
			if (!renderer.updateTableCellRendererComponent(this, widget, value,
					isSelected, hasFocus, rowIndex, colIndex, width)) {
				removeChild(widget);
				widget = null;
			}
		}
		if (widget == null) {
			widget = renderer.getTableCellRendererComponent(this, value,
					isSelected, hasFocus, rowIndex, colIndex, width);
		}

		widgets.get(rowIndex).set(colIndex, widget);

		if (widget != null) {
			widget.setX(currentX);
			widget.setY(rowIndex + yOffset);

			if (widget.getParent() != this) {
				addChild(widget);
			}
		}

		return widget;
	}

	@Override
	public void onResize(TResizeEvent resize) {
		super.onResize(resize);
		reflowData();
	}
}
