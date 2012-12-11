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
import java.util.concurrent.atomic.AtomicInteger;
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

    private static void test13() {
        DAO dao = new DAO();
        try {
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
                dao.setString(1, "Etymology", idao15); dao.setInt(2, 1, idao15); dao.setString(3, "O", idao15); dao.executeUpdate(idao15);
                int idao16 = dao.update("insert into types (ref, type, abr) values(?, ?, ?)");
                dao.setString(1, "Pronunciation", idao16); dao.setInt(2, 2, idao16); dao.setString(3, "V", idao16); dao.executeUpdate(idao16);
                int idao17 = dao.update("insert into types (ref, type, abr) values(?, ?, ?)");
                dao.setString(1, "Synonym", idao17); dao.setInt(2, 11, idao17); dao.setString(3, "Syn", idao17); dao.executeUpdate(idao17);
                int idao18 = dao.update("insert into types (ref, type, abr) values(?, ?, ?)");
                dao.setString(1, "Synonyms", idao18); dao.setInt(2, 12, idao18); dao.setString(3, "Syn", idao18); dao.executeUpdate(idao18);
                int idao19 = dao.update("insert into types (ref, type, abr) values(?, ?, ?)");
                dao.setString(1, "Antonym", idao19); dao.setInt(2, 13, idao19); dao.setString(3, "Ant", idao19); dao.executeUpdate(idao19);
                int idao20 = dao.update("insert into types (ref, type, abr) values(?, ?, ?)");
                dao.setString(1, "Antonyms", idao20); dao.setInt(2, 14, idao20); dao.setString(3, "Ant", idao20); dao.executeUpdate(idao20);
                int idao21 = dao.update("insert into types (ref, type, abr) values(?, ?, ?)");
                dao.setString(1, "Image", idao21); dao.setInt(2, 15, idao21); dao.setString(3, "Pic", idao21); dao.executeUpdate(idao21);
                int idao22 = dao.update("insert into types (ref, type, abr) values(?, ?, ?)");
                dao.setString(1, "English", idao22); dao.setInt(2, 99, idao22); dao.setString(3, "Eng", idao22); dao.executeUpdate(idao22);
                int idao23 = dao.update("insert into types (ref, type, abr) values(?, ?, ?)");
                dao.setString(1, "Noun", idao23); dao.setInt(2, 101, idao23); dao.setString(3, "N", idao23); dao.executeUpdate(idao23);
                int idao24 = dao.update("insert into types (ref, type, abr) values(?, ?, ?)");
                dao.setString(1, "Verb", idao24); dao.setInt(2, 102, idao24); dao.setString(3, "V", idao24); dao.executeUpdate(idao24);
                int idao25 = dao.update("insert into types (ref, type, abr) values(?, ?, ?)");
                dao.setString(1, "Pronoun", idao25); dao.setInt(2, 103, idao25); dao.setString(3, "Pr", idao25); dao.executeUpdate(idao25);
                int idao26 = dao.update("insert into types (ref, type, abr) values(?, ?, ?)");
                dao.setString(1, "Adjective", idao26); dao.setInt(2, 104, idao26); dao.setString(3, "Adj", idao26); dao.executeUpdate(idao26);
                int idao27 = dao.update("insert into types (ref, type, abr) values(?, ?, ?)");
                dao.setString(1, "Adverb", idao27); dao.setInt(2, 105, idao27); dao.setString(3, "Adv", idao27); dao.executeUpdate(idao27);
                int idao28 = dao.update("insert into types (ref, type, abr) values(?, ?, ?)");
                dao.setString(1, "Preposition", idao28); dao.setInt(2, 106, idao28); dao.setString(3, "Prep", idao28); dao.executeUpdate(idao28);
                int idao29 = dao.update("insert into types (ref, type, abr) values(?, ?, ?)");
                dao.setString(1, "Particle", idao29); dao.setInt(2, 107, idao29); dao.setString(3, "Part", idao29); dao.executeUpdate(idao29);
                int idao30 = dao.update("insert into types (ref, type, abr) values(?, ?, ?)");
                dao.setString(1, "Interjection", idao30); dao.setInt(2, 108, idao30); dao.setString(3, "Interj", idao30); dao.executeUpdate(idao30);
                int idao31 = dao.update("insert into types (ref, type, abr) values(?, ?, ?)");
                dao.setString(1, "Conjunction", idao31); dao.setInt(2, 109, idao31); dao.setString(3, "Conj", idao31); dao.executeUpdate(idao31);
                int idao32 = dao.update("insert into types (ref, type, abr) values(?, ?, ?)");
                dao.setString(1, "Determiner", idao32); dao.setInt(2, 110, idao32); dao.setString(3, "Det", idao32); dao.executeUpdate(idao32);
                int idao33 = dao.update("insert into types (ref, type, abr) values(?, ?, ?)");
                dao.setString(1, "Numeral", idao33); dao.setInt(2, 111, idao33); dao.setString(3, "Num", idao33); dao.executeUpdate(idao33);
                int idao34 = dao.update("insert into types (ref, type, abr) values(?, ?, ?)");
                dao.setString(1, "Article", idao34); dao.setInt(2, 112, idao34); dao.setString(3, "Art", idao34); dao.executeUpdate(idao34);
                int idao35 = dao.update("shutdown");
                dao.executeUpdate(idao35);
        } catch(Exception e) {
            
        }
    }

    private static void test15() {
        String s1, s2, s3, s4, s5, lEtym, lEtym1;
        lEtym = "Anglo-Norman abc. [somehting]";
        lEtym1 = "abc <sub>(subscript)</sub> <sub>(subscript)</sub> <sup>supscript</sup> how many spaces left?";
                     s1 = lEtym.replace("Middle English ", "<otp>ME</otp>").
                    replace("Old English", "<otp>OE</otp>").replace("Anglo-Norman", "<otp>AN</otp>");
             s2 = s1.replace(s1.substring(s1.indexOf(" ")), "<ow>" + s1.substring(s1.indexOf(" ")).trim() + "</ow>");
             s3 = lEtym.replaceAll("\\[[^\\]]*\\]", "replaced");
             s4 = lEtym1.replaceAll("<sub>(subscript)</sub>", "").replace("  ", " ").replace("  ", " ").replace("  ", " ");
             s5 = lEtym1.replaceAll("<sup>.*</sup>", "supremoved!");
        System.out.print("\nEtym is :" + lEtym);
        System.out.print("\ns1 is : " + s1);
        System.out.print("\ns3 is : " + s3);
        System.out.print("\ns4 is : " + s4);
        System.out.print("\ns5 is : " + s5);
    }
