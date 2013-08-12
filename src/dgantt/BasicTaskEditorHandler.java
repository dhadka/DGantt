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

import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;

/**
 * Basic {@link MouseAdapter} for selecting, moving and resizing tasks.  One
 * task can be selected using the left-mouse button, and multiple tasks
 * selected by holding down the control key when clicking.  Single tasks can
 * be moved and resized, and even moved to different rows.  Multiple selected
 * tasks can be moved, but the tasks can not change rows.
 * <p>
 * Depending on which {@link Translator} setters are overridden, the type of
 * permitted edits can be controlled.
 */
public class BasicTaskEditorHandler extends MouseAdapter {
	
	/**
	 * The last recorded mouse position, used for determine the distance the
	 * cursor has moved.
	 */
	protected Point lastPoint;
	
	/**
	 * Current editing mode.  Either NONE, RESIZE_START, RESIZE_END, MOVE or 
	 * MULTIPLE.
	 */
	protected int mode;
	
	/**
	 * The {@link GanttChart} connected to this listener.
	 */
	protected GanttChart chart;
	
	/**
	 * Mode indicating no editing is occurring.
	 */
	public static final int NONE = 0;
	
	/**
	 * Mode indicating the start value of the task is being edited.
	 */
	public static final int RESIZE_START = 1;
	
	/**
	 * Mode indicating a single task is being moved.
	 */
	public static final int MOVE = 2;
	
	/**
	 * Mode indicating the end value of the task is being edited.
	 */
	public static final int RESIZE_END = 3;
	
	/**
	 * Mode indicating multiple tasks are being moved.
	 */
	public static final int MULTIPLE = 4;
	
	/**
	 * Class constructor for selecting, moving and resizing tasks in the 
	 * specified {@code GanttChart}.
	 * 
	 * @param chart the {@code GanttChart} connected to this listener
	 */
	public BasicTaskEditorHandler(GanttChart chart) {
		super();
		this.chart = chart;
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
		if (e.isConsumed()) {
			return;
		}
		
		if (e.getButton() != MouseEvent.BUTTON1) {
			return;
		}
		
		Object task = chart.getTaskAtPoint(e.getPoint());
		
		if (task == null) {
			if (!e.isControlDown()) {
				chart.clearSelection();
			}
		} else {
			if (e.isControlDown()) {
				chart.toggleTaskSelection(task);
			} else if (!chart.isTaskSelected(task)) {
				chart.clearSelection();
				chart.selectTask(task);
			}
		}
		
		chart.repaint(chart.getVisibleRect());
		
		if (e.isControlDown()) {
			return;
		}
		
		mode = getMode(e);
		
		if (mode == NONE) {
			return;
		}
		
		lastPoint = e.getPoint();
		
		e.consume();
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {
		if (e.isConsumed()) {
			return;
		}
		
		lastPoint = null;
		chart.repaint(chart.getVisibleRect());
		
		e.consume();
	}
	
	/**
	 * Returns the editing mode for the given {@code MouseEvent}.
	 * 
	 * @param e the {@code MouseEvent} describing the current cursor position
	 * @return the editing mode for the given {@code MouseEvent}
	 */
	protected int getMode(MouseEvent e) {
		if (chart.getSelectedTasks().isEmpty()) {
			return NONE;
		} else if (chart.getSelectedTasks().size() > 1) {
			return MULTIPLE;
		} else {
			return getMode(e, chart.getTaskAtPoint(e.getPoint()));
		}
	}
	
	/**
	 * Returns the editing mode for the given {@code MouseEvent} that is
	 * hovering over the specified task.
	 * 
	 * @param e the {@code MouseEvent} describing the current cursor position
	 * @param task the task the mouse is currently hovering over
	 * @return the editing mode for the given {@code MouseEvent}  that is
	 *         hovering over the specified task
	 */
	protected int getMode(MouseEvent e, Object task) {		
		if (Math.abs(e.getPoint().getX() - chart.canonicalToScreen(
				chart.getTranslator().getStart(task))) <= 1.0) {
			return RESIZE_START;
		} else if (Math.abs(e.getPoint().getX() - chart.canonicalToScreen(
				chart.getTranslator().getEnd(task))) <= 1.0) {
			return RESIZE_END;
		} else {
			return MOVE;
		}
	}
	
	@Override
	public void mouseMoved(MouseEvent e) {
		if (e.isConsumed()) {
			return;
		}
		
		Object task = chart.getTaskAtPoint(e.getPoint());
		
		if (task == null) {
			chart.setCursor(Cursor.getDefaultCursor());
		} else {
			int mode = getMode(e, task);
			
			if (mode == RESIZE_START) {
				chart.setCursor(Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR));
			} else if (mode == RESIZE_END) {
				chart.setCursor(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));
			} else if (mode == MOVE) {
				chart.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
			} else {
				throw new RuntimeException("undefined mode");
			}
			
			e.consume();
		}
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		if (e.isConsumed()) {
			return;
		}
		
		if (lastPoint == null) {
			return;
		}
		
		if (chart.getSelectedTasks().isEmpty()) {
			return;
		}

		Point point = e.getPoint();
		double dx = point.getX() - lastPoint.getX();
		
		for (Object task : chart.getSelectedTasks()) {
			Rectangle2D bounds = chart.getTaskBounds(task);
			
			if (mode == MOVE) {
				Integer destinationRow = chart.getRow(point.getY());
				
				if (destinationRow != null) {
					chart.getTranslator().setRow(task, destinationRow);
				}
			}
			
			if (mode == RESIZE_START) {
				if (bounds.getWidth() - dx <= 0) {
					dx = bounds.getWidth();
				}
				
				bounds.setFrame(bounds.getX()+dx, bounds.getY(), 
						bounds.getWidth()-dx, bounds.getHeight());
			} else if (mode == RESIZE_END) {
				if (bounds.getWidth() + dx <= 0) {
					dx = -bounds.getWidth();
				}
				
				bounds.setFrame(bounds.getX(), bounds.getY(), 
						bounds.getWidth()+dx, bounds.getHeight());
			} else if ((mode == MOVE) || (mode == MULTIPLE)) {
				bounds.setFrame(bounds.getX()+dx, bounds.getY(), 
						bounds.getWidth(), bounds.getHeight());
			}
			
			//if the task extends the entire width of the chart, do not scroll
			if ((bounds.getMinX() > chart.getVisibleRect().getMinX()) ||
					(bounds.getMaxX() < chart.getVisibleRect().getMaxX())) {
				chart.scrollRectToVisible(bounds.getBounds());
			}
			
			chart.getTranslator().setStart(task, 
					chart.screenToCanonical(bounds.getMinX()));
			chart.getTranslator().setEnd(task, 
					chart.screenToCanonical(bounds.getMaxX()));
		}

		chart.repaint();
		chart.fireChangeEvent();
		
		lastPoint = point;
		e.consume();
	}
	
}
