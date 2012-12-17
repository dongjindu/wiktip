package org.wiktionary;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.File;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 *
 * @author yetaai
 * yetaai@gmail.com
 */
public class FL implements FocusListener {
    private HashMap hm;
    private JTextField tf;
//    private static int go = 0;
    public FL(HashMap h) {
        hm = h;
    }
    
    public void  focusGained(FocusEvent fe) {
        try {
            if (((Object) fe.getOppositeComponent()) != null ) {
                JFileChooser jfc;
                File inifile = new File(".");

                if (((JTextField) hm.get("wordlist")).getText().length() > 0) {
                    System.out.println(inifile.getAbsolutePath() +((JTextField) hm.get("dbdir")).getText());
                    jfc = new JFileChooser(inifile.getAbsolutePath() + "/" +((JTextField) hm.get("dbdir")).getText());
                } else {
                    jfc = new JFileChooser();
                }

                int returnVal = jfc.showOpenDialog((JPanel) hm.get("cp"));
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    try {
                        File file = jfc.getSelectedFile();
                        //((JTextField) hm.get("wordlist")).setText(file.getCanonicalPath());
                        JTextField twl = ((JTextField) hm.get("wordlist"));
                        twl.setText(file.getName());
                        Font f = new Font(twl.getFont().getName(), Font.CENTER_BASELINE, twl.getFont().getSize());
                        twl.setFont(f);
                        twl.setForeground(Color.BLACK);
                    } catch (Exception ex) {
                        Logger.getLogger(MWindow.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                //((JTextField) hm.get("htmldir")).requestFocusInWindow();
            }
//            go = (go + 1) % 3;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void focusLost(FocusEvent fe) {
           //((JTextField) hm.get("htmldir")).requestFocusInWindow();
        //do nothing
    }
}

