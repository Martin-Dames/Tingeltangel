
package tingeltangel.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import tingeltangel.gui.MultipleChoiceDialog;
import tingeltangel.gui.StringCallback;


public class Tools {
    static final int[] E = {578, 562, 546, 530, 514, 498, 482, 466, 322, 306, 290, 274, 258, 242, 226, 210, -446, -462, -478, -494, -510, -526, -542, -558, -702, -718, -734, -750, -766, -782, -798, -814};

    static void copy(File source, File destination) throws IOException {
        //System.out.println(source.getAbsolutePath() + " -> " + destination.getAbsolutePath());
        FileChannel sourceChannel = null;
        FileChannel destChannel;
        try {
            sourceChannel = new FileInputStream(source).getChannel();
            destChannel = new FileOutputStream(destination).getChannel();
            destChannel.transferFrom(sourceChannel, 0, sourceChannel.size());
        } catch(Exception e) {
            throw new IOException(e);
        } finally {
            if(sourceChannel != null) {
                sourceChannel.close();
            }
            // destChannel.close();
        }
    }

   
    /**
     *
     * @param code Positionscode in der Indextabelle (1. Feld)
     * @param n Position in der Indextablelle (startet bei 0)
     * @return Position in der Datei
     */
    public static int getPositionInFileFromCode(int code, int n) {
        if (((code & 255) != 0) | (n < 0)) {
            return -1;
        }
        n--;
        code = code >> 8;
        int c = ((code >> 3) & 1) | (((code >> 4) & 1) << 1) | (((code >> 5) & 1) << 2) | (((code >> 7) & 1) << 3) | (((code >> 9) & 1) << 4);
        code -= n * 26 - E[c];
        return code << 8;
    }

    /**
     *
     * @param position Position in der Datei
     * @param n Position in der Indextablelle (startet bei 0)
     * @return Positionscode in der Indextabelle (1. Feld)
     */
    static int getCodeFromPositionInFile(int position, int n) {
        if (((position & 255) != 0) | (n < 0)) {
            return -1;
        }
        n--;
        int b = (position >> 8) + n * 26;
        for (int k = 0; k < E.length; k++) {
            int v = (b - E[k]) << 8;
            if (Tools.getPositionInFileFromCode(v, n + 1) == position) {
                return v;
            }
        }
        return -1;
    }
    
    
    
    private static void setBinaryPath(String propertyName, String path) {
        if(path == null) {
            Properties.setProperty(propertyName + ".path", "");
            Properties.setProperty(propertyName + ".enabled", 0);
        } else {
            Properties.setProperty(propertyName + ".path", path);
            Properties.setProperty(propertyName + ".enabled", 1);
        }
    }
        
    public static void getBinaryPath(final String propertyName, final String winExeName, final String linuxExeName, final StringCallback binaryCallback, final String name, final String question, final String installMessage) {
        
        
        if(System.getProperty("os.name").startsWith("Windows")) {
            
            if(Properties.getProperty(propertyName + ".path") != null) {
                                
                if(Properties.getPropertyAsInteger(propertyName + ".enabled") == 0) {
                    binaryCallback.callback(null);
                } else {
                
                    File f = new File(Properties.getProperty(propertyName + ".path"));
                    if(f.getName().equals(winExeName) && f.canExecute()) {
                        binaryCallback.callback(f.getAbsolutePath());
                    } else {
                        JOptionPane.showMessageDialog(null, "Fehler", name + " konnte nicht gefunden werden", JOptionPane.WARNING_MESSAGE);
                        Properties.setProperty(propertyName + ".path", null);
                        Properties.setProperty(propertyName + ".enabled", null);
                        getBinaryPath(propertyName, winExeName, linuxExeName, binaryCallback, name, question, installMessage);
                    }
                }
                
            } else {
                
                
                StringCallback callback = new StringCallback() {
                    @Override
                    public void callback(String s) {
                        if(s.equals("install")) {
                            JOptionPane.showMessageDialog(null, "Hinweis", installMessage, JOptionPane.INFORMATION_MESSAGE);
                            getBinaryPath(propertyName, winExeName, linuxExeName, binaryCallback, name, question, installMessage);
                        } else if(s.equals("path")) {
                            JFileChooser fc = new JFileChooser();
                            fc.setCurrentDirectory(new java.io.File("."));
                            fc.setDialogTitle("Installationsverzeichniss von " + name);
                            fc.setFileFilter(new FileNameExtensionFilter("windows Executable (*.exe)", "exe"));
                            if(fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                                File f = fc.getSelectedFile();
                                if(f.getName().equals(winExeName) && f.canExecute()) {
                                    setBinaryPath(propertyName, f.getAbsolutePath());
                                    Properties.setProperty(propertyName + ".path", f.getAbsolutePath());
                                    Properties.setProperty(propertyName + ".enabled", 1);
                                    binaryCallback.callback(f.getAbsolutePath());
                                } else {
                                    JOptionPane.showMessageDialog(null, "Fehler", name + " konnte nicht gefunden werden", JOptionPane.WARNING_MESSAGE);
                                    getBinaryPath(propertyName, winExeName, linuxExeName, binaryCallback, name, question, installMessage);
                                }
                            } else {
                                getBinaryPath(propertyName, winExeName, linuxExeName, binaryCallback, name, question, installMessage);
                            }
                        } else if(s.equals("disable")) {
                            Properties.setProperty(propertyName + ".path", null);
                            Properties.setProperty(propertyName + ".enabled", null);
                            binaryCallback.callback(null);
                        }
                    }
                };
                
                String[] options = {
                    "Ich möchte " + name + " installieren",
                    name + " ist installiert. Ich möchte den Pfad zu " + name + " angeben.",
                    "Ich verzichte auf " + name + "."
                };
                
                String[] actions = {"install", "path", "disable"};
                
                MultipleChoiceDialog.show(null, name + " nicht gefunden", question, "OK", options, actions, 0, callback);
                
            }
            
        } else {
            // linux
            String[] path = System.getenv("PATH").split(":");
            for(int i = 0; i < path.length; i++) {
                File file = new File(new File(path[i]), linuxExeName);
                if(file.exists() && file.canExecute()) {
                    binaryCallback.callback(file.getAbsolutePath());
                    return;
                }
            }
            
            JOptionPane.showMessageDialog(null, "Warnung", name + " konnte nicht gefunden werden", JOptionPane.WARNING_MESSAGE);
            binaryCallback.callback(null);
            
        }
        
        
        
    }
    
    
}
