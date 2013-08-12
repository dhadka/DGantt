/* Copyright 2011 David Hadka
 * 
 * This file is part of DGantt.
 * 
 * DGantt is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free 
 * Software Foundation, either version 3 of the License, or (at your option) 
 * any later version.
 * 
 * DGantt is distributed in the hope that it will be useful, but WITHOUT ANY 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS 
 * FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for 
 * more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License 
 * along with the DGantt.  If not, see <http://www.gnu.org/licenses/>.
 */
package dgantt;

import java.util.Date;

/**
 * Translator to extract the necessary Gantt chart details from a user-defined
 * task.  The units of the start and end values are arbitrary.  However, if
 * using the built-in support for dates, the start and end values must be the 
 * date in milliseconds (see {@link Date#getTime}).
 * <p>
 * The default implementations of the {@code set} methods are read-only; the
 * underlying task is never modified unless such methods are overridden.
 */
public abstract class Translator {
	
	/**
	 * Default constructor for a Gantt chart translator.
	 */
	public Translator() {
		super();
	}
	
	/**
	 * Returns the row of the specified task.  The returned value must be in the
	 * range {@code 0 <= getRow(...) < ganttModel.getRowCount()}.
	 * 
	 * @param task the task
	 * @return the row of the specified task
	 */
	public abstract int getRow(Object task);
	
	/**
	 * Returns the start value of the specified task.
	 * 
	 * @param task the task
	 * @return the start value of the specified task
	 */
	public abstract long getStart(Object task);
	
	/**
	 * Returns the end value of the specified task. 
	 *  
	 * @param task the task
	 * @return the end value of the specified task
	 */
	public abstract long getEnd(Object task);
	
	/**
	 * Returns the display text for the specified task.
	 * 
	 * @param task the task
	 * @return the display text of the specified task
	 */
	public abstract String getText(Object task);
	
	/**
	 * Returns the contents of the tooltip popup for the specified task, or
	 * {@code null} if no tooltip popup should be displayed.  The default
	 * implementation is to return {@code null}.
	 * 
	 * @param task the task
	 * @return the contents of the tooltip popup for the specified task, or
	 *         {@code null} if no tooltip popup should be displayed
	 */
	public String getToolTipText(Object task) {
		return null;
	}
	
	/**
	 * Sets the row of the specified task.  The default implementation is
	 * read-only.
	 * 
	 * @param task the task
	 * @param row the new row
	 */
	public void setRow(Object task, int row) {
		//do nothing, default mode is read-only
	}

	/**
	 * Sets the start value of the specified task.  The default implementation 
	 * is read-only.
	 * 
	 * @param task the task
	 * @param start the new start value
	 */
	public void setStart(Object task, long start) {
		//do nothing, default mode is read-only
	}
	
	/**
	 * Sets the end value of the specified task.  The default implementation is
	 * read-only.
	 * 
	 * @param task the task
	 * @param end the new end value
	 */
	public void setEnd(Object task, long end) {
		//do nothing, default mode is read-only
	}

	/**
	 * Sets the display text for the specified task.  The default implementation
	 * is read-only.
	 * 
	 * @param task the task
	 * @param text the new display text
	 */
	public void setText(Object task, String text) {
		//do nothing, default mode is read-only
	}

}
