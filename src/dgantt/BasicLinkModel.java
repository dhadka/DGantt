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

/**
 * Basic {@link LinkModel} implementation.
 */
public class BasicLinkModel extends LinkModel {

	/**
	 * The collection of links.
	 */
	private List<Link> links;
	
	/**
	 * Class constructor for an empty basic link model.
	 */
	public BasicLinkModel() {
		links = new ArrayList<Link>();
	}
	
	@Override
	public int getLinkCount() {
		return links.size();
	}
	
	@Override
	public Link getLinkAt(int index) {
		return links.get(index);
	}
	
	@Override
	public void addLink(Link link) {
		links.add(link);
	}
	
	@Override
	public void removeLink(Link link) {
		links.remove(link);
	}
	
	/**
	 * Adds a new link to the Gantt chart.
	 * 
	 * @param from the source task
	 * @param to the target task
	 * @param type the link type
	 */
	public void addLink(Object from, Object to, LinkType type) {
		addLink(new Link(from, to, type));
	}
	
}
