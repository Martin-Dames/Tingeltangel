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
 * Constants used in the Script-files
 */
public interface ScriptFile {

	/**
	 *  file extension of the file
	 */
	static final String _SRC = ".src";

	/**
	 * file extension with area code "en"
	 */
	static final String _EN_SRC = '_'+Tingeltangel.DEFAULT_AREA_CODE+_SRC;

	/**
	 * Line break
	 */
	static final String LB = "\n";
	
	static final String COMMENT = "//";
	static final String CALL = "call";
	static final String CALLID = "callid";
	static final String CODE = "code";
	static final String RETURN = "return";
	static final String END = "end";
	static final String CONTENT = "[Content]";
	static final String NOTE = "[Note]";
	static final String PRECODE = "Precode=";

	static final String COLON = ":";
	static final String SINGLE_SPACE = " ";




	
}
