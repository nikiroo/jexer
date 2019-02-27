package jexer;

import static jexer.TKeypress.kbBackTab;
import static jexer.TKeypress.kbDown;
import static jexer.TKeypress.kbEnd;
import static jexer.TKeypress.kbEnter;
import static jexer.TKeypress.kbHome;
import static jexer.TKeypress.kbLeft;
import static jexer.TKeypress.kbPgDn;
import static jexer.TKeypress.kbPgUp;
import static jexer.TKeypress.kbRight;
import static jexer.TKeypress.kbShiftTab;
import static jexer.TKeypress.kbTab;
import static jexer.TKeypress.kbUp;
import jexer.event.TKeypressEvent;
import jexer.event.TMouseEvent;
import jexer.event.TResizeEvent;

abstract public class TBrowsableWidget extends TScrollableWidget {
	private int selectedRow;

	abstract protected int getNumberOfRows();

	/**
	 * Basic setup of this class (called by all constructors)
	 */
	private void setup() {
		vScroller = new TVScroller(this, 0, 0, 1);
		hScroller = new THScroller(this, 0, 0, 1);
		fixScrollers();
	}

	/**
	 * Protected constructor.
	 * 
	 * @param parent
	 *            parent widget
	 */
	protected TBrowsableWidget(final TWidget parent) {
		super(parent);
		setup();
	}

	/**
	 * Protected constructor.
	 * 
	 * @param parent
	 *            parent widget
	 * @param x
	 *            column relative to parent
	 * @param y
	 *            row relative to parent
	 * @param width
	 *            width of widget
	 * @param height
	 *            height of widget
	 */
	protected TBrowsableWidget(final TWidget parent, final int x, final int y,
			final int width, final int height) {
		super(parent, x, y, width, height);
		setup();
	}

	/**
	 * Protected constructor used by subclasses that are disabled by default.
	 * 
	 * @param parent
	 *            parent widget
	 * @param enabled
	 *            if true assume enabled
	 */
	protected TBrowsableWidget(final TWidget parent, final boolean enabled) {
		super(parent, enabled);
		setup();
	}

	/**
	 * Protected constructor used by subclasses that are disabled by default.
	 * 
	 * @param parent
	 *            parent widget
	 * @param enabled
	 *            if true assume enabled
	 * @param x
	 *            column relative to parent
	 * @param y
	 *            row relative to parent
	 * @param width
	 *            width of widget
	 * @param height
	 *            height of widget
	 */
	protected TBrowsableWidget(final TWidget parent, final boolean enabled,
			final int x, final int y, final int width, final int height) {
		super(parent, enabled, x, y, width, height);
		setup();
	}

	/**
	 * The currently selected row (or -1 if no row is selected).
	 * 
	 * @return the selected row
	 */
	public int getSelectedRow() {
		return selectedRow;
	}

	/**
	 * The currently selected row (or -1 if no row is selected).
	 * 
	 * @param selectedRow
	 *            the new selected row
	 */
	public void setSelectedRow(int selectedRow) {
		this.selectedRow = selectedRow;
	}

	@SuppressWarnings("unused")
	public void dispatchMove(int fromRow, int toRow) {
		reflowData();
	}

	@SuppressWarnings("unused")
	public void dispatchEnter(int selectedRow) {
		reflowData();
	}

	@Override
	public void onMouseDown(final TMouseEvent mouse) {
		if (mouse.isMouseWheelUp()) {
			vScroller.decrement();
			return;
		}
		if (mouse.isMouseWheelDown()) {
			vScroller.increment();
			return;
		}

		if ((mouse.getX() < getWidth() - 1) && (mouse.getY() < getHeight() - 1)) {
			if (vScroller.getValue() + mouse.getY() < getNumberOfRows()) {
				selectedRow = vScroller.getValue() + mouse.getY();
			}
			dispatchEnter(selectedRow);
			return;
		}

		// Pass to children
		super.onMouseDown(mouse);
	}

	@Override
	public void onKeypress(final TKeypressEvent keypress) {
		// TODO: left/right to switch column?

		int maxX = getNumberOfRows();
		int prevSelectedRow = selectedRow;

		if (keypress.equals(kbLeft)) {
			hScroller.decrement();
		} else if (keypress.equals(kbRight)) {
			hScroller.increment();
		} else if (keypress.equals(kbUp)) {
			if (maxX > 0 && selectedRow < maxX) {
				if (selectedRow > 0) {
					if (selectedRow - vScroller.getValue() == 0) {
						vScroller.decrement();
					}
					selectedRow--;
				} else {
					selectedRow = 0;
				}

				dispatchMove(prevSelectedRow, selectedRow);
			}
		} else if (keypress.equals(kbDown)) {
			if (maxX > 0) {
				if (selectedRow >= 0) {
					if (selectedRow < maxX - 1) {
						selectedRow++;
						if (selectedRow + 1 - vScroller.getValue() == getHeight() - 1) {
							vScroller.increment();
						}
					}
				} else {
					selectedRow = 0;
				}

				dispatchMove(prevSelectedRow, selectedRow);
			}
		} else if (keypress.equals(kbPgUp)) {
			if (selectedRow >= 0) {
				vScroller.bigDecrement();
				selectedRow -= getHeight() - 1;
				if (selectedRow < 0) {
					selectedRow = 0;
				}

				dispatchMove(prevSelectedRow, selectedRow);
			}
		} else if (keypress.equals(kbPgDn)) {
			if (selectedRow >= 0) {
				vScroller.bigIncrement();
				selectedRow += getHeight() - 1;
				if (selectedRow > getNumberOfRows() - 1) {
					selectedRow = getNumberOfRows() - 1;
				}

				dispatchMove(prevSelectedRow, selectedRow);
			}
		} else if (keypress.equals(kbHome)) {
			if (getNumberOfRows() > 0) {
				vScroller.toTop();
				selectedRow = 0;
				dispatchMove(prevSelectedRow, selectedRow);
			}
		} else if (keypress.equals(kbEnd)) {
			if (getNumberOfRows() > 0) {
				vScroller.toBottom();
				selectedRow = getNumberOfRows() - 1;
				dispatchMove(prevSelectedRow, selectedRow);
			}
		} else if (keypress.equals(kbTab)) {
			getParent().switchWidget(true);
		} else if (keypress.equals(kbShiftTab) || keypress.equals(kbBackTab)) {
			getParent().switchWidget(false);
		} else if (keypress.equals(kbEnter)) {
			if (selectedRow >= 0) {
				dispatchEnter(selectedRow);
			}
		} else {
			// Pass other keys (tab etc.) on
			super.onKeypress(keypress);
		}
	}

	@Override
	public void onResize(TResizeEvent event) {
		super.onResize(event);
		reflowData();
	}

	@Override
	public void reflowData() {
		super.reflowData();
		fixScrollers();
	}

	private void fixScrollers() {
		vScroller.setX(Math.max(0, getWidth() - 3));
		vScroller.setHeight(Math.max(1, getHeight() - 2));
		hScroller.setY(Math.max(0, getHeight() - 3));
		hScroller.setWidth(Math.max(1, getWidth() - 3));
	}
}
