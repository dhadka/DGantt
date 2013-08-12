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
 * Enumeration of link types.
 */
public enum LinkType {
	
	/**
	 * A link from the start of the first task to the start of the second task.
	 */
	START_TO_START,
	
	/**
	 * A link from the finish of the first task to the start of the second task.
	 */
	FINISH_TO_START,
	
	/**
	 * A link from the finish of the first task to the finish of the second 
	 * task.
	 */
	FINISH_TO_FINISH

}
