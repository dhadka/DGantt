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

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * A Gantt chart swing component.  A Gantt chart is constructed by providing
 * a {@link GanttModel} and a {@link Translator}.  The {@code GanttModel} stores
 * all tasks displayed in the Gantt chart in addition to controlling several
 * additional settings.  The {@link Translator} extracts the necessary
 * information from user-defined objects, such as the start and end values of
 * the task, to render the Gantt chart tasks.  An optional {@link LinkModel} can
 * provide visual links between tasks to represent arbitrary requirements or
 * constraints.
 * <p>
 * This component provides the basic Gantt chart functionality, but is intended
 * to be extended with custom renderers and listeners to provide more complex 
 * behavior.
 */
public class GanttChart extends JComponent implements GanttModelListener {
	
	private static final long serialVersionUID = 1745970251668888420L;
	
	/**
	 * The {@code GanttModel} storing the tasks and other necessary settings
	 * used by this Gantt chart.
	 */
	private final GanttModel model;
	
	/**
	 * The {@code Translator} used by this Gantt chart to extract the necessary
	 * display information from user-defined tasks.
	 */
	private final Translator translator;
	
	/**
	 * The {@code LinkModel} storing the links between tasks displayed in this
	 * Gantt chart.
	 */
	private final LinkModel linkModel;
	
	/**
	 * The renderer used for drawing rows.
	 */
	private RowRenderer rowRenderer;

	/**
	 * The renderer used for drawing tasks.
	 */
	private TaskRenderer taskRenderer;
	
	/**
	 * The renderer used for drawing links between tasks.
	 */
	private LinkRenderer linkRenderer;
	
	/**
	 * The set of tasks currently selected in this Gantt chart.
	 */
	private Set<Object> selectedTasks;
	
	/**
	 * The listeners registered with this Gantt chart to receive notifications
	 * when the set of selected tasks is changed.
	 */
	private List<GanttSelectionListener> selectionListeners;
	
	/**
	 * The listeners registered with this Gantt chart to receive notifications
	 * when any aspect of the Gantt chart is changed.
	 */
	private List<ChangeListener> changeListeners;
	
	/**
	 * The user-specified zoom.
	 */
	private double zoom;
	
	/**
	 * The minimum value displayed in this Gantt chart, in canonical 
	 * coordinates.
	 */
	protected long rangeMinimum;
	
	/**
	 * The maximum value displayed in this Gantt chart, in canonical 
	 * coordinates.
	 */
	protected long rangeMaximum;

	/**
	 * The height of each row.
	 */
	private int rowHeight;
	
	/**
	 * The dimensions of the empty space surrounding a task.
	 */
	private Insets rowInsets;
	
	/**
	 * Class constructor for a new Gantt chart with the specified Gantt model
	 * and translator.
	 * 
	 * @param model the {@code GanttModel} storing the tasks and other necessary
	 *        settings used by this Gantt chart
	 * @param translator the {@code Translator} used by this Gantt chart to 
	 *        extract the necessary display information from user-defined tasks
	 */
	public GanttChart(GanttModel model, Translator translator) {
		this(model, translator, null);
	}
	
	/**
	 * Class constructor for a new Gantt chart with the specified Gantt model, 
	 * translator and link model.
	 * 
	 * @param model the {@code GanttModel} storing the tasks and other necessary
	 *        settings used by this Gantt chart
	 * @param translator the {@code Translator} used by this Gantt chart to 
	 *        extract the necessary display information from user-defined tasks
	 * @param linkModel the {@code LinkModel} storing the links between tasks 
	 *        displayed in this Gantt chart
	 */
	public GanttChart(GanttModel model, Translator translator, 
			LinkModel linkModel) {
		super();
		this.model = model;
		this.translator = translator;
		this.linkModel = linkModel;
		
		rowRenderer = new BasicRowRenderer();
		taskRenderer = new BasicTaskRenderer();
		linkRenderer = new BasicLinkRenderer();
		
		selectedTasks = new HashSet<Object>();
		selectionListeners = new Vector<GanttSelectionListener>();
		changeListeners = new Vector<ChangeListener>();

		rowHeight = 20;
		rowInsets = new Insets(1, 1, 1, 1);
		zoom = 1.0;
		
		computeRange();
		setToolTipText("");
		
		model.addGanttModelListener(this);
	}

