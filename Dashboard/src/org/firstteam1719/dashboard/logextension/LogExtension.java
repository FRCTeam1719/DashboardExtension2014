/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.firstteam1719.dashboard.logextension;

import edu.wpi.first.smartdashboard.gui.StaticWidget;
import edu.wpi.first.smartdashboard.properties.Property;
import edu.wpi.first.wpilibj.networktablesOverride.NetworkTable;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.io.IOException;

public class LogExtension extends StaticWidget {

    @Override
    public void init() {
        Daemon runner = new Daemon();
        runner.start();
    }

    @Override
    public void propertyChanged(Property prprt) {
    }

    @Override
    protected void paintComponent(Graphics g) {
        Dimension size = getSize();

        g.setColor(Color.BLACK);
        g.drawString("Logging", 0, 0);
        g.fillRect(0, 0, size.width, size.height);
    }
}
