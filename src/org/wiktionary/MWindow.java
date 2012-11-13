/*
 * @Author Yetaai
 * yetaai@gmail.com
 * This software piece is apache license. But the contents generated is governed by wiktionary.org policy.
 */
package org.wiktionary;

import java.awt.*;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusListener;
import java.awt.event.FocusEvent;
import javax.swing.*;
import java.io.*;
import java.sql.*;
import java.util.Properties;
import java.util.HashMap;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.border.EmptyBorder;   
import javax.swing.table.DefaultTableModel;
import javax.swing.JSplitPane;
import java.util.List;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.ScrollPaneLayout;
import javax.swing.ScrollPaneConstants;

public class MWindow {
    private DAO dao = new DAO();
    private ResultSet rs;
    private HashMap<String, Object> hm = new HashMap();
    
    private String[] columnNames = {"Task groups", "Status"};
    private Object[][] data = {{"dummy", 100}};
    private static final Integer pslock = 1;
    
    private DefaultTableModel model = new DefaultTableModel(data, columnNames) {
        private static final long serialVersionUID = 1L;
        @Override
        public Class<?> getColumnClass(int column) {
            return getValueAt(0, column).getClass();
        }

        @Override
        public boolean isCellEditable(int row, int col) {
            return false;
        }
    };
    private JTable table = new JTable(model);
    private     ResultSet[] mrs = new ResultSet[5]; 
    private     Integer m0[] = new Integer[5];
    
