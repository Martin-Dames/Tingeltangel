
package tingeltangel;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.SplashScreen;
import java.io.IOException;
import javax.swing.SwingUtilities;
import tingeltangel.core.Books;
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
        
    private static String mpg123Path;
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                
                final String INSTALL_MPG123 = "MPG123 kann da und da heruntergeladen werden";
                
                
                StringCallback callbackMpg123 = new StringCallback() {
                    @Override
                    public void callback(String path) {
                        mpg123Path = path;
                        startup();
                    }
                };
                
                Tools.getBinaryPath("win_mpg123", "mpg123.exe", "mpg123", callbackMpg123, "MPG123", "Um MP3s abspielen zu können muss MPG123 installiert sein. Bitte triff eine Wahl.", INSTALL_MPG123);
                
            }
        });
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
