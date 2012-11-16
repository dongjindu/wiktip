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
import javax.swing.*;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.DefaultTableModel;

import org.lobobrowser.html.parser.*;
import org.lobobrowser.html.test.*;
import org.lobobrowser.html.*;
import org.w3c.dom.*;



//import java.a
public class DicActionListener implements ActionListener {

    private HashMap<String, Object> hm;
    private static final Integer pslock = 1;
    private Integer processed = 0;

    private ResultSet[] mrs = new ResultSet[5];
    private int[] m = new int[5];
    private int[] p = {0,0,0,0,0};
    private ResultSet rs;
    private DAO dao = new DAO();

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
            createdir();
        } else if (ae.getActionCommand().equals("Exit")) {
            //(JFrame) (((JComponent) ae.getSource()).getRootPane().getParent())
            DAO dao = new DAO();
            try {
                dao.update("shutdown");
                dao.executeUpdate();
            } catch (DAOException ex) {
                Logger.getLogger(DicActionListener.class.getName()).log(Level.SEVERE, null, ex);
            }
            DAO.closeConnection();
            System.exit(0);
        } else if (ae.getActionCommand().equals("Save and Exit")) {
            saveprop(ae);
            createdir();
            DAO dao = new DAO();
            try {
                dao.update("shutdown");
                dao.executeUpdate();
            } catch (DAOException ex) {
                Logger.getLogger(DicActionListener.class.getName()).log(Level.SEVERE, null, ex);
            }
            DAO.closeConnection();
            System.exit(0);
        } else if (ae.getActionCommand().equals("Run")) {
            createdir();
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
                try {
                    Integer numberofwords = 0;
                    synchronized (pslock) {
                        dao.query("select count(word) from voc"
                                + " where word like ? and (htmled = ? or imaged = ? or xed = ?)");
                        String likewords = "fly%";
                        dao.setString(1, likewords);
                        dao.setBoolean(2, false);
                        dao.setBoolean(3, false);
                        dao.setBoolean(4, false);
                        rs = dao.executeQuery();
                        rs.next();
                        numberofwords = rs.getInt(1);
                        System.err.println("Total of words is :" + Integer.valueOf(numberofwords).toString());
                        //System.exit(0);
                        m[0] = numberofwords / 5;
                        m[1] = m[0];
                        m[2] = m[0];
                        m[3] = m[0];
                        m[4] = numberofwords - m[0] - m[1] - m[2] - m[3];
                        Integer accumulate = 0;
                        for (int i = 0; i < m.length; i++) {
                            accumulate = accumulate + m[i];
//Test words: him, whom, 'em, fly, pardon, built-in etc.
                            String sql = "select word, htmled, imaged, xed from voc where (htmled = ? "
                                    + "or imaged = ? or xed = ?)"
                                    + "and word like ? limit ?, ?";
                            dao.query(sql);
                            dao.setBoolean(1, false);
                            dao.setBoolean(2, false);
                            dao.setBoolean(3, false);
                            dao.setString(4, likewords);
                            dao.setInt(5, accumulate - m[i]);
                            dao.setInt(6, m[i]);
                            mrs[i] = dao.executeQuery();
                        }
                        accumulate = 0;
                        for (int i = 0; i < m.length; i++) {
                            accumulate = accumulate + m[i];
                            startTask("Task " + Integer.valueOf(i).toString() + ":" + Integer.valueOf(accumulate - m[i]).toString() + "==>" + Integer.valueOf(accumulate - 1).toString(), i);
                        }
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(DicActionListener.class.getName()).log(Level.SEVERE, null, ex);
                } catch (DAOException daoe) {
                    Logger.getLogger(DicActionListener.class.getName()).log(Level.SEVERE, null, daoe);
                }
            }
        });
    }

    private void startTask(final String str, int rowkey) {
        final DefaultTableModel model = (DefaultTableModel) hm.get("model");
        final JTable table = (JTable) hm.get("table");
        final int key = rowkey;
        model.addRow(new Object[]{str, 0});
        
        SwingWorker<Integer, Integer> worker = new SwingWorker<Integer, Integer>() {
            private Integer lengthOfTask = m[key];
            
            @Override
            protected Integer doInBackground() {
                int current = 0;
//                System.out.println("Length of task for " + Integer.valueOf(key).toString() + ":" + Integer.valueOf(lengthOfTask).toString());
                while (current < lengthOfTask) {
                    if (!table.isDisplayable()) {
                       break;
                    }
                    current = current + 1;
                    try {
                          mrs[key].next();
                          String targetword = mrs[key].getString("word");
                          HGetter hgetter = new HGetter(targetword, hm);                          
                          if (!mrs[key].getBoolean("htmled")) {
                              hgetter.getHtml();
                          }
                          if (!mrs[key].getBoolean("imaged")) {
                              hgetter.getImageAndCount();
                          }
                          if (!mrs[key].getBoolean("xed")) {
                              hgetter.getXed();
                          }
                              
                    } catch (Exception e) {
                       System.out.println("-----------++++++++++");
                       e.printStackTrace();
                       //break;
                    };
                    p[key] = current;
                    publish(current);
                }
                return 300;
            }

            @Override
            protected void process(java.util.List<Integer> c) {
                processed = p[0] + p[1] + p[2] + p[3] + p[4];
                model.setValueAt(100 * processed / (m[0] + m[1] + m[2] + m[3] + m[4]), 0, 1);
                model.setValueAt(100 * c.get(c.size() - 1) / m[key], key + 1, 1);
                model.setValueAt(str + ":" + Integer.valueOf(c.get(c.size() - 1)), key+1, 0);
//                model.setValueAt(c.get(c.size() - 1), key, 0);
            }

            @Override
            protected void done() {
                String text;
                int i = -1;
                if (isCancelled()) {
                    text = "Cancelled";
                } else {
                    try {
                        i = get();
                        text = (i >= 0) ? "Done" : "Disposed";
                    } catch (Exception ignore) {
                        ignore.printStackTrace();
                        text = ignore.getMessage();
                    }
                }
                System.out.println(key + ":" + text + "(" + i + "ms)");
            }
        };
        worker.execute();
    }
    private void initdb() throws SQLException {
        //dao.query("select TABLE_NAME from INFORMATION_SCHEMA.TABLES"); // WHERE TABLE_NAME = ?");
        try {
            dao.query("select word from voc limit 1");
            //dao.setString(1, "a%");
            rs = dao.executeQuery();
        } catch (DAOException daoe) {
            System.err.println("Exception of no vocabulary table caught!");
            daoe.printStackTrace();
            createdb();
        }
    }
    private void createdb() throws SQLException {
        try {
            synchronized (DAO.daolock) {
                dao.update("drop table voctxt if exists");
                dao.executeUpdate();
                System.out.println("Looks like fresh run. Initializing whole local database!");
                dao.update("create text table voctxt (word varchar(100))");
                dao.executeUpdate();
                dao.update("set table voctxt source \""
                        //                    + ((JTextField) hm.get("dbdir")).getText() + "\\"
                        + ((JTextField) hm.get("wordlist")).getText()// + "\"");
                        + ";encoding=UTF-8\"");
//        dao.setString(1, ((JTextField) hm.get("wordlist")).getText() + ";encoding=UTF-8");
                dao.executeUpdate();

                dao.update("drop table voc if exists");
                dao.executeUpdate();
                dao.update("create cached table voc (word varchar(50), "
                        + "htmled boolean default false, "
                        + "imaged boolean default false, "
                        + "rank int default 0,"
                        + "rank1000 int default 0,"
                        + "xed boolean default false,"
                        + "primary key (word)) ");
                dao.executeUpdate();
                dao.update("insert into voc (word, htmled, imaged, rank, xed, rank1000) "
                        + "select distinct(word), ?, ?, ?, ?, ? from voctxt");
                dao.setBoolean(1, false); dao.setBoolean(2, false); dao.setInt(3, 0); dao.setInt(4, 0); dao.setBoolean(5, false);
                dao.executeUpdate();
                //type: 1: etym, 2, pron, 3:image, 4: meaning, 5:Virtual numbered meaning
                dao.update("create cached table voc3(word varchar(50),"
                        + "sn1 int,"
                        + "sn2 int,"
                        + "sn3 int,"
                        + "sn4 int,"
                        + "sn1c varchar(10),"
                        + "sn2c varchar(10),"
                        + "sn3c varchar(10),"
                        + "sn4c varchar(10),"
                        + "type int,"
                        + "etym varchar(50),"
                        + "pron varchar(50),"
                        + "image varchar(50),"
                        + "imageurl varchar(200),"
                        + "antonyms varchar(100),"
                        + "synomyms varchar(100),"
                        + "meaning varchar(200),"
                        + "primary key(word, sn1, sn2, sn3, sn4))");
                dao.executeUpdate();
                dao.update("create cached table etym( word varchar(50),"
                        + "sn int,"
                        + "etym varchar(50),"
                        + "primary key(word, sn))");
                dao.executeUpdate();
                dao.update("create cached table image(word varchar(50),"
                        + "sn int,"
                        + "imageurl varchar(50)");
                dao.executeUpdate();
                dao.update("create cached table pron( word varchar(50),"
                        + "sn int,"
                        + "etymsn int,"
                        + "pron varchar(50),"
                        + "primary key(word, sn))");
                dao.executeUpdate();
                dao.update("create cached table meaning(word varchar(50),"
                        + "sn int,"
                        + "etymsn int,"
                        + "etymsnsub varchar(10),"
                        + "meaning varchar(300),"
                        + "primary key (word, sn))");
                dao.update("checkpoint");
                dao.executeUpdate();
                //dao.query("");
            }
        } catch (DAOException daoe) {
            daoe.printStackTrace();
        }
    }

    private void createdir() {
        //throw new UnsupportedOperationException("Not yet implemented");
        String path1 = ((JTextField) hm.get("htmldir")).getText();
        if (!(new File(path1)).exists()) { new File(path1).mkdir(); new File(path1 + "\\image").mkdir(); }
        String path2 = ((JTextField) hm.get("dbdir")).getText();
        if (!(new File(path2)).exists()) { new File(path2).mkdir(); }
        String path3 = ((JTextField) hm.get("dictdir")).getText();
        if (!(new File(path3)).exists()) { new File(path3).mkdir(); }
    }
}