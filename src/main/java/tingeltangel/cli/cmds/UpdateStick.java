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
package tingeltangel.cli.cmds;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import tingeltangel.cli.CliCommand;
import tingeltangel.cli.CliSwitch;
import tingeltangel.core.Book;
import tingeltangel.core.Repository;
import tingeltangel.core.Stick;
import tingeltangel.core.constants.TxtFile;
import tingeltangel.tools.FileEnvironment;

public class UpdateStick extends CliCommand {

    
    @Override
    public String getName() {
        return("update-stick");
    }

    @Override
    public String getDescription() {
        return("aktualisiert einen Ting-Stift");
    }

    @Override
    public Map<String, CliSwitch> getSwitches() {
        return(new HashMap<String, CliSwitch>());
    }

    @Override
    public void execute(Map<String, String> args) throws Exception {
        
        
        File stick = Stick.getStickPath();
        
        if(stick == null) {
            System.err.println("no stick found");
            System.exit(1);
        }
        
        Iterator<Integer> mids = Stick.getBooks(stick).iterator();
        while(mids.hasNext()) {
            
            int mid = mids.next();
            
            if((mid > 0) && (mid <= 10000)) {
                
                // update txt in repository
                Repository.update(mid, null);
                
                // get repository version
                int repositoryVersion = -1;
                try {
                    repositoryVersion = Integer.parseInt(Repository.getBookTxt(mid).get(TxtFile.KEY_VERSION));
                } catch(Exception e) {
                    System.err.println("update von " + mid + " fehlgeschlagen:");
                    e.printStackTrace(System.err);
                }
                if(repositoryVersion >= 0) {
                    // get stick version
                    int stickVersion = Stick.getBookVersion(stick, mid);
                    if(stickVersion >= 0) {
                        if(repositoryVersion > stickVersion) {
                            // Stick.downloadOfficial(stick, mid);sd
                        }              
                    } else {
                        System.err.println("zugriff auf den stift " + mid + " fehlgeschlagen");
                    }
                    
                }
                
            }
            
        }
        
        
    }
    
}
