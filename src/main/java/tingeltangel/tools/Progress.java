/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tingeltangel.tools;

import javax.swing.JFrame;
import javax.swing.SwingWorker;
import tingeltangel.gui.MasterFrame;

/**
 *
 * @author martin
 */
public abstract class Progress {
    
    public abstract void action(ProgressDialog progressDialog);
    
    public Progress(JFrame frame, String title) {
        final ProgressDialog progressDialog = new ProgressDialog(frame, title);
        new SwingWorker() {
            @Override
            protected Object doInBackground() {
                action(progressDialog);
                progressDialog.done();
                return(null);
            }
        }.execute();
    }
}