	/**
	 * Registers the specified {@link GanttSelectionListener} to receive
	 * notifications from this Gantt chart whenever the set of selected tasks
	 * is changed.
	 * 
	 * @param listener the listener to receive notifications
	 */
	public void addGanttSelectionListener(GanttSelectionListener listener) {
		selectionListeners.add(listener);
	}
	
	/**
	 * Unregisters the specified {@link GanttSelectionListener} to no longer
	 * receieve notifications from this Gantt chart whenever the set of selected
	 * tasks is changed.
	 * 
	 * @param listener the listener to no longer recieve notifications
	 */
	public void removeGanttSelectionListener(GanttSelectionListener listener) {
		selectionListeners.remove(listener);
	}
	
	/**
	 * Registers the specified {@link ChangeListener} to receive notifications
	 * from this Gantt chart whenever its contents are changed.
	 * 
	 * @param listener the listener to receive notifications
	 */
	public void addChangeListener(ChangeListener listener) {
		changeListeners.add(listener);
	}
	
	/**
	 * Unregisters the specified {@link ChangeListener} to no longer receive
	 * notifications from this Gantt chart whenever its contents are changed.
	 * 
	 * @param listener the listener to no longer receive notifications
	 */
	public void removeChangeListener(ChangeListener listener) {
		changeListeners.remove(listener);
	}
	
	/**
	 * Notifies all registered {@link GanttSelectionListener} that the set of
	 * selected tasks has changed.
	 */
	public void fireSelectionEvent() {
		GanttSelectionEvent event = new GanttSelectionEvent(this);
		
		for (GanttSelectionListener listener : selectionListeners) {
			listener.valueChanged(event);
		}
	}
	
	/**
	 * Notifies all registered {@link ChangeListener} that the Gantt chart
	 * contents have changed.
	 */
	public void fireChangeEvent() {
		ChangeEvent event = new ChangeEvent(this);
		
		for (ChangeListener listener : changeListeners) {
			listener.stateChanged(event);
		}
	}

	/**
	 * Adds the specified task to the set of selected tasks.  This method
	 * invokes {@code fireSelectionEvent()}.
	 * 
	 * @param task the task to be selected
	 */
	public void selectTask(Object task) {
		selectedTasks.add(task);
		fireSelectionEvent();
	}
	
	/**
	 * Removes the specified task from the set of selected tasks.  This method
	 * invokes {@code fireSelectionEvent()}.
	 * 
	 * @param task the task to be unselected
	 */
	public void unselectTask(Object task) {
		selectedTasks.remove(task);
		fireSelectionEvent();
	}
	
	/**
	 * Toggles the selection state of the specified task.  This method invokes
	 * {@code fireSelectionEvent()}.
	 * 
	 * @param task the task to be selected/unselected
	 */
	public void toggleTaskSelection(Object task) {
		if (selectedTasks.contains(task)) {
			selectedTasks.remove(task);
		} else {
			selectedTasks.add(task);
		}
		
		fireSelectionEvent();
	}
	
	/**
	 * Returns {@code true} if the specified task is currently selected;
	 * {@code false} otherwise.
	 * 
	 * @param task the task
	 * @return {@code true} if the specified task is currently selected;
	 *         {@code false} otherwise
	 */
	public boolean isTaskSelected(Object task) {
		return selectedTasks.contains(task);
	}
	
	/**
	 * Returns the set of selected tasks.  Changes to the returned set will be
	 * reflected in the Gantt chart, requiring the invocation of 
	 * {@code fireSelectionEvent()}.  This is primarily intended to allow
	 * multiple changes to the set of selected tasks while only firing one
	 * selection event.
	 * 
	 * @return the set of selected tasks
	 */
	public Set<Object> getSelectedTasks() {
		return selectedTasks;
	}
	
