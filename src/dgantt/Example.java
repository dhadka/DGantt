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

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.table.AbstractTableModel;
import javax.swing.undo.UndoManager;

/**
 * An example of creating a Gantt chart and using its basic features.
 */
public class Example {
	
	/**
	 * Stores individual tasks in this example.
	 */
	private static class Task {

		public String id;
		public Date start;
		public Date end;

		public Task(String id, Date start, Date end) {
			super();
			this.id = id;
			this.start = start;
			this.end = end;
		}

	}
	
	/**
	 * An example translator using dates.
	 */
	private static class TaskTranslator extends Translator {

		private List<Task> modules;

		public TaskTranslator(List<Task> modules) {
			super();
			this.modules = modules;
		}

		@Override
		public long getStart(Object task) {
			return ((Task)task).start.getTime();
		}

		@Override
		public long getEnd(Object task) {
			return ((Task)task).end.getTime();
		}

		@Override
		public String getText(Object task) {
			return ((Task)task).id;
		}

		@Override
		public String getToolTipText(Object task) {
			return ((Task)task).id;
		}

		@Override
		public void setStart(Object task, long start) {
			Date startDate = new Date(start);
			((Task)task).start = startDate;
		}
		
		@Override
		public void setEnd(Object task, long end) {
			Date endDate = new Date(end);
			((Task)task).end = endDate;
		}

		@Override
		public void setText(Object task, String text) {
			((Task)task).id = text;
		}

		@Override
		public int getRow(Object task) {
			return modules.indexOf(task);
		}

	}

	/**
	 * Starts the example.
	 * 
	 * @param args unused command line arguments
	 */
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			// do nothing, not an error
		}

		// generate the data
		Calendar calendar = Calendar.getInstance();
		Random random = new Random();
		final List<Task> tasks = new ArrayList<Task>();

		for (int i = 0; i < 20; i++) {
			calendar.add(Calendar.DATE, -random.nextInt(5));

			Date start = calendar.getTime();

			calendar.add(Calendar.DATE, random.nextInt(4) + 2);

			Date end = calendar.getTime();

			tasks.add(new Task("Task " + (i + 1), start, end));
		}

		// create translator allowing the dgantt software to grab the necessary
		// display information from individual modules
		TaskTranslator translator = new TaskTranslator(tasks);

		// create the gantt model, which allows the dgantt software access to
		// all the modules being displayed
		GanttModel dataModel = new BasicGanttModel(tasks);

		// create links
		BasicLinkModel linkModel = new BasicLinkModel();
		linkModel.addLink(tasks.get(0), tasks.get(1), LinkType.FINISH_TO_START);
		linkModel.addLink(tasks.get(2), tasks.get(3), LinkType.START_TO_START);
		linkModel
				.addLink(tasks.get(4), tasks.get(5), LinkType.FINISH_TO_FINISH);

		linkModel.addLink(tasks.get(10), tasks.get(11),
				LinkType.FINISH_TO_START);
		linkModel.addLink(tasks.get(10), tasks.get(12),
				LinkType.FINISH_TO_START);
		linkModel.addLink(tasks.get(10), tasks.get(13),
				LinkType.FINISH_TO_START);

		// create the actual dgantt chart
		final GanttChart chart = new GanttChart(dataModel, translator, linkModel);

		// create a handler for box selection using the left-mouse button
		BasicSelectionHandler boxSelectionHandler = new BasicSelectionHandler(chart);
		chart.addMouseListener(boxSelectionHandler);
		chart.addMouseMotionListener(boxSelectionHandler);

		// create handler for manipulating links
		BasicLinkEditorHandler linkHandler = new BasicLinkEditorHandler(chart);
		chart.addMouseListener(linkHandler);
		chart.addMouseMotionListener(linkHandler);

		// create a handler for zooming using the right-mouse button
		BasicZoomHandler zoomHandler = new BasicZoomHandler(chart);
		chart.addMouseListener(zoomHandler);
		chart.addMouseMotionListener(zoomHandler);

		// create a handler for double-clicking a task to change its text
		BasicDoubleClickHandler doubleClickHandler = new BasicDoubleClickHandler(chart);
		chart.addMouseListener(doubleClickHandler);
		chart.addMouseMotionListener(doubleClickHandler);

		// create a handler for moving, resizing and selecting tasks
		BasicTaskEditorHandler taskEditorHandler = new AdvancedTaskEditorHandler(chart, new UndoManager());
		chart.addMouseListener(taskEditorHandler);
		chart.addMouseMotionListener(taskEditorHandler);

		// create the dgantt header
		final GanttHeader header = new GanttHeader(chart);

		// create a table listing the module id and description
		final GanttTable table = new GanttTable(chart, header,
				new AbstractTableModel() {

					private static final long serialVersionUID = 5721139181257247921L;

					@Override
					public int getColumnCount() {
						return 1;
					}

					@Override
					public int getRowCount() {
						return tasks.size();
					}

					@Override
					public Object getValueAt(int rowIndex, int columnIndex) {
						return tasks.get(rowIndex).id;
					}

					@Override
					public String getColumnName(int column) {
						return "Tasks";
					}

				});

		// stitch the chart, header and table together with a GanttPanel; the
		// GanttPanel organizes the components, adds scrollbars, and ensures
		// the scrollbars operate in unison
		GanttPanel panel = new GanttPanel(chart, header, table);

		// lastly, create and display the window
		final JFrame frame = new JFrame("Dynamic Gantt Chart with Links");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setSize(400, 400);

		JPanel buttonPane = new JPanel(new FlowLayout(FlowLayout.RIGHT));

		buttonPane.add(new JButton(new AbstractAction("Close") {

			private static final long serialVersionUID = 3805426937056229358L;

			@Override
			public void actionPerformed(ActionEvent e) {
				frame.dispose();
			}

		}));

		frame.getContentPane().add(panel, BorderLayout.CENTER);
		frame.getContentPane().add(buttonPane, BorderLayout.SOUTH);
		frame.setVisible(true);
	}

}
