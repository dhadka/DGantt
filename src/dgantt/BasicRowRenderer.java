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

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

/**
 * A basic implementation of a {@link RowRenderer}.
 */
public class BasicRowRenderer implements RowRenderer {
	
	/**
	 * The background color for odd numbered rows.
	 */
	public static final Color ODD_COLOR = new Color(240, 240, 240);
	
	/**
	 * The background color for even numbered rows.
	 */
	public static final Color EVEN_COLOR = Color.WHITE;
	
	/**
	 * Class constructor for a basic row renderer.
	 */
	public BasicRowRenderer() {
		super();
	}

	@Override
	public void paintBackground(Graphics g, GanttChart chart) {
		Graphics2D g2 = (Graphics2D)g;
		Rectangle clip = g2.getClipBounds();
		
		g2.setColor(chart.getBackground());
		g2.fill(clip);
	}

	@Override
	public void paintRow(Graphics g, GanttChart chart, int row,
			Rectangle2D bounds, boolean isTable) {
		Graphics2D g2 = (Graphics2D)g;
		
		if (row % 2 == 1) {
			g2.setColor(ODD_COLOR);
		} else {
			g2.setColor(EVEN_COLOR);
		}
		
		g2.fill(bounds);
	}

}
