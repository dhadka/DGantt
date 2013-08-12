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
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

/**
 * A basic implementation of a {@link LinkRenderer}.
 */
public class BasicLinkRenderer implements LinkRenderer {
	
	/**
	 * The background color of links.
	 */
	public static final Color BACKGROUND = Color.RED;
	
	/**
	 * Class constructor for a basic link renderer.
	 */
	public BasicLinkRenderer() {
		super();
	}

	@Override
	public void paintLink(Graphics g, GanttChart chart, Link link) {
		Graphics2D g2 = (Graphics2D)g;
		Rectangle2D bounds1 = chart.getTaskBounds(link.getFirst());
		Rectangle2D bounds2 = chart.getTaskBounds(link.getSecond());
		Shape arrow = null;
		
		switch (link.getType()) {
		case START_TO_START:
			arrow = getArrow((int)bounds1.getMinX(), (int)bounds1.getCenterY(), 
					(int)bounds2.getMinX(), (int)bounds2.getCenterY());
			break;
		case FINISH_TO_START:
			arrow = getArrow((int)bounds1.getMaxX(), (int)bounds1.getCenterY(), 
					(int)bounds2.getMinX(), (int)bounds2.getCenterY());
			break;
		case FINISH_TO_FINISH:
			arrow = getArrow((int)bounds1.getMaxX(), (int)bounds1.getCenterY(), 
					(int)bounds2.getMaxX(), (int)bounds2.getCenterY());
			break;
		default:
			throw new IllegalStateException();
		}
		
		g2.setColor(BACKGROUND);
		g2.fill(arrow);
		g2.setColor(Color.BLACK);
		g2.draw(arrow);
	}

	// The following is modified from 
	// http://forum.java.sun.com/thread.jsp?forum=57&thread=374342
	private Polygon getArrow(int xCenter, int yCenter, int x, int y) {
		double aDir=Math.atan2(xCenter-x,yCenter-y);
		
		if ((xCenter-x == 0) && (yCenter-y == 0)) {
			//force the arrow to be horizontal left-to-right when no change
			aDir = -1.5707963267948966; 
		}
		
		Polygon tmpPoly=new Polygon();
		tmpPoly.addPoint(xCenter, yCenter);
		int i1=9;
		int i2=6; // make the arrow head the same size regardless of the length
		tmpPoly.addPoint(x+xCor(i2,aDir),y+yCor(i2,aDir));
		tmpPoly.addPoint(x+xCor(i1,aDir+.5),y+yCor(i1,aDir+.5));
		tmpPoly.addPoint(x,y);							// arrow tip
		tmpPoly.addPoint(x+xCor(i1,aDir-.5),y+yCor(i1,aDir-.5));
		tmpPoly.addPoint(x+xCor(i2,aDir),y+yCor(i2,aDir));
		return tmpPoly;
	}

	private static int yCor(int len, double dir) {
		return (int)(len * Math.cos(dir));
	}
	
	private static int xCor(int len, double dir) {
		return (int)(len * Math.sin(dir));
	}


}
