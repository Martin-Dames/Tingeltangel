
package tingeltangel;

import java.io.FileWriter;
import java.io.PrintWriter;
import tingeltangel.core.Codes;
import tingeltangel.core.Translator;

public class TingIDvsCodeID {
    
    private final static int C = 8 * 14;
    
    public static void main(String[] args) throws Exception {
        String[] captions = new String[C];
        int[] index = new int[C];
        PrintWriter out = new PrintWriter(new FileWriter("big_test.ps"));
        for(int i = 0; i < C; i++) {
            index[i] = i;
            captions[i] = Integer.toString(Translator.code2ting(i));
        }
        Codes.drawBigPage(index, captions, out);
        out.close();
    }
    
    
}
