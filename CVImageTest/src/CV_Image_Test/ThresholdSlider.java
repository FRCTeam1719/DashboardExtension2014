/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package CV_Image_Test;

import java.awt.Container;
import java.io.File;
import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import static javax.swing.GroupLayout.Alignment.LEADING;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JSlider;
import javax.swing.JTextField;
import static javax.swing.LayoutStyle.ComponentPlacement.UNRELATED;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *
 * @author wjb
 */
public class ThresholdSlider extends JFrame implements ChangeListener {

    private JFrame frame;
    private JButton updateButton;
    private JButton getImageFileButton;
    private Container panel;
    private boolean sliderStateChanged;
    private JFileChooser fc;

    public static File file;
    public static boolean newFileSelected;
    public static boolean imageUpdate;
    public static JSlider hueLowerSlider;
    public static JSlider hueUpperSlider;
    public static JSlider satSlider;
    public static JSlider valSlider;
    public static JTextField hueLowerText;
    public static JTextField hueUpperText;
    public static JTextField satText;
    public static JTextField valText;

    ThresholdSlider() {
        frame = new JFrame("Threshold Settings");
        frame.setSize(400, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        panel = frame.getContentPane();
        GroupLayout layout = new GroupLayout(panel);
        panel.setLayout(layout);

        fc = new JFileChooser(System.getProperty("user.home") + "\\My Documents" + "\\NetBeansProjects");
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        file = null;

        hueLowerSlider = new JSlider(0, 255, 27);
        hueLowerSlider.setBorder(BorderFactory.createTitledBorder("Hue Lower Threshold Value"));
        hueLowerSlider.setMajorTickSpacing(25);
        hueLowerSlider.setMinorTickSpacing(10);
        hueLowerSlider.setPaintTicks(true);
        hueLowerSlider.setPaintLabels(true);
        hueLowerSlider.addChangeListener(this);

        hueUpperSlider = new JSlider(0, 255, 181);
        hueUpperSlider.setBorder(BorderFactory.createTitledBorder("Hue Upper Threshold Value"));
        hueUpperSlider.setMajorTickSpacing(25);
        hueUpperSlider.setMinorTickSpacing(10);
        hueUpperSlider.setPaintTicks(true);
        hueUpperSlider.setPaintLabels(true);
        hueUpperSlider.addChangeListener(this);

        satSlider = new JSlider(0, 255, 220);
        satSlider.setBorder(BorderFactory.createTitledBorder("Saturation Threshold Value"));
        satSlider.setMajorTickSpacing(25);
        satSlider.setMinorTickSpacing(5);
        satSlider.setPaintTicks(true);
        satSlider.setPaintLabels(true);
        satSlider.addChangeListener(this);

        valSlider = new JSlider(0, 255, 195);
        valSlider.setBorder(BorderFactory.createTitledBorder("Intensity Threshold Value"));
        valSlider.setMajorTickSpacing(25);
        valSlider.setMinorTickSpacing(5);
        valSlider.setPaintTicks(true);
        valSlider.setPaintLabels(true);
        valSlider.addChangeListener(this);

        hueLowerText = new JTextField(Integer.toString(hueLowerSlider.getValue()));
        hueLowerText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                hueLowerTextActionPerformed(evt);
            }
        });
        hueUpperText = new JTextField(Integer.toString(hueUpperSlider.getValue()));
        hueUpperText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                hueUpperTextActionPerformed(evt);
            }
        });
        valText = new JTextField(Integer.toString(valSlider.getValue()));
        valText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                valTextActionPerformed(evt);
            }
        });
        satText = new JTextField(Integer.toString(satSlider.getValue()));
        satText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                satTextActionPerformed(evt);
            }
        });

        updateButton = new javax.swing.JButton();
        updateButton.setText("Update");
        updateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updateButtonActionPerformed(evt);
            }
        });

        getImageFileButton = new javax.swing.JButton();
        getImageFileButton.setText("Get Image File");
        getImageFileButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                getImageFileButtonActionPerformed(evt);
            }
        });

        layout.setHorizontalGroup(layout.createParallelGroup(LEADING)
                .addGroup(layout.createSequentialGroup()
                        .addComponent(hueLowerSlider, javax.swing.GroupLayout.PREFERRED_SIZE, 350, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(hueLowerText, 25, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(layout.createSequentialGroup()
                        .addComponent(hueUpperSlider, javax.swing.GroupLayout.PREFERRED_SIZE, 350, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(hueUpperText, 25, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(layout.createSequentialGroup()
                        .addComponent(satSlider, javax.swing.GroupLayout.PREFERRED_SIZE, 350, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(satText, 25, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(layout.createSequentialGroup()
                        .addComponent(valSlider, javax.swing.GroupLayout.PREFERRED_SIZE, 350, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(valText, 25, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(layout.createSequentialGroup()
                        .addGap(30)
                        .addComponent(updateButton)
                        .addPreferredGap(UNRELATED)
                        .addComponent(getImageFileButton))
        );

        layout.setVerticalGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(hueLowerSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(hueLowerText, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(hueUpperSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(hueUpperText, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(satSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(satText, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(valSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(valText, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(20)
                .addGroup(layout.createParallelGroup()
                        .addComponent(updateButton)
                        .addComponent(getImageFileButton))
                .addContainerGap()
        );
        frame.setVisible(true);

        sliderStateChanged = false;
    }
    
    @Override
    public void setVisible(boolean b){
        super.setVisible(b);
        frame.setVisible(b);
    }
    
    private void hueLowerTextActionPerformed(java.awt.event.ActionEvent evt) {
        hueLowerSlider.setValue(Integer.valueOf(hueLowerText.getText()));
    }

    private void hueUpperTextActionPerformed(java.awt.event.ActionEvent evt) {
        hueUpperSlider.setValue(Integer.valueOf(hueUpperText.getText()));
    }

    private void satTextActionPerformed(java.awt.event.ActionEvent evt) {
        satSlider.setValue(Integer.valueOf(satText.getText()));
    }

    private void valTextActionPerformed(java.awt.event.ActionEvent evt) {
        valSlider.setValue(Integer.valueOf(valText.getText()));
    }

    private void updateButtonActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
        imageUpdate = true;
    }

    private void getImageFileButtonActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
        int fileSelected = fc.showOpenDialog(ThresholdSlider.this); //Pops up Open dialog window
        if (fileSelected == JFileChooser.APPROVE_OPTION) {
            file = fc.getSelectedFile();
            newFileSelected = true;
            System.out.println("Selected File is: " + file.getPath());
        }
    }

    public void stateChanged(ChangeEvent evt) {
        JSlider slider = (JSlider) evt.getSource();
        sliderStateChanged = true;
        if (!slider.getValueIsAdjusting()) {
            if (slider == hueLowerSlider) {
                if (hueLowerSlider.getValue() > hueUpperSlider.getValue()) {
                    hueUpperSlider.setValue(hueLowerSlider.getValue());
                    System.out.println("huelower = " + slider.getValue());
                }
                hueLowerText.setText(Integer.toString(hueLowerSlider.getValue()));
            }
            if (slider == hueUpperSlider) {
                if (hueUpperSlider.getValue() < hueLowerSlider.getValue()) {
                    hueLowerSlider.setValue(hueUpperSlider.getValue());
                    System.out.println("hueUpper = " + slider.getValue());
                }
                hueUpperText.setText(Integer.toString(hueUpperSlider.getValue()));
            }
            if (slider == satSlider) {
                System.out.println("sat = " + slider.getValue());
                satText.setText(Integer.toString(satSlider.getValue()));
            }
            if (slider == valSlider) {
                System.out.println("val = " + slider.getValue());
            }
            valText.setText(Integer.toString(valSlider.getValue()));
            imageUpdate = true;
        }
    }

    public void reset_sliderStateChanged() {
        sliderStateChanged = false;
    }

    public boolean get_sliderStateChanged() {
        return sliderStateChanged;
    }

    public static void reset_imageUpdate() {
        imageUpdate = false;
    }

    public static boolean get_imageUpdate() {
        return imageUpdate;
    }

    public static File getFile() {
        //       System.out.println("File is: " + file.getPath());
        return file;
    }

    public static boolean fileSelected() {
        return newFileSelected;
    }

    public static void clearFileSelected() {
        newFileSelected = false;
    }
}
