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
 * Model storing the collection of links on a Gantt chart.
 */
public abstract class LinkModel {
	
	/**
	 * Default constructor for a link model.
	 */
	public LinkModel() {
		super();
	}

	/**
	 * Returns the number of links.
	 * 
	 * @return the number of links
	 */
	public abstract int getLinkCount();
	
	/**
	 * Returns the link at the specified index.
	 * 
	 * @param index the index
	 * @return the link at the specified index
	 */
	public abstract Link getLinkAt(int index);
	
	/**
	 * Adds a new link to the Gantt chart.
	 * 
	 * @param link the new link
	 */
	public void addLink(Link link) {
		//do nothing, default mode is read-only
	}
	
	/**
	 * Removes the specified link from the Gantt chart.
	 * 
	 * @param link the link to remove
	 */
	public void removeLink(Link link) {
		//do nothing, default mode is read-only
	}
	
}
