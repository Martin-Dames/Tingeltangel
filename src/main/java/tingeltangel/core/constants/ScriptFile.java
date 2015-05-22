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
	static final String CODE = "code";
	static final String RETURN = "return";
	static final String END = "end";
	static final String CONTENT = "[Content]";
	static final String NOTE = "[Note]";
	static final String PRECODE = "Precode=";

	static final String COLON = ":";
	static final String SINGLE_SPACE = " ";




	
}
