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
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.swing.JPanel;

/**
 * Header marking years, months, weeks, days and hours along the length of a 
 * {@link GanttChart}.  A {@link GanttPanel} should be used to synchronize the 
 * components.
 */
public class GanttHeader extends JPanel {
	
	private static final long serialVersionUID = 2729473542931877090L;
	
	/**
	 * The {@code GanttChart} synchronized with this header.
	 */
	private final GanttChart chart;

	/**
	 * Class constructor for a header making years, months, weeks, days and
	 * hours along the length of a {@code GanttChart}.
	 * 
	 * @param chart the {@code GanttChart} synchronized with this heade
	 */
	public GanttHeader(GanttChart chart) {
		super();
		this.chart = chart;
	}
	
	@Override
	public Dimension getPreferredSize() {
		FontMetrics fm = getFontMetrics(getFont());
		return new Dimension(chart.getWidth(), 2*fm.getHeight());
	}
	
	@Override
	public void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D)g;
		FontMetrics fm = getFontMetrics(getFont());

		g2.setColor(getBackground());
		g2.fill(g2.getClipBounds());
		
		if (canRenderRow(g2, Calendar.HOUR_OF_DAY, new SimpleDateFormat("h a"))) {
			renderRow(g2, Calendar.DATE, new SimpleDateFormat("dd MMMM yyyy"), 0, fm.getHeight());
			g2.setColor(Color.GRAY);
			g2.draw(new Line2D.Double(0, fm.getHeight(), getWidth(), fm.getHeight()));
			renderRow(g2, Calendar.HOUR_OF_DAY, new SimpleDateFormat("h a"), fm.getHeight(), fm.getHeight());			
		} else if (canRenderRow(g2, Calendar.DATE, new SimpleDateFormat("dd"))) {
			renderRow(g2, Calendar.MONTH, new SimpleDateFormat("MMM yyyy"), 0, fm.getHeight());
			g2.setColor(Color.GRAY);
			g2.draw(new Line2D.Double(0, fm.getHeight(), getWidth(), fm.getHeight()));
			renderRow(g2, Calendar.DATE, new SimpleDateFormat("dd"), fm.getHeight(), fm.getHeight());
		} else if (canRenderRow(g2, Calendar.WEEK_OF_YEAR, new SimpleDateFormat("'Week' w"))) {
			renderRow(g2, Calendar.MONTH, new SimpleDateFormat("MMM yyyy"), 0, fm.getHeight());
			g2.setColor(Color.GRAY);
			g2.draw(new Line2D.Double(0, fm.getHeight(), getWidth(), fm.getHeight()));
			renderRow(g2, Calendar.WEEK_OF_YEAR, new SimpleDateFormat("'Week' w"), fm.getHeight(), fm.getHeight());
		} else if (canRenderRow(g2, Calendar.MONTH, new SimpleDateFormat("MMM"))) {
			renderRow(g2, Calendar.YEAR, new SimpleDateFormat("yyyy"), 0, fm.getHeight());
			g2.setColor(Color.GRAY);
			g2.draw(new Line2D.Double(0, fm.getHeight(), getWidth(), fm.getHeight()));
			renderRow(g2, Calendar.MONTH, new SimpleDateFormat("MMM"), fm.getHeight(), fm.getHeight());
		} else {
			renderRow(g2, Calendar.YEAR, new SimpleDateFormat("yyyy"), 0, 2.0*fm.getHeight());
		}

		g2.setColor(Color.GRAY);
		g2.draw(new Line2D.Double(0, getHeight()-1, getWidth(), getHeight()-1));
	}
	
	/**
	 * Returns a normalized version of the calendar, such that date attributes
	 * smaller than the specified {@code stepType} are set to 0.
	 * 
	 * @param calendar the original calendar
	 * @param stepType either {@code Calendar.YEAR}, {@code Calendar.MONTH},
	 *        {@code Calendar.WEEK_OF_YEAR}, {@code Calendar.DATE} or
	 *        {@code Calendar.HOUR_OF_DAY}
	 * @return a normalized version of the calendar
	 */
	private Calendar normalizeCalendar(Calendar calendar, int stepType) {
		Calendar result = Calendar.getInstance();
		
		result.clear();
		
		if (stepType == Calendar.YEAR) {
			result.set(Calendar.YEAR, calendar.get(Calendar.YEAR));
		} else if (stepType == Calendar.MONTH) {
			result.set(Calendar.YEAR, calendar.get(Calendar.YEAR));
			result.set(Calendar.MONTH, calendar.get(Calendar.MONTH));
		} else if (stepType == Calendar.WEEK_OF_YEAR) {
			result.set(Calendar.YEAR, calendar.get(Calendar.YEAR));
			result.set(Calendar.WEEK_OF_YEAR, calendar.get(Calendar.WEEK_OF_YEAR));	
		} else if (stepType == Calendar.DATE) {
			result.set(Calendar.YEAR, calendar.get(Calendar.YEAR));
			result.set(Calendar.MONTH, calendar.get(Calendar.MONTH));
			result.set(Calendar.DATE, calendar.get(Calendar.DATE));
		} else if (stepType == Calendar.HOUR_OF_DAY) {
			result.set(Calendar.YEAR, calendar.get(Calendar.YEAR));
			result.set(Calendar.MONTH, calendar.get(Calendar.MONTH));
			result.set(Calendar.DATE, calendar.get(Calendar.DATE));
			result.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY));
		} else {
			throw new IllegalStateException("unsupported stepType");
		}
		
		return result;
	}
	
	/**
	 * Renders a row of the Gantt header.  Headers can consist of multiple rows,
	 * each showing a different resolution.
	 * 
	 * @param g the graphics object used for rendering
	 * @param stepType either {@code Calendar.YEAR}, {@code Calendar.MONTH},
	 *        {@code Calendar.WEEK_OF_YEAR}, {@code Calendar.DATE} or
	 *        {@code Calendar.HOUR_OF_DAY}
	 * @param dateFormat the date formatter
	 * @param y the vertical offset of the row
	 * @param height the height of the row
	 */
	private void renderRow(Graphics g, int stepType, DateFormat dateFormat, 
			double y, double height) {
		Graphics2D g2 = (Graphics2D)g;
		Rectangle2D bounds = g2.getClipBounds();
		long minimum = chart.screenToCanonical(bounds.getMinX());
		long maximum = chart.screenToCanonical(bounds.getMaxX());
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(minimum);
		calendar = normalizeCalendar(calendar, stepType);
		
		double start = chart.canonicalToScreen(calendar.getTimeInMillis());
		double end = 0.0;
		String text = null;
		
		while (calendar.getTimeInMillis() <= maximum) {
			text = dateFormat.format(calendar.getTime());
			calendar.add(stepType, 1);
			end = chart.canonicalToScreen(calendar.getTimeInMillis());

			g2.setColor(Color.GRAY);
			g2.draw(new Line2D.Double(start, y, start, y+height));
			g2.setColor(Color.BLACK);
			TextUtilities.paintString(g2, text, new Rectangle2D.Double(start, y,
					end-start, height).getBounds(), TextUtilities.CENTER, 
					TextUtilities.CENTER, true);
			
			start = end;
		}
		
		text = dateFormat.format(calendar.getTime());
		calendar.add(stepType, 1);
		end = chart.canonicalToScreen(calendar.getTimeInMillis());

		g2.setColor(Color.GRAY);
		g2.draw(new Line2D.Double(start, y, start, y+height));
		g2.setColor(Color.BLACK);
		TextUtilities.paintString(g2, text, new Rectangle2D.Double(start, y, 
				end-start, height).getBounds(), TextUtilities.CENTER, 
				TextUtilities.CENTER, true);
	}
	
	/**
	 * Returns {@code true} if the identically parameterized call to 
	 * {@code renderRow} can render all the text within its bounds; 
	 * {@code false} otherwise.
	 * 
	 * @param g the graphics object used for rendering
	 * @param stepType either {@code Calendar.YEAR}, {@code Calendar.MONTH},
	 *        {@code Calendar.WEEK_OF_YEAR}, {@code Calendar.DATE} or
	 *        {@code Calendar.HOUR_OF_DAY}
	 * @param dateFormat the date formatter
	 * @return {@code true} if the identically parameterized call to 
	 *         {@code renderRow} can render all the text within its bounds; 
	 *         {@code false} otherwise
	 */
	private boolean canRenderRow(Graphics g, int stepType, 
			DateFormat dateFormat) {
		Graphics2D g2 = (Graphics2D)g;
		FontMetrics fm = g2.getFontMetrics();

		Rectangle2D bounds = g2.getClipBounds();
		long minimum = chart.screenToCanonical(bounds.getMinX());
		long maximum = chart.screenToCanonical(bounds.getMaxX());
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(minimum);
		calendar = normalizeCalendar(calendar, stepType);
		
		double start = chart.canonicalToScreen(calendar.getTimeInMillis());
		double end = 0.0;
		String text = null;
		
		while (calendar.getTimeInMillis() <= maximum) {
			text = dateFormat.format(calendar.getTime());
			calendar.add(stepType, 1);
			end = chart.canonicalToScreen(calendar.getTimeInMillis());

			if (end - start < fm.stringWidth(text)) {
				return false;
			}
			
			start = end;
		}
		
		text = dateFormat.format(calendar.getTime());
		calendar.add(stepType, 1);
		end = chart.canonicalToScreen(calendar.getTimeInMillis());

		if (end - start < fm.stringWidth(text)) {
			return false;
		}
		
		return true;
	}

}