//insert into voc3 (word, sn1, meaning) values ('watch', select max(sn1) from voc3 where word = 'watch', 'testing' purpose
    private final AtomicInteger cter = new AtomicInteger();
    private static void test9() {
        //throw new UnsupportedOperationException("Not yet implemented");
        try {
            DAO dao = new DAO();
            int idao = dao.update("delete from voc3 where regexp_matches(word, ?) and type != ?");
            dao.setString(1, ".*", idao);
            dao.setInt(2, 15, idao);
            dao.executeUpdate(idao);
            int idao3 = dao.update("delete from voc3 where regexp_matches(word, ?) and sn1 >= ?");
            dao.setString(1, ".*", idao3);
            dao.setInt(2, 1000, idao3);
            dao.executeUpdate(idao3);
            int idao1 = dao.update("update voc set xed = false where regexp_matches(word, ?)");
            dao.setString(1, ".*", idao1);
            dao.executeUpdate(idao1);
            int idao2 = dao.update("shutdown");
            dao.executeUpdate(idao2);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.gc();
            System.out.println("You can retest now!");
        }
    }
    public static void test14() {
        int i00= 0, idl = 0, iol = 0, iul = 0;
                                        if (idl < 0) {
                                    if (iol < 0) {
                                        i00 = iul;
                                    } else if (iol >= 0) {
                                        if ( iul < 0 ) {
                                            i00 = iol;
                                        } else if (iul >= 0) {
                                            if (iol < iul) {
                                                i00 = iol;
                                            } else if (iol >= iul) {
                                                i00 = iul;
                                            }
                                        }
                                    }
                                } else if ( idl >= 0 ) {
                                    if (iol < 0) {
                                        if (iul < 0) {
                                            i00 = idl;
                                        } else if (iul >= 0) {
                                            if (iul < idl) {
                                                i00 = iul;
                                            } else if (iul >= idl) {
                                                i00 = idl;
                                            }
                                        }
                                    } else if ( iol >= 0) {
                                        if (iul < 0) {
                                            if (idl < iol) {
                                                i00 = idl;
                                            } else if (idl >= iol) {
                                                i00 = iol;
                                            }
                                        } else if (iul >= 0) {
                                            if (iol >= idl) i00 = idl;
                                            if (iul < i00) i00 = iul;
                                        }
                                    }
                                }

    }
    public static void test12() {
        ResultSet rs;  
        try {
            DAO dao = new DAO();
            int idao0 = dao.query("select word, sn1 from voc3 where word = ?");
            dao.setString(1, "sleep", idao0);
            rs = dao.executeQuery(idao0);
            if (!rs.next()) {
                System.out.print("\nSuccessfully get null");
            } else {
                rs.previous(); //Feature not supported for hsqldb.
                while(rs.next()) {
                    System.out.print("\nNow printing all records:" + rs.getString(1) + "," + rs.getInt(2) );
                }
                int sn1key = rs.getInt(1);
                System.out.print("\nWhere is null");
//                if (sn1key==0) {cter.set(1001);};
            }
        } catch (Exception e) {
                System.out.print("\nnull rs throws an exception!");
            e.printStackTrace();
            //cter.set(1000);
        }
    }
    private HashMap<String, String> hm = new HashMap();
    private static int initnumber = 0;
    public static void main(String args[]) throws Exception {
        //test16();
        test9();
    }
    test(HashMap<String, String> phm) {
        hm = phm;
        initnumber = initnumber + 1;
        hm.put("nth init : " + Integer.valueOf(initnumber).toString(), ".");
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
            int idao = dao.query("select ref, type, abr from types");
            rs = dao.executeQuery(idao);
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
        org.jsoup.nodes.Document d = Jsoup.parse("<p><span class=\"infl-inline\"><b class=\"Latn\" lang=\"en\">zebra</b> (<i>plural</i>&nbsp;<span class=\"form-of plural-form-of lang-en\"><b><span class=\"Latn\" lang=\"en\"><a href=\"/wiki/zebras#English\" title=\"zebras\">zebras</a></span></b></span>)</span></p>", "UTF-8");
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
        String s = Jsoup.parse(d.select("span").select("[class*=plural-form-of]").get(0).html(), "UTF-8").text();
        System.out.print("\n" + s);
        System.out.print("\n(transitive)something of the meaning of a verb".replaceAll(Pattern.quote("(transitive)"), ""));
        System.out.println();
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
    private static void test16() {
        DAO dao = new DAO();
        try {
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
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
