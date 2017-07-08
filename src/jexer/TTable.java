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

import java.util.ArrayList;
import java.util.List;

import jexer.bits.CellAttributes;
import jexer.event.TKeypressEvent;
import jexer.event.TMouseEvent;

public class TTable extends TWidget {
	private List<String> headers;
	private List<Integer> columnSizes;
	private boolean showHeader;

	private List<List<String>> lines;
	private int selectedLine;
	private int selectedColumn;

	private int maxLineWidth;
	private int topY;

	// ┃ │
	private final String INTER_COL = " │ ";

	private THScroller hScroller;
	private TVScroller vScroller;

	/**
	 * The action to perform when the user selects an item (clicks or enter).
	 */
	private TAction enterAction = null;

	/**
	 * The action to perform when the user navigates with keyboard.
	 */
	private TAction moveAction = null;

	public TTable(TWidget parent, int x, int y, int width, int height,
			final TAction enterAction, final TAction moveAction) {
		this(parent, x, y, width, height, enterAction, moveAction, null, false);
	}

	public TTable(TWidget parent, int x, int y, int width, int height,
			final TAction enterAction, final TAction moveAction,
			List<String> headers, boolean showHeaders) {
		super(parent, x, y, width, height);

		if (headers != null) {
			this.headers = headers;
		} else {
			this.headers = new ArrayList<String>();
		}

		this.showHeader = showHeaders;
		topY = showHeader ? 2 : 0;

		this.columnSizes = new ArrayList<Integer>(this.headers.size());
		for (int i = 0; i < this.headers.size(); i++) {
			this.columnSizes.add(-1);
		}

		this.lines = new ArrayList<List<String>>();
		this.selectedLine = -1;

		this.enterAction = enterAction;
		this.moveAction = moveAction;

		reflow();
	}

	// -1 = auto
	public void setColumnSize(int index, int size) {
		// no checks because we want IndexOutOfBounds
		columnSizes.set(index, size);
	}

	public int getSelectedLine() {
		return selectedLine;
	}

	public int getSelectedColumn() {
		return selectedColumn;
	}

	public String getSelectedCell() {
		if (selectedLine >= 0 && selectedColumn >= 0) {
			return lines.get(selectedLine).get(selectedColumn);
		}

		return null;
	}

	/**
	 * Perform user selection action.
	 */
	public void dispatchEnter() {
		if (enterAction != null) {
			enterAction.DO();
		}
	}

	/**
	 * Perform list movement action.
	 */
	public void dispatchMove() {
		if (moveAction != null) {
			moveAction.DO();
		}
	}

	// reflow needed when done
	public void addLine(List<String> line) {
		if (line.size() != headers.size()) {
			throw new IllegalArgumentException(
					String.format(
							"Cannot insert a line in a table if the number of items is not equals to that of the header: "
									+ "%d elements in the line <> %d elements in the headers",
							line.size(), headers.size()));
		}

		lines.add(line);
	}

	public int size() {
		return getNumberOfLines();
	}

	public int getNumberOfLines() {
		return lines.size();
	}

	public int getNumberOfColumns() {
		return headers.size();
	}

	// reflow needed when done
	public void clear() {
		selectedLine = -1;
		lines.clear();
	}

	/**
	 * Resize for a new width/height.
	 */
	public void reflow() {
		// Reset the lines
		selectedLine = -1;
		selectedColumn = -1;

		computeLinesSize();

		// Start at the top
		if (vScroller == null) {
			vScroller = new TVScroller(this, getWidth() - 1, topY,
					getHeight() - 1);
		} else {
			vScroller.setX(getWidth() - 1);
			vScroller.setHeight(getHeight() - 1);
		}
		vScroller.setBottomValue(size() - getHeight() + 1);
		vScroller.setTopValue(0);
		vScroller.setValue(0);
		if (vScroller.getBottomValue() < 0) {
			vScroller.setBottomValue(0);
		}
		vScroller.setBigChange(getHeight() - 1);

		// Start at the left
		if (hScroller == null) {
			hScroller = new THScroller(this, 0, getHeight() - 1, getWidth() - 1);
		} else {
			hScroller.setY(getHeight() - 1);
			hScroller.setWidth(getWidth() - 1);
		}
		hScroller.setRightValue(maxLineWidth - getWidth() + 1);
		hScroller.setLeftValue(0);
		hScroller.setValue(0);
		if (hScroller.getRightValue() < 0) {
			hScroller.setRightValue(0);
		}
		hScroller.setBigChange(getWidth() - 1);
	}

	private void computeLinesSize() {
		// compute maxLineWidth and auto column sizes
		maxLineWidth = 0;
		int visibleColumns = 0;
		int lastAutoColumn = -1;
		int lastPositiveColumn = -1;
		for (int i = 0; i < columnSizes.size(); i++) {
			int columnSize = columnSizes.get(i);
			if (columnSize != 0) {
				visibleColumns++;
			}

			if (columnSize > 0) {
				maxLineWidth += columnSize;
				lastPositiveColumn = i;
			} else if (columnSize < 0) {
				columnSize = 0;
				for (int j = -1; j < lines.size(); j++) {
					List<String> line;
					if (j < 0) {
						line = headers;
					} else {
						line = lines.get(j);
					}

					lastAutoColumn = i;
					columnSize = Math.min(-line.get(i).length(), columnSize);
				}

				columnSizes.set(i, columnSize);
				maxLineWidth += -columnSize;
			}
		}

		int expandColumn = lastAutoColumn;
		if (expandColumn == -1) {
			expandColumn = lastPositiveColumn;
		}

		if (visibleColumns > 0) {
			maxLineWidth += (visibleColumns - 1) * INTER_COL.length();
		}

		// expand last auto col (or last col) to max size
		if (expandColumn >= 0 && maxLineWidth < getWidth()) {
			columnSizes.set(expandColumn, columnSizes.get(expandColumn)
					- (getWidth() - maxLineWidth));
		}
	}

