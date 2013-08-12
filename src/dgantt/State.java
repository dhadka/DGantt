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

/**
 * Immutable class for storing the original position of the tasks being
 * manipulated by this editor.
 */
public class State {
	
	/**
	 * The task.
	 */
	private final Object task;
	
	/**
	 * The original start value of the task.
	 */
	private final long start;
	
	/**
	 * The original end value of the task.
	 */
	private final long end;
	
	/**
	 * The original row of the task.
	 */
	private final int row;
	
	/**
	 * Class constructor storing the original position of a task.
	 * 
	 * @param task the task
	 * @param start the original start value of the task
	 * @param end the original end value of the task
	 * @param row the original row of the task
	 */
	public State(Object task, long start, long end, int row) {
		super();
		this.task = task;
		this.start = start;
		this.end = end;
		this.row = row;
	}

	/**
	 * Returns the task.
	 * 
	 * @return the task
	 */
	public Object getTask() {
		return task;
	}

	/**
	 * Returns the original start value of the task.
	 * 
	 * @return the original start value of the task
	 */
	public long getStart() {
		return start;
	}

	/**
	 * Returns the original end value of the task.
	 * 
	 * @return the original end value of the task
	 */
	public long getEnd() {
		return end;
	}

	/**
	 * Returns the original row of the task.
	 * 
	 * @return the original row of the task
	 */
	public int getRow() {
		return row;
	}
	
}