	/**
	 * Returns the first selected task, or {@code null} if no tasks are
	 * selected.
	 * 
	 * @return the first selected task, or {@code null} if no tasks are
	 *         selected
	 */
	public Object getSelectedTask() {
		if (selectedTasks.isEmpty()) {
			return null;
		}
		
		return selectedTasks.iterator().next();
	}
	
	/**
	 * Unselects all previously selected tasks.  This method invokes 
	 * {@code fireSelectionEvent()}.
	 */
	public void clearSelection() {
		selectedTasks.clear();
		fireSelectionEvent();
	}
	
	/**
	 * Forces the Gantt chart to recompute its minimum and maximum values and,
	 * if necessary, resize and repaint itself.
	 */
	public void resize() {
		long oldMinimum = rangeMinimum;
		long oldMaximum = rangeMaximum;
		
		computeRange();

		if ((oldMinimum != rangeMinimum) || (oldMaximum != rangeMaximum)) {
			forceRevalidateAndRepaint();
		}
	}
	
	/**
	 * Forces this Gantt chart to revalidate and repaint, but also forces any
	 * parent {@link JScrollPane} or {@link GanttPanel} to revalidate and
	 * repaint.
	 */
	protected void forceRevalidateAndRepaint() {
		revalidate();
		repaint();
		
		if (getParent() instanceof JViewport) {
			JViewport viewport = (JViewport)getParent();
			viewport.revalidate();
			viewport.repaint();
			
			if (viewport.getParent() instanceof JScrollPane) {
				JScrollPane scrollPane = (JScrollPane)viewport.getParent();
				scrollPane.revalidate();
				scrollPane.repaint();
			}
		}
	}
	
	/**
	 * Computes the minimum and maximum values contained in this Gantt chart.
	 */
	private void computeRange() {
		rangeMinimum = Long.MAX_VALUE;
		rangeMaximum = Long.MIN_VALUE;
		
		for (int i=0; i<model.getTaskCount(); i++) {
			Object task = model.getTaskAt(i);
			
			rangeMinimum = Math.min(rangeMinimum, translator.getStart(task));
			rangeMaximum = Math.max(rangeMaximum, translator.getEnd(task));
		}
		
		if ((rangeMinimum == Long.MAX_VALUE) || (rangeMaximum == Long.MIN_VALUE)) {
			//model empty
			rangeMinimum = 0;
			rangeMaximum = 0;
		}
	}
	
	@Override
	public Dimension getPreferredSize() {
		int width;
		
		if (getParent() instanceof JViewport) {
			width = (int)(zoom * getParent().getWidth());
		} else {
			width = super.getPreferredSize().width;
		}
		
		int height = model.getRowCount() * getRowHeight();

		return new Dimension(width, height);
	}

	/**
	 * Returns the row at the specified vertical position in screen coordinates;
	 * or {@code null} if the position is out of bounds.
	 * 
	 * @param y the vertical position in screen coordinates
	 * @return the row at the specified vertical position in screen coordinates;
	 *         or {@code null} if the position is out of bounds
	 */
	public Integer getRow(double y) {
		if (y < 0)
			return null;
		
		if (y > getHeight())
			return null;
		
		int row = (int)(y / getRowHeight());
		
		if (row >= model.getRowCount())
			return null;

		return row;
	}
	
	/**
	 * Returns the scaling factor used to convert between canonical and screen
	 * coordinates.
	 * 
	 * @return the scaling factor used to convert between canonical and screen
	 *         coordinates
	 */
	private double getScale() {
		return (double)(getWidth() - rowInsets.left - rowInsets.right) /
				(double)(rangeMaximum - rangeMinimum);
	}
	
	/**
	 * Converts from canonical coordinates to screen coordinates.
	 * 
	 * @param value the canonical coordinate value
	 * @return the screen coordinate value
	 */
	public double canonicalToScreen(long value) {
		return getScale()*(value - rangeMinimum) + rowInsets.left;
	}
	
