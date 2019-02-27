package jexer;

import java.util.ArrayList;
import java.util.List;

import jexer.bits.CellAttributes;
import jexer.event.TResizeEvent;

/**
 * A table widget to display and browse through tabular data.
 * 
 * @author niki
 */
public class TTableOld extends TBrowsableWidget {
	private List<String> headers;
	private List<Integer> columnSizes;
	private boolean showHeader;

	private List<List<String>> rows;
	private int selectedColumn;

	private int maxRowWidth;
	private int topY;

	// some nice characters: ┃ │ |
	private final String INTER_COL = " │ ";

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

		this.rows = new ArrayList<List<String>>();
		setSelectedRow(-1);
		this.selectedColumn = -1;

		setHeaders(headers, showHeaders);

		this.enterAction = enterAction;
		this.moveAction = moveAction;

		reflowData();
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

		List<String> row = null;
		if (rows.size() > 0) {
			row = rows.get(0);
		}

		if (row != null && row.size() != headers.size()) {
			throw new IllegalArgumentException(
					String.format(
							"Cannot set the headers of a table if the number of items is not equals to that of the current data rows: "
									+ "%d elements in the data rows <> %d elements in the headers",
							row.size(), headers.size()));
		}

		this.headers = headers;
		this.showHeader = showHeaders;
		this.topY = this.showHeader ? 2 : 0;
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
		if (selectedRow < -1 || selectedRow >= getNumberOfRows()) {
			throw new IndexOutOfBoundsException(String.format(
					"Cannot set row %d on a table with %d rows", selectedRow,
					getNumberOfRows()));
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
		if (selectedColumn < -1 || selectedColumn >= getNumberOfColumns()) {
			throw new IndexOutOfBoundsException(String.format(
					"Cannot set column %d on a table with %d columns",
					selectedColumn, getNumberOfColumns()));
		}

		this.selectedColumn = selectedColumn;
	}

