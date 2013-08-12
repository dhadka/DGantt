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
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JViewport;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * A Swing component for displaying a {@link GanttChart}, {@link GanttHeader}
 * and {@link GanttTable}.  A {@link JScrollPane} is created to provide
 * scrolling, and listeners are established to ensure the three subcomponents 
 * are synchronized and correctly aligned.
 */
public class GanttPanel extends JPanel {
	
	private static final long serialVersionUID = -1542893401367609772L;
	
	/**
	 * The {@code GanttChart} contained in this {@code GanttPanel}.
	 */
	private final GanttChart chart;
	
	/**
	 * The {@code GanttHeader} contained in this {@code GanttPanel}.
	 */
	private final GanttHeader header;
	
	/**
	 * The {@code ganttTable} contained in this {@code GanttTable}.
	 */
	private final GanttTable table;
	
	/**
	 * Class constructor for a {@code GanttPanel} displaying the specified
	 * {@code GanttChart}, {@code GanttHeader} and {@code GanttTable}.
	 * 
	 * @param chart the {@code GanttChart}
	 * @param header the {@code GanttHeader}
	 * @param table the {@code GanttTable}
	 */
	public GanttPanel(GanttChart chart, GanttHeader header, GanttTable table) {
		super();
		this.chart = chart;
		this.header = header;
		this.table = table;
		
		layoutComponents();
	}
	
	/**
	 * Lays out the component on this {@code GanttPanel} and establishes the
	 * necessary listeners to synchronize all the components.
	 */
	private void layoutComponents() {
		final JScrollPane tableScrollPane = new JScrollPane(table, 
				JScrollPane.VERTICAL_SCROLLBAR_NEVER, 
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		tableScrollPane.setBorder(BorderFactory.createEmptyBorder());
		
		JPanel leftPanel = new JPanel(new BorderLayout());
		leftPanel.add(tableScrollPane, BorderLayout.CENTER);

		final JScrollPane chartScrollPane = new JScrollPane(chart, 
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, 
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		chartScrollPane.setBorder(BorderFactory.createEmptyBorder());
		chartScrollPane.setColumnHeaderView(header);
		
		JPanel rightPanel = new JPanel(new BorderLayout());
		rightPanel.add(chartScrollPane, BorderLayout.CENTER);
		
		JSplitPane splitPane = new JSplitPane();
		splitPane.setResizeWeight(0);
		splitPane.setLeftComponent(leftPanel);
		splitPane.setRightComponent(rightPanel);
		splitPane.setDividerLocation(100);
		
		JPanel upperRightCorner = new JPanel();
		upperRightCorner.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, 
				Color.GRAY));
		chartScrollPane.setCorner(JScrollPane.UPPER_RIGHT_CORNER, 
				upperRightCorner);
		
		//The next two listeners enforce the requirement that both JScrollPanes
		//must either be both showing or both hiding their horizontal scroll
		//bar.  This is necessary since otherwise the visible heights of the
		//two JScrollPanes will be different, which can cause discrepancies
		//when scrolling to the very bottom.
		chartScrollPane.getViewport().addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				boolean visible = chart.getWidth() > chartScrollPane
						.getViewport().getWidth();
				visible |= table.getWidth() > tableScrollPane
						.getViewport().getWidth();
				int policy = visible ? JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS
						: JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED;
				tableScrollPane.setHorizontalScrollBarPolicy(policy);
				chartScrollPane.setHorizontalScrollBarPolicy(policy);
			}
		});
		
		tableScrollPane.getViewport().addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				boolean visible = chart.getWidth() > chartScrollPane
						.getViewport().getWidth();
				visible |= table.getWidth() > tableScrollPane
						.getViewport().getWidth();
				int policy = visible ? JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS
						: JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED;
				tableScrollPane.setHorizontalScrollBarPolicy(policy);
				chartScrollPane.setHorizontalScrollBarPolicy(policy);
			}
		});
		
		//This listener causes the GanttTable to display the same visible
		//section as the GanttChart.
		chartScrollPane.getVerticalScrollBar().addAdjustmentListener(
				new AdjustmentListener() {
					
			public void adjustmentValueChanged(AdjustmentEvent e) {
				Rectangle r = chart.getVisibleRect();
				r.x = 0;
				r.width = table.getWidth();
				table.scrollRectToVisible(r);
			}
			
		});
		
		//The following lines tell the JScrollPanes to use a backingstore, 
		//which causes all graphics to be rendered to an image buffer in memory.
		//Then scrolling need only copy from the image buffer rather than render
		//new image data.
		tableScrollPane.getViewport().setScrollMode(
				JViewport.BACKINGSTORE_SCROLL_MODE);
		chartScrollPane.getColumnHeader().setScrollMode(
				JViewport.BACKINGSTORE_SCROLL_MODE);
		chartScrollPane.getViewport().setScrollMode(
				JViewport.BACKINGSTORE_SCROLL_MODE);
		
		setLayout(new BorderLayout());
		add(splitPane, BorderLayout.CENTER);
	}

}
