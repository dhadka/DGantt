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
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

/**
 * Basic {@link MouseAdapter} for adding links using the right-mouse button.
 */
public class BasicLinkEditorHandler extends MouseAdapter {
	
	/**
	 * The source task.
	 */
	private Object selectedTask;
	
	/**
	 * The current position of the mouse cursor.
	 */
	private Point selectedPoint;
	
	/**
	 * The last position of the mouse cursor, used for clearing the previously
	 * rendered ghost line.
	 */
	private Point lastPoint;
	
	/**
	 * The target task.
	 */
	private Object targetTask;
	
	/**
	 * The bounds of the target task, used for rendering an outline.
	 */
	private Rectangle2D targetBounds;
	
	/**
	 * The {@code GanttChart} connected to this listener.
	 */
	private final GanttChart chart;

	/**
	 * Class constructor for adding tasks using the right-mouse button.
	 * 
	 * @param chart the {@code GanttChart} connected to this listener
	 */
	public BasicLinkEditorHandler(GanttChart chart) {
		super();
		this.chart = chart;
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
		if (e.isConsumed()) {
			return;
		}
		
		if (e.getButton() != MouseEvent.BUTTON3) {
			return;
		}
		
		selectedPoint = e.getPoint();
		selectedTask = chart.getTaskAtPoint(selectedPoint);
		
		if (selectedTask == null) {
			selectedPoint = null;
			return;
		}
		
		lastPoint = selectedPoint;
		
		Graphics2D g2 = (Graphics2D)chart.getGraphics();
		g2.setXORMode(Color.WHITE);
		g2.draw(new Line2D.Double(selectedPoint, lastPoint));
		g2.fill(new Rectangle(selectedPoint.x-2, selectedPoint.y-2, 5, 5));
		g2.setPaintMode();
		
		chart.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
		
		e.consume();
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {
		if (e.isConsumed() || (selectedPoint == null)) {
			return;
		}
		
		Graphics2D g2 = (Graphics2D)chart.getGraphics();
		g2.setXORMode(Color.WHITE);
		
		if (targetBounds != null) {
			g2.draw(targetBounds);
		}
		
		g2.draw(new Line2D.Double(selectedPoint, lastPoint));
		g2.fill(new Rectangle(selectedPoint.x-2, selectedPoint.y-2, 5, 5));
		g2.setPaintMode();
		
		chart.setCursor(Cursor.getDefaultCursor());
		
		lastPoint = e.getPoint();
		Object hoverTask = chart.getTaskAtPoint(lastPoint);
		
		if (hoverTask != null) {
			chart.getLinkModel().addLink(new Link(selectedTask, hoverTask, 
					LinkType.FINISH_TO_START));
			chart.repaint(chart.getVisibleRect());
		}
		
		selectedPoint = null;
		selectedTask = null;
		lastPoint = null;
		targetTask = null;
		targetBounds = null;
		
		e.consume();
	}
	
	@Override
	public void mouseEntered(MouseEvent e) {
		if (e.isConsumed() || (selectedTask == null)) {
			return;
		}
		
		e.consume();
	}
	
	@Override
	public void mouseExited(MouseEvent e) {
		if (e.isConsumed() || (selectedTask == null)) {
			return;
		}
		
		e.consume();
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.isConsumed() || (selectedTask == null)) {
			return;
		}
		
		e.consume();
	}
	
	@Override
	public void mouseMoved(MouseEvent e) {
		if (e.isConsumed() || (selectedTask == null)) {
			return;
		}

		e.consume();
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		if (e.isConsumed() || (selectedTask == null)) {
			return;
		}

		Graphics2D g2 = (Graphics2D)chart.getGraphics();
		g2.setXORMode(Color.WHITE);
		g2.draw(new Line2D.Double(selectedPoint, lastPoint));

		lastPoint = e.getPoint();
		Object hoverTask = chart.getTaskAtPoint(lastPoint);
		
		if ((hoverTask == null) || (hoverTask == selectedTask)) {
			if (targetBounds != null) {
				g2.draw(targetBounds);
			}
			
			targetBounds = null;
		} else if (hoverTask != targetTask) {
			if (targetBounds != null) {
				g2.draw(targetBounds);
			}
			
			targetTask = hoverTask;
			targetBounds = chart.getTaskBounds(targetTask, 
					chart.getRow(lastPoint.getY()), 0, 0);
			targetBounds.setRect(targetBounds.getX()-1.0, 
					targetBounds.getY()-1.0, targetBounds.getWidth()+2.0, 
					targetBounds.getHeight()+2.0);
			g2.draw(targetBounds);
		}
		
		g2.draw(new Line2D.Double(selectedPoint, lastPoint));
		g2.setPaintMode();
		
		e.consume();
	}
	
}