	/**
	 * Converts from screen coordinates to canonical coordinates.
	 * 
	 * @param x the screen coordinate value
	 * @return the canonical coordinate value
	 */
	public long screenToCanonical(double x) {
		return (long)((x - rowInsets.left) / getScale()) + rangeMinimum;
	}
	
	/**
	 * Returns the rectangular bounds of the specified task in screen
	 * coordinates.
	 * 
	 * @param task the task
	 * @return the rectangular bounds of the specified task in screen
	 *         coordinates
	 */
	public Rectangle2D getTaskBounds(Object task) {
		int row = translator.getRow(task);
		double start = canonicalToScreen(translator.getStart(task));
		double end = canonicalToScreen(translator.getEnd(task));
		double top = row*getRowHeight() + rowInsets.top;
		
		return new Rectangle2D.Double(start, top, end - start, rowHeight);
	}
	
	/**
	 * Returns the rectangular bounds of the specified task in screen
	 * coordinates, whose position and width are modified by the additional
	 * parameters.
	 * 
	 * @param task the task
	 * @param row the new row
	 * @param dstart the change in position
	 * @param dwidth the change in width
	 * @return the rectangular bounds of the specified task in screen
	 *         coordinates, whose position and width are modified by the 
	 *         additional parameters
	 */
	public Rectangle2D getTaskBounds(Object task, int row, double dstart, 
			double dwidth) {
		double start = canonicalToScreen(translator.getStart(task));
		double end = canonicalToScreen(translator.getEnd(task));
		double top = row*getRowHeight() + rowInsets.top;
		
		return new Rectangle2D.Double(start + dstart, top, end - start + dwidth,
				rowHeight);
	}
	
	/**
	 * Returns the rectangular bounds of the specified row in screen 
	 * coordinates.
	 * 
	 * @param row the row
	 * @return the rectangular bounds of the specified row in screen 
	 *         coordinates
	 */
	public Rectangle2D getRowBounds(int row) {		
		return new Rectangle2D.Double(0.0, row*getRowHeight(), getWidth(),
				getRowHeight());
	}
	
