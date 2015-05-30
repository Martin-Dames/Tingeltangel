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

import java.util.HashMap;
import java.util.Map;
import tingeltangel.cli.CliCommand;
import tingeltangel.cli.CliSwitch;
import tingeltangel.core.Repository;

/**
 *
 * @author mdames
 */
public class BooksUpdate extends CliCommand {

    @Override
    public String getName() {
        return("books-update");
    }

    @Override
    public String getDescription() {
        return("Aktualisiert die Buchliste");
    }

    @Override
    public Map<String, CliSwitch> getSwitches() {
        return(new HashMap<String, CliSwitch>());
    }

    @Override
    public void execute(Map<String, String> args) throws Exception {
        Repository.update(null);
    }
    
}
