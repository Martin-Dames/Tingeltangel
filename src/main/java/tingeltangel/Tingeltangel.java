
package tingeltangel;

import java.io.IOException;

import javax.swing.SwingUtilities;

import tingeltangel.core.Books;
import tingeltangel.core.CLI;
import tingeltangel.core.Properties;
import tingeltangel.core.Tools;
import tingeltangel.gui.MasterFrame;
import tingeltangel.gui.StringCallback;

public class Tingeltangel {
    
    public static int MAIN_FRAME_POS_X = 50;
    public static int MAIN_FRAME_POS_Y = 50;
    public static int MAIN_FRAME_WIDTH = 1200;
    public static int MAIN_FRAME_HEIGHT = 700;
    public static String MAIN_FRAME_TITLE = "Tingeltangel";
    public static String MAIN_FRAME_VERSION = " v0.1";
    
    // public final static String BASE_URL = "http://system.ting.eu/book-files";
    
    // for testing (some ting servers may be down :-( )
    public static final String BASE_URL = "http://62.75.252.55/book-files";
    
    /**
     * default area code
     */
    public static final String DEFAULT_AREA_CODE="en";
        
    private static String mpg123Path;
    
    public static void main(String[] args) {
        
        
        if(!CLI.cli(args)) {
        
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {

                    final String INSTALL_MPG123 = "MPG123 kann unter http://www.mpg123.de/download.shtml heruntergeladen werden";


                    StringCallback callbackMpg123 = new StringCallback() {
                        @Override
                        public void callback(String path) {
                            mpg123Path = path;
                            startup();
                        }
                    };

                    Tools.getBinaryPath(Properties.WIN_MPG123, "mpg123.exe", "mpg123", callbackMpg123, "MPG123", "Um MP3s abspielen zu können muss MPG123 installiert sein. Bitte triff eine Wahl.", INSTALL_MPG123);

                }
            });
        }
    }
    
    private static void startup() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    Books.quickSearch(new Thread() {
                        @Override
                        public void run() {
                            new MasterFrame();
                        }
                    });
                } catch(IOException ioe) {
                    ioe.printStackTrace(System.out);
                }
            }
        });
    }
    
    
    public static String mpg123Path() {
        return(mpg123Path);
    }
}
