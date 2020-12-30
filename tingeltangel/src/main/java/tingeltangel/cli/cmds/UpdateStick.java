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

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import tingeltangel.cli.CliCommand;
import tingeltangel.cli.CliSwitch;
import tingeltangel.core.Repository;
import tingeltangel.core.TingStick;
import tingeltangel.core.Translator;
import tingeltangel.core.constants.TxtFile;

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
        
        
        TingStick stick = TingStick.getStick();
        
        if(stick == null) {
            System.err.println("no stick found");
            System.exit(1);
        }
        
        HashSet<Integer> _tbds = stick.getTBD();
        LinkedList<Integer> _books = stick.getBooks();
        
        _tbds.addAll(_books);
        
        HashSet<Integer> remove = new HashSet<Integer>();
        remove.addAll(_tbds);
        
        Iterator<Integer> mids = _tbds.iterator();
        while(mids.hasNext()) {
            
            int mid = mids.next();
            
            if((mid > 0) && (mid <= Translator.MAX_MID)) {
                
                // update txt in repository
                System.out.println("repository: update mid " + mid);
                
                try {
                    if(!Repository.txtExists(mid)) {
                        Repository.search(mid);
                    }
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
                        int stickVersion = stick.getBookVersion(mid);
                        if(stickVersion >= 0) {
                            if(repositoryVersion > stickVersion) {
                                System.err.println("auf den stift kopieren...");
                                stick.copyFromRepositoryToStick(mid);
                                remove.remove(mid);
                            }              
                        } else {
                            System.err.println("zugriff auf den stift " + mid + " fehlgeschlagen");
                        }

                    }
                    System.out.println("repository: update mid " + mid + " fertig");
                } catch(FileNotFoundException fnfe) {
                    System.err.println("ignoriere buch " + mid);
                }
            }
            
        }
        
        mids = remove.iterator();
        String tbd = "";
        while(mids.hasNext()) {    
            String mid = Integer.toString(mids.next());
            while(mid.length() < 5) {
                mid = "0" + mid;
            }
            mid += "\r\n";
            tbd += mid;
        }
        // write tbd to TBD.TXT
        OutputStream out = new FileOutputStream(stick.getTBDFile());
        out.write(tbd.getBytes());
        out.flush();
        out.close();
        
        System.out.println("stick update ist fertig");
        System.exit(0);
        
    }
    
    public static void main(String[] args) {
        
        try {
            new UpdateStick().execute(null);
        } catch (Exception ex) {
            Logger.getLogger(UpdateStick.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
