package jexer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.swing.table.TableModel;

import jexer.TTableSimpleTextCellRenderer.CellRendererMode;

public class TTable extends TScrollableWidget {
	static private TTableCellRenderer defaultCellRenderer = new TTableSimpleTextCellRenderer(
			CellRendererMode.NORMAL);
	static private TTableCellRenderer defaultSeparatorRenderer = new TTableSimpleTextCellRenderer(
			CellRendererMode.SEPARATOR);
	static private TTableCellRenderer defaultHeaderRenderer = new TTableSimpleTextCellRenderer(
			CellRendererMode.HEADER);

	/**
	 * The action to perform when the user selects an item (clicks or enter).
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

	private int headerSize = 2; // in lines (rows), def is 2

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

	public TableModel getModel() {
		return model;
	}

	public void setModel(TableModel model) {
		this.model = model;
		resetData();
	}

	public void setRowData(Object[][] data, Object[] names) {
		setRowData(TTableSimpleTextModel.convert(data), Arrays.asList(names));
	}

	public void setRowData(
			final Collection<? extends Collection<? extends Object>> data,
			final Collection<Object> names) {

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

		reflow();
	}

	private void reflow() {
		int numOfRows = rows.size();
		int numOfCols = columns.size();
		for (int rowIndex = -headerSize; rowIndex < numOfRows; rowIndex++) {
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
							0, value, false, false);
					currentX += tcol.getWidth();

					if (displayColIndex + 1 < numOfCols) {
						TWidget sep = updateData(headers, 1, colIndex,
								separatorRenderer, currentX, -1, value, false,
								false);
						if (sep != null) {
							currentX += sep.getWidth();
						}
					}
				} else if (rowIndex < 0) {
					// Skip (empty row for headers)
				} else {
					Object value = model.getValueAt(rowIndex, colIndex);
					boolean isSelected = rowIndex == 2; // TODO
					boolean hasFocus = isSelected;

					updateData(rows, rowIndex, colIndex, cellRenderer,
							currentX, headerSize, value, isSelected, hasFocus);
					currentX += tcol.getWidth();

					if (displayColIndex + 1 < numOfCols) {
						TWidget sep = updateData(separators, rowIndex,
								colIndex, separatorRenderer, currentX,
								headerSize, value, isSelected, hasFocus);
						if (sep != null) {
							currentX += sep.getWidth();
						}
					}
				}
			}
		}
	}

	// update data and make sure widget is added to 'this' (if not null)
	private TWidget updateData(List<List<TWidget>> widgets, int rowIndex,
			int colIndex, TTableCellRenderer renderer, int currentX,
			int yOffset, Object value, boolean isSelected, boolean hasFocus) {

		TWidget widget = widgets.get(rowIndex).get(colIndex);
		if (widget != null) {
			if (!renderer.updateTableCellRendererComponent(this, widget, value,
					isSelected, hasFocus, rowIndex, colIndex)) {
				removeChild(widget);
				widget = null;
			}
		}
		if (widget == null) {
			widget = renderer.getTableCellRendererComponent(this, value,
					isSelected, hasFocus, rowIndex, colIndex);
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
}
