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

import java.util.EventObject;

/**
 * Event object representing changes to a {@link GanttModel}.
 */
public class GanttModelEvent extends EventObject {

	private static final long serialVersionUID = -4746264910686760132L;

	/**
	 * The index of the first changed row.
	 */
	private final int firstRow;
	
	/**
	 * The index of the last changed row.
	 */
	private final int lastRow;
	
	/**
	 * Class constructor for a Gantt model event indicating all rows have
	 * changed.
	 * 
	 * @param model the changed Gantt model
	 */
	public GanttModelEvent(GanttModel model) {
		this(model, 0, model.getRowCount());
	}
	
	/**
	 * Class constructor for a Gantt model event indicating only the specified
	 * row has changed.
	 * 
	 * @param model the changed Gantt model
	 * @param row the index of the changed row
	 */
	public GanttModelEvent(GanttModel model, int row) {
		this(model, row, row);
	}
	
	/**
	 * Class constructor for a Gantt model event indicating the specified rows
	 * have changed.
	 * 
	 * @param model the changed Gantt model
	 * @param firstRow the index of the first changed row
	 * @param lastRow the index of the last changed row
	 */
	public GanttModelEvent(GanttModel model, int firstRow, int lastRow) {
		super(model);
		this.firstRow = firstRow;
		this.lastRow = lastRow;
	}

	@Override
	public GanttModel getSource() {
		return (GanttModel)super.getSource();
	}

	/**
	 * Returns the index of the first changed row.
	 * 
	 * @return the index of the first changed row
	 */
	public int getFirstRow() {
		return firstRow;
	}

	/**
	 * Returns the index of the last changed row.
	 * 
	 * @return the index of the last changed row
	 */
	public int getLastRow() {
		return lastRow;
	}

}
