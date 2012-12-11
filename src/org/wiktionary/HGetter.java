package org.wiktionary;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.awt.*;
import java.io.*;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.*;
import java.util.regex.Pattern;

import javax.swing.*;
import javax.swing.JTextField;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.html2.HTMLElement;

import org.xml.sax.InputSource;
import org.lobobrowser.html.*;
import org.lobobrowser.html.gui.*;
import org.lobobrowser.html.parser.*;
import org.lobobrowser.html.test.*;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Node;
//import org.w3c.dom.Node;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;


public class HGetter {
    private String word;
    private String conword;
    private static HashMap<String, Object> hm;
    private org.w3c.dom.Document doc;
    private static String htmlpath;
    private org.jsoup.nodes.Document jsoupdoc;
    public boolean filefound = true;
    private static DAO dao = new DAO();
    private final AtomicInteger lcter = new AtomicInteger();

    public HGetter(String pword, HashMap<String, Object> phm) {
        if (hm == null) { hm = phm; }
        if (htmlpath == null) {htmlpath = ((JTextField) hm.get("htmldir")).getText();}
        word = pword;
        if (word.equals("con")) {
            conword = "conkkk";
        } else if (word.equals("c/o")) {
            conword = "cslashokkk";
        } else {
            conword = word;
        }
        try {
            int idao0 = dao.query("select max(sn1) from voc3 where word = ? and sn1 >= ?");
            dao.setString(1, word, idao0);
            dao.setInt(2, 1000, idao0);
            ResultSet rs = dao.executeQuery(idao0);
            if (rs == null) {
                lcter.set(1000);
            } else {
                rs.next();
                if (rs.getInt(1) == 0) {
                    lcter.set(1000);
                } else if (rs.getInt(1) < 1000) {
                    lcter.set(1000);
                } else if (rs.getInt(1) >= 1000) {
                    lcter.set(rs.getInt(1) + 1);
                }
            }
        } catch (Exception e) {
            System.out.print("\nException for newRef() initialization in HGetter constructor.");
            e.printStackTrace();
        }
    }
    public void getHtml() {
        try {
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            Result output = new StreamResult(new File(htmlpath + "\\" + conword + ".html"));
            //Logger.getLogger("org.lobobrowser").setLevel(Level.OFF);
            // Open a connection on the URL we want to render first.
            //String uri = "http://lobobrowser.org/browser/home.jsp";
            //String uri = "http://en.wiktionary.org/wiki/built-in";
            String uri = "http://en.wiktionary.org/wiki/" + word;
            URL url = new URL(uri);
            URLConnection connection = url.openConnection();
            connection.setConnectTimeout(370000);
            InputStream in = connection.getInputStream();
            
            Reader reader = new InputStreamReader(in);

            org.xml.sax.InputSource is = new InputSourceImpl(reader, uri);
            is.setEncoding("UTF-8");
            HtmlPanel htmlPanel = new HtmlPanel();
            UserAgentContext ucontext = new LocalUserAgentContext();
            HtmlRendererContext rendererContext =
                    new LocalHtmlRendererContext(htmlPanel, ucontext);


            DocumentBuilderImpl builder =
                    new DocumentBuilderImpl(
                    rendererContext.getUserAgentContext(),
                    rendererContext);
            doc = builder.parse(is);
            Source input = new DOMSource(doc);
            transformer.transform(input, output);
            in.close();
            
            int idao = dao.update("update voc set htmled = ? where word = ?");
            dao.setBoolean(1, true, idao);
            dao.setString(2, word, idao);
            dao.executeUpdate(idao);
        } catch (java.io.FileNotFoundException ioe) {
            filefound = false;
        } catch (Exception ex) {
            System.out.println("The one in misake during transforming :" + word);
            ex.printStackTrace();//other exceptions not supported yet!
        }
    }

    void xImage() {
        //throw new UnsupportedOperationException("Not yet implemented");
            try {
                if (jsoupdoc == null) {
                    openJsoupDoc();
                    if (jsoupdoc == null) {
                        throw new Exception("Cannot find html source. Inconsistant status in DB...");
                    }
                }
                Elements images = jsoupdoc.select("img.thumbimage");
                for (int i = 0; i < images.size(); i++) {
                    String uri = images.get(i).attr("src");
                    URL url = new URL("http:" + uri);
                    ReadableByteChannel rbc = Channels.newChannel(url.openStream());
                    String ifn;
                    String uripath = url.toURI().getPath();
                    ifn = word + Integer.valueOf(100 + i).toString() + uripath.substring(uripath.lastIndexOf("."));
                    FileOutputStream fos = new FileOutputStream(htmlpath + "\\image\\" + ifn);
                    fos.getChannel().transferFrom(rbc, 0, 1 << 24);
                    //System.out.println("Getting image!");
                    int idao = dao.update("insert into voc3 (word, sn1, sn2, sn3, sn4, type, imageurl, image) "
                            + "values(?, ?, ?, ?, ?, ?, ?, ?)");
                    dao.setString(1, word, idao);
                    dao.setInt(2, i + 100, idao); //Only for image
                    dao.setInt(3, 0, idao);
                    dao.setInt(4, 0, idao);
                    dao.setInt(5, 0, idao);
                    dao.setInt(6, Ref.getType("image"), idao);
                    dao.setString(7, uripath, idao);
                    dao.setString(8, ifn, idao);
                    dao.executeUpdate(idao);
                }
                int idao1 = dao.update("update voc set imaged = ? where word = ?");
                dao.setBoolean(1, true, idao1);
                dao.setString(2, word, idao1);
                dao.executeUpdate(idao1);
                
            } catch (DAOException daoe) {
                try {
                    System.err.print("\nRolling back in xImageAndCount");
                    int idao1 = dao.update("delete from voc3 where word = ?");
                    dao.setString(1, word, idao1);
                    dao.executeUpdate(idao1);
                    int idao2 = dao.update("update voc set imaged = ? where word = ?");
                    dao.setBoolean(1, false, idao2);
                    dao.setString(2, word, idao2);
                    dao.executeUpdate(idao2);
                } catch (Exception exi) {
                    System.err.print("\nException caught when rollback in xImageAndCount");
                }
            } catch (Exception ex) {
                System.err.print("\nException caught in xImageAndCount! " + word + "......");
                try {
                    ex.printStackTrace();
                    System.err.print("\nRolling back in xImageAndCount");
                    int idao1 = dao.update("delete from voc3 where word = ?");
                    dao.setString(1, word, idao1);
                    dao.executeUpdate(idao1);
                    int idao2 = dao.update("update voc set imaged = ? where word = ?");
                    dao.setBoolean(1, false, idao2);
                    dao.setString(2, word, idao2);
                    dao.executeUpdate(idao2);
                } catch (Exception exi) {
                    System.err.print("\nException caught when rollback in xImageAndCount");
                }
                if (word.equals("gee")) {
                    System.err.print("\nStart to ouptut stacktrace of gee in xImageAndCount");
                    ex.printStackTrace();
                    System.err.print("\nFinish output stacktrace of gee in xImageAndCount");
                }
            }
        
        //doc
    }

