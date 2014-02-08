/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.firstteam1719.dashboard.cameraController;

import edu.wpi.first.smartdashboard.gui.StaticWidget;
import edu.wpi.first.smartdashboard.properties.Property;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

/**
 *
 * @author Chance
 */
public class CammeraSetter extends StaticWidget {

    @Override
    public void init() {
        System.out.println("runnning init");
        new Thread(new CammeraSetterThread()).start();
    }

    @Override
    public void propertyChanged(Property prprt) {
    }

    @Override
    protected void paintComponent(Graphics g) {
        Dimension size = getSize();

        g.setColor(Color.BLACK);
        g.fillRect(0, 0, 200, 200);


    }
}
