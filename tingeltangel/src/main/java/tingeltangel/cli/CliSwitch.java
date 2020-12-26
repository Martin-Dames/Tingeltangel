/*
    Copyright (C) 2015   Martin Dames <martin@bastionbytes.de>
  
    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License along
    with this program; if not, write to the Free Software Foundation, Inc.,
    51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
  
*/

package tingeltangel.cli;

/**
 *
 * @author mdames
 */
public abstract class CliSwitch implements Comparable {
    
    public abstract String getName();
    public abstract String getLabel();
    public abstract String getDescription();
    public abstract boolean isOptional();
    public abstract boolean hasArgument();
    // default null means complex or no default value
    public abstract String getDefault();
    public abstract boolean acceptValue(String value);
    
    
    @Override
    public int compareTo(Object object) {
        return(getName().compareTo(((CliSwitch)object).getName()));
    }
}
