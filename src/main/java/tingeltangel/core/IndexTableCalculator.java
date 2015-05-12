
package tingeltangel.core;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import tingeltangel.gui.MultipleChoiceDialog;
import tingeltangel.tools.Callback;


public class IndexTableCalculator {
    static final int[] E = {578, 562, 546, 530, 514, 498, 482, 466, 322, 306, 290, 274, 258, 242, 226, 210, -446, -462, -478, -494, -510, -526, -542, -558, -702, -718, -734, -750, -766, -782, -798, -814};

    
    /**
     *
     * @param code Positionscode in der Indextabelle (1. Feld)
     * @param n Position in der Indextablelle (startet bei 0)
     * @return Position in der Datei
     */
    public static int getPositionInFileFromCode(int code, int n) {
        if (((code & 255) != 0) | (n < 0)) {
            return -1;
        }
        n--;
        code = code >> 8;
        int c = ((code >> 3) & 1) | (((code >> 4) & 1) << 1) | (((code >> 5) & 1) << 2) | (((code >> 7) & 1) << 3) | (((code >> 9) & 1) << 4);
        code -= n * 26 - E[c];
        return code << 8;
    }

    /**
     *
     * @param position Position in der Datei
     * @param n Position in der Indextablelle (startet bei 0)
     * @return Positionscode in der Indextabelle (1. Feld)
     */
    static int getCodeFromPositionInFile(int position, int n) {
        if (((position & 255) != 0) | (n < 0)) {
            return -1;
        }
        n--;
        int b = (position >> 8) + n * 26;
        for (int k = 0; k < E.length; k++) {
            int v = (b - E[k]) << 8;
            if (IndexTableCalculator.getPositionInFileFromCode(v, n + 1) == position) {
                return v;
            }
        }
        return -1;
    }
    
    
}