	// line can be null
	private void drawLine(List<String> line, int xOffset, int y,
			CellAttributes color, CellAttributes colorSep) {
		int x = 0;
		for (int i = 0; i < columnSizes.size(); i++) {
			int columnSize = Math.abs(columnSizes.get(i));
			if (columnSize != 0) {
				String formatString = "%-" + Integer.toString(columnSize) + "s";
				String data = String.format(formatString, line == null ? ""
						: line.get(i));
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
			drawLine(headers, hScroller.getValue(), 0, colorHeaders,
					colorHeadersSep);
			// TODO: draw horizontal line? Empty line with seps? blank line?
			// drawLine(null, hScroller.getValue(), 1, colorHeaders,
			// colorHeadersSep);
		}

		CellAttributes color = null;
		for (int i = begin; i < size(); i++) {
			if (i == selectedLine) {
				color = getTheme().getColor("tlist.selected");
			} else if (isAbsoluteActive()) {
				color = getTheme().getColor("tlist");
			} else {
				color = getTheme().getColor("tlist.inactive");
			}

			drawLine(lines.get(i), hScroller.getValue(), topY, color, colorSep);
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

		// Pad the rest with blank lines
		for (int i = topY; i < getHeight() - 1; i++) {
			getScreen().hLineXY(0, i, getWidth() - 1, ' ', color);
		}
	}

	/**
	 * Handle mouse press events.
	 * 
	 * @param mouse
	 *            mouse button press event
	 */
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
			if (vScroller.getValue() + mouse.getY() < size()) {
				selectedLine = vScroller.getValue() + mouse.getY() - topY;
			}
			dispatchEnter();
			return;
		}

		// Pass to children
		super.onMouseDown(mouse);
	}

	/**
	 * Handle keystrokes.
	 * 
	 * @param keypress
	 *            keystroke event
	 */
	@Override
	public void onKeypress(final TKeypressEvent keypress) {
		// TODO: could be exported to a base class allowing internal right(int
		// count), left(int count)...
		if (keypress.equals(kbLeft)) {
			hScroller.decrement();
		} else if (keypress.equals(kbRight)) {
			hScroller.increment();
		} else if (keypress.equals(kbUp)) {
			if (size() > 0) {
				if (selectedLine >= 0) {
					if (selectedLine > 0) {
						if (selectedLine - vScroller.getValue() == 0) {
							vScroller.decrement();
						}
						selectedLine--;
					}
				} else {
					selectedLine = size() - 1;
				}
			}
			if (selectedLine >= 0) {
				dispatchMove();
			}
		} else if (keypress.equals(kbDown)) {
			if (size() > 0) {
				if (selectedLine >= 0) {
					if (selectedLine + topY + 1 < size() - 1) {
						selectedLine++;
						if (selectedLine + topY + 1 - vScroller.getValue() == getHeight() - 1) {
							vScroller.increment();
						}
					}
				} else {
					selectedLine = 0;
				}
			}
			if (selectedLine >= 0) {
				dispatchMove();
			}
		} else if (keypress.equals(kbPgUp)) {
			vScroller.bigDecrement();
			if (selectedLine >= 0) {
				selectedLine -= getHeight() - 1;
				if (selectedLine < 0) {
					selectedLine = 0;
				}
			}
			if (selectedLine >= 0) {
				dispatchMove();
			}
		} else if (keypress.equals(kbPgDn)) {
			vScroller.bigIncrement();
			if (selectedLine >= 0) {
				selectedLine += getHeight() - 1;
				if (selectedLine > size() - 1) {
					selectedLine = size() - 1;
				}
			}
			if (selectedLine >= 0) {
				dispatchMove();
			}
		} else if (keypress.equals(kbHome)) {
			vScroller.toTop();
			if (size() > 0) {
				selectedLine = 0;
			}
			if (selectedLine >= 0) {
				dispatchMove();
			}
		} else if (keypress.equals(kbEnd)) {
			vScroller.toBottom();
			if (size() > 0) {
				selectedLine = size() - 1;
			}
			if (selectedLine >= 0) {
				dispatchMove();
			}
		} else if (keypress.equals(kbTab)) {
			getParent().switchWidget(true);
		} else if (keypress.equals(kbShiftTab) || keypress.equals(kbBackTab)) {
			getParent().switchWidget(false);
		} else if (keypress.equals(kbEnter)) {
			if (selectedLine >= 0) {
				dispatchEnter();
			}
		} else {
			// Pass other keys (tab etc.) on
			super.onKeypress(keypress);
		}
	}
}
