/**
 * @author yetaai
 * yetaai@gmail.com
 * This software piece is apache license. But the contents generated is governed by wiktionary.org policy.
 */
package org.wiktionary;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.Exception;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.JComboBox;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
//import java.a
public class DicActionListener implements ActionListener {
    private HashMap<String, Object> hm;

    public DicActionListener(HashMap h) {
        hm = h;
    }
    @Override
    public void actionPerformed(ActionEvent ae){
        //throw new UnsupportedOperationException("Not supported yet.");
/*        if (ae.getSource() instanceof JButton) {
            System.out.println("Command" + ((JButton) ae.getSource()).getActionCommand());
        }
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
//            genDict();
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
                }
            }
            try {
                Res.getProp().save();
            } catch (Exception e) {
                e.printStackTrace();
            }        
    }
}
