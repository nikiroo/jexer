package jexer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.swing.table.TableModel;

import jexer.TTableOldSimpleTextCellRenderer.CellRendererMode;
import jexer.bits.CellAttributes;
import jexer.bits.ColorTheme;

/**
 * A table widget to display and browse through tabular data.
 * 
 * @author niki
 */
public class TTableOld extends TBrowsableWidget {
	private List<String> headers;
	private List<Integer> columnSizes;
	private boolean showHeader;

	private List<TTableColumn> columns = new ArrayList<TTableColumn>();
	private TableModel model;

	// private List<List<String>> rows;
	private int selectedColumn;

	static private TTableOldCellRenderer defaultSeparatorRenderer = new TTableOldSimpleTextCellRenderer(
			CellRendererMode.SEPARATOR);
	static private TTableOldCellRenderer defaultHeaderRenderer = new TTableOldSimpleTextCellRenderer(
			CellRendererMode.HEADER);
	static private TTableOldCellRenderer defaultHeaderSeparatorRenderer = new TTableOldSimpleTextCellRenderer(
			CellRendererMode.HEADER_SEPARATOR);

	private TTableOldCellRenderer separatorRenderer;
	private TTableOldCellRenderer headerRenderer;
	private TTableOldCellRenderer headerSeparatorRenderer;

	/**
	 * The action to perform when the user selects an item (clicks or enter).
	 */
	private TAction enterAction = null;

	/**
	 * The action to perform when the user navigates with keyboard.
	 */
	private TAction moveAction = null;

	/**
	 * Create a new {@link TTableOld}.
	 * 
	 * @param parent
	 *            the parent widget
	 * @param x
	 *            the X position
	 * @param y
	 *            the Y position
	 * @param width
	 *            the width of the {@link TTableOld}
	 * @param height
	 *            the height of the {@link TTableOld}
	 * @param enterAction
	 *            an action to call when a cell is selected
	 * @param moveAction
	 *            an action to call when the currently active cell is changed
	 */
	public TTableOld(TWidget parent, int x, int y, int width, int height,
			final TAction enterAction, final TAction moveAction) {
		this(parent, x, y, width, height, enterAction, moveAction, null, false);
	}

	/**
	 * Create a new {@link TTableOld}.
	 * 
	 * @param parent
	 *            the parent widget
	 * @param x
	 *            the X position
	 * @param y
	 *            the Y position
	 * @param width
	 *            the width of the {@link TTableOld}
	 * @param height
	 *            the height of the {@link TTableOld}
	 * @param enterAction
	 *            an action to call when a cell is selected
	 * @param moveAction
	 *            an action to call when the currently active cell is changed
	 * @param headers
	 *            the headers of the {@link TTableOld}
	 * @param showHeaders
	 *            TRUE to show the headers on screen
	 */
	public TTableOld(TWidget parent, int x, int y, int width, int height,
			final TAction enterAction, final TAction moveAction,
			List<String> headers, boolean showHeaders) {
		super(parent, x, y, width, height);

		this.model = new TTableSimpleTextModel(new Object[][] {});
		setSelectedRow(-1);
		this.selectedColumn = -1;

		setHeaders(headers, showHeaders);

		this.enterAction = enterAction;
		this.moveAction = moveAction;

		reflowData();
	}

	public TableModel getModel() {
		return model;
	}

	public List<TTableColumn> getColumns() {
		return columns;
	}

	public TTableOldCellRenderer getSeparatorRenderer() {
		return separatorRenderer != null ? separatorRenderer
				: defaultSeparatorRenderer;
	}

	public void setSeparatorRenderer(TTableOldCellRenderer separatorRenderer) {
		this.separatorRenderer = separatorRenderer;
	}

	public TTableOldCellRenderer getHeaderRenderer() {
		return headerRenderer != null ? headerRenderer : defaultHeaderRenderer;
	}

	public void setHeaderRenderer(TTableOldCellRenderer headerRenderer) {
		this.headerRenderer = headerRenderer;
	}

