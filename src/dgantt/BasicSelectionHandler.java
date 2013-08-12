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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;

/**
 * Basic {@link MouseAdapter} for selecting multiple tasks in a Gantt chart.  
 */
public class BasicSelectionHandler extends MouseAdapter {
	
	/**
	 * The stroke used for rendering the selection box.
	 */
	public static final Stroke BoxStroke = new BasicStroke(1.0f, 
			BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1.0f, 
			new float[] { 4.0f }, 0.0f);
	
	/**
	 * The {@link GanttChart} connected to this listener.
	 */
	private final GanttChart chart;
	
	/**
	 * The starting point of the selection; or {@code null} if no selection
	 * is currently in progress.
	 */
	private Point startPoint;
	
	/**
	 * The bounds of the last selection box.  This is used by the XOR painting
	 * to efficiently remove the previously drawn selection box.
	 */
	private Rectangle2D lastBox;

	/**
	 * Class constructor for selecting multiple tasks in a Gantt chart.
	 * 
	 * @param chart the {@code GanttChart} connected to this listener
	 */
	public BasicSelectionHandler(GanttChart chart) {
		super();
		this.chart = chart;
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (e.isConsumed()) {
			return;
		}
		
		if (e.isControlDown()) {
			return;
		}
		
		if ((e.getButton() == MouseEvent.BUTTON1) && (e.getClickCount() == 1) && 
		    	(chart.getTaskAtPoint(e.getPoint()) == null)) {
			startPoint = e.getPoint();
			e.consume();
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (e.isConsumed() || (startPoint == null)) {
			return;
		}
		
		Graphics2D g2 = (Graphics2D)chart.getGraphics();
		g2.setXORMode(Color.WHITE);
		g2.setStroke(BoxStroke);
		
		chart.getSelectedTasks().clear();
		
		if (lastBox != null) {
			g2.draw(lastBox);
			
			for (int i=0; i<chart.getModel().getTaskCount(); i++) {
				Object task = chart.getModel().getTaskAt(i);
				Rectangle2D bounds = chart.getTaskBounds(task);
				
				if (bounds.intersects(lastBox)) {
					chart.getSelectedTasks().add(task);
				}
			}
		}
		
		startPoint = null;
		lastBox = null;
		
		chart.repaint();
		chart.fireSelectionEvent();
		
		e.consume();
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if (e.isConsumed() || (startPoint == null)) {
			return;
		}
		
		Point endPoint = e.getPoint();
		Graphics2D g2 = (Graphics2D)chart.getGraphics();
		g2.setXORMode(Color.WHITE);
		g2.setStroke(BoxStroke);
		
		if (lastBox != null) {
			g2.draw(lastBox);
		}
		
		lastBox = new Rectangle2D.Double(Math.min(startPoint.getX(), 
				endPoint.getX()),
				Math.min(startPoint.getY(), endPoint.getY()),
				Math.abs(endPoint.getX() - startPoint.getX()), 
				Math.abs(endPoint.getY() - startPoint.getY()));
		g2.draw(lastBox);
		
		e.consume();
	}
	
}
