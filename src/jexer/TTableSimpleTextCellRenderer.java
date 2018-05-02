package jexer;

public class TTableSimpleTextCellRenderer implements TTableCellRenderer {
	private boolean separator;
	private boolean header;

	public enum CellRendererMode {
		NORMAL, SEPARATOR, HEADER;
	}

	public TTableSimpleTextCellRenderer() {
		this(CellRendererMode.NORMAL);
	}

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

	protected String getText(Object value) {
		if (separator) {
			// some nice characters: ┃ │ |
			return " │ ";
		}

		return "" + value;
	}

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
