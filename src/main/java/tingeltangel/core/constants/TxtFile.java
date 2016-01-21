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
 * Constants used in the TXT-files
 */
public interface TxtFile {

	/**
	 *  file extension of the file
	 */
	static final String _TXT = ".txt";

	/**
	 * file extension with area code "en"
	 */
	static final String _EN_TXT = '_'+Tingeltangel.DEFAULT_AREA_CODE+_TXT;
	
	/**
	 * separator between keys and values
	 */
	static final String ROW_SEPARATOR = ": ";
	
	static final String KEY_NAME = "Name";
	static final String KEY_AUTHOR = "Author";
	static final String KEY_PUBLISHER = "Publisher";
	static final String KEY_VERSION = "Book Version";
	static final String KEY_URL = "URL";
	static final String KEY_AREA_CODE = "Book Area Code";
	static final String KEY_THUMB_MD5="ThumbMD5";
	static final String KEY_FILE_MD5="FileMD5";
	static final String KEY_SCRIPT_MD5 = "ScriptMD5";
}
