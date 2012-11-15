/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.wiktionary;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

/**
 *
 * @author rose
 */
public class test {

    public static void main(String args[]) throws Exception {
        test1();
    }

    public static void test() {
        try {
            DAO dao = new DAO();
            /*
             * String p = "z%"; dao.update("update voc set htmled = false where
             * word not like ?"); dao.setString(1, p); dao.executeUpdate();
             */

            dao.update("create cached table image(word varchar(50),"
                    + "sn int,"
                    + "imageurl varchar(200), primary key(word, sn))");
            dao.executeUpdate();

            dao.query("select word from voc where htmled = false limit 21000, 10");
            ResultSet rs;
            rs = dao.executeQuery();
            while (rs.next()) {
                System.out.println(rs.getString(1));
            }
        } catch (DAOException daoe) {
            daoe.printStackTrace();
            System.err.print("!DAO Exception!");
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            System.err.print("!SQL Exception!");
        }
    }

    private static void test1() {
        XPathFactory xPathfactory = XPathFactory.newInstance();
        XPath xpath = xPathfactory.newXPath();
        //XPathExpression expr = xpath.compile(<xpath_expression>);
        try {
            //throw new UnsupportedOperationException("Not yet implemented");
            String path = new File(".").getCanonicalPath() + "\\html\\";
            Document doc = Res.loadXMLFromFile(new File(path + "fly.html"));
            XPathExpression expr = xpath.compile("/html/body");
            //NodeList nl = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
            NodeList nl = doc.get
            for (int i = 0; i < nl.getLength(); i++) {
               System.out.print("Nodename: " + nl.item(i).getNodeName() + "..");
               System.out.print("Nodetype: " + nl.item(i).getNodeType() + "..");
               System.out.print("Nodevalue" + nl.item(i).getNodeValue().toString());
            }
            //doc
        } catch (Exception ex) {
            Logger.getLogger(test.class.getName()).log(Level.SEVERE, null, ex);
        }
        ;

    }
}
