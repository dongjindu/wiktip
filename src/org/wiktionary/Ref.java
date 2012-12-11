/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.wiktionary;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author yetaai
 */
public class Ref {
    private static HashMap<String, Integer> typebyref = new HashMap();
    private static HashMap<Integer, String> refbytype = new HashMap();
    private static HashMap<Integer, String> abrbytype = new HashMap();
    private static HashMap<String, String> eoppath = new HashMap();
    
    static {
        DAO dao = new DAO();
        ResultSet rs;
        try {
            int idao = dao.query("select ref, type, abr from types");
            rs = dao.executeQuery(idao);
            while (rs.next()) {
                typebyref.put(rs.getString(1).toUpperCase(), Integer.valueOf(rs.getInt(2)));
                refbytype.put(Integer.valueOf(rs.getInt(2)), rs.getString(1).toUpperCase());
                abrbytype.put(Integer.valueOf(rs.getInt(2)), rs.getString(3));
            }
            iniEop();
        } catch (DAOException ex) {
            Logger.getLogger(Ref.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException e) {
            Logger.getLogger(Ref.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    public static int getType (String ref) {
        return typebyref.get(ref.toUpperCase());
    }
    public static String getRef (int type) {
        return refbytype.get(type);
    }
    public static String getAbr (int type) {
        return abrbytype.get(type);
    }
    public static String getAbr(String ref) {
        return abrbytype.get(typebyref.get(ref.toUpperCase()));
    }
    public static boolean hasType (String ref) {
        return typebyref.containsKey(ref.toUpperCase());
    }
    public static boolean hasType (int type) {
        return refbytype.containsKey(type);
    }
    public static boolean hasRef(String ref) {
        return typebyref.containsKey(ref.toUpperCase());
    }
    public static boolean hasRef(int type) {
        return refbytype.containsKey(type);
    }
    public static boolean hasAbr (String abr) {
        return abrbytype.containsValue(abr);
    }
    public static boolean isNonChildType (String ref) {
        if (typebyref.get(ref.toUpperCase()) < 10) {
            return true;
        } else {
            return false;
        }
    }
    public static boolean isNonChildType (int type) {
        if (type < 10) {
            return true;
        } else {
            return false;
        }
    }
    public static boolean isEtym(String ref) {
        if (typebyref.get(ref.toUpperCase()) == 1) {
            return true;
        } else {
            return false;
        }
    }
    public static boolean isPronun(String ref) {
        if (typebyref.get(ref.toUpperCase()) == 2) {
            return true;
        } else {
            return false;
        }
    }
    public static boolean isSynonym(String ref) {
        if (typebyref.get(ref.toUpperCase()) == 11 || typebyref.get(ref.toUpperCase()) == 12) {
            return true;
        } else {
            return false;
        }
    }
    public static boolean isAntonym(String ref) {
        if (typebyref.get(ref.toUpperCase()) == 13 || typebyref.get(ref.toUpperCase()) == 14) {
            return true;
        } else {
            return false;
        }
    }

    private static void iniEop() {
        eoppath.put("l-e", "le");
        eoppath.put("l-p", "lp");
        eoppath.put("l-o", "lo");
        eoppath.put("le-p", "lep");
        eoppath.put("le-o", "leo");
        eoppath.put("lp-e", "lpe");
        eoppath.put("lp-o", "lpo");
        eoppath.put("lo-o", "loo");
        eoppath.put("lep-o", "leop");
        eoppath.put("lpe-o", "lpeo");
        eoppath.put("lpeo-p", "lp");
        eoppath.put("lepo-p", "lep");
        eoppath.put("lpeo-e", "lpe");
        eoppath.put("lepo-e", "le");
        //throw new UnsupportedOperationException("Not yet implemented");
    }
}
