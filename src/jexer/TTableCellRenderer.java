package jexer;

public interface TTableCellRenderer {
	// NULL is allowed
	public TWidget getTableCellRendererComponent(TTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column);

	public boolean updateTableCellRendererComponent(TTable table,
			TWidget component, Object value, boolean isSelected,
			boolean hasFocus, int row, int column);
}