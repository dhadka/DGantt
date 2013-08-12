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

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEdit;

/**
 * A more advanced task editor than {@link BasicTaskEditorHandler}, which
 * demonstrates features like snapping tasks to day boundaries, adding edits
 * to an {@link UndoManager}, etc.
 */
public class AdvancedTaskEditorHandler extends BasicTaskEditorHandler {

	/**
	 * The collection of states for the tasks currently being edited.
	 */
	protected List<State> states;

	/**
	 * The {@link UndoableEdit} if an edit is currently in progress; otherwise
	 * {@code null}.
	 */
	protected GanttChartUndoableEdit edit;
	
	/**
	 * The undo manager where completed edits are committed.
	 */
	protected UndoManager undoManager;

	/**
	 * {@code true} if the {@link GanttChart#fireChangeEvent()} method is 
	 * invoked as an edit is in progress; {@code false} otherwise.
	 */
	protected boolean fireChangeDuringEdit = true;
	
	/**
	 * The minimum allowed duration of a task.  Default is one day.
	 */
	protected long minimumDuration = 1000 * 60 * 60 * 24;
	
	/**
	 * Class constructor for an advanced task editor handler.
	 * 
	 * @param chart the {@code GanttChart} connected to this listener.
	 * @param undoManager the {@code UndoManager} where completed edits are
	 *        committed
	 */
	public AdvancedTaskEditorHandler(GanttChart chart, 
			UndoManager undoManager) {
		super(chart);
		this.undoManager = undoManager;

		states = new ArrayList<State>();
	}

	/**
	 * Returns {@code true} if the {@link GanttChart#fireChangeEvent()} method
	 * is invoked as an edit is in progress; {@code false} otherwise.
	 * 
	 * @return {@code true} if the {@link GanttChart#fireChangeEvent()} method
	 *         is invoked as an edit is in progress; {@code false} otherwise
	 */
	public boolean isFireChangeDuringEdit() {
		return fireChangeDuringEdit;
	}

	/**
	 * If {@code true}, the {@link GanttChart#fireChangeEvent()} method will be
	 * invoked as an edit is in progress; {@code false} otherwise.
	 * 
	 * @param fireChangeDuringEdit {@code true} if the 
	 *        {@link GanttChart#fireChangeEvent()} method is invoked as an edit
	 *        is in progress; {@code false} otherwise
	 */
	public void setFireChangeDuringEdit(boolean fireChangeDuringEdit) {
		this.fireChangeDuringEdit = fireChangeDuringEdit;
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (e.isConsumed()) {
			return;
		}

		if (e.getButton() != MouseEvent.BUTTON1) {
			return;
		}

		Object selectedTask = chart.getTaskAtPoint(e.getPoint());
		
		if (selectedTask == null) {
			if (!e.isControlDown()) {
				chart.clearSelection();
			}
		} else {
			if (e.isControlDown()) {
				chart.toggleTaskSelection(selectedTask);
			} else if (!chart.isTaskSelected(selectedTask)) {
				chart.clearSelection();
				chart.selectTask(selectedTask);
			}
		}

		chart.repaint(chart.getVisibleRect());

		if (e.isControlDown()) {
			return;
		}

		lastPoint = e.getPoint();
		
		for (Object task : chart.getSelectedTasks()) {
			states.add(new State(task, 
					chart.getTranslator().getStart(task), 
					chart.getTranslator().getEnd(task), 
					chart.getTranslator().getRow(task)));
		}

		mode = getMode(e);
		
		if (mode == NONE) {
			return;
		}

		edit = new GanttChartUndoableEdit(GanttChartUndoableEdit.MOUSE, chart,
				undoManager);
		
		for (State state : states) {
			edit.addEditedTask(state.getTask());
		}
		
		edit.grabBeforeSnapshot();
		e.consume();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (e.isConsumed()) {
			return;
		}

		if (!e.getPoint().equals(lastPoint) && (edit != null)) {
			edit.grabAfterSnapshot();
			edit.commit();
		}
		
		edit = null;
		lastPoint = null;
		states.clear();

		chart.resize();
		chart.repaint();
		chart.fireChangeEvent();

		mouseMoved(e);

		e.consume();
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if (e.isConsumed()) {
			return;
		}

		if (lastPoint == null) {
			return;
		}

		if (states.isEmpty()) {
			return;
		}

		Point point = e.getPoint();
		long dx = chart.screenToCanonical(point.getX()) 
				- chart.screenToCanonical(lastPoint.getX());

		for (int i = 0; i < states.size(); i++) {
			State state = states.get(i);
			Object task = state.getTask();
			long start = state.getStart();
			long end = state.getEnd();

			if (mode == RESIZE_START) {
				if (start + dx >= end - minimumDuration) {
					dx = end - start - minimumDuration;
				}
				
				start = start + dx;
			} else if (mode == RESIZE_END) {
				if (end + dx <= start + minimumDuration) {
					dx = start - end + minimumDuration;
				}
				
				end = end + dx;
			} else if ((mode == MOVE) || (mode == MULTIPLE)) {
				start = start + dx;
				end = end + dx;
			}

			if ((chart.canonicalToScreen(start) < chart.getVisibleRect().getMinX())
					&& (chart.canonicalToScreen(end) > chart.getVisibleRect().getMaxX())) {
				// do not scroll
			} else {
				Rectangle2D bounds = chart.getTaskBounds(task);
				bounds.setFrame(chart.canonicalToScreen(start), bounds.getY(),
						chart.canonicalToScreen(end) - chart.canonicalToScreen(start), 
						bounds.getHeight());
				chart.scrollRectToVisible(bounds.getBounds());
			}

			// snap dimension to days
			Calendar cal = Calendar.getInstance();
			
			if (mode != RESIZE_END) {
				cal.setTimeInMillis(start);
				cal.set(Calendar.HOUR_OF_DAY, 0);
				cal.set(Calendar.MINUTE, 0);
				cal.set(Calendar.SECOND, 0);
				cal.set(Calendar.MILLISECOND, 0);
				chart.getTranslator().setStart(task, cal.getTimeInMillis());
			}
			
			if (mode != RESIZE_START) {
				cal.setTimeInMillis(end);
				cal.set(Calendar.HOUR_OF_DAY, 0);
				cal.set(Calendar.MINUTE, 0);
				cal.set(Calendar.SECOND, 0);
				cal.set(Calendar.MILLISECOND, 0);
				chart.getTranslator().setEnd(task, cal.getTimeInMillis());
			}

			if (mode == MOVE) {
				Integer destinationRow = chart.getRow(point.getY());
				
				if (destinationRow != null) {
					int row = chart.getRow(lastPoint.getY())
							+ (destinationRow - state.getRow());
					
					if (row != chart.getTranslator().getRow(task)) {
						chart.getTranslator().setRow(task, row);
					}
				}
			}
		}

		chart.resize();
		chart.repaint();
		
		if (isFireChangeDuringEdit()) {
			chart.fireChangeEvent();
		}

		e.consume();
	}

}
