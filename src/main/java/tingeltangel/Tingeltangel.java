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
package tingeltangel;

import java.io.IOException;
import javax.swing.SwingUtilities;
import tingeltangel.cli.CLI;
import tingeltangel.core.Repository;
import tingeltangel.gui.EditorFrame;

public class Tingeltangel {
    
    public static int MAIN_FRAME_POS_X = 50;
    public static int MAIN_FRAME_POS_Y = 50;
    public static int MAIN_FRAME_WIDTH = 1200;
    public static int MAIN_FRAME_HEIGHT = 700;
    public static String MAIN_FRAME_TITLE = "Tingeltangel";
    public static String MAIN_FRAME_VERSION = " v0.2-beta2";
    
    public final static String BASE_URL = "http://system.ting.eu/book-files";
    
    
    /**
     * default area code
     */
    public static final String DEFAULT_AREA_CODE="en";
        
    public static void main(String[] args) throws Exception {
        if(!CLI.cli(args)) {

            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    if(Repository.getIDs().length == 0) {
                        try {
                            Repository.initialUpdate(new Thread() {
                                @Override
                                public void run() {
                                    new EditorFrame();
                                }
                            });
                        } catch (IOException ex) {
                            ex.printStackTrace();
                            new EditorFrame();
                        }
                    } else {
                        new EditorFrame();
                    }
                }
            });
        }
    }
}
