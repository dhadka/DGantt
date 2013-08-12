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
import java.awt.geom.Rectangle2D;

/**
 * Renderer for drawing the background of a Gantt chart.  The area rendered by 
 * these to methods overlap, with {@code paintBackground} invoked prior to 
 * {@code paintRow}s.
 */
public interface RowRenderer {

	/**
	 * Renders the background of the Gantt chart.  This is called prior to 
	 * any {@code paintRow} invocations.
	 * 
	 * @param g the graphics object used for rendering
	 * @param chart the Gantt chart
	 */
	public void paintBackground(Graphics g, GanttChart chart);
	
	/**
	 * Renders the background of a single row in the Gantt chart.
	 * 
	 * @param g the graphics object used for rendering
	 * @param chart the Gantt chart
	 * @param row the index of the row
	 * @param bounds the rectangular bounds of the row
	 * @param isTable {@code true} if this is rendering the background in a
	 *        {@link GanttTable}; {@code false} if rendering the background in 
	 *        a {@code GanttChart}
	 */
	public void paintRow(Graphics g, GanttChart chart, int row,
			Rectangle2D bounds, boolean isTable);
	
}
