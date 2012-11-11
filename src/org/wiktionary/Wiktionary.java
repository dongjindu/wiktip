/*
 * @Author Yetaai
 * yetaai@gmail.com
 * This software piece is apache license. But the contents generated is governed by wiktionary.org policy.
 */
package org.wiktionary;

import java.awt.Container;
import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

public class Wiktionary {
    public static void main(String[] args) throws Exception {
        try {
            EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    createAndShowGUI();
                }
            });
        } catch(Exception e) {
                e.printStackTrace();
        } finally {
//            DAO.closeConnection(); //Cann't do this! as this will happen before the end of the whole program!
            //Release resources here.
        }
    }
    public static void createAndShowGUI() {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        MWindow m = new MWindow();
        frame.setTitle(m.getAppName());
//        frame.setBounds(10, 10, 630, 470); //Generally used for JComponents position and size when no layout is used.we can set layout manager to null.
        Container cp = frame.getContentPane();
        m.makeUI(cp);
        frame.setSize(640, 620);
        //frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        //frame.pack();
        frame.setVisible(true);
    }
}

