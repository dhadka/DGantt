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

import java.util.EventListener;

/**
 * Listener interface for receiving notifications whenever the selected tasks
 * in a Gantt chart are changed.
 */
public interface GanttSelectionListener extends EventListener {
	
	/**
	 * Invoked by a Gantt chart whenever the selected tasks in a Gantt chart
	 * are changed.
	 * 
	 * @param e the selection event
	 */
	public void valueChanged(GanttSelectionEvent e);

}
