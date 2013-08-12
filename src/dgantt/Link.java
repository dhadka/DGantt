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

/**
 * Represents an individual link between two tasks.
 */
public class Link {

	/**
	 * The first (source) task.
	 */
	private final Object first;
	
	/**
	 * The second (target) task.
	 */
	private final Object second;
	
	/**
	 * The link type.
	 */
	private final LinkType type;
	
	/**
	 * Class constructor for a new link between the first and the second tasks.
	 * 
	 * @param first the first task
	 * @param second the second task
	 * @param type the link type
	 */
	public Link(Object first, Object second, LinkType type) {
		super();
		this.first = first;
		this.second = second;
		this.type = type;
	}

	/**
	 * Returns the first task (source) for this link.
	 * 
	 * @return the first task (source) for this link
	 */
	public Object getFirst() {
		return first;
	}

	/**
	 * Returns the second task (target) for this link.
	 * 
	 * @return the second task (target) for this link
	 */
	public Object getSecond() {
		return second;
	}

	/**
	 * Returns the type of this link.
	 * 
	 * @return the type of this link
	 */
	public LinkType getType() {
		return type;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((first == null) ? 0 : first.hashCode());
		result = prime * result + ((second == null) ? 0 : second.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Link other = (Link)obj;
		if (first == null) {
			if (other.first != null) {
				return false;
			}
		} else if (!first.equals(other.first)) {
			return false;
		}
		if (second == null) {
			if (other.second != null) {
				return false;
			}
		} else if (!second.equals(other.second)) {
			return false;
		}
		if (type == null) {
			if (other.type != null) {
				return false;
			}
		} else if (!type.equals(other.type)) {
			return false;
		}
		return true;
	}
	
}
