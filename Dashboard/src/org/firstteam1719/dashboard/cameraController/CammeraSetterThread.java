package org.firstteam1719.dashboard.cameraController;

import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.tables.TableKeyNotDefinedException;

public class CammeraSetterThread implements Runnable {

    public static NetworkTable SmartDashboard = NetworkTable.getTable("SmartDashboard");

    public CammeraSetterThread() {
    }

    @Override
    public void run() {
        while (true) {
            try {
                boolean isAtonomus = SmartDashboard.getBoolean("isAtonomus");
                if (isAtonomus) {
                    HttpWraper.send("http://10.17.19.11/sm/sm.srv?root_ImageSource_I0_Sensor_Contrast=100&action=modify");
                    HttpWraper.send("http://10.17.19.11/sm/sm.srv?root_ImageSource_I0_Sensor_ExposureValue=0&action=modify");
                } else {
                    HttpWraper.send("http://10.17.19.11/sm/sm.srv?root_ImageSource_I0_Sensor_Contrast=50&action=modify");
                    HttpWraper.send("http://10.17.19.11/sm/sm.srv?root_ImageSource_I0_Sensor_ExposureValue=50&action=modify");
                }
            } catch (TableKeyNotDefinedException e) {
                System.out.println("Caught exception: " + e);
            }

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                System.out.println("Caught exception: " + e);
            }
        }
    }
}
