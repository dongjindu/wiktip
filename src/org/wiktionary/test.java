/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.wiktionary;

import com.sun.xml.internal.ws.addressing.W3CAddressingConstants;
import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Node;
//import org.w3c.dom.Node;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

/**
 *
 * @author rose
 */
public class test {

    public static void main(String args[]) throws Exception {
        test4();
    }

    public static void test() {
        try {
            DAO dao = new DAO();
            ResultSet rs;
            /*
             * String p = "z%"; dao.update("update voc set htmled = false where
             * word not like ?"); dao.setString(1, p); dao.executeUpdate();
             */
/*            dao.update("drop table voc3 if exists");
            dao.executeUpdate();
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
                    + "meaning varchar(800),"
                    + "primary key(word, sn1, sn2, sn3, sn4))");
            dao.executeUpdate();
            dao.update("update voc set imaged = false");
            dao.executeUpdate();*/
            dao.query("select * from voc where imaged = true");
            rs = dao.executeQuery();
            while (rs.next()) {
                System.out.print(rs.getString(1) + "\t");
//                System.out.print(rs.getString("sn1") + "\t");
//                System.out.print(rs.getString("image") + "\t");
//                System.out.print(rs.getString("imageurl"));
                System.out.println();
                //System.out.println(rs.getInt("sn1"));
            }

            /*
             * dao.update("create cached table image(word varchar(50)," + "sn
             * int," + "imageurl varchar(200), primary key(word, sn))");
             * dao.executeUpdate();
             *
             * dao.query("select word from voc where htmled = false limit 21000,
             * 10"); ResultSet rs; rs = dao.executeQuery(); while (rs.next()) {
             * System.out.println(rs.getString(1));
            }
             */
        } catch (DAOException daoe) {
            daoe.printStackTrace();
            System.err.print("!DAO Exception!");
        } catch (SQLException sqle) {
            sqle.printStackTrace();
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
             * System.out.println("w3c Nodevalue: " + node.getNodeValue() + "..");
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
            for (int i=0; i<es.size(); i++) {
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
            dao.query("select word, sn1 from voc3 where sn1 > 0");
            rs = dao.executeQuery();
            while (rs.next()) {
                System.out.print(rs.getString(1) + "\t");
                System.out.print(rs.getInt(2));
                System.out.println();
            }
            String[] str = new String[5];
            for (int i=0; i<str.length; i++) {
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
        split = a.split("\\.");
        System.out.println("\u002E");
        System.out.println(split.length);
        for (int i =0; i< split.length; i++) {
            System.out.print(split[i]);
            System.out.print("\t");
            System.out.println(i);
        }
//        System.out.println(split = a.split("."));
    }
}
