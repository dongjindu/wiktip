/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.wiktionary;

import com.sun.xml.internal.ws.addressing.W3CAddressingConstants;
import java.io.*;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JTextField;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
//import org.w3c.dom.Node;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

/**
 *
 * @author rose
 */
public class test1 {

    private static HashMap<String, String> hm = new HashMap();

    public static void main(String args[]) throws Exception {
        /*
         * hm.put("key put in main of test1", "value put in main of test1"); for
         * (int i = 0; i < 3; i ++) { test t = new test(hm); } for
         * (Map.Entry<String, String> an:hm.entrySet()) { System.out.print("\n"
         * + an.getKey() + an.getValue()); }
         */
        //genDict();
        test();
    }

    private static void genDict() {
        try {
            DAO dao = new DAO();
            ResultSet rs;
            int i = 0, j = 0;
            int i1 = 0, i2 = 0, i3 = 0, i4 = 0;
            OutputStream o = new FileOutputStream(new File("c:\\temp\\zz002.xml"));
            OutputStreamWriter ow = new OutputStreamWriter(o, "UTF-8");
            BufferedWriter mbrWriter = new BufferedWriter(ow);
            /*
             * dao.query("select count(*) from voc3 where type < 10 or type >
             * 100"); rs = dao.executeQuery(); rs.next(); j = rs.getInt(1);
             */
            dao.query("select word, sn1, sn2, sn3, sn4, type, etym, pronun, pronus, pronuk, image, meaning from voc3 "
                    + "where type < 10 or type > 100 order by word, sn1, sn2, sn3, sn4");
            rs = dao.executeQuery();

            while (rs.next()) {
                j = j + 1;
            }
            String word0 = "", lword0 = "";
            int sn1 = 0, sn2 = 0, sn3 = 0, sn4 = 0;
            int lsn1 = 0, lsn2 = 0, lsn3 = 0, lsn4 = 0;
            ow.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            ow.append("<?xml-stylesheet type=\"text/xsl\" href=\"dict.xsl\"?>");
            ow.append("<dict><item>");
            while (rs.next()) {
                String slayer = "";
                word0 = rs.getString(1);
                sn1 = rs.getInt(2);
                sn2 = rs.getInt(3);
                sn3 = rs.getInt(4);
                sn4 = rs.getInt(5);
                if (!word0.equals(lword0)) {
                    if (i > 0) {
                        ow.append("</d1></item><item>");
                    }
                    ow.append("<word>" + word0 + "</word0>");
                    ow.append(casetype(rs));
                    ow.append("<d1>");
                    i1 = 0;
                    slayer = "";
                } else if (word0.equals(lword0) && sn1 != lsn1) {
                    if (i1 > 0) {
                        ow.append("</d2></d1><d2>");
                    }
                    ow.append(casetype(rs));
                    ow.append("<d2>");
                    i2 = 0;
                } else if (word0.equals(lword0) && sn1 == lsn1 && sn2 != lsn2) {
                    if (i2 > 0) {
                        ow.append("</d3></d2><d3>");
                    }
                    ow.append(casetype(rs));
                    ow.append("<d3>");
                    i3 = 0;
                } else if (word0.equals(lword0) && sn1 == lsn1 && sn2 == lsn2 && sn3 != lsn3) {
                    if (i3 > 0) {
                        ow.append("</d3>");
                    }
                    ow.append(casetype(rs));
                    i4 = 0;
                } else if (word0.equals(lword0) && sn1 == lsn1 && sn2 == lsn2 && sn3 == lsn3) {
                    ow.append(casetype(rs));
                }
                lword0 = word0;
                lsn1 = sn1;
                lsn2 = sn2;
                lsn3 = sn3;
                lsn4 = sn4;
                i = i + 1;
                i1 = i1 + 1;
                i2 = i2 + 1;
                i3 = i3 + 1;
                i4 = i4 + 1;
            }
            ow.append("</item></dict>");
            mbrWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //throw new UnsupportedOperationException("Not yet implemented");
    }

    private static String casetype(ResultSet rs) {
        try {
            //word, sn1, sn2, sn3, sn4, type, etym, pronun, pronus, pronuk, image, meaning
            //1,    2,   3,   4,   5,   6,    7,    8,      9,      10,     11,    12
            String s = "";
            switch (rs.getInt("TYPE")) {
                case 1:
                    s = "<etym>" + rs.getString(7) + "</etym>";
                    break;
                case 2:
                    for (int ii = 8; ii < 11; ii++) {
                        if (rs.getString(ii) != null) {
                            if (!rs.getString(ii).trim().equals("")) {
                                if (ii ==8 || ii == 9) {
                                    s = s + "<pr>" + rs.getInt(ii) + "</pr>";
                                    break; //Only one pronunciation will be found and general first then us then uk. 
                                }
                            }
                        }
                    }
                    break;
                case 101:lll
                    break;
                default:
                    s = "";
            }
            return s;
        } catch (Exception e) {
            e.printStackTrace();
            return "Not found";
        }
        //throw new UnsupportedOperationException("Not yet implemented");
    }

    private static void test() {
        String s1 = "   ";
        System.out.print("\n");
        System.out.print(s1.trim().equals(""));
        //throw new UnsupportedOperationException("Not yet implemented");
    }
}
