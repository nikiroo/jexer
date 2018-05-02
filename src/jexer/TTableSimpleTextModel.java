package jexer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

public class TTableSimpleTextModel implements TableModel {

	private TableModel model;

	public TTableSimpleTextModel(Object[][] data) {
		this(convert(data));
	}

	public TTableSimpleTextModel(
			final Collection<? extends Collection<? extends Object>> data) {

		int maxItemsPerRow = 0;
		for (Collection<? extends Object> rowOfData : data) {
			maxItemsPerRow = Math.max(maxItemsPerRow, rowOfData.size());
		}

		int i = 0;
		final String[][] odata = new String[data.size()][maxItemsPerRow];
		for (Collection<? extends Object> rowOfData : data) {
			odata[i] = new String[maxItemsPerRow];
			int j = 0;
			for (Object pieceOfData : rowOfData) {
				odata[i][j] = "" + pieceOfData;
				j++;
			}
			i++;
		}

		final int maxItemsPerRowFinal = maxItemsPerRow;
		this.model = new AbstractTableModel() {
			@Override
			public String getValueAt(int rowIndex, int columnIndex) {
				return odata[rowIndex][columnIndex];
			}

			@Override
			public int getRowCount() {
				return odata.length;
			}

			@Override
			public int getColumnCount() {
				return maxItemsPerRowFinal;
			}
		};
	}

	@Override
	public int getRowCount() {
		return model.getRowCount();
	}

	@Override
	public int getColumnCount() {
		return model.getColumnCount();
	}

	@Override
	public String getColumnName(int columnIndex) {
		return model.getColumnName(columnIndex);
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return model.getColumnClass(columnIndex);
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return model.isCellEditable(rowIndex, columnIndex);
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		return model.getValueAt(rowIndex, columnIndex);
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		model.setValueAt(aValue, rowIndex, columnIndex);
	}

	@Override
	public void addTableModelListener(TableModelListener l) {
		model.addTableModelListener(l);
	}

	@Override
	public void removeTableModelListener(TableModelListener l) {
		model.removeTableModelListener(l);
	}

	static Collection<Collection<Object>> convert(Object[][] data) {
		Collection<Collection<Object>> dataCollection = new ArrayList<Collection<Object>>(
				data.length);
		for (Object pieceOfData : data) {
			dataCollection.add(Arrays.asList(pieceOfData));
		}

		return dataCollection;
	}
}
