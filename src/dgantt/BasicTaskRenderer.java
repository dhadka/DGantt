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
import java.awt.geom.Rectangle2D;

/**
 * A basic implementation of a {@link TaskRenderer}.
 */
public class BasicTaskRenderer implements TaskRenderer {
	
	/**
	 * The foreground color of tasks.
	 */
	public static final Color FOREGROUND = Color.BLACK;
	
	/**
	 * The background color of tasks.
	 */
	public static final Color BACKGROUND = new Color(127, 127, 255);
	
	/**
	 * Class constructor for a basic task renderer.
	 */
	public BasicTaskRenderer() {
		super();
	}

	@Override
	public void paintTask(Graphics g, GanttChart chart, Object task,
			Rectangle2D bounds, boolean selected) {
		Graphics2D g2 = (Graphics2D)g;

		g2.setColor(BACKGROUND);
		g2.fill(bounds);
		
		g2.setColor(Color.BLACK);
		g2.draw(bounds);
		
		if (selected) {
			g2.setColor(Color.RED);
			g2.draw(bounds);
		}
		
		g2.setColor(FOREGROUND);
		TextUtilities.paintString(g2, chart.getTranslator().getText(task), 
				bounds.getBounds(), TextUtilities.CENTER, TextUtilities.CENTER);
	}

}
