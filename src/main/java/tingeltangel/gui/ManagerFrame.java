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

package tingeltangel.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.Iterator;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import tingeltangel.Tingeltangel;
import tingeltangel.core.Repository;
import tingeltangel.core.Stick;
import tingeltangel.tools.Callback;

public class ManagerFrame extends JFrame {
    
    private final static Logger log = LogManager.getLogger(ManagerFrame.class);
    
    private final JPanel centerPanel = new JPanel();
    private final JLabel statusLabel = new JLabel("kein Stift gefunden");
    private boolean online = false;
    
    public ManagerFrame() {
        super(Tingeltangel.MAIN_FRAME_TITLE + Tingeltangel.MAIN_FRAME_VERSION);
        
        
        JFrame.setDefaultLookAndFeelDecorated(true);

        setBounds(
                    Tingeltangel.MAIN_FRAME_POS_X,
                    Tingeltangel.MAIN_FRAME_POS_Y,
                    Tingeltangel.MAIN_FRAME_WIDTH + getInsets().left + getInsets().right,
                    Tingeltangel.MAIN_FRAME_HEIGHT + getInsets().top + getInsets().bottom
        );
        
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        
        setContentPane(getPanel());
        
        
        Runnable task = new TimerTask() {
            @Override
            public void run() {
                try {
                    Stick stick = Stick.getStick();
                    if(online && (stick == null)) {
                        // go offline
                        online = false;
                        statusLabel.setText("keinen Stift gefunden");
                        centerPanel.removeAll();
                    } else if((!online) && (stick != null)) {
                        // go online
                        online = true;
                        goOnline();
                    }
                } catch(IOException ioe) {
                    ioe.printStackTrace();
                }
            }
        };
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(task, 3, 3, TimeUnit.SECONDS);
        
        setVisible(true);
    }

    private void goOnline() {
        statusLabel.setText("Stift gefunden");
        try {
            Stick stick = Stick.getStick();
            Iterator<Integer> ids = stick.getBooks().iterator();
            
            while(ids.hasNext()) {
                int id = ids.next();
                centerPanel.add(getBookPanel(id), PushBorderLayout.PAGE_START);
            }
            
        } catch(IOException ioe) {
            log.warn("Stick konnte nicht geöffnet werden", ioe);
            JOptionPane.showMessageDialog(this, "Stick konnte nicht geöffnet werden");
        }
    }
    
    private JPanel getBookPanel(int mid) throws IOException {
        JPanel panel = new JPanel();
        panel.add(new JLabel(Integer.toString(mid)));
        
        
        // get version from stick
        Stick stick = Stick.getStick();
        int stickVersion = stick.getBookVersion(mid);
        
        // get version from repository
        int repositoryVersion = Integer.parseInt(Repository.getBookTxt(mid).get("Version"));
        
        
        return(panel);
    }
    
    private JPanel getPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(statusLabel, BorderLayout.NORTH);
        panel.add(centerPanel, BorderLayout.CENTER);
        panel.add(getRightPanel(), BorderLayout.EAST);
        return(panel);
    }
    
    private JPanel getRightPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new PushBorderLayout());
        
        addButton(panel, "test 1", new Callback<Object>() {
            @Override
            public void callback(Object t) {
                System.out.println("test 1");
            }
        });
        addButton(panel, "test 2", new Callback<Object>() {
            @Override
            public void callback(Object t) {
                System.out.println("test 2");
            }
        });
        addButton(panel, "test 3", new Callback<Object>() {
            @Override
            public void callback(Object t) {
                System.out.println("test 3");
            }
        });
        addButton(panel, "test 4", new Callback<Object>() {
            @Override
            public void callback(Object t) {
                System.out.println("test 4");
            }
        });
        
        return(panel);
    }
    
    private void addButton(JPanel panel, String label, final Callback<Object> callback) {
        JButton button = new JButton(label);
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                callback.callback(null);
            }
        });
        panel.add(button, PushBorderLayout.PAGE_START);
    }
}
