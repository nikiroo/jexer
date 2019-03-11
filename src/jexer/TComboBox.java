/*
 * Jexer - Java Text User Interface
 *
 * The MIT License (MIT)
 *
 * Copyright (C) 2017 Kevin Lamonte
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
 * @author Kevin Lamonte [kevin.lamonte@gmail.com]
 * @version 1
 */
package jexer;

import static jexer.TKeypress.kbAltDown;
import static jexer.TKeypress.kbBackTab;
import static jexer.TKeypress.kbShiftTab;
import static jexer.TKeypress.kbTab;

import java.util.ArrayList;
import java.util.List;

import jexer.bits.CellAttributes;
import jexer.bits.GraphicsChars;
import jexer.event.TKeypressEvent;
import jexer.event.TMouseEvent;
import jexer.event.TResizeEvent;
import jexer.event.TResizeEvent.Type;

/**
 * TComboBox implements a combobox containing a drop-down list and edit
 * field.  Alt-Down can be used to show the drop-down.
 */
public class TComboBox extends TWidget {

    // ------------------------------------------------------------------------
    // Variables --------------------------------------------------------------
    // ------------------------------------------------------------------------

    /**
     * The list of items in the drop-down.
     */
    private TList list;

    /**
     * The edit field containing the value to return.
     */
    private TField field;

    /**
     * The action to perform when the user selects an item (clicks or enter).
     */
    private TAction updateAction = null;

    /**
     * The height of the list of values when it is shown, or -1 to use the 
     * number of values in the list as the height.
     */
    private int valuesHeight = -1;
    
    /**
     * The values shown by the drop-down list.
     */
    private List<String> values = new ArrayList<String>();
    
    // ------------------------------------------------------------------------
    // Constructors -----------------------------------------------------------
    // ------------------------------------------------------------------------

    /**
     * Public constructor.
     *
     * @param parent parent widget
     * @param x column relative to parent
     * @param y row relative to parent
     * @param width visible combobox width, including the down-arrow
     * @param values the possible values for the box, shown in the drop-down
     * @param valuesIndex the initial index in values, or -1 for no default
     * value
     * @param valuesHeight the height of the values drop-down when it is
     * visible, or -1 to use the number of values as the height of the list
     * @param updateAction action to call when a new value is selected from
     * the list or enter is pressed in the edit field
     */
    public TComboBox(final TWidget parent, final int x, final int y,
        final int width, final List<String> values, final int valuesIndex,
        final int valuesHeight, final TAction updateAction) {

        // Set parent and window
        super(parent, x, y, width, 1);

        this.updateAction = updateAction;
        this.values = values;
        this.valuesHeight = valuesHeight;

        field = new TField(this, 0, 0, Math.max(0, width - 1), false, "",
            updateAction, null);
        if (valuesIndex >= 0) {
            field.setText(values.get(valuesIndex));
        }
        
        // TODO: why is this required to tab out of the combo?
        TField test = addField(0, 0, 1, true);
        test.setActive(false);
        test.setEnabled(false);
        test.setVisible(false);
        //
        
        setHeight(1);
        activate(field);
    }
    
    /**
     * Display the drop-down menu.
     */
    private void displayList() {
    	if (this.list != null) {
    		hideList();
    	}
    	
    	int valuesHeight = this.valuesHeight;
    	if (valuesHeight < 0) {
        	this.valuesHeight = values == null ? 0 : values.size() + 1;
        }
    	
		TList list = new TList(this, values, 0, 1, getWidth(), valuesHeight,
	            new TAction() {
					@Override
	                public void DO() {
	                	TList list = TComboBox.this.list;
	                	if (list == null) {
	                		return;
	                	}
	                	
	                    field.setText(list.getSelected());
	                    hideList();
	                    if (updateAction != null) {
	                        updateAction.DO();
	                    }
	                }
	            }
	        );
		
		int i = -1;
		if (values != null) {
			for (i = 0; i < values.size(); i++) {
	            if (values.get(i).equals(field.getText())) {
	                list.setSelectedIndex(i);
	                break;
	            }
	        }
			
			if (i >= values.size()) {
				i = -1;
			}
		}
		
        list.setSelectedIndex(i);
        
		list.setEnabled(true);
        list.setVisible(true);
        
        this.list = list;
        
        reflowData();
        activate(list);
    }
    