    private void addComponents() {
        final JPanel p = new JPanel();
//        Container p = f.getContentPane();
        JScrollPane sp = new JScrollPane(p);
        //f.getContentPane().add(BorderLayout.CENTER, sp);
//        ((Container) hm.get("cp")).add(BorderLayout.CENTER, sp);
        hm.put("p", p); //better to be done after object construction
        hm.put("sp", sp);//cp (ContentPane of frame is already in hm)
        //hm.put("frame", (JFrame) p.getRootPane().getParent()); //Not sure could get JFrame's rootpane. Or just call a loop to get the JFrame?
        p.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        p.setLayout(new GridBagLayout());

//        JPanel p = new JPanel(new GridBagLayout());
        JLabel lmaxlength = new JLabel("Max length each word");
 //           lmaxlength.setBounds(5, 5, 200, 100);
        JLabel lmaxitem50 = new JLabel("Max items for top 50 words");
        JLabel lmaxitem500 = new JLabel("Max items for top 500 words");
        JLabel lmaxitem = new JLabel("Max items for other words");
        JLabel lranklevel = new JLabel("Rank Level");
        JLabel lwordlist = new JLabel("Word list input file");
        lwordlist.setForeground(Color.blue);
        JLabel lhtmldir = new JLabel("HTML directory name");
        JLabel ldictdir = new JLabel("Dictionary directory name");
        JLabel ldbdir = new JLabel("Database directory name");
            
        JTextField tmaxlength = new JTextField((Res.getProp().getString("maxlength") == null) ? "300" : Res.getProp().getString("maxlength"));
        JTextField tmaxitem50 = new JTextField((Res.getProp().getString("maxitem50") == null) ? "8" : Res.getProp().getString("maxitem50"));
        JTextField tmaxitem500 = new JTextField((Res.getProp().getString("maxitem500") == null) ? "5" : Res.getProp().getString("maxitem500"));
        JTextField tmaxitem = new JTextField((Res.getProp().getString("maxitem") == null) ? "2" : Res.getProp().getString("maxitem"));
        String[] ranklevel = {
            "Rank per 1000",
            "Rank in detail"
        };
        JComboBox tranklevel = new JComboBox(ranklevel);
        if (Res.getProp().getString("ranklevel") == null) {
            tranklevel.setSelectedItem(0);
        } else if (Res.getProp().getString("ranklevel").equals(ranklevel[0])) {
            tranklevel.setSelectedIndex(0);
        } else if (Res.getProp().getString("ranklevel").equals(ranklevel[1])) {
            tranklevel.setSelectedIndex(1);
        } else {
            tranklevel.setSelectedItem(0);
        }
        tranklevel.setEditable(false);
        tranklevel.addActionListener(tranklevel);

        final JTextField twordlist = new JTextField((Res.getProp().getString("wordlist") == null) ? "" : Res.getProp().getString("wordlist"));
        final JTextField thtmldir = new JTextField((Res.getProp().getString("htmldir") == null) ? "html" : Res.getProp().getString("htmldir"));
        final JTextField tdictdir = new JTextField((Res.getProp().getString("dictdir") == null) ? "dict" : Res.getProp().getString("dictdir"));
        final JTextField tdbdir = new JTextField((Res.getProp().getString("dbdir") == null) ? "db" : Res.getProp().getString("dbdir"));
        
        final JButton runbutton = new JButton("Run");
        final JButton savebutton = new JButton("Save");
        final JButton saveexitbutton = new JButton("Save and Exit");
        final JButton exitbutton = new JButton("Exit");

        GridBagConstraints c = new GridBagConstraints();
        Insets insets0 = new Insets(5, 50, 5, 5);
        Insets insets1 = new Insets(5, 5, 5, 45);
        c.weightx = 0.1;
        c.weighty = 0.1;
        c.insets = insets0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridy = 0;
        c.gridx = 0;
        p.add(lmaxlength, c);
//            c.insets = new Insets(5, 50, 5, 2);
        c.gridy = 0;
        c.gridx = 1;
        c.insets = insets1;
        p.add(tmaxlength, c);
        c.gridy = 1;
        c.gridx = 0;
        c.insets = insets0;
        p.add(lmaxitem50, c);
        c.gridx = 1;
        c.insets = insets1;
        p.add(tmaxitem50, c);
        c.gridy = 2;
        c.gridx = 0;
        c.insets = insets0;
        p.add(lmaxitem500, c);
        c.gridx = 1;
        c.insets = insets1;
        p.add(tmaxitem500, c);
        c.gridy = 3;
        c.gridx = 0;
        c.insets = insets0;
        p.add(lmaxitem, c);
        c.gridx = 1;
        c.insets = insets1;
        p.add(tmaxitem, c);
        c.gridy = 4;
        c.gridx = 0;
        c.insets = insets0;
        p.add(lranklevel, c);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 1;
        c.insets = insets1;
        p.add(tranklevel, c);
        c.gridy = 6;
        c.gridx = 0;
        c.insets = insets0;
        p.add(lwordlist, c);
        c.gridx = 1;
        c.insets = insets1;
        p.add(twordlist, c);
        c.gridy = 7;
        c.gridx = 0;
        c.insets = insets0;
        p.add(lhtmldir, c);
        c.gridx = 1;
        c.insets = insets1;
        p.add(thtmldir, c);
        c.gridy = 8;
        c.gridx = 0;
        c.insets = insets0;
        p.add(ldictdir, c);
        c.gridx = 1;
        c.insets = insets1;
        p.add(tdictdir, c);
        c.gridy = 9;
        c.gridx = 0;
        c.insets = insets0;
        p.add(ldbdir, c);
        c.gridx = 1;
        c.insets = insets1;
        p.add(tdbdir, c);

        c.gridy = 10;
        c.gridx = 0;
        c.insets = insets0;
        p.add(runbutton, c);
        c.gridx = 1;
        c.insets = insets1;
        p.add(saveexitbutton, c);
        c.gridy = 11;
        c.gridx = 0;
        c.insets = insets0;
        p.add(savebutton, c);
        c.gridx = 1;
        c.insets = insets1;
        p.add(exitbutton, c);
        hm.put("maxlength", tmaxlength);
        hm.put("maxitem50", tmaxitem50);
        hm.put("maxitem500", tmaxitem500);
        hm.put("maxitem", tmaxitem);
        hm.put("ranklevel", tranklevel);
        hm.put("wordlist", twordlist);
        hm.put("htmldir", thtmldir);
        hm.put("dictdir", tdictdir);
        hm.put("dbdir", tdbdir);
        DicActionListener dal = new DicActionListener(hm);
        savebutton.addActionListener(dal);
        runbutton.addActionListener(dal);
        saveexitbutton.addActionListener(dal);
        exitbutton.addActionListener(dal);
        twordlist.addFocusListener(new FL(hm)); //Focus Listner is not the only way nor best way. but works right now.
    }
    public void makeUI(Container cp) {
        hm.put("cp", cp);
        addComponents();
        addTable();
//get topmost JFrame if necessary
 /*       Container parent = cp;
        while (parent.getParent() != null) {
            parent = parent.getParent();
            System.out.println("What is Parent of cp if not an Instance of JFrame?" + parent.toString());
        }
        if (parent instanceof JFrame) {
            System.out.println("Finally this is an Instance of JFrame?");
        };
*/
/*        try {
            dao.query("select word from voctxt limit 10 offset 1 ");
            rs = dao.executeQuery();
            while (rs.next()) {
                System.out.println(rs.getString(1) + " Printing inside makeUI()");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } */
    }
    public String getAppName() {
        String appname = "";
        String oldappname= "";
        try {
             oldappname = Res.getProp().getString("appname");
             if ((oldappname == null) || oldappname.length() < 10) {
                 while ((appname == null) || appname.length() < 1) {
                     appname = JOptionPane.showInputDialog("Give your dictionary generator a name", oldappname);
                 }
             }
        } catch (Exception e) {
             e.printStackTrace();
        }
        try {
            if (!(oldappname == appname) && oldappname.length() < 10) {
                Res.getProp().setProperty("appname", appname);
                Res.getProp().save();
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        return appname;
    }

    private void addTable() {
        //throw new UnsupportedOperationException("Not yet implemented");
        JScrollPane ppara = (JScrollPane) hm.get("sp");
        JPanel p = new JPanel(new BorderLayout());
        JScrollPane pstatus = new JScrollPane(p);
        p.add(table.getTableHeader(), BorderLayout.NORTH);
        p.add(table,BorderLayout.CENTER);
        
        table.setSize(p.getWidth()-10, p.getHeight()-10);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.getColumnModel().getColumn(0).setPreferredWidth(100);
        table.getColumnModel().getColumn(1).setPreferredWidth(500);
        
        TableColumn column = table.getColumnModel().getColumn(1);
        column.setCellRenderer(new ProgressRenderer());

        JSplitPane splp = new JSplitPane(JSplitPane.VERTICAL_SPLIT, ppara, pstatus);
        splp.setDividerLocation(360 + splp.getInsets().top);
        ((Container) hm.get("cp")).add(BorderLayout.CENTER, splp);
        
        hm.put("table", table);
/*
        JPanel pb2 = new JPanel(new FlowLayout());
        pb2.add( new JButton("status"));
        pb2.add( new JButton("status1"));
        
        JPanel pb3 = new JPanel(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.gridx = 0; gc.gridy = 0; gc.gridheight = 2;
        pb3.add(pb1, gc);
        gc.gridx = 0; gc.gridy = 3; gc.gridheight = 2;
        pb3.add(pb2, gc);
        msp.add(new JScrollPane(pb3), "status");
*/
    }
}

class ProgressRenderer extends DefaultTableCellRenderer {
    private final JProgressBar b = new JProgressBar(0, 100);
    public ProgressRenderer() {
        super();
        setOpaque(true);
        b.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Integer i = (Integer) value;
        String text = "Completed";
        if (i < 0) {
            text = "Error";
        } else if (i < 100) {
            b.setValue(i);
            if (row == 0 ) {
                b.setForeground(Color.green);
            } else if (row%2 == 0) {
                b.setForeground(Color.darkGray);
            } else if (row%2 == 1) {
                b.setForeground(Color.ORANGE);
            }
            return b;
        }
        super.getTableCellRendererComponent(table, text, isSelected, hasFocus, row, column);
        return this;
    }
}

