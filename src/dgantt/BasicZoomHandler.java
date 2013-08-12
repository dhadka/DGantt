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
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Basic mouse handler for controlling the zoom level of a Gantt chart.
 * @author David
 *
 */
public class BasicZoomHandler extends MouseAdapter {
	
	private Point startPoint;
	
	private Point lastPoint;
	
	private GanttChart chart;
	
	public BasicZoomHandler(GanttChart chart) {
		this.chart = chart;
	}

	public void mousePressed(MouseEvent e) {
		if (e.isConsumed())
			return;
		
		if (e.getButton() != MouseEvent.BUTTON3)
			return;

		startPoint = e.getPoint();
		
		Graphics g = chart.getGraphics();
		g.setXORMode(Color.WHITE);
		g.drawLine(startPoint.x, 0,	startPoint.x, chart.getPreferredSize().height);
		g.setPaintMode();
		
		e.consume();
	}
	
	public static void zoomRectangle(Rectangle r, double zoom) {
		r.x = (int)(zoom*r.x);
		r.width = (int)(zoom*r.width);
	}
	
	public void mouseReleased(MouseEvent e) {
		if (e.isConsumed() || (startPoint == null))
			return;
		
		Graphics g = chart.getGraphics();
		g.setXORMode(Color.WHITE);
		if (lastPoint != null)
			g.drawLine(lastPoint.x, 0,	lastPoint.x, chart.getPreferredSize().height);
		g.drawLine(startPoint.x, 0,	startPoint.x, chart.getPreferredSize().height);
		g.setPaintMode();

		lastPoint = e.getPoint();
		if (Math.abs(lastPoint.getX() - startPoint.getX()) < 5) {
			//do nothing
		} else if (startPoint.getX() >= lastPoint.getX()) {
			chart.setZoom(1.0);
			chart.scrollRectToVisible(new Rectangle(new Point(0, 0), chart.getPreferredSize()));
		} else {
			long min = chart.screenToCanonical(startPoint.getX());
			long max = chart.screenToCanonical(lastPoint.getX());
			double relZoom = (1.0/chart.getZoom())*((double)(chart.rangeMaximum-chart.rangeMinimum)/(double)(max-min));
			
			//this chunk of code computes the new visible
			//rectangle after zoom
			Rectangle visibleRect = chart.getVisibleRect();
			Rectangle zoomRect = new Rectangle(startPoint.x, 0,
					lastPoint.x, chart.getPreferredSize().height);
			Rectangle intersection = visibleRect.intersection(zoomRect);
			zoomRectangle(intersection, relZoom);
			
			chart.setZoom(chart.getZoom()*relZoom);
			chart.scrollRectToVisible(intersection);
		}
		
		startPoint = null;
		lastPoint = null;
		
		e.consume();
	}
	
	public void mouseDragged(MouseEvent e) {
		if (e.isConsumed() || (startPoint == null))
			return;
		
		Graphics g = chart.getGraphics();
		g.setXORMode(Color.WHITE);
		if (lastPoint != null)
			g.drawLine(lastPoint.x, 0,	lastPoint.x, chart.getPreferredSize().height);
		
		lastPoint = e.getPoint();
		g.drawLine(lastPoint.x, 0, lastPoint.x, chart.getPreferredSize().height);
		g.setPaintMode();
		
		e.consume();
	}
	
}
