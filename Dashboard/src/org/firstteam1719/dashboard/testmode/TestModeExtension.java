/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.firstteam1719.dashboard.testmode;

import edu.wpi.first.smartdashboard.gui.StaticWidget;
import edu.wpi.first.smartdashboard.properties.Property;
import edu.wpi.first.wpilibj.networktablesOverride.NetworkTable;

public class TestModeExtension extends StaticWidget {

    NewJFrame buttons;
    JPanel buttonsJ;
    static NetworkTable testTable = NetworkTable.getTable("LiveWindow");
    @Override
    public void init() {
        buttonsJ = new JPanel();
        this.add(buttonsJ);
    }

    @Override
    public void propertyChanged(Property prprt) {
        
   }

    public static void setNetworkTableNumber(int number){
        testTable.putNumber("testNumber", number);
    }
}
