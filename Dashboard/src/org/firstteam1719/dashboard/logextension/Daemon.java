/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.firstteam1719.dashboard.logextension;

import edu.wpi.first.wpilibj.networktablesOverride.NetworkTable;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Daemon extends Thread {

    @Override
    public void run() {
        //Initialization code may take a while to run.
        //Moved to the secondary thread to avoid hanging on the main thread
        System.out.println("Log initializing");
        NetworkTable.setClientMode();
        NetworkTable.setIPAddress("10.17.19.2");
        try {
            NetworkTable.initialize();
        } catch (IOException ex) {
            System.err.println(ex);
        }
        NetworkTable SmartDashboard = NetworkTable.getTable("SmartDashboard");
        SmartDashboard.putString("Log", "");
        while (true) {
            FileWriter writer;
            NetworkTable.getTable("SmartDashboard");
            try {
                writer = new FileWriter("robotlogs.txt", true);
                String tempMessage = SmartDashboard.getString("Log");
                System.out.println("Log added: " + tempMessage);
                writer.write(tempMessage);
                writer.close();
            } catch (FileNotFoundException | UnsupportedEncodingException ex) {
                Logger.getLogger(LogExtension.class.getName()).log(Level.SEVERE, null, ex);
                System.err.println(ex.toString());
            } catch (IOException ex) {
                Logger.getLogger(Daemon.class.getName()).log(Level.SEVERE, null, ex);
            }

            SmartDashboard.putString("Log", "");

            try {
                Thread.sleep(500);
            } catch (InterruptedException ex) {
                Logger.getLogger(Daemon.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
