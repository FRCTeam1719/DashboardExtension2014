package org.firstteam1719.dashboard.cameraController;

import edu.wpi.first.smartdashboard.gui.StaticWidget;
import edu.wpi.first.smartdashboard.properties.Property;
import java.awt.Color;
import java.awt.Graphics;

public class CammeraSetter extends StaticWidget {

    @Override
    public void init() {
        System.out.println("CammeraSetter Initializing");
        new Thread(new CammeraSetterThread()).start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, 200, 200);
    }

    @Override
    public void propertyChanged(Property prprt) {
    }
}
