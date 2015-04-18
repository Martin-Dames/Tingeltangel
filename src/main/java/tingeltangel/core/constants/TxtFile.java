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
