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

import java.awt.Graphics;

/**
 * Renderer for drawing links on a Gantt chart.
 */
public interface LinkRenderer {

	/**
	 * Renders the specified link on the Gantt chart.
	 * 
	 * @param g the graphics object used for rendering
	 * @param chart the Gantt chart containing the links
	 * @param link the link to be rendered
	 */
	public void paintLink(Graphics g, GanttChart chart, Link link);
	
}
