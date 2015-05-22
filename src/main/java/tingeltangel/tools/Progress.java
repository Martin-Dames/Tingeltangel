/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
