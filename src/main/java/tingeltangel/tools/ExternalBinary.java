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

import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import tingeltangel.core.Properties;
import tingeltangel.gui.MultipleChoiceDialog;

/**
 *
 * @author martin
 */
public class ExternalBinary {

    public static void getBinaryPath(final String propertyName, final String winExeName, final String linuxExeName, final Callback<String> binaryCallback, final String name, final String question, final String installMessage) {
        if (System.getProperty("os.name").startsWith("Windows")) {
            if (Properties.getProperty(propertyName + Properties._PATH) != null) {
                if (Properties.getPropertyAsInteger(propertyName + Properties._ENABLED) == 0) {
                    binaryCallback.callback(null);
                } else {
                    File f = new File(Properties.getProperty(propertyName + Properties._PATH));
                    if (f.getName().equals(winExeName) && f.canExecute()) {
                        binaryCallback.callback(f.getAbsolutePath());
                    } else {
                        JOptionPane.showMessageDialog(null, name + " konnte nicht gefunden werden", "Fehler", JOptionPane.WARNING_MESSAGE);
                        Properties.setProperty(propertyName + Properties._PATH, null);
                        Properties.setProperty(propertyName + Properties._ENABLED, null);
                        getBinaryPath(propertyName, winExeName, linuxExeName, binaryCallback, name, question, installMessage);
                    }
                }
            } else {
                Callback<String> callback = new Callback<String>() {


                    @Override
                    public void callback(String s) {
                        if (s.equals("install")) {
                            JOptionPane.showMessageDialog(null, installMessage, "Hinweis", JOptionPane.INFORMATION_MESSAGE);
                            getBinaryPath(propertyName, winExeName, linuxExeName, binaryCallback, name, question, installMessage);
                        } else if (s.equals("path")) {
                            JFileChooser fc = new JFileChooser();
                            fc.setCurrentDirectory(new File("."));
                            fc.setDialogTitle("Installationsverzeichniss von " + name);
                            fc.setFileFilter(new FileNameExtensionFilter("windows Executable (*.exe)", "exe"));
                            if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                                File f = fc.getSelectedFile();
                                if (f.getName().equals(winExeName) && f.canExecute()) {
                                    setBinaryPath(propertyName, f.getAbsolutePath());
                                    binaryCallback.callback(f.getAbsolutePath());
                                } else {
                                    JOptionPane.showMessageDialog(null, name + " konnte nicht gefunden werden", "Fehler", JOptionPane.WARNING_MESSAGE);
                                    getBinaryPath(propertyName, winExeName, linuxExeName, binaryCallback, name, question, installMessage);
                                }
                            } else {
                                getBinaryPath(propertyName, winExeName, linuxExeName, binaryCallback, name, question, installMessage);
                            }
                        } else if (s.equals("disable")) {
                            setBinaryPath(propertyName, null);
                            binaryCallback.callback(null);
                        }
                    }
                };
                String[] options = {"Ich m\u00f6chte " + name + " installieren", name + " ist installiert. Ich m\u00f6chte den Pfad zu " + name + " angeben.", "Ich verzichte auf " + name + "."};
                String[] actions = {"install", "path", "disable"};
                MultipleChoiceDialog.show(null, name + " nicht gefunden", question, "OK", options, actions, 0, callback);
            }
        } else {
            String[] path = System.getenv("PATH").split(":");
            for (int i = 0; i < path.length; i++) {
                File file = new File(new File(path[i]), linuxExeName);
                if (file.exists() && file.canExecute()) {
                    binaryCallback.callback(file.getAbsolutePath());
                    return;
                }
            }
            JOptionPane.showMessageDialog(null, "Warnung", name + " konnte nicht gefunden werden", JOptionPane.WARNING_MESSAGE);
            binaryCallback.callback(null);
        }
    }

    private static void setBinaryPath(String propertyName, String path) {
        if (path == null) {
            Properties.setProperty(propertyName + Properties._PATH, "");
            Properties.setProperty(propertyName + Properties._ENABLED, 0);
        } else {
            Properties.setProperty(propertyName + Properties._PATH, path);
            Properties.setProperty(propertyName + Properties._ENABLED, 1);
        }
    }
    
}
