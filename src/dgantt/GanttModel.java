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

import java.util.List;
import java.util.Vector;

/**
 * Model storing the collection of tasks displayed in the Gantt chart.
 */
public abstract class GanttModel {
	
	/**
	 * The {@link GanttModelListener}s registered to receive Gantt model changed
	 * events from this Gantt model.
	 */
	private final List<GanttModelListener> listeners;
	
	/**
	 * Default constructor for a Gantt model.
	 */
	public GanttModel() {
		super();
		
		listeners = new Vector<GanttModelListener>();
	}
	
	/**
	 * Returns the number of rows in this Gantt chart.
	 * 
	 * @return the number of rows in this Gantt chart
	 */
	public abstract int getRowCount();
	
	/**
	 * Returns the number of tasks contained in this Gantt chart.
	 * 
	 * @return the number of tasks contained in this Gantt chart
	 */
	public abstract int getTaskCount();
	
	/**
	 * Returns the task at the specified index.
	 * 
	 * @param index the index
	 * @return the task at the specified index
	 */
	public abstract Object getTaskAt(int index);

	/**
	 * Registers the specified {@code GanttModelListener} to receive events
	 * from this Gantt model.
	 * 
	 * @param listener the {@code GanttModelListener} to receive events from
	 *        this Gantt model
	 */
	public void addGanttModelListener(GanttModelListener listener) {
		listeners.add(listener);
	}
	
	/**
	 * Unregisters the specified {@code GanttModelListener} to no longer receive
	 * events from this Gantt model.
	 * 
	 * @param listener the {@code GanttModelListener} to no longer receive 
	 *        events from this Gantt model
	 */
	public void removeGanttModelListener(GanttModelListener listener) {
		listeners.remove(listener);
	}
	
	/**
	 * Notifies all registered {@code GanttModelListener}s that this model has
	 * been changed.
	 */
	public void fireGanttModelChanged() {
		fireGanttModelChanged(new GanttModelEvent(this));
	}
	
	/**
	 * Notifies all registered {@code GanttModelListener}s that the specified
	 * row has been changed.
	 * 
	 * @param row the index of the changed row
	 */
	public void fireGanttModelChanged(int row) {
		fireGanttModelChanged(new GanttModelEvent(this, row));
	}
	
	/**
	 * Notifies all registered {@code GanttModelListener}s that the specified
	 * rows have been changed.
	 * 
	 * @param firstRow the index of the first changed row
	 * @param lastRow the index of the last changed row
	 */
	public void fireGanttModelChanged(int firstRow, int lastRow) {
		fireGanttModelChanged(new GanttModelEvent(this, firstRow, lastRow));
	}
	
	/**
	 * Invokes the {@link GanttModelListener#ganttModelChanged(GanttModelEvent)}
	 * method on all registered {@code GanttModelListener}s.
	 * 
	 * @param event the event
	 */
	public void fireGanttModelChanged(GanttModelEvent event) {
		for (GanttModelListener listener : listeners) {
			listener.ganttModelChanged(event);
		}
	}
	
	/**
	 * Adds a new task to this Gantt model.  The default implementation
	 * is read-only.
	 * 
	 * @param task the new task
	 */
	public void addTask(Object task) {
		//do nothing, default mode is read-only
	}
	
	/**
	 * Removes the specified task from this Gantt model.  The default 
	 * implementation is read-only.
	 * 
	 * @param task the task to remove
	 */
	public void removeTask(Object task) {
		//do nothing, default mode is read-only
	}

}
