/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tingeltangel.gui;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import tingeltangel.tools.Binary;

/**
 *
 * @author mdames
 */
public class BinaryLocationsDialog extends javax.swing.JDialog {

    /**
     * Creates new form BinaryLocationsDialog
     */
    public BinaryLocationsDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        init();
    }
    
    private void init() {
        try {
            File mpg123 = Binary.getBinary(Binary.MPG123);
            if(mpg123 != null && mpg123.canExecute()) {
                mpg123Label.setText(mpg123.getCanonicalPath());
                mpg123Label.setBackground(new Color(240, 240, 240));
            } else {
                mpg123Label.setText(" ");
                mpg123Label.setBackground(new Color(255, 100, 100));
            }
            File espeak = Binary.getBinary(Binary.ESPEAK);
            if(espeak != null && espeak.canExecute()) {
                espeakLabel.setText(espeak.getCanonicalPath());
                espeakLabel.setBackground(new Color(240, 240, 240));
            } else {
                espeakLabel.setText(" ");
                espeakLabel.setBackground(new Color(255, 100, 100));
            }
            File lame = Binary.getBinary(Binary.LAME);
            if(lame != null && lame.canExecute()) {
                lameLabel.setText(lame.getCanonicalPath());
                lameLabel.setBackground(new Color(240, 240, 240));
            } else {
                lameLabel.setText(" ");
                lameLabel.setBackground(new Color(255, 100, 100));
            }
            File avconv = Binary.getBinary(Binary.AVCONV);
            if(avconv != null && avconv.canExecute()) {
                avconvLabel.setText(avconv.getCanonicalPath());
                avconvLabel.setBackground(new Color(240, 240, 240));
            } else {
                avconvLabel.setText(" ");
                avconvLabel.setBackground(new Color(255, 100, 100));
            }
        } catch(IOException ioe) {
            throw new Error(ioe);
        }
        pack();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        mpg123Label = new javax.swing.JLabel();
        editMpg123Button = new javax.swing.JButton();
        espeakLabel = new javax.swing.JLabel();
        editEspeakButton = new javax.swing.JButton();
        lameLabel = new javax.swing.JLabel();
        editLameButton = new javax.swing.JButton();
        editAvconvButton = new javax.swing.JButton();
        avconvLabel = new javax.swing.JLabel();
        okButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Konfiguration externer Binärdateien");

        jLabel1.setText("mpg123:");

        jLabel2.setText("Audiowiedergabe:");

        jLabel3.setText("Text To Speech:");

        jLabel4.setText("eSpeak:");

        jLabel5.setText("lame:");

        jLabel6.setText("yaml-Import:");

        jLabel7.setText("avconv");

        mpg123Label.setText("jLabel8");
        mpg123Label.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        editMpg123Button.setText("...");
        editMpg123Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editMpg123ButtonActionPerformed(evt);
            }
        });

        espeakLabel.setText("jLabel9");
        espeakLabel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        editEspeakButton.setText("...");
        editEspeakButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editEspeakButtonActionPerformed(evt);
            }
        });

        lameLabel.setText("jLabel10");
        lameLabel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        editLameButton.setText("...");
        editLameButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editLameButtonActionPerformed(evt);
            }
        });

        editAvconvButton.setText("...");
        editAvconvButton.setToolTipText("");
        editAvconvButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editAvconvButtonActionPerformed(evt);
            }
        });

        avconvLabel.setText("jLabel11");
        avconvLabel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        okButton.setText("ok");
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3)
                            .addComponent(jLabel6))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel4)
                            .addComponent(jLabel5)
                            .addComponent(jLabel7))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(avconvLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 244, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(editAvconvButton))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(espeakLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(editEspeakButton))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(lameLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(editLameButton))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(mpg123Label, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(editMpg123Button))))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(okButton)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(mpg123Label)
                    .addComponent(editMpg123Button))
                .addGap(18, 18, 18)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(espeakLabel)
                    .addComponent(editEspeakButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(lameLabel)
                    .addComponent(editLameButton))
                .addGap(18, 18, 18)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(editAvconvButton)
                    .addComponent(avconvLabel))
                .addGap(18, 18, 18)
                .addComponent(okButton)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void editLameButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editLameButtonActionPerformed
        JFileChooser fc = new JFileChooser();
        if(fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File f = fc.getSelectedFile();
            if(f.canExecute()) {
                Binary.setBinary(Binary.LAME, f);
                init();
            }
        }
    }//GEN-LAST:event_editLameButtonActionPerformed

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
        setVisible(false);
    }//GEN-LAST:event_okButtonActionPerformed

    private void editMpg123ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editMpg123ButtonActionPerformed
        JFileChooser fc = new JFileChooser();
        if(fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File f = fc.getSelectedFile();
            if(f.canExecute()) {
                Binary.setBinary(Binary.MPG123, f);
                init();
            }
        }
    }//GEN-LAST:event_editMpg123ButtonActionPerformed

    private void editEspeakButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editEspeakButtonActionPerformed
        JFileChooser fc = new JFileChooser();
        if(fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File f = fc.getSelectedFile();
            if(f.canExecute()) {
                Binary.setBinary(Binary.ESPEAK, f);
                init();
            }
        }
    }//GEN-LAST:event_editEspeakButtonActionPerformed

    private void editAvconvButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editAvconvButtonActionPerformed
        JFileChooser fc = new JFileChooser();
        if(fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File f = fc.getSelectedFile();
            if(f.canExecute()) {
                Binary.setBinary(Binary.AVCONV, f);
                init();
            }
        }
    }//GEN-LAST:event_editAvconvButtonActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(BinaryLocationsDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(BinaryLocationsDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(BinaryLocationsDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(BinaryLocationsDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                BinaryLocationsDialog dialog = new BinaryLocationsDialog(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel avconvLabel;
    private javax.swing.JButton editAvconvButton;
    private javax.swing.JButton editEspeakButton;
    private javax.swing.JButton editLameButton;
    private javax.swing.JButton editMpg123Button;
    private javax.swing.JLabel espeakLabel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel lameLabel;
    private javax.swing.JLabel mpg123Label;
    private javax.swing.JButton okButton;
    // End of variables declaration//GEN-END:variables
}
