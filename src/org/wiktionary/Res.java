/*
 * @Author Yetaai
 * yetaai@gmail.com
 * This software piece is apache license. But the contents generated is governed by wiktionary.org policy.
 */

package org.wiktionary;

import java.io.*;

import java.sql.*;
import org.apache.commons.configuration.*;
import org.xml.sax.*;
import javax.xml.stream.*;
import javax.xml.parsers.*;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.w3c.dom.*;

public class Res {
    private static String PROPERTYFILE="config.properties";
    public final static String DRIVER_NAME = "org.hsqldb.jdbcDriver";
    public static String IPADDRESS = "localhost";
    public static String PORT = "3306";
    public static String DBNAME = "Dictionary";
	
//    public static String DATABASE_ADDRESS = "jdbc:hsqldb:hsql://" + IPADDRESS + ":" + PORT + "/hsql1";    
//    public static String DATABASE_ADDRESS = "jdbc:mysql://localhost:3306/dictionary?useUnicode=yes&characterEncoding=UTF-8";    
//    public static String DATABASE_ADDRESS = "jdbc:hsqldb:file:z:\\wikitionary\\testdb";
    public static String DATABASE_ADDRESS = "jdbc:hsqldb:file:";
    //public final static String DATABASE_ADDRESS = "jdbc:hsqldb:hsql://localhost:9002/hsql1";    
        
    private static PropertiesConfiguration prop = null;
    private static DAO dao;
    static {
        try {
            prop = new PropertiesConfiguration(PROPERTYFILE);
        } catch (ConfigurationException ce) {
            ce.printStackTrace();
        }
        try {
            if (prop.getString("dbdir") == null ) {
                DATABASE_ADDRESS = DATABASE_ADDRESS + (new File(".").getCanonicalPath()) + "\\db\\db";
            } else {
                DATABASE_ADDRESS = DATABASE_ADDRESS + (new File(".").getCanonicalPath()) + "\\" +
                        prop.getString("dbdir") + "\\db";
            }
            System.out.println(DATABASE_ADDRESS);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
    /**
     * @throws ConfigurationException
     */
    public Res() {
        try {
            int a = 3;
        } catch(Exception ce) {
            ce.printStackTrace();
        }
    }
    public static PropertiesConfiguration getProp() {
        return prop;
    }

    public static Document loadXMLFromString(String xml) throws Exception
    {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        InputSource is = new InputSource(new StringReader(xml));
        return builder.parse(is);
    }
    public static Document loadXMLFromFile(File file) throws Exception
    {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        InputSource is = new InputSource(new InputStreamReader(new FileInputStream(file), "UTF-8"));
        is.setEncoding("UTF-8");
        return builder.parse(is);
    }

}