    /**
     * Hide the drop-down menu.
     */
    private void hideList() {
    	TList list = this.list;
    	
    	if (list != null) {
	    	list.setEnabled(false);
	        list.setVisible(false);
	        removeChild(list);
	        setHeight(1);
	        activate(field);
	        
	        this.list = null;
    	}
    }

    // ------------------------------------------------------------------------
    // Event handlers ---------------------------------------------------------
    // ------------------------------------------------------------------------

    /**
     * Returns true if the mouse is currently on the down arrow.
     *
     * @param mouse mouse event
     * @return true if the mouse is currently on the down arrow
     */
    private boolean mouseOnArrow(final TMouseEvent mouse) {
        if ((mouse.getY() == 0)
            && (mouse.getX() == getWidth() - 1)
        ) {
            return true;
        }
        return false;
    }

    /**
     * Handle mouse down clicks.
     *
     * @param mouse mouse button down event
     */
    @Override
    public void onMouseDown(final TMouseEvent mouse) {
        if ((mouseOnArrow(mouse)) && (mouse.isMouse1())) {
            // Make the list visible or not.
            if (list != null) {
                hideList();
            } else {
                displayList();
            }
        }
    }

    /**
     * Handle keystrokes.
     *
     * @param keypress keystroke event
     */
    @Override
    public void onKeypress(final TKeypressEvent keypress) {
        if (keypress.equals(kbAltDown)) {
            displayList();
            return;
        }

        if (keypress.equals(kbTab)
            || (keypress.equals(kbShiftTab))
            || (keypress.equals(kbBackTab))
        ) {
            if (list != null) {
                hideList();
                return;
            }
        }

        // Pass to parent for the things we don't care about.
        super.onKeypress(keypress);
    }

    // ------------------------------------------------------------------------
    // TWidget ----------------------------------------------------------------
    // ------------------------------------------------------------------------

    /**
     * Draw the combobox down arrow.
     */
    @Override
    public void draw() {
        CellAttributes comboBoxColor;

        if (isAbsoluteActive()) {
            comboBoxColor = getTheme().getColor("tcombobox.active");
        } else {
            comboBoxColor = getTheme().getColor("tcombobox.inactive");
        }

        getScreen().putCharXY(getWidth() - 1, 0, GraphicsChars.DOWNARROW,
            comboBoxColor);
    }

    // ------------------------------------------------------------------------
    // TComboBox --------------------------------------------------------------
    // ------------------------------------------------------------------------

    /**
     * Get combobox text value.
     *
     * @return text in the edit field
     */
    public String getText() {
        return field.getText();
    }

    /**
     * Set combobox text value.
     *
     * @param text the new text in the edit field
     */
    public void setText(final String text) {
        field.setText(text);
    }
    
    /**
     * Make sure the widget displays all its elements correctly according to
     * the current size and content.
     */
    public void reflowData() {
    	// TODO: why setW/setH/reflow not enough for the scrollbars?
    	TList list = this.list;
    	if (list != null) {
    		int valuesHeight = this.valuesHeight;
        	if (valuesHeight < 0) {
            	this.valuesHeight = values == null ? 0 : values.size() + 1;
            }
        	
			list.onResize(new TResizeEvent(Type.WIDGET, getWidth(),
					valuesHeight));
			
			setHeight(valuesHeight + 1);
    	}
    	
		field.onResize(new TResizeEvent(Type.WIDGET, getWidth(),
				field.getHeight()));
    }

    @Override
    public void onResize(TResizeEvent resize) {
    	super.onResize(resize);
    	reflowData();
    }
}
