/**
 * @author yetaai yetaai@gmail.com This software piece is apache license. But
 * the contents generated is governed by wiktionary.org policy.
 */
package org.wiktionary;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

//import java.a
public class DicActionListener implements ActionListener {

    private HashMap<String, Object> hm;
    private static final Integer pslock = 1;
    private Integer processed = 0;
    private ResultSet[] mrs;
    protected Integer ts, tsf; //tsf is the tasks finished of download and extract. ts is the number used to initiate master arrays and ResultSets
    private int[] m;
    private int[] p;
    private ResultSet rs;
    private static DAO dao = new DAO();
    private boolean b9 = false;
    private final static int NUMBER_OF_TASKS = 10;

    public DicActionListener(HashMap h) {
        hm = h;
        ts = NUMBER_OF_TASKS;
        tsf = 0;
        m = new int[ts];
        p = new int[ts];
        mrs = new ResultSet[ts];
        for (int i1 = 0; i1 < ts; i1++) {
            m[i1] = 0;
            p[i1] = 0;
        }
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
            createDir();
        } else if (ae.getActionCommand().equals("Exit")) {
            //(JFrame) (((JComponent) ae.getSource()).getRootPane().getParent())
            try {
                int idao = dao.update("shutdown");
                dao.executeUpdate(idao);
            } catch (DAOException ex) {
                Logger.getLogger(DicActionListener.class.getName()).log(Level.SEVERE, null, ex);
            }
            DAO.closeConnection();
            System.exit(0);
        } else if (ae.getActionCommand().equals("Save and Exit")) {
            saveprop(ae);
            createDir();
            DAO dao = new DAO();
            try {
                int idao = dao.update("shutdown");
                dao.executeUpdate(idao);
            } catch (DAOException ex) {
                Logger.getLogger(DicActionListener.class.getName()).log(Level.SEVERE, null, ex);
                System.exit(0);
            }
            DAO.closeConnection();
            System.exit(0);
        } else if (ae.getActionCommand().equals("Run")) {
            createDir();
            downloadAndExtract();
            //genDict();
/*
             * synchronized(tsf) { while (tsf < NUMBER_OF_TASKS) { try{
             * tsf.wait(); } catch (InterruptedException ie) { if (tsf < 10 ) {
             * continue; } else { break; } } }
            }
             */
            /*
            Thread t = new Thread(new Runnable() {
                @Override 
                public void run() {
                    synchronized (tsf) {
                        boolean b = true;
//                        while (b) {
                            try {
                                tsf.wait();
                            } catch (InterruptedException ie) {
                                System.out.print("\nInterrupted and tsf is of value now:");
                                System.out.print(tsf);
                                if (tsf < NUMBER_OF_TASKS) {
  //                                  continue;
                                    System.out.print("\ntsf should not be interrupted!");
                                } else {
                                    b = false;
                                }
                            }
//                        }
                    }
                    genDict();
                }
            });
            t.start();
            */
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

    private void downloadAndExtract() {
        //throw new UnsupportedOperationException("Not yet implemented");
        EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                try {
                    initdb();
                } catch (SQLException ex) {
                    Logger.getLogger(DicActionListener.class.getName()).log(Level.SEVERE, null, ex);
                }
                try {
                    Integer numberofwords = 0;
                    int idao = dao.query("select count(word) from voc"
                            + " where regexp_matches(word, ?) and (htmled = ? or imaged = ? or xed = ?)");
                    String likewords = ".*";
                    dao.setString(1, likewords, idao);
                    dao.setBoolean(2, false, idao);
                    dao.setBoolean(3, false, idao);
                    dao.setBoolean(4, false, idao);
                    rs = dao.executeQuery(idao);
                    rs.next();
                    numberofwords = rs.getInt(1);
                    System.err.println("Total of words is :" + Integer.valueOf(numberofwords).toString());
                    //System.exit(0);
                    m[0] = numberofwords / ts;
                    for (int i1 = 1; i1 < ts - 1; i1++) {
                        m[i1] = m[0];
                    }
                    m[ts - 1] = numberofwords - (m[0] * (ts - 1));
                    Integer accumulate = 0;
                    for (int i = 0; i < ts; i++) {
                        accumulate = accumulate + m[i];
//Test words: him, whom, 'em, fly, pardon, built-in etc.
                        String sql = "select word, htmled, imaged, xed from voc where (htmled = ? "
                                + "or imaged = ? or xed = ?)"
                                + "and regexp_matches(word, ?) limit ?, ?";
                        int idao1 = dao.query(sql);
                        dao.setBoolean(1, false, idao1);
                        dao.setBoolean(2, false, idao1);
                        dao.setBoolean(3, false, idao1);
                        dao.setString(4, likewords, idao1);
                        dao.setInt(5, accumulate - m[i], idao1);
                        dao.setInt(6, m[i], idao1);
                        mrs[i] = dao.executeQuery(idao1);
                    }
                    accumulate = 0;
                    for (int i = 0; i < ts; i++) {
                        accumulate = accumulate + m[i];
                        startTask("Task " + Integer.valueOf(i).toString() + ":" + Integer.valueOf(accumulate - m[i]).toString() + "==>" + Integer.valueOf(accumulate - 1).toString(), i);
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
//        model.addRow(new Object[]{str, 0});
        if (model.getRowCount() <= ts) {
            model.addRow(new Object[]{str, 0});
        } else if (model.getRowCount() > ts) {
            model.setValueAt(str, key + 1, 0);
            model.setValueAt(0, key + 1, 1);
        }

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
                        if (hgetter.filefound) {
                            if (!mrs[key].getBoolean("imaged")) {
                                hgetter.xImage();
                            }
                            if (!mrs[key].getBoolean("xed")) {
                                hgetter.getXed();
                            }
                        }
                    } catch (Exception e) {
                        //System.out.println("Exception caught in doInBackground");
                        e.printStackTrace();
                    };
                    p[key] = current;
                    publish(current);
                }
                return 300;
            }

            @Override
            protected void process(java.util.List<Integer> c) {
                int totalm = 0;
                processed = 0;
                for (int i = 0; i < ts; i++) {
                    processed = processed + p[i];
                    totalm = totalm + m[i];
                }
                int percent = 100 * processed / totalm;
                model.setValueAt(100 * processed / totalm, 0, 1);
                model.setValueAt("Overall: " + Integer.valueOf(percent).toString() + "%", 0, 0);
                model.setValueAt(100 * c.get(c.size() - 1) / m[key], key + 1, 1);
                model.setValueAt(str + ":" + Integer.valueOf(c.get(c.size() - 1)), key + 1, 0);
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
                tsf = tsf + 1;
/*
                synchronized (tsf) { 

                    if (tsf == NUMBER_OF_TASKS) {
                        System.out.print("\nNotifyAll() called for tsf");
                        tsf.notifyAll();
                    }
                    
                }
                    */
                System.out.print("\n" + key + ":" + text + "(" + i + "ms)");
                if (tsf == NUMBER_OF_TASKS) {
                    genDict();
                }
            }
        };
        worker.execute();
    }

    private void initdb() throws SQLException {
        //dao.query("select TABLE_NAME from INFORMATION_SCHEMA.TABLES"); // WHERE TABLE_NAME = ?");
        try {
            int idao = dao.query("select word from voc limit 1");
            //dao.setString(1, "a%");
            rs = dao.executeQuery(idao);
            if (!rs.next()) {
                createdb();
            }
        } catch (DAOException daoe) {
            System.err.print("\nException of no vocabulary table caught!");
//            daoe.printStackTrace();
            createdb();
        }
    }

    private void createdb() {
        try {
            System.out.println("Looks like fresh run. Initializing whole local database!");
            int idao1 = dao.update("drop table voctxt if exists");
            dao.executeUpdate(idao1);
            int idao2 = dao.update("create text table voctxt (word varchar(100))");
            dao.executeUpdate(idao2);
            int idao3 = dao.update("set table voctxt source \""
                    //                    + ((JTextField) hm.get("dbdir")).getText() + "\\"
                    + ((JTextField) hm.get("wordlist")).getText()// + "\"");
                    + ";encoding=UTF-8\"");
//        dao.setString(1, ((JTextField) hm.get("wordlist")).getText() + ";encoding=UTF-8");
            dao.executeUpdate(idao3);

            int idao4 = dao.update("drop table voc if exists");
            dao.executeUpdate(idao4);
            int idao5 = dao.update("create cached table voc (word varchar(50), "
                    + "htmled boolean default false, "
                    + "imaged boolean default false, "
                    + "rank int default 0,"
                    + "rank1000 int default 0,"
                    + "xed boolean default false,"
                    + "primary key (word)) ");
            dao.executeUpdate(idao5);
            int idao6 = dao.update("insert into voc (word, htmled, imaged, rank, xed, rank1000) "
                    + "select distinct(word), ?, ?, ?, ?, ? from voctxt");
            dao.setBoolean(1, false, idao6);
            dao.setBoolean(2, false, idao6);
            dao.setInt(3, 0, idao6);
            dao.setInt(4, 0, idao6);
            dao.setBoolean(5, false, idao6);
            dao.executeUpdate(idao6);
            int idao7 = dao.update("update voc set word = left(word, length(word) - 1) where regexp_matches(word, ?)");
            dao.setString(1, "^.*.:.*", idao7);
            dao.executeUpdate(idao7);
            int idao8 = dao.update("drop table voc3 if exists");
            dao.executeUpdate(idao8);
            //type: 1: etym, 2, pron, 3:image, 4: meaning, 5:Virtual numbered meaning
            int idao9 = dao.update("create cached table voc3(word varchar(50),"
                    + "sn1 int,"
                    + "sn2 int,"
                    + "sn3 int,"
                    + "sn4 int,"
                    + "type int,"
                    + "etym varchar(300),"
                    + "pronun varchar(50),"
                    + "pronus varchar(50),"
                    + "pronuk varchar(50),"
                    + "image varchar(50),"
                    + "imageurl varchar(500),"
                    + "antonyms varchar(100),"
                    + "synomyms varchar(100),"
                    + "meaning varchar(2100)," //Plugs additional 100 for <st>*</st> and <mgi></mgi>
                    + "plural varchar(800),"
                    + "vsubcat varchar(2),"
                    + "v3rd varchar(60),"
                    + "vpresentp varchar(60),"
                    + "vsimplepast varchar(60),"
                    + "vpastp varchar(60),"
                    + "compare8 varchar(60),"
                    + "compare9 varchar(60),"
                    + "primary key(word, sn1, sn2, sn3, sn4))");
            dao.executeUpdate(idao9);
            int idao10 = dao.update("drop table types if exists");
            dao.executeUpdate(idao10);
            int idao11 = dao.update("create memory table types(ref varchar(50), type int, abr varchar(10))");
            dao.executeUpdate(idao11);
            int idao12 = dao.update("create unique index types1 on types (ref)");
            dao.executeUpdate(idao12);
            int idao13 = dao.update("create unique index types2 on types (type, abr)");
            dao.executeUpdate(idao13);
            int idao14 = dao.update("create index types3  on types (abr)");
            dao.executeUpdate(idao14);
            int idao15 = dao.update("insert into types (ref, type, abr) values(?, ?, ?)");
            dao.setString(1, "Etymology", idao15);
            dao.setInt(2, 1, idao15);
            dao.setString(3, "o", idao15);
            dao.executeUpdate(idao15);
            int idao16 = dao.update("insert into types (ref, type, abr) values(?, ?, ?)");
            dao.setString(1, "Pronunciation", idao16);
            dao.setInt(2, 2, idao16);
            dao.setString(3, "prn", idao16);
            dao.executeUpdate(idao16);
            int idao17 = dao.update("insert into types (ref, type, abr) values(?, ?, ?)");
            dao.setString(1, "Synonym", idao17);
            dao.setInt(2, 11, idao17);
            dao.setString(3, "syn", idao17);
            dao.executeUpdate(idao17);
            int idao18 = dao.update("insert into types (ref, type, abr) values(?, ?, ?)");
            dao.setString(1, "Synonyms", idao18);
            dao.setInt(2, 12, idao18);
            dao.setString(3, "syn", idao18);
            dao.executeUpdate(idao18);
            int idao19 = dao.update("insert into types (ref, type, abr) values(?, ?, ?)");
            dao.setString(1, "Antonym", idao19);
            dao.setInt(2, 13, idao19);
            dao.setString(3, "ant", idao19);
            dao.executeUpdate(idao19);
            int idao20 = dao.update("insert into types (ref, type, abr) values(?, ?, ?)");
            dao.setString(1, "Antonyms", idao20);
            dao.setInt(2, 14, idao20);
            dao.setString(3, "ant", idao20);
            dao.executeUpdate(idao20);
            int idao21 = dao.update("insert into types (ref, type, abr) values(?, ?, ?)");
            dao.setString(1, "Image", idao21);
            dao.setInt(2, 15, idao21);
            dao.setString(3, "img", idao21);
            dao.executeUpdate(idao21);
            int idao22 = dao.update("insert into types (ref, type, abr) values(?, ?, ?)");
            dao.setString(1, "English", idao22);
            dao.setInt(2, 99, idao22);
            dao.setString(3, "Eng", idao22);
            dao.executeUpdate(idao22);
            int idao23 = dao.update("insert into types (ref, type, abr) values(?, ?, ?)");
            dao.setString(1, "Noun", idao23);
            dao.setInt(2, 101, idao23);
            dao.setString(3, "n", idao23);
            dao.executeUpdate(idao23);
            int idao24 = dao.update("insert into types (ref, type, abr) values(?, ?, ?)");
            dao.setString(1, "Verb", idao24);
            dao.setInt(2, 102, idao24);
            dao.setString(3, "v", idao24);
            dao.executeUpdate(idao24);
            int idao25 = dao.update("insert into types (ref, type, abr) values(?, ?, ?)");
            dao.setString(1, "Pronoun", idao25);
            dao.setInt(2, 103, idao25);
            dao.setString(3, "pr", idao25);
            dao.executeUpdate(idao25);
            int idao26 = dao.update("insert into types (ref, type, abr) values(?, ?, ?)");
            dao.setString(1, "Adjective", idao26);
            dao.setInt(2, 104, idao26);
            dao.setString(3, "adj", idao26);
            dao.executeUpdate(idao26);
            int idao27 = dao.update("insert into types (ref, type, abr) values(?, ?, ?)");
            dao.setString(1, "Adverb", idao27);
            dao.setInt(2, 105, idao27);
            dao.setString(3, "adv", idao27);
            dao.executeUpdate(idao27);
            int idao28 = dao.update("insert into types (ref, type, abr) values(?, ?, ?)");
            dao.setString(1, "Preposition", idao28);
            dao.setInt(2, 106, idao28);
            dao.setString(3, "prep", idao28);
            dao.executeUpdate(idao28);
            int idao29 = dao.update("insert into types (ref, type, abr) values(?, ?, ?)");
            dao.setString(1, "Particle", idao29);
            dao.setInt(2, 107, idao29);
            dao.setString(3, "part", idao29);
            dao.executeUpdate(idao29);
            int idao30 = dao.update("insert into types (ref, type, abr) values(?, ?, ?)");
            dao.setString(1, "Interjection", idao30);
            dao.setInt(2, 108, idao30);
            dao.setString(3, "interj", idao30);
            dao.executeUpdate(idao30);
            int idao31 = dao.update("insert into types (ref, type, abr) values(?, ?, ?)");
            dao.setString(1, "Conjunction", idao31);
            dao.setInt(2, 109, idao31);
            dao.setString(3, "conj", idao31);
            dao.executeUpdate(idao31);
            int idao32 = dao.update("insert into types (ref, type, abr) values(?, ?, ?)");
            dao.setString(1, "Determiner", idao32);
            dao.setInt(2, 110, idao32);
            dao.setString(3, "det", idao32);
            dao.executeUpdate(idao32);
            int idao33 = dao.update("insert into types (ref, type, abr) values(?, ?, ?)");
            dao.setString(1, "Numeral", idao33);
            dao.setInt(2, 111, idao33);
            dao.setString(3, "num", idao33);
            dao.executeUpdate(idao33);
            int idao34 = dao.update("insert into types (ref, type, abr) values(?, ?, ?)");
            dao.setString(1, "Article", idao34);
            dao.setInt(2, 112, idao34);
            dao.setString(3, "art", idao34);
            dao.executeUpdate(idao34);
            int idao35 = dao.update("insert into types (ref, type, abr) values(?, ?, ?)");
            dao.setString(1, "Abbreviation", idao35);
            dao.setInt(2, 112, idao35);
            dao.setString(3, "ab", idao35);
            dao.executeUpdate(idao35);
            int idao36 = dao.update("drop table dict if exists");
            dao.executeUpdate(idao36);
            int idao37 = dao.update("create cached table dict(word varchar(50), txt varchar(3000), primary key(word))");
            dao.executeUpdate(idao37);
            int idao38 = dao.update("checkpoint");
            dao.executeUpdate(idao38);
        } catch (DAOException daoe) {
            daoe.printStackTrace();
        }
    }

    private void createDir() {
        //throw new UnsupportedOperationException("Not yet implemented");
        String path1 = ((JTextField) hm.get("htmldir")).getText();
        if (!(new File(path1)).exists()) {
            new File(path1).mkdir();
        }
        if (!(new File(path1 + "\\image").exists())) {
            new File(path1 + "\\image").mkdir();
        }
        String path2 = ((JTextField) hm.get("dbdir")).getText();
        if (!(new File(path2)).exists()) {
            new File(path2).mkdir();
        }
        String path3 = ((JTextField) hm.get("dictdir")).getText();
        if (!(new File(path3)).exists()) {
            new File(path3).mkdir();
        }
    }

    private void genDict() {
        try {
            DAO dao = new DAO();
            ResultSet rs, rs1;
            int max0 = Integer.parseInt(((JTextField) hm.get("maxlength")).getText());
            int i = 0, j = 0;
            int iword = 0, iword00 = 0; //length per word
            int i1 = 0, i2 = 0, i3 = 0, i4 = 0;
            OutputStream o = new FileOutputStream(new File(((JTextField) hm.get("dictdir")).getText() + "\\dict5-200.xml"));
            OutputStreamWriter ow = new OutputStreamWriter(o, "UTF-8");
            BufferedWriter mbrWriter = new BufferedWriter(ow);
            /*
             * dao.query("select count(*) from voc3 where type < 10 or type >
             * 100"); rs = dao.executeQuery(); rs.next(); j = rs.getInt(1);
             */
            int idao = dao.query("select word, sn1, sn2, sn3, sn4, type, etym, pronun, pronus, pronuk, image, meaning, plural,"
                    + " vsubcat, v3rd, vpresentp, vsimplepast, vpastp, compare8, compare9 from voc3 "
                    + "where (type < 10 or (type = 15 and sn1 = 100) or type > 100) "
                    + "and (sn1 < 1000) "
                    + "and regexp_matches(word, ?)"
                    + "order by word, sn1, sn2, sn3, sn4 "
                    + "");
            //'abcdef, ghijklmn, opqrs, tuvwxyz
            String regexpwords = ".*";
            dao.setString(1, regexpwords, idao);
            rs = dao.executeQuery(idao);

            int idao1 = dao.query("select count(*) from voc3 where type < 10 or type > 100");
            rs1 = dao.executeQuery(idao1);
            rs1.next();
            j = rs1.getInt(1);
            System.err.print("\nTotal number of words to be included into dictionary" + Integer.valueOf(j).toString() + "\n");

            String word0 = "", lword0 = "";
            int sn1 = 0, sn2 = 0, sn3 = 0, sn4 = 0;
            int lsn1 = 0, lsn2 = 0, lsn3 = 0, lsn4 = 0;
            boolean b1 = false, b2 = false, b3 = false, b4 = false; //Actually not used because layer 1 exists always.
            boolean bl1 = false, bl2 = false, bl3 = false, bl4 = false;
            ow.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            ow.append("<?xml-stylesheet type=\"text/xsl\" href=\"dictfo.xsl\"?>\n");
            ow.append("<dict>");
            while (rs.next()) {
                String slayer = "";
                lword0 = word0;
                word0 = rs.getString(1);
                sn1 = rs.getInt(2);
                sn2 = rs.getInt(3);
                sn3 = rs.getInt(4);
                sn4 = rs.getInt(5);
                //System.out.print("\n" + word0 + ":");System.out.print(sn1); System.out.print(","); System.out.print(sn2); System.out.print(","); System.out.print(sn3); System.out.print(","); System.out.print(sn4);
                //System.out.print("\n" + "\t");System.out.print(b1); System.out.print(","); System.out.print(b2); System.out.print(","); System.out.print(b3); System.out.print(","); System.out.print(b4);
                //System.out.print("\n" + "\t");System.out.print(bl1); System.out.print(","); System.out.print(bl2); System.out.print(","); System.out.print(bl3); System.out.print(","); System.out.print(bl4);
                bl1 = b1;
                bl2 = b2;
                bl3 = b3;
                bl4 = b4;
                LS lls = new LS();
                if (!word0.equals(lword0)) {
                    iword = 0;
                    b1 = true;
                    b2 = false;
                    b3 = false;
                    b4 = false;
                    //ow.append("</d1>");
                    if (i > 0) {
                        if (bl3) {
                            ow.append("</d3>");
                        }
                        if (bl2) {
                            ow.append("</d2>");
                        }
                        if (bl1) {
                            ow.append("</d1>");
                        }
                        ow.append("</d0></item>\n<item>");
                    }
                    if (i == 0) {
                        ow.append("\n<item>");
                    }

                    ow.append("<word>" + word0 + "</word>" + supPic(word0) + "<d0>");
                    i1 = 0;
                    i2 = 0;
                    i3 = 0;
                    i4 = 0;

                    b1 = true;
                    b2 = false;
                    b3 = false;
                    b4 = false;
                    ow.append("<d1>"); // of course i1 == 0 right now.
                    i1 = i1 + 1;
                    i2 = 0;
                    i3 = 0;
                    i4 = 0;

                    if (sn2 > 0) { //event of level sn2(layer3) happend as well but i2 = 0 right now.
                        b2 = true;
                        b3 = false;
                        b4 = false;
                        ow.append("<d2>");
                        i2 = i2 + 1;
                        i3 = 0;
                        i4 = 0;
                    }
                    lls = casetype(rs);
                    iword = iword + lls.l;
                    ow.append(lls.s);
                } else if (word0.equals(lword0) && sn1 != lsn1 && (iword < max0 || rs.getInt("TYPE") == 15)) {
                    b1 = true;
                    b2 = false;
                    b3 = false;
                    b4 = false;
                    if (i1 > 0) {
                        if (bl3) {
                            ow.append("</d3>");
                        }
                        if (bl2) {
                            ow.append("</d2>");
                        }
                        ow.append("</d1><d1>");
                    }
                    if (i1 == 0) {
                        ow.append("<d1>");
                    }
                    lls = casetype(rs);
                    iword = iword + lls.l;
                    ow.append(lls.s);
                    i1 = i1 + 1;
                    i2 = 0;
                    i3 = 0;
                    i4 = 0;
                } else if ((word0.equals(lword0) && sn1 == lsn1 && sn2 != lsn2) && (iword < max0 || rs.getInt("TYPE") == 15)) {
                    b2 = true;
                    b3 = false;
                    b4 = false;
                    if (i2 > 0) {
                        if (bl3) {
                            ow.append("</d3>");
                        }
                        ow.append("</d2><d2>");
                    }
                    if (i2 == 0) {
                        ow.append("<d2>");
                    }
                    lls = casetype(rs);
                    iword = iword + lls.l;
                    ow.append(lls.s);
                    i2 = i2 + 1;
                    i3 = 0;
                    i4 = 0;
                } else if (word0.equals(lword0) && sn1 == lsn1 && sn2 == lsn2 && sn3 != lsn3 && (iword < max0 || rs.getInt("TYPE") == 15)) {
                    b3 = true;
                    b4 = false;
                    if (i3 > 0) {
                        ow.append("</d3><d3>");
                    }
                    if (i3 == 0) {
                        ow.append("<d3>");
                    }
                    lls = casetype(rs);
                    iword = iword + lls.l;
                    ow.append(casetype(rs).s);
                    i3 = i3 + 1;
                    i4 = 0;
                } else if (word0.equals(lword0) && sn1 == lsn1 && sn2 == lsn2 && sn3 == lsn3 && (iword < max0 || rs.getInt("TYPE") == 15)) {
                    b4 = true;
                    lls = casetype(rs);
                    iword = iword + lls.l;
                    ow.append(lls.s);
                    i4 = i4 + 1;
                }
                    //System.out.print("\nWord: " + word0 + " 's meaning length is");
                    //System.out.print(iword);
                    //System.out.println(lls.s);
                lword0 = word0;
                lsn1 = sn1;
                lsn2 = sn2;
                lsn3 = sn3;
                lsn4 = sn4;
                i = i + 1;
            }
            if (b3) {
                ow.append("</d3>");
            }
            if (b2) {
                ow.append("</d2>");
            }
            if (b1) {
                ow.append("</d1>");
            }

            ow.append("</d0></item></dict>");
            mbrWriter.close();
            Object[] opts = {"OK"};
            JOptionPane.showConfirmDialog(((JPanel) hm.get("p")).getRootPane().getParent(), "XML file Generated!", "Congratulations", JOptionPane.PLAIN_MESSAGE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //throw new UnsupportedOperationException("Not yet implemented");
    }

    private LS casetype(ResultSet rs) {
        //StringEscapeUtils seu = new StringEscapeUtils();
        try {
            //word, sn1, sn2, sn3, sn4, type, etym, pronun, pronus, pronuk, image, meaning
            //1,    2,   3,   4,   5,   6,    7,    8,      9,      10,     11,    12
            String s = "", s1 = "";
            switch (rs.getInt("TYPE")) {
                case 1://Etymology
                    s1 = simEtym(rs.getString(7));
                    if (s1.length() > 1) {
                        s = "<etym>" + simEtym(rs.getString(7)) + "</etym>";
                    } else {
                        s = "";
                    }
                    break;
                case 2://Pronunciation
                    for (int ii = 8; ii < 11; ii++) {
                        if (rs.getString(ii) != null) {
                            if (!rs.getString(ii).trim().equals("")) {
                                if (ii == 8 || ii == 9) {
                                    s = s + "<pr>" + rs.getString(ii) + "</pr>";
                                    break; //Only one pronunciation will be found and general first then us then uk. 
                                }
                            }
                        }
                    }
                    break;
                case 15: //Image
                    s = "<img src=\"../" + ((JTextField) hm.get("htmldir")).getText() + "/image/" + rs.getString("image") + "\"></img>";
                    break;
                case 101: //Noun: plural and meaning
                    if (rs.getString("plural").equals(rs.getString("word") + "s") && cleanMg(rs.getString("meaning")).length() > 1) {
                        s = "<wt>" + Ref.getAbr(101) + "</wt>" + "<mg>" + cleanMg(rs.getString("meaning")) + "</mg>";
                    } else if (!(rs.getString("plural").equals(rs.getString("word") + "s")) && cleanMg(rs.getString("meaning")).length() > 1 ) {
                        s = "<wt>" + Ref.getAbr(101) + "</wt><plural>" + rs.getString("plural") + "</plural><mg>" + cleanMg(rs.getString("meaning")) + "</mg>";
                    } else {
                        s= "";
                    }
                    break;
                case 102: //Verb
                    if (cleanMg(rs.getString("meaning")).length()> 1) {
                        s = "<wt>" + rs.getString("vsubcat") + "</wt><mg>"
                            + cleanMg(rs.getString("meaning")).replace("(transitive, intransitive)", "").replace("(intransitive, transitive)", "").replace("(transitive)", "").replace("(intransitive)", "").replace("(transitive, ", "(").replace("(intransitive, ", "(").replace(", intransitive)", ")").replace(", transitive)", ")")
                            + "</mg>";
                    } else {
                        s = "";
                    }
                    break;
                case 104: //Adj
                    if (cleanMg(rs.getString("meaning")).length() > 1) {
                        s = "<wt>" + Ref.getAbr(104) + "</wt><mg>" + cleanMg(rs.getString("meaning")) + "</mg>";
                    } else {
                        s = "";
                    }
                    break;
                case 105: //Adv
                    if (cleanMg(rs.getString("meaning")).length() > 1) {
                        s = "<wt>" + Ref.getAbr(105) + "</wt><mg>" + cleanMg(rs.getString("meaning")) + "</mg>";
                    } else {
                        s = "";
                    }
                    break;
                default:
                    if (rs.getInt("TYPE") > 100 && cleanMg(rs.getString("meaning")).length() > 1) {
                        s = "<wt>" + Ref.getAbr(rs.getInt("TYPE")) + "</wt><mg>" + cleanMg(rs.getString("meaning")) + "</mg>";
                    } else {
                        s = "";
                    }
                    break;
            }
            LS ls = new LS();
            ls.l = s.length();
            ls.s = s;
            return ls;
        } catch (Exception e) {
            e.printStackTrace();
            LS ls = new LS();
            ls.l = 9;
            ls.s = "Not found";
            return ls;
        }
        //throw new UnsupportedOperationException("Not yet implemented");
    }

    private String simEtym(String lEtym) {
        String s1;
        String s2;
        if (lEtym.startsWith("Middle English") || lEtym.startsWith("Old English") || lEtym.startsWith("Anglo-Norman")
                || lEtym.startsWith("Middle French") || lEtym.startsWith("Late Latin")) {
            s1 = lEtym.replace("Middle English", "<otp>ME</otp>").
                    replace("Old English", "<otp>OE</otp>").replace("Anglo-Norman", "<otp>AN</otp>").
                    replace("Middle French", "<otp>OF</otp>").replace("Late Latin", "<otp>L.La</otp>").trim();
            if (s1.contains(" ")) {
                s2 = s1.replace(s1.substring(s1.indexOf(" ")), "<ow>" + s1.substring(s1.indexOf(" ")).trim() + "</ow>");
            } else {
                s2 = s1;
            }
            //s2 = s1.split(" ");
            return s2;
        } else if (lEtym.startsWith("French") || lEtym.startsWith("Italian") || lEtym.startsWith("Latin")) {
            s1 = lEtym.replace("French", "<otp>Fr</otp>").
                    replace("Italian", "<otp>Ita</otp>").
                    replace("Latin", "<otp>La</otp>");
            if (s1.contains(" ")) {
                s2 = s1.replace(s1.substring(s1.indexOf(" ")), "<ow>" + s1.substring(s1.indexOf(" ")).trim() + "</ow>");
            } else {
                s2 = s1;
            }
            return s2;
        } else {
            return ""; //Other forms of Etymology are discarded in the output right now.
        }
        //throw new UnsupportedOperationException("Not yet implemented");
    }

    private String supPic(String word0) {
        //throw new UnsupportedOperationException("Not yet implemented");
        boolean b;

        try {
            int idao = dao.query("select word from voc3 where word = ? and sn1 = ?");
            dao.setString(1, word0, idao);
            dao.setInt(2, 100, idao);
            ResultSet rs = dao.executeQuery(idao);
            if (rs.next()) {
                return "<sup>p</sup>";
            } else {
                return "";
            }
        } catch (Exception e) {
            return "";
        }
    }
    /*
     * @Para String smg: Meaning String to be cleaned
     *
     */

    private String cleanMg(String smg) {
        //throw new UnsupportedOperationException("Not yet implemented");
        //Remove all thing inside [], or sub/sup and escape & to &amp;
        int l = 0;
        int max0 = Integer.parseInt(((JTextField) hm.get("maxlength")).getText());
        String smg0 = "", smg1 = "";
        smg0 = smg.replaceAll("\\[[^\\]]*\\]", "").replace("(figuratively)", "(Fig)").replace("<sub>.*</sub>", "").replace("<sup>.*</sup>", "").replace("  ", " ").replace("  ", " ");
        smg0 = smg0.replace("(Obsolete)", "(Obs)").replace("(obsolete)", "(Obs)").replace("(Obsolete, ", "(Obs,").replace("(obsolete,", "(Obs,").replace(", obsolete)", ", Obs)").replace(",obsolete)", ", Obs)");
        String[] sa = smg0.split("<sc>\\*</sc>");
        //System.err.println("smg0" + smg0);
        for (int i = 0; i < Math.min(sa.length, Integer.parseInt(((JTextField) hm.get("maxitem")).getText()) ); i++) {
            l = l + sa[i].length();
            String escmgi = "";
            escmgi = escXml(sa[i].substring(5, sa[i].length() - 6));
            if (((l <= max0 && escmgi.length() > 1 ) || i == 0 )) { // && (escmgi.indexOf("(Obsolete)") < 0 && escmgi.indexOf("(obsolete)") < 0)) {
                //System.out.println("sa[i]:" + sa[i].substring(5, sa[i].length() - 6));
                smg1 = smg1 + "<mgi>" + escmgi + "</mgi>" + "<sc>*</sc>";
          //      if (smg1.indexOf("(Obs)")> 0 || smg1.indexOf("(Obs,") > 0 || smg1.indexOf(", Obs") > 0) {
          //          break; //if one item is Obsolete then enough(my prefer)
          //      }
            } else {
                break;
            }
        }
        if (smg1.length() > 10) {
            smg1 = smg1.substring(0, smg1.length() - 10);
        } 
        //smg = smg.replace("&", "&amp;");
        return smg1;
    }

    String escXml(String smg) { //&, ', ", <, >
        int k = 0;
        while (true) {
            if (k == smg.length() || k > smg.length()) {
               break;
            }
            if (smg.substring(k, k + 1).equals("&")) {
                if (k > smg.length() - 5 && k < smg.length()) {
                    String s0 = smg.substring(0, k);
                    String s1 = smg.substring(k + 1);
                    smg = s0 + "&amp;" + s1;
                    k = k + 5;
                    continue;
                } else if (k <= smg.length() - 5) {
                    if (!smg.substring(k, k + 5).equalsIgnoreCase("&amp;")) {
                        String s0 = "";
                        s0 = smg.substring(0, k);
                        String s1 = smg.substring(k + 1);
                        smg = s0 + "&amp;" + s1;
                        k = k + 5;
                        continue;
                    } else {
                        k = k + 5;
                        continue;
                    }
                }
            } else if (smg.substring(k, k + 1).equals("'")) {
                String s0 = "";
                s0 = smg.substring(0, k);
                String s1 = smg.substring(k + 1);
                smg = s0 + "&apos;" + s1;
                k = k + 6;
                continue;
            } else if (smg.substring(k, k + 1).equals("\"")) {
                String s0 = "";
                s0 = smg.substring(0, k);
                String s1 = smg.substring(k + 1);
                smg = s0 + "&quot;" + s1;
                k = k + 6;
                continue;
            } else if (smg.substring(k, k + 1).equals("<")) {
                String s0 = "";
                s0 = smg.substring(0, k);
                String s1 = smg.substring(k + 1);
                smg = s0 + "&lt;" + s1;
                k = k + 4;
                continue;
            } else if (smg.substring(k, k + 1).equals(">")) {
                String s0 = "";
                s0 = smg.substring(0, k);
                String s1 = smg.substring(k + 1);
                smg = s0 + "&gt;" + s1;
                k = k + 4;
                continue;
            } else {
                k = k + 1;
                //continue not necessary
            }
        }
        return smg;
    }
}

class LS {
    int l;
    String s;
}
