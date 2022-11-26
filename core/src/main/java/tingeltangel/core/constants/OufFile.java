/*
    Copyright (C) 2016
  
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

package tingeltangel.core.constants;

import tingeltangel.Tingeltangel;

/**
 * Constants used in the OUF-files
 */
public interface OufFile {

	/**
	 *  file extension of the file
	 */
	static final String _OUF = ".ouf";

	/**
	 * file extension with area code "en"
	 */
	static final String _EN_OUF = '_'+Tingeltangel.DEFAULT_AREA_CODE+_OUF;


	
}
