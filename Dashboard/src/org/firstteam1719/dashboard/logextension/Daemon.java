/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.firstteam1719.dashboard.logextension;

import edu.wpi.first.wpilibj.networktablesOverride.NetworkTable;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Zach
 */
public class Daemon extends Thread {

    
    
    @Override
    public void run() {
        while (true) {

            FileWriter writer=null;
            NetworkTable SmartDashboard = NetworkTable.getTable("SmartDashboard");
            
            try {
                writer = new FileWriter("robotlogs.txt", true);
                
                String tempMessage = SmartDashboard.getString("Log");
                
                writer.write(tempMessage);
                
                System.out.println("Log added: "+tempMessage);
                writer.close();
            } catch ( FileNotFoundException | UnsupportedEncodingException ex) {
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
