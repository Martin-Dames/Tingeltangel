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

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;
import tingeltangel.core.Entry;
import tingeltangel.core.Page;
import tingeltangel.core.Tupel;
import tingeltangel.tools.FileEnvironment;

/**
 *
 * @author martin
 */
public class GfxEditFrame extends javax.swing.JInternalFrame implements EntryListener {

    private final static int SHRINK = 12; // the 12 is taken from tingeltangel.core.Codes.PNG_BLOCK_SIZE[1]
    
    private MasterFrame masterFrame;
    private final JPanel canvas;
    
    private BufferedImage img = null;
    
    private int dx = 0;
    private int dy = 0;
    private int w = 0;
    private int h = 0;
    private double scale = 0;
    
    private Entry entry = null;
    
    private final static Color[] COLORS;
    
    static {
        // generate 1000 different colors
        COLORS = new Color[1000];
        int i = 0;
        for(int r = 0; r < 10; r++) {
            for(int g = 0; g < 10; g++) {
                for(int b = 0; b < 10; b++) {
                    COLORS[i++] = new Color(r * 20 + 25, g * 20 + 25, b * 20 + 25, 0x80);
                }
            }
        }
        // shuffle
        int index;
        Color tmp;
        Random random = new Random(12345); // seed with any constant value
        for(i = COLORS.length - 1; i > 0; i--) {
            index = random.nextInt(i + 1);
            tmp = COLORS[index];
            COLORS[index] = COLORS[i];
            COLORS[i] = tmp;
        }
    }
    