	public TTableOldCellRenderer getHeaderSeparatorRenderer() {
		return headerSeparatorRenderer != null ? headerSeparatorRenderer
				: defaultHeaderSeparatorRenderer;
	}

	public void setHeaderSeparatorRenderer(
			TTableOldCellRenderer headerSeparatorRenderer) {
		this.headerSeparatorRenderer = headerSeparatorRenderer;
	}

	/**
	 * Change the headers of the table.
	 * <p>
	 * Note that if some data is present, the number of columns <b>MUST</b> be
	 * identical.
	 * 
	 * @param headers
	 *            the new headers
	 * @param showHeaders
	 *            TRUE to show them on screen
	 */
	public void setHeaders(List<String> headers, boolean showHeaders) {
		if (headers == null) {
			headers = new ArrayList<String>();
		}

		if (getColumnCount() != headers.size()) {
			throw new IllegalArgumentException(
					String.format(
							"Cannot set the headers of a table if the number of items is not equals to that of the current data rows: "
									+ "%d elements in the data rows <> %d elements in the headers",
							getColumnCount(), headers.size()));
		}

		this.headers = headers;
		this.showHeader = showHeaders;
		this.columnSizes = new ArrayList<Integer>(headers.size());
		for (int i = 0; i < headers.size(); i++) {
			this.columnSizes.add(-1);
		}
	}

	/**
	 * Set the size of a column by index (-1 for auto size).
	 * 
	 * @param index
	 *            the index of the column
	 * @param size
	 *            the size or -1 for auto
	 * @throws IndexOutOfBoundsException
	 *             when the index is out of bounds
	 */
	public void setColumnSize(int index, int size) {
		// no checks because we want IndexOutOfBounds
		columnSizes.set(index, size);
	}

	/**
	 * The currently selected row (or -1 if no row is selected).
	 * <p>
	 * You may want to call {@link TTableOld#reflowData()} when done to see the
	 * changes.
	 * 
	 * @param selectedRow
	 *            the selected row
	 */
	@Override
	public void setSelectedRow(int selectedRow) {
		if (selectedRow < -1 || selectedRow >= getRowCount()) {
			throw new IndexOutOfBoundsException(String.format(
					"Cannot set row %d on a table with %d rows", selectedRow,
					getRowCount()));
		}

		super.setSelectedRow(selectedRow);
	}

	/**
	 * The currently selected column (or -1 if no column is selected).
	 * 
	 * @return the selected column
	 */
	public int getSelectedColumn() {
		return selectedColumn;
	}

	/**
	 * The currently selected column (or -1 if no column is selected).
	 * <p>
	 * You may want to call {@link TTableOld#reflowData()} when done to see the
	 * changes.
	 * 
	 * @param selectedColumn
	 *            the selected column
	 */
	public void setSelectedColumn(int selectedColumn) {
		if (selectedColumn < -1 || selectedColumn >= getColumnCount()) {
			throw new IndexOutOfBoundsException(String.format(
					"Cannot set column %d on a table with %d columns",
					selectedColumn, getColumnCount()));
		}

		this.selectedColumn = selectedColumn;
	}

	/**
	 * The currently selected cell.
	 * 
	 * @return the cell
	 */
	public Object getSelectedCell() {
		int selectedRow = getSelectedRow();
		if (selectedRow >= 0 && selectedColumn >= 0) {
			return model.getValueAt(selectedRow, selectedColumn);
		}

		return null;
	}

	@Override
	public void dispatchEnter(int selectedRow) {
		super.dispatchEnter(selectedRow);
		if (enterAction != null) {
			enterAction.DO();
		}
	}

	@Override
	public void dispatchMove(int fromRow, int toRow) {
		super.dispatchMove(fromRow, toRow);
		if (moveAction != null) {
			moveAction.DO();
		}
	}

	@Override
	public int getRowCount() {
		return model.getRowCount();
	}

	/**
	 * The number of columns.
	 * 
	 * @return the number of columns
	 */
	public int getColumnCount() {
		return model.getColumnCount();
	}

