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

package tingeltangel.tools;

import javax.swing.JFrame;
import javax.swing.SwingWorker;

/**
 *
 * @author martin
 */
public abstract class Progress {
    
    public abstract void action(ProgressDialog progressDialog);
    
    public Progress(final JFrame frame, String title) {
        final ProgressDialog progressDialog = new ProgressDialog(frame, title);
        new SwingWorker() {
            @Override
            protected Object doInBackground() {
                action(progressDialog);
                progressDialog.done();
                frame.setEnabled(true);
                return(null);
            }
        }.execute();
    }
}
