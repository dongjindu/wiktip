/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.wiktionary;

import com.sun.xml.internal.ws.addressing.W3CAddressingConstants;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
public class test {
    private HashMap<String, String> hm = new HashMap();
    private static int initnumber = 0;
    public static void main(String args[]) throws Exception {
        test6();
    }
    test(HashMap<String, String> phm) {
        hm = phm;
        initnumber = initnumber + 1;
        hm.put("nth init : " + Integer.valueOf(initnumber).toString(), ".");
    }
    public static void testa() {
        try {
            DAO dao = new DAO();
            ResultSet rs;
            /*
             * String p = "z%"; dao.update("update voc set htmled = false where
             * word not like ?"); dao.setString(1, p); dao.executeUpdate();
             */
            dao.update("drop table voc3 if exists");
            dao.executeUpdate();
            dao.update("create cached table voc3(word varchar(50),"
                    + "sn1 int,"
                    + "sn2 int,"
                    + "sn3 int,"
                    + "sn4 int,"
                    + "type int,"
                    + "etym varchar(50),"
                    + "pronun varchar(50),"
                    + "pronus varchar(50),"
                    + "pronuk varchar(50),"
                    + "image varchar(50),"
                    + "imageurl varchar(200),"
                    + "antonyms varchar(100),"
                    + "synomyms varchar(100),"
                    + "meaning varchar(2000),"
                    + "primary key(word, sn1, sn2, sn3, sn4))");
            dao.executeUpdate();
            dao.update("drop table types if exists");
            dao.executeUpdate();
            dao.update("create memory table types(ref varchar(50), type int, abr varchar(10))");
            dao.executeUpdate();
            dao.update("create unique index types1 on types (ref)");
            dao.executeUpdate();
            dao.update("create unique index types2 on types (type, abr)");
            dao.executeUpdate();
            dao.update("create index types3  on types (abr)");
            dao.executeUpdate();
            dao.update("insert into types (ref, type, abr) values(?, ?, ?)");
            dao.setString(1, "Etymology");
            dao.setInt(2, 1);
            dao.setString(3, "O");
            dao.executeUpdate();
            dao.update("insert into types (ref, type, abr) values(?, ?, ?)");
            dao.setString(1, "Pronunciation");
            dao.setInt(2, 2);
            dao.setString(3, "");
            dao.executeUpdate();
            dao.update("insert into types (ref, type, abr) values(?, ?, ?)");
            dao.setString(1, "Synonym");
            dao.setInt(2, 11);
            dao.setString(3, "Syn");
            dao.executeUpdate();
            dao.update("insert into types (ref, type, abr) values(?, ?, ?)");
            dao.setString(1, "Synonyms");
            dao.setInt(2, 12);
            dao.setString(3, "Syn");
            dao.executeUpdate();
            dao.update("insert into types (ref, type, abr) values(?, ?, ?)");
            dao.setString(1, "Antonym");
            dao.setInt(2, 13);
            dao.setString(3, "Ant");
            dao.executeUpdate();
            dao.update("insert into types (ref, type, abr) values(?, ?, ?)");
            dao.setString(1, "Antonyms");
            dao.setInt(2, 14);
            dao.setString(3, "Ant");
            dao.executeUpdate();
            dao.update("insert into types (ref, type, abr) values(?, ?, ?)");
            dao.setString(1, "Image");
            dao.setInt(2, 15);
            dao.setString(3, "Pic");
            dao.executeUpdate();
            dao.update("insert into types (ref, type, abr) values(?, ?, ?)");
            dao.setString(1, "Noun");
            dao.setInt(2, 101);
            dao.setString(3, "n");
            dao.executeUpdate();
            dao.update("insert into types (ref, type, abr) values(?, ?, ?)");
            dao.setString(1, "Verb");
            dao.setInt(2, 102);
            dao.setString(3, "v");
            dao.executeUpdate();
            dao.update("insert into types (ref, type, abr) values(?, ?, ?)");
            dao.setString(1, "Pronoun");
            dao.setInt(2, 103);
            dao.setString(3, "pr");
            dao.executeUpdate();
            dao.update("insert into types (ref, type, abr) values(?, ?, ?)");
            dao.setString(1, "Adjective");
            dao.setInt(2, 104);
            dao.setString(3, "Adj");
            dao.executeUpdate();
            dao.update("insert into types (ref, type, abr) values(?, ?, ?)");
            dao.setString(1, "Adverb");
            dao.setInt(2, 105);
            dao.setString(3, "Adv");
            dao.executeUpdate();
            dao.update("insert into types (ref, type, abr) values(?, ?, ?)");
            dao.setString(1, "Preposition");
            dao.setInt(2, 106);
            dao.setString(3, "prep");
            dao.executeUpdate();
            dao.update("insert into types (ref, type, abr) values(?, ?, ?)");
            dao.setString(1, "Particle");
            dao.setInt(2, 107);
            dao.setString(3, "part");
            dao.executeUpdate();
            dao.update("insert into types (ref, type, abr) values(?, ?, ?)");
            dao.setString(1, "Interjection");
            dao.setInt(2, 108);
            dao.setString(3, "interj");
            dao.executeUpdate();
            dao.update("insert into types (ref, type, abr) values(?, ?, ?)");
            dao.setString(1, "Conjuction");
            dao.setInt(2, 109);
            dao.setString(3, "conj");
            dao.executeUpdate();
            dao.update("insert into types (ref, type, abr) values(?, ?, ?)");
            dao.setString(1, "Determinal");
            dao.setInt(2, 110);
            dao.setString(3, "det");
            dao.executeUpdate();
            dao.update("insert into types (ref, type, abr) values(?, ?, ?)");
            dao.setString(1, "Numeral");
            dao.setInt(2, 111);
            dao.setString(3, "num");
            dao.executeUpdate();
            dao.update("drop table dict if exists");
            dao.executeUpdate();
            dao.update("create cached table dict(word varchar(50), txt varchar(3000), primary key(word))");
            dao.executeUpdate();
            dao.update("update voc set imaged = false");
            dao.executeUpdate();
            dao.update("checkpoint");
            dao.executeUpdate();
            /*
             * dao.query("select * from voc where imaged = true"); rs =
             * dao.executeQuery(); while (rs.next()) {
             * System.out.print(rs.getString(1) + "\t"); //
             * System.out.print(rs.getString("sn1") + "\t"); //
             * System.out.print(rs.getString("image") + "\t"); //
             * System.out.print(rs.getString("imageurl")); System.out.println();
             * //System.out.println(rs.getInt("sn1")); }
             */
            /*
             * dao.update("create cached table image(word varchar(50)," + "sn
             * int," + "imageurl varchar(200), primary key(word, sn))");
             * dao.executeUpdate();
             *
             * dao.query("select word from voc where htmled = false limit 21000,
             * 10"); ResultSet rs; rs = dao.executeQuery(); while (rs.next()) {
             * System.out.println(rs.getString(1)); }
             */
        } catch (DAOException daoe) {
            daoe.printStackTrace();
            System.err.print("!DAO Exception!");
//        } catch (SQLException sqle) {
//            sqle.printStackTrace();
            System.err.print("!SQL Exception!");
        }
    }

