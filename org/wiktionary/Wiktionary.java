/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.wiktionary;


import java.awt.EventQueue;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.Future;
import java.util.Properties;
import javax.swing.JFrame;
import javax.swing.WindowConstants;
import javax.swing.JOptionPane;


public class Wiktionary {

    public static void main(String[] args) throws Exception {
            if (args.length == 3) {
                Resources.IPADDRESS = args[0];
                Resources.PORT = args[1];
                Resources.DBNAME = args[2];
                Resources.DATABASE_ADDRESS = Resources.DRIVER_NAME + "://" +Resources.IPADDRESS + ":" + Resources.PORT + "/" + Resources.DBNAME 
                    + "?useUnicode=yes&characterEncoding=UTF-8";  
            }
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                createAndShowGUI();
            }
        });
    }
    public static void createAndShowGUI() {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setTitle(JOptionPane.showInputDialog(null, "Name your dictionary maker:"));
        frame.getContentPane().add(new TableProgressBar().makeUI());
        frame.setSize(640, 480);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}

