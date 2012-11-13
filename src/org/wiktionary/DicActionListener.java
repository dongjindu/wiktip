/**
 * @author yetaai yetaai@gmail.com This software piece is apache license. But
 * the contents generated is governed by wiktionary.org policy.
 */
package org.wiktionary;

import java.nio.Buffer;
import java.io.*;
import java.nio.charset.Charset;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.Exception;
import java.sql.ResultSet;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.JComboBox;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.sql.SQLException;
import java.sql.SQLDataException;
import java.util.logging.Level;
import java.util.logging.Logger;
//import java.a
public class DicActionListener implements ActionListener {

    private HashMap<String, Object> hm;
    private static final Integer pslock = 1;

    public DicActionListener(HashMap h) {
        hm = h;
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        //throw new UnsupportedOperationException("Not supported yet.");
/*
         * if (ae.getSource() instanceof JButton) { System.out.println("Command"
         * + ((JButton) ae.getSource()).getActionCommand()); }
         */
        if (ae.getActionCommand().equals("Save")) {
            saveprop(ae);
        } else if (ae.getActionCommand().equals("Exit")) {
            //(JFrame) (((JComponent) ae.getSource()).getRootPane().getParent())
            DAO.closeConnection();
            System.exit(0);
        } else if (ae.getActionCommand().equals("Save and Exit")) {
            saveprop(ae);
            DAO.closeConnection();
            System.exit(0);
        } else if (ae.getActionCommand().equals("Run")) {
            genDict();
        }
    }

    private void saveprop(ActionEvent ae) {
        for (Map.Entry entry : hm.entrySet()) {
            if (entry.getValue() instanceof JTextField) {
                System.out.println(entry.getKey() + ":" + ((JTextField) entry.getValue()).getText());
                Res.getProp().setProperty((String) (entry.getKey()), ((JTextField) entry.getValue()).getText());
            } else if (entry.getValue() instanceof JComboBox) {
                System.out.println(entry.getKey() + ":" + ((JComboBox) entry.getValue()).getSelectedItem().toString());
                Res.getProp().setProperty((String) (entry.getKey()), ((JComboBox) entry.getValue()).getSelectedItem().toString());
            } else {
                System.out.println(entry.getKey() + ":" + entry.getValue().toString());
            }
        }
        try {
            Res.getProp().save();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void genDict() {
        //throw new UnsupportedOperationException("Not yet implemented");
        EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                DAO dao = new DAO();
                ResultSet rs = null;
                try {
                    initdb();
                    /*
                     * try { FileInputStream file = new FileInputStream((String)
                     * hm.get("wordlist")); BufferedReader reader = new
                     * BufferedReader(new InputStreamReader(file)); String line =
                     * null; while ((line=reader.readLine()) != null) {
                     *
                     * }
                     * } catch (IOException e) { e.printStackTrace(); }
                     */
                } catch (SQLException ex) {
                    Logger.getLogger(DicActionListener.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    private void initdb() throws SQLException {
        ResultSet rs;
        DAO dao = new DAO();
        //dao.query("select TABLE_NAME from INFORMATION_SCHEMA.TABLES"); // WHERE TABLE_NAME = ?");
        try {
            dao.query("select word from voctxt limit 1");
            //dao.setString(1, "a%");
            rs = dao.executeQuery();
            if ( rs.getRow() == 0 ){ 
                System.err.println("no rows in resultset!");
                createdb();
            }
        } catch (DAOException daoe) {
            System.err.println("Exception of no vocabulary table caught!");
            createdb();
        }
    }
    private void createdb() throws SQLException {
        try {
            DAO dao = new DAO();
            dao.update("drop table voctxt if exists");
            dao.executeUpdate();
            System.out.println("Looks like fresh run. Initializing whole local database!");
            dao.update("create text table voctxt (word varchar(50))");
            dao.executeUpdate();
            dao.update("set table voctxt source off");
            dao.executeUpdate();
//            System.err.println("set table voctxt source \"" + ((JTextField) hm.get("wordlist")).getText() + ";encoding=UTF-8\"");//encoding=UTF-8\"");
            dao.update("set table voctxt source \"" + ((JTextField) hm.get("wordlist")).getText() + ";encoding=UTF-8\"");
//        dao.setString(1, ((JTextField) hm.get("wordlist")).getText() + ";encoding=UTF-8");
            dao.executeUpdate();
            dao.query("drop table voc if exists");
            dao.executeUpdate();
            dao.query("create table voc (word varchar(50), dragged boolean default false, "
                    + "suggested boolean default false, over10000 boolean default false, docnull boolean default false, m1count int default 0, mw text default ?, primary key (word))");
            dao.executeUpdate();
            dao.query("");
        } catch (DAOException sqle) {
            sqle.printStackTrace();
        }
    }
}