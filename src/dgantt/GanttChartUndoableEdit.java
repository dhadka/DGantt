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

import java.util.ArrayList;
import java.util.List;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEdit;

/**
 * An {@link UndoableEdit} for recording changes made to a {@link GanttChart} 
 * and providing the means to undo such changes.  Four steps are required: 
 * 1) add edited tasks; 2) grab a snapshot of the before states; 3) grab
 * a snapshot of the after states; and 4) commit the changes to the 
 * {@link UndoManager}.
 */
public class GanttChartUndoableEdit extends AbstractUndoableEdit {

	private static final long serialVersionUID = 1177932236828804040L;

	/**
	 * Constant for identifying edits made using the keyboard.
	 */
	public static final int KEYBOARD = 0;
	
	/**
	 * Constant for identifying edits made using the mouse.
	 */
	public static final int MOUSE = 1;

	/**
	 * The type of edit.  Either KEYBOARD or MOUSE.
	 */
	private int type;
	
	/**
	 * The {@code UndoManager} where edits are saved once committed.
	 */
	private final UndoManager undoManager;
	
	/**
	 * The {@code GanttChart} producing the edits.
	 */
	private final GanttChart chart;

	/**
	 * The collection of currently edited tasks.
	 */
	private List<Object> editedTasks;
	
	/**
	 * The before states of all the currently edited tasks.
	 */
	private List<State> beforeState;
	
	/**
	 * The after states of all currently edited tasks.
	 */
	private List<State> afterState;

	/**
	 * Class constructor for a new {@code UndoableEdit} to a {@code GanttChart}.
	 * 
	 * @param type the type of edit; either KEYBOARD or MOUSE
	 * @param chart the {@code GanttChart} producing the edit
	 * @param undoManager the {@code UndoManager} where the edit is saved once
	 *        committed
	 */
	public GanttChartUndoableEdit(int type, GanttChart chart,
			UndoManager undoManager) {
		super();
		this.type = type;
		this.chart = chart;
		this.undoManager = undoManager;

		editedTasks = new ArrayList<Object>();
	}

	@Override
	public void undo() {
		for (State state : beforeState) {
			Translator translator = chart.getTranslator();
			translator.setStart(state.getTask(), state.getStart());
			translator.setEnd(state.getTask(), state.getEnd());
			translator.setRow(state.getTask(), state.getRow());
		}
		
		chart.fireChangeEvent();
		chart.repaint();
	}

	@Override
	public void redo() {
		for (State state : afterState) {
			chart.getTranslator().setStart(state.getTask(), state.getStart());
			chart.getTranslator().setEnd(state.getTask(), state.getEnd());
			chart.getTranslator().setRow(state.getTask(), state.getRow());
		}
		
		chart.fireChangeEvent();
		chart.repaint();
	}

	@Override
	public boolean canRedo() {
		return true;
	}

	/**
	 * Adds the specified task to this {@code UndoableEdit}.  Only tasks added 
	 * to this {@code UndoableEdit} will be recorded.
	 * 
	 * @param task the task being edited
	 */
	public void addEditedTask(Object task) {
		editedTasks.add(task);
	}

	/**
	 * Records the before state of all tasks.
	 */
	public void grabBeforeSnapshot() {
		beforeState = new ArrayList<State>();
		
		for (Object task : editedTasks) {
			beforeState.add(new State(task, 
					chart.getTranslator().getStart(task), 
					chart.getTranslator().getEnd(task), 
					chart.getTranslator().getRow(task)));
		}
	}

	/**
	 * Records the after state of all tasks.
	 */
	public void grabAfterSnapshot() {
		afterState = new ArrayList<State>();
		
		for (Object task : editedTasks) {
			afterState.add(new State(task,
					chart.getTranslator().getStart(task), 
					chart.getTranslator().getEnd(task), 
					chart.getTranslator().getRow(task)));
		}
	}

	@Override
	public boolean addEdit(UndoableEdit edit) {
		if (edit instanceof GanttChartUndoableEdit) {
			GanttChartUndoableEdit newedit = (GanttChartUndoableEdit)edit;
		
			if ((type == KEYBOARD) && (newedit.type == KEYBOARD)) {
				if (editedTasks.equals(newedit.editedTasks)) {
					afterState = newedit.afterState;
					return true;
				} else {
					return false;
				}
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	/**
	 * Saves this {@code UndoableEdit} to the {@code UndoManater}.
	 */
	public void commit() {
		if ((beforeState == null) || (afterState == null)) {
			throw new IllegalStateException("incomplete edit");
		}

		undoManager.addEdit(this);
		
		beforeState = null;
		afterState = null;
	}

}
