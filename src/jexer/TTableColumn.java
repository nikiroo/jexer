package jexer;

import javax.swing.table.TableModel;

public class TTableColumn {
	private int modelIndex;
	private int width;
	private Object headerValue;

	TTableCellRenderer cellRenderer;
	TTableCellRenderer separatorRenderer;
	TTableCellRenderer headerRenderer;

	public TTableColumn(int modelIndex) {
		this(modelIndex, null);
	}

	public TTableColumn(int modelIndex, String colName) {
		this(modelIndex, colName, null);
	}

	// set the width and preferred with the the max data size
	public TTableColumn(int modelIndex, Object colValue, TableModel model) {
		this.modelIndex = modelIndex;

		if (model != null) {
			int maxDataSize = 0;
			for (int i = 0; i < model.getRowCount(); i++) {
				maxDataSize = Math.max(maxDataSize,
						("" + model.getValueAt(i, modelIndex)).length());
			}

			setWidth(maxDataSize);
			setPreferredWidth(maxDataSize);
		}

		if (colValue != null) {
			setHeaderValue(colValue);
		}
	}

	public int getModelIndex() {
		return modelIndex;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public void setPreferredWidth(int i) {
		// TODO Auto-generated method stub
	}

	public Object getHeaderValue() {
		return headerValue;
	}

	public void setHeaderValue(Object headerValue) {
		this.headerValue = headerValue;
	}

	public TTableCellRenderer getCellRenderer() {
		return cellRenderer;
	}

	public void setCellRenderer(TTableCellRenderer cellRenderer) {
		this.cellRenderer = cellRenderer;
	}

	public TTableCellRenderer getSeparatorRenderer() {
		return separatorRenderer;
	}

	public void setSeparatorRenderer(TTableCellRenderer separatorRenderer) {
		this.separatorRenderer = separatorRenderer;
	}

	public TTableCellRenderer getHeaderRenderer() {
		return headerRenderer;
	}

	public void setHeaderRenderer(TTableCellRenderer headerRenderer) {
		this.headerRenderer = headerRenderer;
	}
}
