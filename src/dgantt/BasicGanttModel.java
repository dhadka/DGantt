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

/**
 * Basic {@link GanttModel} implementation where each task is placed on a
 * separate row.
 */
public class BasicGanttModel extends GanttModel {
	
	/**
	 * The collection of tasks.
	 */
	private List<?> tasks;

	/**
	 * Class constructor for a basic Gantt model containing the specified tasks.
	 * 
	 * @param tasks the tasks contained within this Gantt model
	 */
    public BasicGanttModel(List<?> tasks) { 
    	super(); 
    	this.tasks = tasks;
    }

	@Override
	public int getRowCount() {
		return tasks.size();
	}

	@Override
	public Object getTaskAt(int index) {
		return tasks.get(index);
	}

	@Override
	public int getTaskCount() {
		return tasks.size();
	}

}


