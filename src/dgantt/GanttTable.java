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
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

/**
 * A {@link JTable} to be displayed alongside a {@link GanttChart}.  The 
 * look-and-feel of the {@code JTable} is adjusted to conform with the 
 * look-and-feel of the {@code GanttChart}.  A {@link GanttPanel} should be used
 * to synchronize the components.
 */
public class GanttTable extends JTable implements TableCellRenderer {
	
	private static final long serialVersionUID = 2597593882834133527L;
	
	/**
	 * The {@code GanttChart} synchronized with this table.
	 */
	private final GanttChart chart;
	
	/**
	 * The {@link GanttHeader} synchronized with this table.
	 */
	private final GanttHeader header;

	/**
	 * Class constructor for a {@code JTable} to be synchronized with a
	 * {@code GanttChart} and {@code GanttHeader}, typically within a
	 * {@code GanttPanel}.
	 * 
	 * @param chart the {@code GanttChart} synchronized with this table
	 * @param header the {@code GanttHeader} synchronized with this table
	 * @param tableModel
	 */
	public GanttTable(GanttChart chart, GanttHeader header, 
			TableModel tableModel) {
		super(tableModel);
		this.chart = chart;
		this.header = header;
		
		setRowHeight(chart.getRowHeight());
		
		for (int i=0; i<getColumnModel().getColumnCount(); i++) {
			getColumnModel().getColumn(i).setHeaderRenderer(this);
			getColumnModel().getColumn(i).setCellRenderer(this);
		}

		setShowGrid(false);
		setIntercellSpacing(new Dimension(0, 0));
		setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
	}
	
	@Override
	public JComponent getTableCellRendererComponent(final JTable table, 
			Object value, boolean isSelected, boolean hasFocus, final int row, 
			int column) {
		JLabel label = new JLabel(value.toString()) {

			private static final long serialVersionUID = -1857424941970648741L;

			@Override
			protected void paintComponent(Graphics g) {
				if (row >= 0) {
					chart.getRowRenderer().paintRow(g, chart, row, 
							g.getClipBounds(), true);
				}
				
				super.paintComponent(g);
			}
			
		};
		
		if (row < 0) {
			label.setOpaque(true);
			label.setBackground(header.getBackground());
			label.setPreferredSize(new Dimension(1, 
					header.getPreferredSize().height));
			label.setBorder(BorderFactory.createCompoundBorder(
					BorderFactory.createMatteBorder(0, 1, 1, 0, Color.GRAY),
					BorderFactory.createEmptyBorder(0, 2, 0, 0)));
		} else {
			label.setBorder(BorderFactory.createCompoundBorder(
					BorderFactory.createMatteBorder(0, 1, 0, 0, Color.GRAY),
					BorderFactory.createEmptyBorder(0, 2, 0, 0)));
		}
		
		return label;
	}

}