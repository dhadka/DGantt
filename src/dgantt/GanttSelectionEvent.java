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

import java.util.EventObject;

/**
 * Event object representing selection events in a Gantt chart.  The selected
 * tasks can be retrieved using {@code e.getSource().getSelectedTasks()}.
 */
public class GanttSelectionEvent extends EventObject {
	
	private static final long serialVersionUID = 8622965228178188295L;

	/**
	 * Class constructor for a new Gantt selection event originating from the
	 * specified source.
	 * 
	 * @param source the Gantt chart from which this event originated
	 */
	public GanttSelectionEvent(GanttChart source)  {
		super(source);
	}

	@Override
	public GanttChart getSource() {
		return (GanttChart)super.getSource();
	}

}