    /**
     * Creates new form GfxEditFrame
     */
    public GfxEditFrame(MasterFrame frame) {
        this.masterFrame = frame;
        initComponents();
        canvas = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if(img != null) {
                    int[][] raster = masterFrame.getBook().getPages().get(pagesComboBox.getSelectedIndex()).raster;
                    g.drawImage(img, dx, dy, w, h, null);
                    // draw raster
                    for(int x = 0; x < raster.length; x++) {
                        for(int y = 0; y < raster[0].length; y++) {
                            if(raster[x][y] != 0) {
                                Color color = COLORS[(raster[x][y] - 15001) % COLORS.length];
                                if((entry != null) && (entry.getTingID() == raster[x][y])) {
                                    color = new Color(color.getRed(), color.getGreen(), color.getBlue(), 0xb0);
                                }
                                g.setColor(color);
                                g.fillRect(dx + (int)((x * SHRINK) / scale), dy + (int)((y * SHRINK) / scale), 1, 1);
                            }
                        }
                    }
                }
            }
        };
        panel.setLayout(new GridLayout(1, 1));
        panel.add(canvas);
        canvas.setSize(panel.getWidth(), panel.getHeight());
        canvas.addMouseMotionListener(new MouseMotionListener() {

            @Override
            public void mouseDragged(MouseEvent e) {
                if(entry != null) {
                    int[][] raster = masterFrame.getBook().getPages().get(pagesComboBox.getSelectedIndex()).raster;
                    int x = e.getX() - dx;
                    int y = e.getY() - dy;
                    if(x >= 0 && x < w && y >= 0 && y < h) {
                        x = (int)(x * scale) / SHRINK;
                        y = (int)(y * scale) / SHRINK;
                        circle(x, y, (Integer)brushSizeSpinner.getValue(), entry.getTingID(), raster);
                        raster[x][y] = entry.getTingID();
                        canvas.repaint();
                    }
                }
            }

            @Override
            public void mouseMoved(MouseEvent e) {
            }
        });
        
        pagesComboBox.setRenderer(new ListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Tupel<Integer, String> tupel = (Tupel<Integer, String>)value;
                if(tupel == null) {
                    return(new JLabel());
                }
                return(new JLabel((tupel.a + 1) + " - " + tupel.b));
            }
        });
        
                
        if(masterFrame.getBook().getPages().size() > 0) {
            try {
                showPage(masterFrame.getBook().getPages().get(0));
            } catch(IOException e) {
                JOptionPane.showMessageDialog(this, "Das Bild konnte nicht geladen werden");
                e.printStackTrace(System.out);
            }
        }
        
        frame.addEntryListener(this);
    }
        
    public void update() {
        // update pagesComboBox
        DefaultComboBoxModel pagesModel = (DefaultComboBoxModel)pagesComboBox.getModel();
        pagesModel.removeAllElements();
        Iterator<Page> pages = masterFrame.getBook().getPages().iterator();
        int i = 0;
        while(pages.hasNext()) {
            Page page = pages.next();
            pagesModel.addElement(new Tupel<Integer, String>(i, page.image));
            i++;
        }
    }
    
    private void circle(int x, int y, int r, int v, int[][] raster) {
        if(erasorToggleButton.isSelected()) {
            v = 0;
        }
        int rq = r * r;
        for(int ix = x - r; ix <= x + r; ix++) {
            int ixq = (ix - x) * (ix - x);
            for(int iy = y - r; iy <= y + r; iy++) {
                if(ixq + (iy - y) * (iy - y) <= rq) {
                    raster[ix][iy] = v;
                }
            }
        }
    }
    
    private void showPage(Page page) throws IOException {
        
        
        if(page.image != null) {
            File pagesDirectory = FileEnvironment.getPagesDirectory(masterFrame.getBook().getID());
        
            img = ImageIO.read(new File(pagesDirectory, page.image));
            //fit image into panel
            double panelAspect = (double)panel.getWidth() / (double)panel.getHeight();
            double imageAspect = (double)img.getWidth() / (double)img.getHeight();

            dx = 0;
            dy = 0;
            w = panel.getWidth();
            h = panel.getHeight();

            if(panelAspect > imageAspect) {
                // left and right bars
                w = (int)(h * imageAspect);
                dx = (panel.getWidth() - w) / 2;
                scale = (double)img.getHeight() / (double)h;
            } else {
                // top and bottom bars
                h = (int)(w / imageAspect);
                dy = (panel.getHeight() - h) / 2;
                scale = (double)img.getWidth() / (double)w;
            }

            if(page.raster == null) {
                File rasterFile = new File(pagesDirectory, page.image.substring(0, page.image.lastIndexOf('.')) + ".raster");
                if(rasterFile.canRead()) {
                    DataInputStream in = new DataInputStream(new FileInputStream(rasterFile));

                    int _w = in.readShort();
                    int _h = in.readShort();
                    
                    if((_w != img.getWidth() / SHRINK) || (_h != img.getHeight() / SHRINK)) {
                        in.close();
                        throw new IOException("raster size missmatch (perhaps you sould delete '" + rasterFile.getCanonicalPath() + "')");
                    }
                    
                    page.raster = new int[_w][_h];
                    
                    for(int x = 0; x < w; x++) {
                        for(int y = 0; y < h; y++) {
                            page.raster[x][y] = in.readShort();
                        }
                    }
                    
                    in.close();
                } else {
                    page.raster = new int[img.getWidth() / SHRINK][img.getHeight() / SHRINK];
                }
            }
        }
    }
    
    private void loadAndShowImage(File file) throws IOException {
        File pagesDirectory = FileEnvironment.getPagesDirectory(masterFrame.getBook().getID());
        
        // copy image
        File target = new File(pagesDirectory, file.getName());
        if(!file.equals(target)) {
        	FileEnvironment.copy(file, target);
        }
        
        
        Page page = masterFrame.getBook().getPages().get(pagesComboBox.getSelectedIndex());
        
        page.description = "";
        page.image = file.getName();
        page.raster = null;
        // delete raster file if exists
        File rasterFile = new File(pagesDirectory, file.getName().substring(0, file.getName().lastIndexOf('.')) + ".raster");
        if(rasterFile.exists()) {
            rasterFile.delete();
        }
        showPage(page);
    }
    
    private void repaintCanvas() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                canvas.repaint();
            }
        });
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pagesComboBox = new javax.swing.JComboBox();
        addPageButton = new javax.swing.JButton();
        brushSizeSpinner = new javax.swing.JSpinner();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        colorPanel = new javax.swing.JPanel();
        loadImageButton = new javax.swing.JButton();
        erasorToggleButton = new javax.swing.JToggleButton();
        panel = new javax.swing.JPanel();
        descriptionTextField = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setPreferredSize(new java.awt.Dimension(1000, 960));

        pagesComboBox.setModel(new DefaultComboBoxModel());
        pagesComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pagesComboBoxActionPerformed(evt);
            }
        });

        addPageButton.setText("hinzufügen");
        addPageButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addPageButtonActionPerformed(evt);
            }
        });

        brushSizeSpinner.setModel(new javax.swing.SpinnerNumberModel(10, 2, 100, 2));

        jLabel1.setText("Pinselgröße:");

        jLabel2.setText("Seite:");

        jLabel3.setText("Farbe:");

        colorPanel.setBackground(new java.awt.Color(153, 0, 51));

        javax.swing.GroupLayout colorPanelLayout = new javax.swing.GroupLayout(colorPanel);
        colorPanel.setLayout(colorPanelLayout);
        colorPanelLayout.setHorizontalGroup(
            colorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        colorPanelLayout.setVerticalGroup(
            colorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 21, Short.MAX_VALUE)
        );

        loadImageButton.setText("Bild laden...");
        loadImageButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadImageButtonActionPerformed(evt);
            }
        });

        erasorToggleButton.setText("Radieren");

        javax.swing.GroupLayout panelLayout = new javax.swing.GroupLayout(panel);
        panel.setLayout(panelLayout);
        panelLayout.setHorizontalGroup(
            panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 816, Short.MAX_VALUE)
        );
        panelLayout.setVerticalGroup(
            panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 660, Short.MAX_VALUE)
        );

        descriptionTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                descriptionTextFieldActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(layout.createSequentialGroup()
                            .addGap(12, 12, 12)
                            .addComponent(jLabel2)
                            .addGap(12, 12, 12)
                            .addComponent(addPageButton)
                            .addGap(0, 0, Short.MAX_VALUE))
                        .addGroup(layout.createSequentialGroup()
                            .addContainerGap()
                            .addComponent(pagesComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGroup(layout.createSequentialGroup()
                            .addContainerGap()
                            .addComponent(descriptionTextField)))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(jLabel1)
                                    .addGap(28, 28, 28)
                                    .addComponent(brushSizeSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(jLabel3)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(colorPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                            .addComponent(erasorToggleButton, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(loadImageButton, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                .addComponent(panel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(5, 5, 5)
                                .addComponent(jLabel2))
                            .addComponent(addPageButton))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(pagesComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(descriptionTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(brushSizeSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addComponent(colorPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(erasorToggleButton)
                        .addGap(18, 18, 18)
                        .addComponent(loadImageButton)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void addPageButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addPageButtonActionPerformed
        Page p = new Page();
        masterFrame.getBook().getPages().add(p);
        update();
    }//GEN-LAST:event_addPageButtonActionPerformed

    private void pagesComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pagesComboBoxActionPerformed
        System.out.println(evt.getActionCommand());
        Page p = masterFrame.getBook().getPages().get(pagesComboBox.getSelectedIndex());
        try {
            showPage(p);
            repaintCanvas();
        } catch(IOException e) {
            JOptionPane.showMessageDialog(this, "Das Bild konnte nicht geladen werden");
            e.printStackTrace(System.out);
        }
    }//GEN-LAST:event_pagesComboBoxActionPerformed

    private void loadImageButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadImageButtonActionPerformed
        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new FileNameExtensionFilter("Code Tabelle (*.png)", "png"));
        if(fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                System.out.println("loading " + fc.getSelectedFile().getCanonicalPath());
                loadAndShowImage(fc.getSelectedFile());
                System.out.println("image loaded");
                repaintCanvas();
            } catch(Exception e) {
                JOptionPane.showMessageDialog(this, "Das Bild konnte nicht geladen werden");
                e.printStackTrace(System.out);
            }
        }
    }//GEN-LAST:event_loadImageButtonActionPerformed

    private void descriptionTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_descriptionTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_descriptionTextFieldActionPerformed

 
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addPageButton;
    private javax.swing.JSpinner brushSizeSpinner;
    private javax.swing.JPanel colorPanel;
    private javax.swing.JTextField descriptionTextField;
    private javax.swing.JToggleButton erasorToggleButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JButton loadImageButton;
    private javax.swing.JComboBox pagesComboBox;
    private javax.swing.JPanel panel;
    // End of variables declaration//GEN-END:variables

    @Override
    public void entrySelected(Entry entry) {
        this.entry = entry;
        colorPanel.setBackground(COLORS[entry.getTingID() - 15001]);
        repaintCanvas();
    }

}