    synchronized void getXed() {
        try {
            Integer a = 0;
            synchronized (a) {
                //throw new UnsupportedOperationException("Not yet implemented");
                if (jsoupdoc == null) {
                    openJsoupDoc();
                    if (jsoupdoc == null) {
                        throw new Exception("Cannot find html source. Inconsistant status in DB...");
                    }
                }
                Elements es = jsoupdoc.select("span.tocnumber"); //tocnumber elements
                String r = null; //root tocnumber
//                String s = "\n" + word + " getXed Started!\n";
//                System.out.print(s);
                int n0 = 0, j = 0, k = 0;
                int[] osn = {0, 0, 0, 0, 0, 0};
                int[] osnunfiltered = {0, 0, 0, 0, 0, 0};
                int[] lastosnunfiltered = {0, 0, 0, 0, 0, 0};
                int[] sn = {0, 0, 0, 0, 0, 0};
                int[] lastosn = {0, 0, 0, 0, 0, 0};
                int[] lastsn = {0, 0, 0, 0, 0, 0};
//                int elayer = 99999, player = 99999, snlayer = 0; //previous elayer or player.
                String eppath = "", lasteppath = "";
                boolean bincut = false, bref = false; //Actually cut started until last cut.
                int[] bcutsnroot = {0, 0, 0, 0, 0, 0};
                boolean b1 = false;
                boolean xed = false;
                loops:
                {
                    for (int i = 0; i < es.size(); i++) {
                        org.jsoup.nodes.Element e = es.get(i); //e is the element with ID of tocnumber
                        String[] split = e.html().split("\\.");
                        String tocid = "", tocidfull = "";
                        String s6 = e.parent().attr("href");

                        for (int i1 = 0; i1 < osn.length; i1++) {
                            lastosnunfiltered[i1] = osnunfiltered[i1];
                        }

                        for (int i1 = 0; i1 < Math.min(6, split.length); i1++) {
                            osnunfiltered[i1] = Integer.parseInt(split[i1]);
                        }
                        for (int i1 = split.length; i1 < 6; i1++) {
                            osnunfiltered[i1] = 0;
                        }
                        
                        String ps0 = "\nps0 Output OSNunfiltered now for ";
                        ps0 = ps0 + e.html() + "," + e.parent().attr("href")
                                + " :";
                        for (int i1 = 0; i1 < 6; i1++) {
                            ps0 = ps0
                                    + osnunfiltered[i1] + " , ";
                        }
//                        System.out.print(ps0);
                      
  //                      System.out.print("\n indicator values of bref and bincut for row "); System.out.print(i); System.out.print(":\t");
  //                      System.out.print(bref); System.out.print("\t");System.out.print(bincut);
                        bref = Ref.hasRef(s6.indexOf("_") > 0 ? s6.substring(1, s6.indexOf("_")) : s6.substring(1)); //Only the type in ref will be a mapped sn[] 
                        if (!bref) {
                            newRef(s6.indexOf("_") > 0 ? s6.substring(1, s6.indexOf("_")) : s6.substring(1)); //So far the only post processing and not necessary for runtime.
                        }
                        if (!bref && !bincut) { //Starting of cut
                            bincut = !bref;
                            for (int i1 = 0; i1 < osn.length; i1++) {
                                bcutsnroot[i1] = osnunfiltered[i1];
                            }
                            continue;
                        }
                        if (bincut) { //Check if current toc is the children of the bcutsnroot
                            boolean bsubnode = true; //Assumpt true
                            for (int i1 = 0; i1 < cl(bcutsnroot); i1++) {
                                if (bcutsnroot[i1] != osnunfiltered[i1]) {
                                    bsubnode = false;
                                    break;
                                }
                            }
                            if (bsubnode) {
                                continue; //cut
                            } else if (!bsubnode) {
                                if (!bref) {
                                    bincut = true;
                                    for (int i1 = 0; i1 < osn.length; i1++) {
                                        bcutsnroot[i1] = osnunfiltered[i1];
                                    }
                                    continue;
                                } else if (bref) {
                                    bincut = false;
                                }
                            }
                        }

                        if (r != null) {
                            j = k;
                            if (e.html().length() == r.length()
                                    && !(e.html().equals(r))) {
                                break loops;
                            }
                            n0 = n0 + 1;

                            String s3 = s6.indexOf("_")>0 ? s6.substring(1, s6.indexOf("_")) : s6.substring(1);
                            lasteppath = eppath;
                            if (s3.equalsIgnoreCase("Etymology")) {
                                if (lasteppath.equals("l")) {
                                    eppath = "le";
                                } else if (lasteppath.equals("le")) {
                                    eppath = "le";
                                } else if (lasteppath.equals("lp")) {
                                    eppath = "lpe";
                                } else if (lasteppath.equals("lpe") && cl(osnunfiltered) < cl(lastosnunfiltered)) {
                                    eppath = "lpe";
                                } else if (lasteppath.equals("lpe") && cl(osnunfiltered) >= cl(lastosnunfiltered) && cl(osnunfiltered) > 2 ) {
                                    continue;
                                } else if ( lasteppath.equals("lep") && cl(osnunfiltered) < cl(lastosnunfiltered) ) {
                                    eppath = "le";
                                } else if (lasteppath.equals("lep") && cl(osnunfiltered) >= cl(lastosnunfiltered) && cl(osnunfiltered) > 2 ) {
                                    continue; //discard this etymology
                                }
                            } else if (s3.equalsIgnoreCase("Pronunciation")) {
                                if (lasteppath.equals("l")) {
                                    eppath = "lp";
                                } else if (lasteppath.equals("le")) {
                                    eppath = "lep";
                                } else if (lasteppath.equals("lp")) {
                                    eppath = "lp";
                                } else if ( lasteppath.equals("lpe") && cl(osnunfiltered) < cl(lastosnunfiltered) ) {
                                    eppath = "lp";
                                } else if ( lasteppath.equals("lpe") && cl(osnunfiltered) >= cl(lastosnunfiltered) && cl(osnunfiltered) > 2) {
                                    continue; //discard this pronunciation.
                                } else if (lasteppath.equals("lep") && cl(osnunfiltered) >= cl(lastosnunfiltered) && cl(osnunfiltered) > 2) {
                                    continue;
                                } else if (lasteppath.equals("lep") && cl(osnunfiltered) < cl(lastosnunfiltered) )  {  
                                    eppath = "lep";
                                }
                            }
                            if (word.equals("watch"))
                                System.out.print("\nlastep:" + lasteppath + ". ep:" + eppath);

                            Boolean movetree = false;
                            for (int i2 = 0; i2 < osn.length; i2++) {
                                lastosn[i2] = osn[i2];
                                lastsn[i2] = sn[i2];
                            }

                            for (int i2 = 0; i2 < osn.length; i2++) {
                                osn[i2] = osnunfiltered[i2];
                            }
                            /*
                             * String ps1 = "\nps1 Output osn now : "; for (int
                             * i1=0; i1<6; i1++) { ps1 = ps1 + osn[i1] + ";"; }
                             * ps1 = ps1; System.out.print(ps1);
                             */
                            String st1 = "";
                            if (cl(lastosn) > cl(osn)) { //New branch
                                int up = 0;
                                if ((lasteppath.equals("lep")|| lasteppath.equals("ep")) && eppath.equals("le")) {
                                    sn[1] = sn[1] + 1;
                                    for (int i2 = 2; i2 < sn.length; i2 ++) {
                                        sn[i2] = 0;
                                    }
                                } else if ((lasteppath.equals("lpe") || lasteppath.equals("lp")) && eppath.equals("lp")) {
                                    sn[1] = sn[1] + 1;
                                    for (int i2 = 2; i2 < sn.length; i2 ++) {
                                        sn[i2] = 0;
                                    }
                                } else if ( lasteppath.equals("lpe") && eppath.equals("lpe") && s3.equals("Etymology")){
                                    sn[2] = sn[2] + 1;
                                    for (int i2 = 3; i2 < sn.length; i2 ++) {
                                        sn[i2] = 0;
                                    }
                                } else if ( lasteppath.equals("lep") && eppath.equals("lep") && s3.equals("Pronunciation")) {
                                    sn[2] = sn[2] + 1;
                                    for (int i2 = 3; i2 < sn.length; i2 ++) {
                                        sn[i2] = 0;
                                    }
                                } else {
                                    up = cl(lastsn) - cl(lastosn) + cl(osn) - 1;
                                    sn[up] = sn[up] + 1;
                                    for (int i6 = up + 1; i6 < Math.min(6, osn.length); i6++) {
                                        sn[i6] = 0;
                                    }
                                }
                            } else if (cl(lastosn) == cl(osn)) { //
                                String s7 = es.get(j).parent().attr("href"); //Consider how to use j(the cycle index of last cut root)
                                String s8 = s7.indexOf("_") > 0 ? s7.substring(1, s7.indexOf("_")) : s7.substring(1);
                                if ((s8.equalsIgnoreCase("Etymology") || s8.equalsIgnoreCase("Pronunciation")) &&
                                        !s8.equals(s3)){
                                    movetree = true;
                                };
                                if (movetree) {
                                    sn[cl(sn)] = 1;
                                    for (int i2 = cl(sn) + 1; i2 < sn.length; i2++) {
                                        sn[i2] = 0;
                                    }
                                } else {
                                    st1 = lasteppath + ", " + eppath + ", s3:" + s3 + ", cl(lastsn) :" +Integer.valueOf(cl(lastsn)).toString();
                                    if (eppath.equals("lep") && lasteppath.equals("lep")
                                            && s3.equalsIgnoreCase("Pronunciation") ){
                                        sn[2] = sn[2] + 1;
                                        for (int i2 = 3; i2 < sn.length; i2 ++) {
                                            sn[i2] = 0;
                                        }
                                    } else if (eppath.equals("lpe") && lasteppath.equals("lpe") &&
                                            s3.equalsIgnoreCase("Etymology")) {
                                        sn[2] = sn[2] + 1;
                                        for (int i2 = 3; i2 < sn.length; i2 ++) {
                                            sn[i2] = 0;
                                        }
                                    } else if (lasteppath.equals("lep") && eppath.equals("le") &&
                                            cl(lastsn) > 3 && s3.equalsIgnoreCase("Etymology")) {
                                        sn[1] = sn[1] + 1;
                                        for (int i2 = 2; i2 < sn.length; i2 ++) {
                                            sn[i2] = 0;
                                        }
                                    } else if (lasteppath.equals("lpe") && eppath.equals("lp") &&
                                            cl(lastsn) > 3 && s3.equalsIgnoreCase("Pronunciation")) {
                                        sn[1] = sn[1] + 1;
                                        for (int i2 = 2; i2 < sn.length; i2 ++) {
                                            sn[i2] = 0;
                                        }
                                    } else {
                                        sn[cl(sn) - 1] = sn[cl(sn) - 1] + 1;
                                    }
                                }
                            } else if (cl(lastosn) < cl(osn)) {
                                sn[cl(sn)] = 1;
                            }
                            String s = "\n\n" + word + ":: tocnumber is " + e.html() + ". Href is " + e.parent().attr("href");
                            if (word.equals("bullshit")) System.out.print(s);
                            s = "\nosn :";
                            for (int i1 = 0; i1 < 6; i1++) {
                                s = s + Integer.valueOf(osn[i1]).toString() + ",";
                            }
                            s = s + "\nsn  :";
                            for (int i1 = 0; i1 < 6; i1++) {
                                s = s + Integer.valueOf(sn[i1]).toString() + ",";
                            }
                            s = s + "\nmovetree: " + movetree.toString() + ", " + st1;
                            if (word.equals("bughghkjhllshit")) System.out.print(s);
                            
/*                            synchronized (DAO.daolock) {
                                try {
                                    DAO dao = new DAO();
//                                    dao.update("drop table voc3test if exists"); 
//                                    dao.executeUpdate();
//                                    dao.update("create cached table voc3test (iden identity, word varchar(50), sn1 int, sn2 int, sn3 int, sn4 int, sn5 int)");
//                                    dao.executeUpdate();
                                    dao.update("insert into voc3test(word,sn1, sn2, sn3, sn4, sn5) values" + "( ?, ?, ?, ?, ?, ?)");
                                    dao.setString(1, word);
                                    dao.setInt(2,sn[1]);
                                    dao.setInt(3, sn[2]);
                                    dao.setInt(4,sn[3]);
                                    dao.setInt(5, sn[4]);
                                    dao.setInt(6,sn[5]);
                                    dao.executeUpdate();
                                } catch (Exception ex) {
                                    System.out.print("\nException caught for voc3test at " + word + ":");
                                    System.out.print(sn[0]);
                                    System.out.print(",");
                                    System.out.print(sn[1]);
                                    System.out.print(",");
                                    System.out.print(sn[2]);
                                    System.out.print(",");
                                    System.out.print(sn[3]);
                                    System.out.print(",");
                                    System.out.print(sn[4]);
                                    System.out.print(",");
                                    System.out.print(sn[5]);
                                    System.out.print("....");
                                }
                            }
*/
                            tocidfull = e.parent().attr("href").substring(1);
                            tocid = tocidfull.indexOf("_") > 0 ? tocidfull.substring(0, tocidfull.indexOf("_")) : tocidfull.substring(0);
//System.out.print("\ntocid is " + tocid + ". tocidfull is " + tocidfull);
/*
                             * if (word.equals("gee")) { System.out.print("\n" +
                             * word + ":" +Integer.valueOf(sn[1]).toString() +
                             * "," + Integer.valueOf(sn[2]).toString() + "," +
                             * Integer.valueOf(sn[3]).toString() + "," +
                             * Integer.valueOf(sn[4]).toString() + ". " + tocid
                             * +". in getXed"); }
                             *
                             */
                            if (Ref.hasRef(tocid) && Ref.isEtym(tocid)) {
                                xEtym(tocidfull, sn);
                            } else if (Ref.hasRef(tocid) && Ref.isPronun(tocid)) {
                                xPron(tocidfull, sn);
                            } else if (Ref.hasRef(tocid) && Ref.getType(tocid) > 100 && Ref.getType(tocid) <= 1000) {
                                xMeaning(tocidfull, sn);
                            } else if (Ref.hasRef(tocid) && Ref.isSynonym(tocid)) {
                                xSyn(tocidfull, sn);
                            } else if (Ref.hasRef(tocid) && Ref.isAntonym(tocid)) {
                                xAnt(tocidfull, sn);
                            } else if (!Ref.hasRef(tocid)) {
                                //Could never be executed now that it has been filtered by bcut indicator.
                                newRef(tocid); //Later have to check Referrence manually to decide if I want to extract it anyway.
                            }
                            xed = true;
                            int idao = dao.update("update voc set xed = true where word = ?");
                            dao.setString(1, word, idao);
                            dao.executeUpdate(idao);
                            k = i;
                        } else { //r is null so far
                            String s9 = e.parent().attr("href");
                            String s10 = s9.indexOf("_") > 0 ? s9.substring(1, s9.indexOf("_")) : s9.substring(1);
                            if (s10.equalsIgnoreCase("English")) {
                                for (int i2 = 0; i2 < osn.length; i2++) {
                                    osn[i2] = osnunfiltered[i2];
                                }
                                r = e.html();
                                eppath = eppath + "l";
                                sn[0] = sn[0] + 1;
                            }
                            k = i;
                        }
                    } //End of outmost for
                } // End of label loops
            }
        } catch (Exception ex) {
            System.out.print("\nException caught in getXed! " + word + "......");
            ex.printStackTrace();
            //ex.printStackTrace();
        }
    }

