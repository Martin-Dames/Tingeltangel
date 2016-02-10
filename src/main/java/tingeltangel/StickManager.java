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
import tingeltangel.core.Repository;
import tingeltangel.gui.ManagerFrame;

public class StickManager {
    
    
    /**
     * default area code
     */
    public static final String DEFAULT_AREA_CODE="en";
 
    private final static Logger log = LogManager.getLogger(StickManager.class);
    
    public static void main(String[] args) throws Exception {
        
        log.info("Starting Tingeltangel" + Tingeltangel.MAIN_FRAME_VERSION);
        log.info("\tos.name     : " + System.getProperty("os.name"));
        log.info("\tos.version  : " + System.getProperty("os.version"));
        log.info("\tos.arch     : " + System.getProperty("os.arch"));
        log.info("\tjava.version: " + System.getProperty("java.version"));
        log.info("\tjava.vendor : " + System.getProperty("java.vendor"));
        
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                if(Repository.getIDs().length == 0) {
                    try {
                        Repository.initialUpdate(new Thread() {
                            @Override
                            public void run() {
                                new ManagerFrame();
                            }
                        });
                    } catch (IOException ex) {
                        ex.printStackTrace();
                        new ManagerFrame();
                    }
                } else {
                    new ManagerFrame();
                }
            }
        });
    }
    
}
