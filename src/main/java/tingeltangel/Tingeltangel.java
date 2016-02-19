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
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import tingeltangel.cli.CLI;
import tingeltangel.core.Repository;
import tingeltangel.gui.EditorFrame;
import tingeltangel.gui.ManagerFrame;

public class Tingeltangel {
    
    public static int MAIN_FRAME_POS_X = 50;
    public static int MAIN_FRAME_POS_Y = 50;
    public static int MAIN_FRAME_WIDTH = 1200;
    public static int MAIN_FRAME_HEIGHT = 700;
    public static String MAIN_FRAME_TITLE = "Tingeltangel";
    public static String MAIN_FRAME_VERSION = " v0.2-beta3";
    
    public final static String BASE_URL = "http://system.ting.eu/book-files";
    
    
    /**
     * default area code
     */
    public static final String DEFAULT_AREA_CODE="en";
 
    private final static Logger log = LogManager.getLogger(Tingeltangel.class);
    
    public static void main(String[] args) throws Exception {
        
        log.info("Starting Tingeltangel" + MAIN_FRAME_VERSION);
        log.info("\tos.name     : " + System.getProperty("os.name"));
        log.info("\tos.version  : " + System.getProperty("os.version"));
        log.info("\tos.arch     : " + System.getProperty("os.arch"));
        log.info("\tjava.version: " + System.getProperty("java.version"));
        log.info("\tjava.vendor : " + System.getProperty("java.vendor"));
        
        boolean startEditor = false;
        boolean startManager = false;
        
        if((args.length) > 0 && (args[0].toLowerCase().equals("gui-editor"))) {
            startEditor = true;
        }
        if((args.length) > 0 && (args[0].toLowerCase().equals("gui-manager"))) {
            startManager = true;
        }
        
        final boolean _startEditor = startEditor;
        
        if(startManager || startEditor) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    if(Repository.getIDs().length == 0) {
                        try {
                            Repository.initialUpdate(new Thread() {
                                @Override
                                public void run() {
                                    startGUI(_startEditor);
                                }
                            });
                        } catch (IOException ex) {
                            log.warn("initial update failed", ex);
                            startGUI(_startEditor);
                        }
                    } else {
                        startGUI(_startEditor);
                    }
                }
            });
        } else {
            if(args.length == 0) {
              tingeltangel.cli_ng.CLI.run();
            } else if(!CLI.cli(args)) {
                log.warn("starting cli failed");
            }
        }
    }
    
    private static void startGUI(boolean startEditor) {
        if(startEditor) {
            new EditorFrame();
        } else {
            new ManagerFrame();
        }
    }
}