	/**
	 * Clear the content of the {@link TTableOld}.
	 * <p>
	 * It will not affect the headers.
	 * <p>
	 * You may want to call {@link TTableOld#reflowData()} when done to see the
	 * changes.
	 */
	public void clear() {
		setSelectedRow(-1);
		selectedColumn = -1;
		setModel(new TTableSimpleTextModel(new Object[][] {}));
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
		reflowData();
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
	 * Resize for a new width/height.
	 */
	@Override
	public void reflowData() {
		super.reflowData();
		computeRowsSize(true);
	}

	/**
	 * Compute {@link TTableOld#maxRowWidth} and auto column sizes (negative
	 * values in {@link TTableOld#columnSizes}).
	 * 
	 * @param forceReflowCol
	 *            TRUE to force a reflow of the size on each column, too
	 */
	private void computeRowsSize(boolean forceReflowCol) {
		int lastAutoColumn = -1;
		int rowWidth = 0;

		int i = 0;
		for (TTableColumn tcol : columns) {
			if (forceReflowCol) {
				tcol.reflowData();
			}

			if (!tcol.isForcedWidth()) {
				lastAutoColumn = i;
			}

			rowWidth += tcol.getWidth();

			i++;
		}

		if (!columns.isEmpty()) {
			rowWidth += (i - 1) * getSeparatorRenderer().getWidthOf(null);

			int extraWidth = getWidth() - rowWidth;
			if (extraWidth > 0) {
				if (lastAutoColumn < 0) {
					lastAutoColumn = columns.size() - 1;
				}
				TTableColumn tcol = columns.get(lastAutoColumn);
				tcol.expandWidthTo(tcol.getWidth() + extraWidth);
				rowWidth += extraWidth;
			}
		}
	}

	/**
	 * Draw the given row (or an empty one if row is NULL) at the specified
	 * index and offset.
	 * 
	 * @param rowIndex
	 *            the index of the row to draw or -1 for the headers
	 * @param y
	 *            the Y position
	 */
	private void drawRow(int rowIndex, int y) {
		for (int i = 0; i < getColumnCount(); i++) {
			TTableColumn tcol = columns.get(i);
			Object value;
			if (rowIndex < 0) {
				value = headers.get(i);
			} else {
				value = model.getValueAt(rowIndex, i);
			}

			if (i > 0) {
				TTableOldCellRenderer sep = rowIndex < 0 ? getHeaderSeparatorRenderer()
						: getSeparatorRenderer();
				sep.renderTableCell(this, null, rowIndex, i - 1, y);
			}

			if (rowIndex < 0) {
				getHeaderRenderer()
						.renderTableCell(this, value, rowIndex, i, y);
			} else {
				tcol.getRenderer().renderTableCell(this, value, rowIndex, i, y);
			}
		}
	}

	@Override
	public void draw() {
		ColorTheme theme = getTheme();

		int begin = vScroller.getValue();
		int y = this.showHeader ? 2 : 0;

		if (showHeader) {
			CellAttributes colorHeaders = getHeaderRenderer()
					.getCellAttributes(theme, false, isAbsoluteActive());
			drawRow(-1, 0);
			// TODO: draw horizontal row? Empty row with seps? blank row?
			// drawRow(null, hScroller.getValue(), 1, colorHeaders,
			// colorHeadersSep);
			String formatString = "%-" + Integer.toString(getWidth()) + "s";
			String data = String.format(formatString, "");
			getScreen().putStringXY(0, 1, data, colorHeaders);
		}

		for (int i = begin; i < getRowCount(); i++) {
			drawRow(i, y);
			y++;
			if (y >= getHeight() - 1) {
				break;
			}
		}

		CellAttributes color;
		if (isAbsoluteActive()) {
			color = getTheme().getColor("tlist");
		} else {
			color = getTheme().getColor("tlist.inactive");
		}

		// Pad the rest with blank rows
		for (int i = y; i < getHeight() - 1; i++) {
			getScreen().hLineXY(0, i, getWidth() - 1, ' ', color);
		}
	}
}