    private static void testxmlbydocumentbuilder() {
        XPathFactory xPathfactory = XPathFactory.newInstance();
        XPath xpath = xPathfactory.newXPath();
        //XPathExpression expr = xpath.compile(<xpath_expression>);
        try {
            //throw new UnsupportedOperationException("Not yet implemented");
            String path = new File(".").getCanonicalPath() + "\\html\\";
            Document doc = Res.loadXMLFromFile(new File(path + "flytest.html"));
            XPathExpression expr = xpath.compile("/html/body");
            //NodeList nl = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
            NodeList nl = doc.getChildNodes();
            for (int i = 0; i < nl.getLength(); i++) {
                System.out.print("Nodename: " + nl.item(i).getNodeName() + "..");
                System.out.print("Nodetype: " + nl.item(i).getNodeType() + "..");
                System.out.print("Nodevalue" + nl.item(i).getNodeValue().toString());
            }
            //doc
        } catch (Exception ex) {
            Logger.getLogger(test.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void test1() {
        try {
            //throw new UnsupportedOperationException("Not yet implemented");
            File input = new File((new File(".").getCanonicalPath()) + "\\html\\fly.html");
            org.jsoup.nodes.Document doc = Jsoup.parse(input, "UTF-8", "");
            List<Node> nl = doc.childNodes();
            /*
             * for (int i = 0; i < nl.size(); i ++) {
             * System.out.print("Nodename: " + nl.get(i).nodeName() + "..");
             * System.out.print("Node class attribute: " +
             * nl.get(i).attr("class") + ".."); System.out.println("Outerhtml:"
             * + nl.get(i).outerHtml());
             *//*
             * org.w3c.dom.Node node = (org.w3c.dom.Node) nl.get(i);
             * System.out.print("w3c Nodename: " + node.getNodeName() + "..");
             * System.out.print("w3c Nodetype: " + node.getNodeType() + "..");
             * System.out.println("w3c Nodevalue: " + node.getNodeValue() +
             * "..");
             */
//            }
            Elements pngs = doc.select("img.thumbimage"
                    + "");
            for (int i = 0; i < pngs.size(); i++) {
                //System.out.print("Element class name: " + pngs.get(i).className());
                //System.out.print("Element value: " + pngs.get(i).val());
                //System.out.print("Element text: " + pngs.get(i).text());
                //System.out.print("Element class name by attr:" + pngs.get(i).attr("class"));
                //System.out.print("Element url: " + pngs.get(i).absUrl("srcset"));
                //System.out.print("Element base uri: " + pngs.get(i).baseUri());
                //System.out.print("Ele :" + pngs.get(i).attributes().toString());
                System.out.println(" tag name:" + pngs.get(i).tagName());
            }
            String ifn = "abc/def/hijk";
            int si0 = ifn.lastIndexOf('/');
            int si1 = ifn.substring(0, si0 - 1).lastIndexOf('/');
            if (ifn.substring(si0).equals("/")) {
                System.out.println("file name with last char is /: " + ifn.substring(si1 + 1, si0));
            } else {
                System.out.println("file name with last char is not /: " + ifn.substring(si0 + 1, ifn.length()));
            }

        } catch (IOException ex) {
            Logger.getLogger(test.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void test2() {
        //throw new UnsupportedOperationException("Not yet implemented");
        File input;
        try {
            input = new File((new File(".").getCanonicalPath()) + "\\html\\fly.html");
            org.jsoup.nodes.Document doc = Jsoup.parse(input, "UTF-8", "");
            Elements es = doc.select("span.tocnumber");
            for (int i = 0; i < es.size(); i++) {
                System.out.println("toc number find: " + es.get(i).parent().outerHtml().toString());
                System.out.println("Sibling element  html: " + es.get(i).siblingElements().get(0).html());
                System.out.println("id of parent: " + es.get(i).parent().attr("href"));
                System.out.println("Attribute to get classname: " + es.get(i).attr("class"));
            }
        } catch (IOException ex) {
            Logger.getLogger(test.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private static void test3() {
        try {
            DAO dao = new DAO();
            ResultSet rs;
            dao.query("select ref, type, abr from types");
            rs = dao.executeQuery();
            while (rs.next()) {
                System.out.print(rs.getString(1) + "\t");
                System.out.print(rs.getInt(2));
                System.out.print("\t");
                System.out.print(rs.getString(3));
                System.out.println();
            }
            String[] str = new String[5];
            for (int i = 0; i < str.length; i++) {
                System.out.print("i is: ");
                System.out.print(i);
                System.out.println();
            }
            System.out.println(str.length);
        } catch (DAOException daoe) {
        } catch (SQLException sqle) {
        }
    }

    private static void test4() {
        //throw new UnsupportedOperationException("Not yet implemented");
        String a = "abc.def";
        String[] split = {"", "", "", ""};
        ArrayList al;
        Collection c;
        Collections c1;
        split = a.split("\\.");
        System.out.println("\u002E");
        System.out.println(split.length);
        for (int i = 0; i < split.length; i++) {
            System.out.print(split[i]);
            System.out.print("\t");
            System.out.println(i);
        }
//        System.out.println(split = a.split("."));
    }

    private static void test5() {
        try {
            DAO dao = new DAO();
            dao.update("drop table types if exists");
            dao.executeUpdate();
            dao.update("create memory table types(ref varchar(50), type int, abr varchar(10))");
            dao.executeUpdate();
            dao.update("create unique index types1 on types (ref)");
            dao.executeUpdate();
            dao.update("create unique index types2 on types (type, abr)");
            dao.executeUpdate();
            dao.update("create index types3  on types (abr)");
            dao.executeUpdate();
            dao.update("insert into types (ref, type, abr) values(?, ?, ?)");
            dao.setString(1, "English");
            dao.setInt(2, 99);
            dao.setString(3, "eng");
            dao.executeUpdate();
            dao.update("insert into types (ref, type, abr) values(?, ?, ?)");
            dao.setString(1, "Etymology");
            dao.setInt(2, 1);
            dao.setString(3, "O");
            dao.executeUpdate();
            dao.update("insert into types (ref, type, abr) values(?, ?, ?)");
            dao.setString(1, "Pronunciation");
            dao.setInt(2, 2);
            dao.setString(3, "");
            dao.executeUpdate();
            dao.update("insert into types (ref, type, abr) values(?, ?, ?)");
            dao.setString(1, "Synonym");
            dao.setInt(2, 11);
            dao.setString(3, "Syn");
            dao.executeUpdate();
            dao.update("insert into types (ref, type, abr) values(?, ?, ?)");
            dao.setString(1, "Synonyms");
            dao.setInt(2, 12);
            dao.setString(3, "Syn");
            dao.executeUpdate();
            dao.update("insert into types (ref, type, abr) values(?, ?, ?)");
            dao.setString(1, "Antonym");
            dao.setInt(2, 13);
            dao.setString(3, "Ant");
            dao.executeUpdate();
            dao.update("insert into types (ref, type, abr) values(?, ?, ?)");
            dao.setString(1, "Antonyms");
            dao.setInt(2, 14);
            dao.setString(3, "Ant");
            dao.executeUpdate();
            dao.update("insert into types (ref, type, abr) values(?, ?, ?)");
            dao.setString(1, "Image");
            dao.setInt(2, 15);
            dao.setString(3, "Pic");
            dao.executeUpdate();
            dao.update("insert into types (ref, type, abr) values(?, ?, ?)");
            dao.setString(1, "Noun");
            dao.setInt(2, 101);
            dao.setString(3, "n");
            dao.executeUpdate();
            dao.update("insert into types (ref, type, abr) values(?, ?, ?)");
            dao.setString(1, "Verb");
            dao.setInt(2, 102);
            dao.setString(3, "v");
            dao.executeUpdate();
            dao.update("insert into types (ref, type, abr) values(?, ?, ?)");
            dao.setString(1, "Pronoun");
            dao.setInt(2, 103);
            dao.setString(3, "pr");
            dao.executeUpdate();
            dao.update("insert into types (ref, type, abr) values(?, ?, ?)");
            dao.setString(1, "Adjective");
            dao.setInt(2, 104);
            dao.setString(3, "Adj");
            dao.executeUpdate();
            dao.update("insert into types (ref, type, abr) values(?, ?, ?)");
            dao.setString(1, "Adverb");
            dao.setInt(2, 105);
            dao.setString(3, "Adv");
            dao.executeUpdate();
            dao.update("insert into types (ref, type, abr) values(?, ?, ?)");
            dao.setString(1, "Preposition");
            dao.setInt(2, 106);
            dao.setString(3, "prep");
            dao.executeUpdate();
            dao.update("insert into types (ref, type, abr) values(?, ?, ?)");
            dao.setString(1, "Particle");
            dao.setInt(2, 107);
            dao.setString(3, "part");
            dao.executeUpdate();
            dao.update("insert into types (ref, type, abr) values(?, ?, ?)");
            dao.setString(1, "Interjection");
            dao.setInt(2, 108);
            dao.setString(3, "interj");
            dao.executeUpdate();
            dao.update("insert into types (ref, type, abr) values(?, ?, ?)");
            dao.setString(1, "Conjuction");
            dao.setInt(2, 109);
            dao.setString(3, "conj");
            dao.executeUpdate();
            dao.update("insert into types (ref, type, abr) values(?, ?, ?)");
            dao.setString(1, "Determinal");
            dao.setInt(2, 110);
            dao.setString(3, "det");
            dao.executeUpdate();
            dao.update("insert into types (ref, type, abr) values(?, ?, ?)");
            dao.setString(1, "Numeral");
            dao.setInt(2, 111);
            dao.setString(3, "num");
            dao.executeUpdate();
            dao.update("drop table dict if exists");
            dao.executeUpdate();
            dao.update("create cached table dict(word varchar(50), txt varchar(3000), primary key(word))");
            dao.executeUpdate();
            //throw new UnsupportedOperationException("Not yet implemented");
            dao.update("checkpoint");
            dao.executeUpdate();
            dao.query("select ref, type, abr from types");
            ResultSet rs;
            rs = dao.executeQuery();
            while (rs.next()) {
                System.out.println(rs.getString(1) + "," + Integer.valueOf(rs.getInt(2)).toString() + "," + rs.getString(3));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static void testetym() {
        //throw new UnsupportedOperationException("Not yet implemented");
        File input;
        try {
            input = new File((new File(".").getCanonicalPath()) + "\\html\\fly.html");
            org.jsoup.nodes.Document doc = Jsoup.parse(input, "UTF-8", "");
            //Elements es = doc.select("span.tocnumber");
            org.jsoup.nodes.Element e = doc.getElementById("Etymology_1").parent();

            Elements es = new Elements(); // =e.siblingElements();
            org.jsoup.nodes.Element en = e;
            int k = 0;
            label2:
            {
                do {
                    en = en.nextElementSibling();
                    Elements enk = en.children();
                    for (int i = 0; i < enk.size(); i++) {
                        if (enk.get(i).attr("class").equals("etyl")) {
                            String s2 = enk.get(i).html() + "::" + enk.get(i).nextElementSibling().html();
                            System.out.println("s2:" + s2);
                            break label2;
                        }
                    }
                    if (en != null) {
                        es.add(en);
                        System.out.print(Jsoup.parse(en.html()).text());
                    }

                    k = k + 1;
                    System.out.print(k);
                    System.out.print(",");
                    //if (k>200) break;
                } while (en != null);
            }
            System.out.print("\n");
            label1:
            {
                /*
                 * for (int i=0; i<es.size(); i++) { //System.out.print(": " +
                 * es.get(i).html()); System.out.print(" , Sibling element
                 * tagname: " + es.get(i).tagName()); // System.out.println("id
                 * of parent: " + es.get(i).parent().attr("href"));
                 * System.out.println(" , Attribute to get classname: " +
                 * es.get(i).attr("class")); Elements eskids =
                 * es.get(i).children(); for (int j=0; j < eskids.size(); j++) {
                 * if (eskids.get(j).attr("class").equals("etyl")) {
                 * System.out.println("Only class etyl!!!" +
                 * Jsoup.parse(eskids.get(j).html()).text());
                 * System.out.print("Next sibling of etyl class element :" +
                 * Jsoup.parse(eskids.get(j).nextElementSibling().outerHtml()).text());
                 * break label1; } }
            }
                 */
            }
        } catch (IOException ex) {
            Logger.getLogger(test.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void testpronun() {
        try {
            File input = new File((new File(".").getCanonicalPath()) + "\\html\\neither.html");
            org.jsoup.nodes.Document doc = Jsoup.parse(input, "UTF-8", "");
            //Elements es = doc.select("span.tocnumber");
            org.jsoup.nodes.Element e = doc.getElementById("Pronunciation").parent();
            if (e == null) {
                throw new Exception("find element by ID failed!");
            }
            System.out.println(e.tagName());
            Elements es = e.nextElementSibling().children();
            //Elements es = e.parents();

            for (int i = 0; i < es.size(); i++) {
                String s2;
                if (Jsoup.parse(es.get(i).outerHtml()).select("a").select(".extiw").get(0).attr("title").equals("w:British English")) {
                    System.out.println("British English");
                }
                System.out.println(Jsoup.parse(es.get(i).outerHtml()).select("a"));
                System.out.println(es.get(i).tagName());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void test6() {
//        org.jsoup.nodes.Document d = Jsoup.parse("<ol><li>how many kids does d have?</li></ol><ul><li>1:level 1 li<ul><li>level 2 li</li></ul></li><li>2: still level1</li></ul>");
        org.jsoup.nodes.Document d = Jsoup.parse("<ltext><wpr>yur-!self</wpr><date>14th century</date>(1 a):that identical one that is you used reflexively you might hurt yourself, for emphasis carry them yourself, or in absolute constructions(b):your normal, healthy, or sane condition or self you haven't been yourself lately(2):oneself<entry>do-it-yourself</entry><date>1952</date>:an activity in which one does something oneself or on one's own initiative</ltext>");
        //for (int i = 0; i < d.select("ul").get(1).parent().select("li").size(); i++) {
            //System.out.println("child" + Integer.valueOf(i).toString() + d.select("ul").get(1).parent().select("li").get(i).html());
        //}
        //for (int i = 0; i < d.children().select("li").size(); i++) {
            //      System.out.print("now printing " + Integer.valueOf(i).toString() + "th html");
            //      System.out.println(d.children().select("li").html());
        //}
        //for (int i = 0; i < d.children().get(0).children().size(); i++) {
            //      System.out.println("tag: " + d.children().get(0).children().get(i).tagName());
        //}
        //String s = d.children().select("ul>li").get(0).html();
        String s = d.select("ltext").get(0).ownText();
        System.out.print("\n" + s);
//        System.out.println("until substring <ul>" + s.substring(0, s.indexOf("<ul>")));
//        System.out.println("Empty string length:" + "  ".length());
//        System.out.println("user.home is " + System.getProperty("user.home"));
//        System.out.println("indexOf boundary testing: " + Integer.valueOf("abc".indexOf("a")));

//        throw new UnsupportedOperationException("Not yet implemented");
    }

    private static void test7() {
        try {
            org.jsoup.nodes.Document jsoupdoc;
            File file = new File("C:\\Users\\rose\\Documents\\NetBeansProjects\\wiktionary\\html\\abacus.html");
            jsoupdoc = Jsoup.parse(file, "UTF-8");
            Elements images = jsoupdoc.select("img.thumbimage");
            for (int i = 0; i < images.size(); i++) {
                String uri = images.get(i).attr("src");
                URL url = new URL("http://" + uri);
                System.out.println(url.toURI().getPath().substring(url.toURI().getPath().lastIndexOf("\\.")));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        //throw new UnsupportedOperationException("Not yet implemented");
    }

    private static void test8() {
        //throw new UnsupportedOperationException("Not yet implemented");
        Pattern p = Pattern.compile("^poop|^princess");
        Matcher m = p.matcher("poop");
        System.out.print("\n");
        System.out.print(m.find());
    }

    private static void test9() {
        DAO dao = new DAO();
        try {
            dao.update("drop table types if exists");
            dao.executeUpdate();
            dao.update("create memory table types(ref varchar(50), type int, abr varchar(10))");
            dao.executeUpdate();
            dao.update("create unique index types1 on types (ref)");
            dao.executeUpdate();
            dao.update("create unique index types2 on types (type, abr)");
            dao.executeUpdate();
            dao.update("create index types3  on types (abr)");
            dao.executeUpdate();
            dao.update("insert into types (ref, type, abr) values(?, ?, ?)");
            dao.setString(1, "Etymology");
            dao.setInt(2, 1);
            dao.setString(3, "O");
            dao.executeUpdate();
            dao.update("insert into types (ref, type, abr) values(?, ?, ?)");
            dao.setString(1, "Pronunciation");
            dao.setInt(2, 2);
            dao.setString(3, "");
            dao.executeUpdate();
            dao.update("insert into types (ref, type, abr) values(?, ?, ?)");
            dao.setString(1, "Synonym");
            dao.setInt(2, 11);
            dao.setString(3, "Syn");
            dao.executeUpdate();
            dao.update("insert into types (ref, type, abr) values(?, ?, ?)");
            dao.setString(1, "Synonyms");
            dao.setInt(2, 12);
            dao.setString(3, "Syn");
            dao.executeUpdate();
            dao.update("insert into types (ref, type, abr) values(?, ?, ?)");
            dao.setString(1, "Antonym");
            dao.setInt(2, 13);
            dao.setString(3, "Ant");
            dao.executeUpdate();
            dao.update("insert into types (ref, type, abr) values(?, ?, ?)");
            dao.setString(1, "Antonyms");
            dao.setInt(2, 14);
            dao.setString(3, "Ant");
            dao.executeUpdate();
            dao.update("insert into types (ref, type, abr) values(?, ?, ?)");
            dao.setString(1, "Image");
            dao.setInt(2, 15);
            dao.setString(3, "Pic");
            dao.executeUpdate();
            dao.update("insert into types (ref, type, abr) values(?, ?, ?)");
            dao.setString(1, "English");
            dao.setInt(2, 99);
            dao.setString(3, "Eng");
            dao.executeUpdate();
            dao.update("insert into types (ref, type, abr) values(?, ?, ?)");
            dao.setString(1, "Noun");
            dao.setInt(2, 101);
            dao.setString(3, "n");
            dao.executeUpdate();
            dao.update("insert into types (ref, type, abr) values(?, ?, ?)");
            dao.setString(1, "Verb");
            dao.setInt(2, 102);
            dao.setString(3, "v");
            dao.executeUpdate();
            dao.update("insert into types (ref, type, abr) values(?, ?, ?)");
            dao.setString(1, "Pronoun");
            dao.setInt(2, 103);
            dao.setString(3, "pr");
            dao.executeUpdate();
            dao.update("insert into types (ref, type, abr) values(?, ?, ?)");
            dao.setString(1, "Adjective");
            dao.setInt(2, 104);
            dao.setString(3, "Adj");
            dao.executeUpdate();
            dao.update("insert into types (ref, type, abr) values(?, ?, ?)");
            dao.setString(1, "Adverb");
            dao.setInt(2, 105);
            dao.setString(3, "Adv");
            dao.executeUpdate();
            dao.update("insert into types (ref, type, abr) values(?, ?, ?)");
            dao.setString(1, "Preposition");
            dao.setInt(2, 106);
            dao.setString(3, "prep");
            dao.executeUpdate();
            dao.update("insert into types (ref, type, abr) values(?, ?, ?)");
            dao.setString(1, "Particle");
            dao.setInt(2, 107);
            dao.setString(3, "part");
            dao.executeUpdate();
            dao.update("insert into types (ref, type, abr) values(?, ?, ?)");
            dao.setString(1, "Interjection");
            dao.setInt(2, 108);
            dao.setString(3, "interj");
            dao.executeUpdate();
            dao.update("insert into types (ref, type, abr) values(?, ?, ?)");
            dao.setString(1, "Conjunction");
            dao.setInt(2, 109);
            dao.setString(3, "conj");
            dao.executeUpdate();
            dao.update("insert into types (ref, type, abr) values(?, ?, ?)");
            dao.setString(1, "Determiner");
            dao.setInt(2, 110);
            dao.setString(3, "det");
            dao.executeUpdate();
            dao.update("insert into types (ref, type, abr) values(?, ?, ?)");
            dao.setString(1, "Numeral");
            dao.setInt(2, 111);
            dao.setString(3, "num");
            dao.executeUpdate();
            dao.update("insert into types (ref, type, abr) values(?, ?, ?)");
            dao.setString(1, "Article");
            dao.setInt(2, 112);
            dao.setString(3, "num");
            dao.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void test10() {
        try {
            int done = 0;
            FileWriter fw = new FileWriter("c:\\temp\\zz001.txt");
            BufferedWriter mbrWriter = new BufferedWriter(fw);
            while (done < 10000000) {
                done = done + 1;
                String s1 = "\ns1 " + Integer.valueOf(done).toString();
                //do writings
                fw.append(s1);
            }
            mbrWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //throw new UnsupportedOperationException("Not yet implemented");
    }
}