    private void openJsoupDoc() {
        try {
            //throw new UnsupportedOperationException("Not yet implemented");
            File input = new File( htmlpath + "\\" + conword + ".html");
            jsoupdoc = Jsoup.parse(input, "UTF-8");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(HGetter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException uee) {
            System.out.println("Jsoup encoding exception!");
            //uee.printStackTrace();
        } catch (Exception e) {
            System.out.println("Jsoup open html failed!");
            //e.printStackTrace();
        }
    }
    
    private static int cl(int[] ia) throws Exception { //count how to layersfor specialized array here only
        int j = 0;
        for (int i=0; i<ia.length; i++) {
            if (ia[i] != 0) {
                j = j + 1;
            }
        }
        return j;
    }

    private void xEtym(String t, int[] sn){
        try {
            //throw new UnsupportedOperationException("Not yet implemented");
            if (jsoupdoc == null) {
                openJsoupDoc();
                if (jsoupdoc == null) {
                    throw new Exception("Cannot find html source. Inconsistant status in DB...");
                }
            }
            org.jsoup.nodes.Element e = jsoupdoc.getElementById(t);
            org.jsoup.nodes.Element e0 = e.parent();
            String etym = "";
            l1:
            {
                while (e0 != null) {
                    e0 = e0.nextElementSibling();
                    if (e0 == null) {
                        break l1;
                    }
                    org.jsoup.nodes.Document d = Jsoup.parse(e0.html(), "UTF-8");
                    if (d == null) {
                        System.out.println("parse failed" + e0.html());
                    }
                    Elements es0 = d.select(".etyl"); //e0.select("etyl") does not work

                    org.jsoup.nodes.Element e1;
                    if (!es0.isEmpty()) {
                        e1 = es0.get(0);
                        org.jsoup.nodes.Element e1a = null;
                        org.jsoup.select.Elements e1as = null;
                        e1as = e1.parent().select("a");
                        if (!(e1as.isEmpty())) {
                            e1a = e1as.get(0);
                            etym = Jsoup.parse(e1.html(), "UTF-8").text() + " " + Jsoup.parse(e1a.html(), "UTF-8").text();
                        } else {
                            etym = Jsoup.parse(e1.html(), "UTF-8").text();
                        }
                        etym = etym.substring(0, Math.min(50, etym.length()));
                        break l1;
                    } else if (es0.isEmpty() && e0.tagName().equals("p")) {
                        etym = d.text();
                        etym = etym.substring(0, Math.min(50, etym.length()));
                        break l1;
                    } else if (es0.isEmpty() && e0.tagName().equals("ul")) {
                        org.jsoup.nodes.Document d1 = Jsoup.parse(e0.select("li").get(0).html(), "UTF-8");
                        etym = d.text();
                        break l1;
                    } else if (es0.isEmpty() && (e0.tagName().equals("h3") || e0.tagName().equals("h4"))) {
                        break l1;
                    }
                }
            }
            if (etym.length() > 1) {
                try { //If not add database record, then sn number could be out of order.
                    int idao = dao.update("insert into voc3 (word, sn1, sn2, sn3, sn4, type, etym) values"
                            + "(?, ?, ?, ?, ?, ?, ?)");
                    dao.setString(1, word, idao);
                    dao.setInt(2, sn[1], idao);
                    dao.setInt(3, sn[2], idao);
                    dao.setInt(4, sn[3], idao);
                    dao.setInt(5, sn[4], idao);
                    dao.setInt(6, Ref.getType(t.contains("_") ? t.substring(0, t.indexOf("_")) : t), idao);
                    dao.setString(7, etym, idao);
                    dao.executeUpdate(idao);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else if (etym.length() <= 1) {
                try {
                    int idao = dao.update("insert into voc3 (word, sn1, sn2, sn3, sn4, type, etym) values"
                            + "(?, ?, ?, ?, ?, ?, ?)");
                    dao.setString(1, word, idao);
                    dao.setInt(2, sn[1], idao);
                    dao.setInt(3, sn[2], idao);
                    dao.setInt(4, sn[3], idao);
                    dao.setInt(5, sn[4], idao);
                    dao.setInt(6, Ref.getType(t.contains("_") ? t.substring(0, t.indexOf("_")) : t), idao);
                    dao.setString(7, "Unknown", idao);
                    dao.executeUpdate(idao);
                } catch(Exception ex) {
                    ex.printStackTrace();
                }
            }
        } catch (Exception ex) {
            System.err.print("\nException caught in xEtym! " + word + "......");
            //ex.printStackTrace();
        }
    }

    private void xPron(String t, int[] sn){
        //throw new UnsupportedOperationException("Not yet implemented");
        try {
            if (jsoupdoc == null) {
                openJsoupDoc();
                if (jsoupdoc == null) {
                    throw new Exception("Cannot find html source. Inconsistant status in DB...");
                }
            }
            org.jsoup.nodes.Element e = jsoupdoc.getElementById(t);
            org.jsoup.nodes.Element e0 = e.parent();
            String pronunuk = "", pronunus = "", pronun = "";
            Boolean pronunukfound = false, pronunusfound = false, pronunfound = false;
            l1:
            {
                while (e0 != null) {
                    e0 = e0.nextElementSibling();
                    if (e0 == null) {
                        break;
                    }
                    if (e0.tagName().equals("ul")) {
                        org.jsoup.nodes.Document d = Jsoup.parse(e0.outerHtml(), "UTF-8");
                        org.jsoup.select.Elements es0 = d.select(".IPA");
                        if (es0.size() > 0) {
                            for (int i = 0; i < es0.size(); i++) {
                                org.jsoup.select.Elements es1 = Jsoup.parse(es0.get(i).parent().html()).select(".extiw");
                                if (es1.select("[title~=British]").size() > 0) {
                                    pronunuk = Jsoup.parse(es0.get(i).html(), "UTF-8").text();
                                    if (es0.get(i).nextElementSibling() != null) {
                                        if (es0.get(i).nextElementSibling().tagName().equals("IPA")) {
                                            pronunuk = pronunuk + "," + Jsoup.parse(es0.get(i).nextElementSibling().html(), "UTF-8").text();
                                        }
                                    }
                                    pronunukfound = true;
                                }
                                if (es1.select("[title~=American]").size() > 0) {
                                    pronunus = Jsoup.parse(es0.get(i).html(), "UTF-8").text();
                                    if (es0.get(i).nextElementSibling() != null) {
                                        if (es0.get(i).nextElementSibling().tagName().equals("IPA")) {
                                            pronunus = pronunus + "," + Jsoup.parse(es0.get(i).nextElementSibling().html(), "UTF-8").text();
                                        }
                                    }
                                    pronunusfound = true;
                                }
                                if (!(pronunusfound || pronunukfound) && es1.select("[title~=English]").size() > 0) {
                                    pronun = Jsoup.parse(es0.get(i).html(), "UTF-8").text();
                                    if (es0.get(i).nextElementSibling() != null) {
                                        if (es0.get(i).nextElementSibling().tagName().equals("IPA")) {
                                            pronun = pronun + "," + Jsoup.parse(es0.get(i).nextElementSibling().html(), "UTF-8").text();
                                        }
                                    }
                                    pronunfound = true;
                                } else if (!(pronunusfound || pronunukfound)) {
                                    pronun = Jsoup.parse(es0.get(i).html(), "UTF-8").text();
                                    pronunfound = true;
                                }
                                if ((pronunukfound && pronunusfound) || pronunfound) {
                                    break;
                                }
                            }
                        }
                        break l1; // Upon first "ul" found, execute pronunciation search. And break at the end of search. Meaning second "ul" is not even in the loop
                    }
                }
            }// end of l1 label block
            if (!(pronunukfound || pronunusfound || pronunfound)) {
                l2: //alternative search path
                {while (e0 != null) {
                        e0 = e0.nextElementSibling();
                        if (e0 == null) {
                            break;
                        }
                        if (e0.tagName().equals("p")) {
                            org.jsoup.nodes.Document d = Jsoup.parse(e0.outerHtml(), "UTF-8");
                            org.jsoup.select.Elements es0 = d.select(".IPA");
                            if (es0.size() > 0) {
                                pronun = Jsoup.parse(es0.get(0).html(), "UTF-8").text();
                                pronunfound = true;
                                break l2;
                            }
                        }
                    }
                }
            }
            if (pronunukfound || pronunusfound || pronunfound) {
                try {
                    int idao = dao.update("insert into voc3 (word, sn1, sn2, sn3, sn4, type, pronun, pronuk, pronus) values (?, ?, ?, ?, ?, ?, ?, ?, ?)");
                    dao.setString(1, word, idao);
                    dao.setInt(2, sn[1], idao);
                    dao.setInt(3, sn[2], idao);
                    dao.setInt(4, sn[3], idao);
                    dao.setInt(5, sn[4], idao);
                    dao.setInt(6, Ref.getType(t.contains("_") ? t.substring(0, t.indexOf("_")) : t), idao);
                    dao.setString(7, pronun, idao);
                    dao.setString(8, pronunuk, idao);
                    dao.setString(9, pronunus, idao);
                    dao.executeUpdate(idao);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        } catch (Exception ex) {
            System.err.print("\nException caught in xPron before insert into voc3! " + word + "......");
            //ex.printStackTrace();
        }
    }

    private void xMeaning(String t, int[] sn){
        //throw new UnsupportedOperationException("Not yet implemented");
        try {
            if (jsoupdoc == null) {
                openJsoupDoc();
                if (jsoupdoc == null) {
                    throw new Exception("Cannot find html source. Inconsistant status in DB...");
                }
            }
            org.jsoup.nodes.Element e = jsoupdoc.getElementById(t);
            org.jsoup.nodes.Element e0 = e.parent();
            String meaning = "", sp = "", sv = "", sv3rd = "", svpresentp = "", svsimplepast = "", svpastp = "", sc8 = "", sc9 = "";
            boolean b0 = false;
            boolean bn= false; //b1 is set true once the first of plural forms of a noun is found
            boolean bv1 = false, bv2 = false; //b2 is set true once the verb subcategory of v, vi, or vt was identified.
            boolean bca = false; //b3 is set true once the comparative and superlative forms of adj and adv were identified.
            
            boolean b10 = false, b20 = false, b30 = false; //name map to b1, b2, b3. 
            String t0 = t.indexOf("_") > 0 ? t.substring(0, t.indexOf("_")) : t.substring(0);
            while (e0 != null) {
                e0 = e0.nextElementSibling();
                if (e0 == null) {
                    break;
                }
                
                if (Ref.getType(t0) == 101) {
                    if (!bn && e0.tagName().equals("p") && e0.select("[class*=plural-form-of]").size() > 0) {
                        sp = Jsoup.parse(e0.select("[class*=plural-form-of]").get(0).html(), "UTF-8").text();
                        bn = true;
                    }
                } else if (Ref.getType(t0) == 102) {
                    //transitive && intransitive;
                    if (!bv1 ) {
                        if (e0.tagName().equals("p")) {
                            if (!(e0.select("span").select("[class*=third-person-singular-form-of]").isEmpty())) {
                                sv3rd = Jsoup.parse(e0.select("span").select("[class*=third-person-singular-form-of]").get(0).html(), "UTF-8").ownText();
                                bv1 = true;
                            }
                            if (!(e0.select("span").select("[class*=present-participle-form-of]").isEmpty())) {
                                svpresentp = Jsoup.parse(e0.select("span").select("[class*=present-participle-form-of]").get(0).html(), "UTF-8").ownText();
                                bv1 = true;
                            }
                            if (!(e0.select("span").select("[class*=simple-past]").isEmpty())) {
                                svsimplepast = Jsoup.parse(e0.select("span").select("[class*=simple-past]").get(0).html(), "UTF-8").ownText();
                                bv1 = true;
                            }
                            if (!( e0.select("span").select("[class*=past]").select("[class*=participle]").isEmpty() )) {
                                svpastp = Jsoup.parse(e0.select("span").select("[class*=past]").select("[class*=participle-form-of]").get(0).html(), "UTF-8").ownText();
                                bv1 = true;
                            }
                            
                        }
                    }
                    if (!bv2) {
                        if (e0.tagName().equals("ol")) {
                            if (e0.select("a").select("[title=transitive]").size() > 0 &&
                                    e0.select("a").select("[title=intransitive").size() > 0) {
                                sv = "v";
                            } else if (e0.select("a").select("[title=transitive]").size() > 0 &&
                                    e0.select("a").select("[title=intransitive]").isEmpty()) {
                                sv = "vt"; 
                            } else if (e0.select("a").select("[title=transitive]").isEmpty() &&
                                    e0.select("a").select("[title=intransitive]").size() > 0) {
                                sv = "vi";
                            } else if (e0.select("a").select("[title=transitive]").isEmpty() &&
                                    e0.select("a").select("[title=intransitive]").isEmpty()) {
                                sv = "v";//son of bitch, I lost patience finally
                            }
                            bv2 = true;
                        }
                    }
                } else if (Ref.getType(t0) == 104 || Ref.getType(t0) == 105) {
                    //Comparative form of Adj
                    if (!bca && e0.tagName().equals("p")) {
                        if (!e0.select("span").select("[class*=comparative-form-of]").isEmpty()) {
                            sc8 = Jsoup.parse(e0.select("span").select("[class*=comparative-form-of]").get(0).html(), "UTF-8").text();
                            //System.out.print("\nextracting comparative form, result: " + e0.select("span").select("[class*=comparative-form-of]").get(0).html());
                        }
                        if (!e0.select("span").select("[class*=superlative-form-of]").isEmpty()) {
                            sc9 = Jsoup.parse(e0.select("span").select("[class*=superlative-form-of]").get(0).html(), "UTF-8").text();
                            //System.out.print("extracting superlative form, result: " + sc9);
                            bca = true;
                        }
                    }
                } 
                if (e0.tagName().equals("ol")) {
                    org.jsoup.select.Elements es0 = e0.children();
                    for (int i = 0; i < es0.size(); i++) {
                        //System.out.print("\nli:" + es0.get(i).html());
                        if (es0.get(i).tagName().equals("li")) {
//                        System.out.print("\nli:" + es0.get(i).html());
                            org.jsoup.nodes.Element e1 = es0.get(i);
                            if (e1.html().indexOf("<dl>") >= 0 || e1.html().indexOf("<ul>") >= 0 
                                    || e1.html().indexOf("<ol>") >= 0) {
                                int idl = e1.html().indexOf("<dl>");
                                int iul = e1.html().indexOf("<ul>");
                                int iol = e1.html().indexOf("<ol>");
                                int i00 = 10000; //Not possible to reach
                                if (idl >= 0)  i00 = idl;
                                if (iul >= 0) i00 = Math.min(i00, iul); 
                                if (iol >= 0) i00 = Math.min(i00, iol); 
                                
                                String limeaning = e1.html().substring(0, i00);
/*                                if (word.equals("accomplice")) {
                                    System.out.println(word + " e1.html() is: " + e1.html() + ". limeaning text is " + Jsoup.parse(limeaning, "UTF-8").text());
                                    System.out.println("iul:" + Integer.valueOf(iul).toString());
                                    System.out.println("idl:" + Integer.valueOf(idl).toString());
                                    System.out.println("iol:" + Integer.valueOf(iol).toString());
                                    System.exit(0);
                                }*/
                                if (meaning.length() + Jsoup.parse(limeaning, "UTF-8").text().length() <= (2000 - 1) ) {
                                    if (meaning.length() < 2) {
                                        meaning = "<mgi>" + Jsoup.parse(limeaning, "UTF-8").text().trim() + "</mgi>";
                                    } else {
                                        meaning = meaning + "<sc>*</sc><mgi>" + Jsoup.parse(limeaning, "UTF-8").text().trim() + "</mgi>";
                                    }
                                } else if (meaning.length() +Jsoup.parse(limeaning, "UTF-8").text().length() > (2000 - 1)) {
                                    break;
                                }
                            } else {
                                String limeaning = e1.html();
                                if (meaning.length() + Jsoup.parse(limeaning, "UTF-8").text().length() <= (2000 - 1)) {
                                    if (meaning.length() < 2) {
                                        meaning = "<mgi>" + Jsoup.parse(limeaning, "UTF-8").text().trim() + "</mgi>";
                                    } else {
                                        meaning = meaning + "<sc>*</sc><mgi>" + Jsoup.parse(limeaning, "UTF-8").text().trim() + "</mgi>";
                                    }
                                } else if (meaning.length() + Jsoup.parse(limeaning, "UTF-8").text().length() > (2000 - 1)) {
                                    break;
                                }
                            }
                        }
                    }

                    break; //Only search one ol element then break from while block.
                }
            }
            if (meaning.length() > 1) {
                if (Ref.getType(t0) == 102) {
                    meaning.replace("(intransitive)", "").replace("(transitive)", "").replace("(intransitive,", "(").replace("(transitive,", "("); 
                }
                try {
                    int idao = dao.update("insert into voc3 (word, sn1, sn2, sn3, sn4, type, "
                            + "meaning, plural, vsubcat, v3rd, vpresentp, vsimplepast, vpastp, "
                            + "compare8, compare9) values"
                            + "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
                    dao.setString(1, word, idao);
                    dao.setInt(2, sn[1], idao);
                    dao.setInt(3, sn[2], idao);
                    dao.setInt(4, sn[3], idao);
                    dao.setInt(5, sn[4], idao);
                    dao.setInt(6, Ref.getType(t.contains("_") ? t.substring(0, t.indexOf("_")) : t), idao);
                    dao.setString(7, meaning, idao); 
                    dao.setString(8, sp, idao);
                    dao.setString(9, sv, idao);
                    dao.setString(10, sv3rd, idao);
                    dao.setString(11, svpresentp, idao);
                    dao.setString(12, svsimplepast, idao);
                    dao.setString(13, svpastp, idao);
                    dao.setString(14, sc8, idao);
                    dao.setString(15, sc9, idao);
                    dao.executeUpdate(idao);
                } catch (Exception ex) {
                    //System.err.println("Exception caught in xMeaning");
                    System.out.print("\nException caught when insert into voc3 in xMeaning: " + meaning);
                    //ex.printStackTrace();
                }
            } else if (meaning.length() <= 1) {
                try {
                    while (e0 != null) { //Secondary search loop but only meaning self. plusal, 
                        e0 = e0.nextElementSibling();
                        if (e0 == null) break;
                        if (e0.tagName().equals("p")) {
                            meaning = "<mgi>" + Jsoup.parse(e0.html(), "UTF-8").text() + "</mgi>";
                            break;
                        }
                    }
                    if (meaning.length() > 1) {
                        int idao = dao.update("insert into voc3 (word, sn1, sn2, sn3, sn4, type, "
                                + "meaning, plural, vsubcat, v3rd, vpresentp, vsimplepast, vpastp, "
                                + "compare8, compare9) values"
                                + "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
                        dao.setString(1, word, idao);
                        dao.setInt(2, sn[1], idao);
                        dao.setInt(3, sn[2], idao);
                        dao.setInt(4, sn[3], idao);
                        dao.setInt(5, sn[4], idao);
                        dao.setInt(6, Ref.getType(t.contains("_") ? t.substring(0, t.indexOf("_")) : t), idao);
                        dao.setString(7, meaning.substring(1), idao); //Remove first character of "*"
                        dao.setString(8, sp, idao);
                        dao.setString(9, sv, idao);
                        dao.setString(10, sv3rd, idao);
                        dao.setString(11, svpresentp, idao);
                        dao.setString(12, svsimplepast, idao);
                        dao.setString(13, svpastp, idao);
                        dao.setString(14, sc8, idao);
                        dao.setString(15, sc9, idao);
                        dao.executeUpdate(idao);
                    }
                } catch (Exception ex) {
                    //System.err.println("Exception caught in xMeaning");
                    System.out.print("\nException caught when insert into voc3 in xMeaning 2nd loop search: " + meaning);
                    //ex.printStackTrace();
                }
            }
        } catch (Exception ex) {
            System.out.print("\nException caught in xMeaning before insert into voc3! " + word + "......");
            //ex.printStackTrace();
        }
    }

    private synchronized void newRef(String tocid) {
        try {
//            System.out.print("\nsn1 of newRef for " + word + ": ");
//            System.out.print(Integer.valueOf(cter.get() + 1).toString());
//            System.out.print(" : " + tocid);
            int idao = dao.update("insert into voc3 (word, sn1, sn2, sn3, sn4, meaning) values (?, ?, 0, 0, 0, ?)");
            dao.setString(1, word, idao);
            dao.setInt(2, lcter.getAndIncrement(), idao);
            dao.setString(3, tocid, idao);
            dao.executeUpdate(idao);
        } catch (Exception e) {
            System.err.print("\nException caught in newRef: " + word + ".");
            e.printStackTrace();
            try {
                int idao = dao.update("insert into voc3 (word, sn1, sn2, sn3, sn4, meaning) values (?, ?, 0, 0, 0, ?)");
                dao.setString(1, word, idao);
                dao.setInt(2, lcter.getAndIncrement(), idao);
                dao.setString(3, tocid, idao);
                dao.executeUpdate(idao);
            } catch (Exception ex) {
                //do nothing
            }
        }
        //throw new UnsupportedOperationException("Not yet implemented");
        
    }

    private void xSyn(String tocidfull, int[] sn) {
        //throw new UnsupportedOperationException("Not yet implemented");
    }

    private void xAnt(String tocidfull, int[] sn) {
        //throw new UnsupportedOperationException("Not yet implemented");
    }

    private static class LocalUserAgentContext
            extends SimpleUserAgentContext {

        public LocalUserAgentContext() {
        }

        public String getAppMinorVersion() {
            return "0";
        }

        public String getAppName() {
            return "BarebonesTest";
        }

        public String getAppVersion() {
            return "1";
        }

        public String getUserAgent() {
            return "Mozilla/4.0 (compatible;) CobraTest/1.0";
        }
    }

    private static class LocalHtmlRendererContext
            extends SimpleHtmlRendererContext {
        // Override methods from SimpleHtmlRendererContext 
        // to provide browser functionality to the renderer.

        public LocalHtmlRendererContext(HtmlPanel contextComponent,
                UserAgentContext ucontext) {
            super(contextComponent, ucontext);
        }

        public void linkClicked(HTMLElement linkNode,
                URL url, String target) {
            super.linkClicked(linkNode, url, target);
            // This may be removed: 
            System.out.println("## Link clicked: " + linkNode);
        }

        public HtmlRendererContext open(URL url,
                String windowName, String windowFeatures,
                boolean replace) {
            // This is called on window.open().
            HtmlPanel newPanel = new HtmlPanel();
            JFrame frame = new JFrame();
            frame.setSize(600, 400);
            frame.getContentPane().add(newPanel);
            HtmlRendererContext newCtx = new LocalHtmlRendererContext(newPanel, this.getUserAgentContext());
            newCtx.navigate(url, "_this");
            return newCtx;
        }
    }
}