	@Override
	public void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D)g;
		Rectangle clip = g2.getClipBounds();
		
		rowRenderer.paintBackground(g, this);
		
		for (int i=0; i<getModel().getRowCount(); i++) {
			Rectangle2D bounds = getRowBounds(i);
			
			if (bounds.intersects(clip)) {
				rowRenderer.paintRow(g, this, i, bounds, false);
			}
		}
			
		for (int i=0; i<model.getTaskCount(); i++) {
			Object task = model.getTaskAt(i);
			Rectangle2D bounds = getTaskBounds(task);
			
			if (bounds.intersects(clip)) {
				taskRenderer.paintTask(g, this, task, bounds, isTaskSelected(task));
			}
		}
		
		if ((linkRenderer != null) && (linkModel != null)) {
			for (int i=0; i<linkModel.getLinkCount(); i++) {
				linkRenderer.paintLink(g, this, linkModel.getLinkAt(i));
			}
		}
	}

	@Override
	public void ganttModelChanged(GanttModelEvent event) {
		//check to see if selected tasks still exist
		Iterator<Object> iterator = selectedTasks.iterator();
		boolean selectionChanged = false;
		
		while (iterator.hasNext()) {
			Object task = iterator.next();
			boolean found = false;
			
			for (int i=0; i<model.getTaskCount(); i++) {
				if (model.getTaskAt(i).equals(task)) {
					found = true;
					break;
				}
			}
			
			if (!found) {
				iterator.remove();
				selectionChanged = true;
			}
		}
		
		computeRange();
		forceRevalidateAndRepaint();
		fireChangeEvent();
		
		if (selectionChanged) {
			fireSelectionEvent();
		}
	}
	
	/**
	 * Returns the task at the specified point; or {@code null} if no task
	 * exists at that point.  Pick ordering is such that selected tasks are
	 * picked first, followed by tasks in reverse order from their position
	 * in the {@code GanttModel}.
	 * 
	 * @param point the point
	 * @return the task at the specified point; or {@code null} if no task
	 *         exists at that point
	 */
	public Object getTaskAtPoint(Point point) {
		//see if mouse is over selected task first
		for (Object task : selectedTasks) {
			Rectangle2D bounds = getTaskBounds(task);
			
			if (bounds.contains(point)) {
				return task;			
			}
		}
		
		for (int i=model.getTaskCount()-1; i>=0; i--) {
			Object task = model.getTaskAt(i);
			Rectangle2D bounds = getTaskBounds(task);
			
			if (bounds.contains(point)) {
				return task;
			}
		}
		
		return null;	
	}
	
	@Override
	public String getToolTipText(MouseEvent e) {
		Object task = getTaskAtPoint(e.getPoint());
		
		if (task == null) {
			return null;
		} else {
			return translator.getToolTipText(task);
		}
	}
	
	/**
	 * Sets the user-specified zoom.
	 * 
	 * @param zoom the new zoom
	 */
	public void setZoom(double zoom) {
		this.zoom = zoom;

		setSize(getPreferredSize());
		forceRevalidateAndRepaint();
	}
	
	/**
	 * Returns the user-specified zoom.
	 * 
	 * @return the user-specified zoom
	 */
	public double getZoom() {
		return zoom;
	}

	/**
	 * Returns the {@code GanttModel} storing the tasks and other necessary 
	 * settings used by this Gantt chart.
	 * 
	 * @return the {@code GanttModel} storing the tasks and other necessary 
	 *         settings used by this Gantt chart
	 */
	public GanttModel getModel() {
		return model;
	}

	/**
	 * Returns the {@code Translator} used by this Gantt chart to extract the 
	 * necessary display information from user-defined tasks.
	 * 
	 * @return the {@code Translator} used by this Gantt chart to extract the 
	 *         necessary display information from user-defined tasks
	 */
	public Translator getTranslator() {
		return translator;
	}

	/**
	 * Returns the {@code LinkModel} storing the links between tasks displayed 
	 * in this Gantt chart.
	 * 
	 * @return the {@code LinkModel} storing the links between tasks displayed 
	 *         in this Gantt chart
	 */
	public LinkModel getLinkModel() {
		return linkModel;
	}

	/**
	 * Returns the renderer used for drawing the background.
	 * 
	 * @return the renderer used for drawing the background
	 */
	public RowRenderer getRowRenderer() {
		return rowRenderer;
	}

	/**
	 * Sets the renderer used for drawing the background.
	 * 
	 * @param rowRenderer the renderer used for drawing the background
	 */
	public void setRowRenderer(RowRenderer rowRenderer) {
		this.rowRenderer = rowRenderer;
	}

	/**
	 * Returns the renderer used for drawing tasks.
	 * 
	 * @return the renderer used for drawing tasks
	 */
	public TaskRenderer getTaskRenderer() {
		return taskRenderer;
	}

	/**
	 * Sets the renderer used for drawing tasks.
	 * 
	 * @param taskRenderer the renderer used for drawing tasks
	 */
	public void setTaskRenderer(TaskRenderer taskRenderer) {
		this.taskRenderer = taskRenderer;
	}

	/**
	 * Returns the renderer used for drawing links between tasks.
	 * 
	 * @return the renderer used for drawing links between tasks
	 */
	public LinkRenderer getLinkRenderer() {
		return linkRenderer;
	}

	/**
	 * Sets the renderer used for drawing links between tasks.
	 * 
	 * @param linkRenderer the renderer used for drawing links between tasks
	 */
	public void setLinkRenderer(LinkRenderer linkRenderer) {
		this.linkRenderer = linkRenderer;
	}

	/**
	 * Returns the height of each row, including any insets.
	 * 
	 * @return the height of each row
	 */
	public int getRowHeight() {
		return rowInsets.top + rowHeight + rowInsets.bottom;
	}

}