	/**
	 * The currently selected cell.
	 * 
	 * @return the cell
	 */
	public String getSelectedCell() {
		int selectedRow = getSelectedRow();
		if (selectedRow >= 0 && selectedColumn >= 0) {
			return rows.get(selectedRow).get(selectedColumn);
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

	/**
	 * Add a row to the table.
	 * <p>
	 * Note that if some data is present, the number of columns <b>MUST</b> be
	 * identical.
	 * <p>
	 * You may want to call {@link TTableOld#reflowData()} when done to see the
	 * changes.
	 * 
	 * @param row
	 *            the row to add
	 */
	public void addRow(List<String> row) {
		if (row.size() != headers.size()) {
			throw new IllegalArgumentException(
					String.format(
							"Cannot insert a row in a table if the number of items is not equals to that of the header: "
									+ "%d elements in the row <> %d elements in the headers",
							row.size(), headers.size()));
		}

		rows.add(row);
	}

	/**
	 * The size of the table in number of rows.
	 * 
	 * @return the size
	 */
	public int size() {
		return getNumberOfRows();
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
		rows.clear();
	}

	/**
	 * Resize for a new width/height.
	 */
	@Override
	public void reflowData() {
		super.reflowData();
		computeRowsSize();
	}

	/**
	 * Compute {@link TTableOld#maxRowWidth} and auto column sizes (negative
	 * values in {@link TTableOld#columnSizes}).
	 */
	private void computeRowsSize() {
		maxRowWidth = 0;
		int visibleColumns = 0;
		int lastAutoColumn = -1;
		int lastPositiveColumn = -1;
		for (int i = 0; i < columnSizes.size(); i++) {
			int columnSize = columnSizes.get(i);
			if (columnSize != 0) {
				visibleColumns++;
			}

			if (columnSize > 0) {
				maxRowWidth += columnSize;
				lastPositiveColumn = i;
			} else if (columnSize < 0) {
				columnSize = 0;
				for (int j = -1; j < rows.size(); j++) {
					List<String> row;
					if (j < 0) {
						row = headers;
					} else {
						row = rows.get(j);
					}

					lastAutoColumn = i;
					columnSize = Math.min(-row.get(i).length(), columnSize);
				}

				columnSizes.set(i, columnSize);
				maxRowWidth += -columnSize;
			}
		}

		int expandColumn = lastAutoColumn;
		if (expandColumn == -1) {
			expandColumn = lastPositiveColumn;
		}

		if (visibleColumns > 0) {
			maxRowWidth += (visibleColumns - 1) * INTER_COL.length();
		}

		// expand last auto col (or last col) to max size
		if (expandColumn >= 0 && maxRowWidth < getWidth()) {
			columnSizes.set(expandColumn, columnSizes.get(expandColumn)
					- (getWidth() - maxRowWidth));
		}
	}

	/**
	 * Draw the given row (or an empty one if row is NULL) at the specified
	 * index and offset.
	 * 
	 * @param row
	 *            the row to draw
	 * @param xOffset
	 *            the (positive or 0) X offset to apply while drawing
	 * @param y
	 *            the Y position
	 * @param color
	 *            the characters colour
	 * @param colorSep
	 *            the separators colour
	 */
	private void drawRow(List<String> row, int xOffset, int y,
			CellAttributes color, CellAttributes colorSep) {
		int x = 0;
		for (int i = 0; i < columnSizes.size(); i++) {
			int columnSize = Math.abs(columnSizes.get(i));
			if (columnSize != 0) {
				String formatString = "%-" + Integer.toString(columnSize) + "s";
				String data = String.format(formatString, row == null ? ""
						: row.get(i));
				if (data.length() > xOffset) {
					data = data.substring(xOffset);
					getScreen().putStringXY(x, y, data, color);
				}

				x += columnSize - xOffset;
				for (char car : INTER_COL.toCharArray()) {
					getScreen().putCharXY(x, y, car,
							car == ' ' ? color : colorSep);
					x++;
				}

				xOffset -= data.length();
				if (xOffset < 0) {
					xOffset = 0;
				}
			}
		}
	}

	@Override
	public void draw() {
		// TODO: change colours
		CellAttributes colorSep = getTheme().getColor("tlist.inactive");
		CellAttributes colorHeaders = getTheme().getColor("tlist");
		CellAttributes colorHeadersSep = getTheme().getColor("tlist.inactive");
		int begin = vScroller.getValue();
		int topY = this.topY;

		if (showHeader) {
			drawRow(headers, hScroller.getValue(), 0, colorHeaders,
					colorHeadersSep);
			// TODO: draw horizontal row? Empty row with seps? blank row?
			// drawRow(null, hScroller.getValue(), 1, colorHeaders,
			// colorHeadersSep);
			String formatString = "%-" + Integer.toString(getWidth()) + "s";
			String data = String.format(formatString, "");
			getScreen().putStringXY(0, 1, data, colorHeaders);
		}

		CellAttributes color = null;
		int selectedRow = getSelectedRow();
		for (int i = begin; i < size(); i++) {
			if (i == selectedRow) {
				color = getTheme().getColor("tlist.selected");
			} else if (isAbsoluteActive()) {
				color = getTheme().getColor("tlist");
			} else {
				color = getTheme().getColor("tlist.inactive");
			}

			drawRow(rows.get(i), hScroller.getValue(), topY, color, colorSep);
			topY++;
			if (topY >= getHeight() - 1) {
				break;
			}
		}

		if (isAbsoluteActive()) {
			color = getTheme().getColor("tlist");
		} else {
			color = getTheme().getColor("tlist.inactive");
		}

		// Pad the rest with blank rows
		for (int i = topY; i < getHeight() - 1; i++) {
			getScreen().hLineXY(0, i, getWidth() - 1, ' ', color);
		}
	}
}
