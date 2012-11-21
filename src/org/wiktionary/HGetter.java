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
import java.util.HashMap;
import java.util.logging.*;

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
    private static HashMap<String, Object> hm;
    private org.w3c.dom.Document doc;
    private static String htmlpath;
    private org.jsoup.nodes.Document jsoupdoc;
    public boolean filefound = true;

    public HGetter(String pword, HashMap<String, Object> phm) {
        if (hm == null) { hm = phm; }
        if (htmlpath == null) {htmlpath = ((JTextField) hm.get("htmldir")).getText();}
        word = pword;
    }
    public void getHtml() {
        try {
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            Result output = new StreamResult(new File(htmlpath + "\\" + word + ".html"));
            //Logger.getLogger("org.lobobrowser").setLevel(Level.OFF);
            // Open a connection on the URL we want to render first.
            //String uri = "http://lobobrowser.org/browser/home.jsp";
            //String uri = "http://en.wiktionary.org/wiki/built-in";
            String uri = "http://en.wiktionary.org/wiki/" + word;
            URL url = new URL(uri);
            URLConnection connection = url.openConnection();
            InputStream in = connection.getInputStream();

            Reader reader = new InputStreamReader(in);

            InputSource is = new InputSourceImpl(reader, uri);
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
            DAO dao = new DAO();
            dao.update("update voc set htmled = ? where word = ?");
            dao.setBoolean(1, true);
            dao.setString(2, word);
            dao.executeUpdate();
        } catch (java.io.FileNotFoundException ioe) {
            filefound = false;
        } catch (Exception ex) {
            //other exceptions not supported yet!
        }
    }

    void xImageAndCount() throws Exception {
        //throw new UnsupportedOperationException("Not yet implemented");
        if (jsoupdoc == null) {
            openJsoupDoc();
            if (jsoupdoc == null) {throw new Exception("Cannot find html source. Inconsistant status in DB...");}
        }
        DAO dao = new DAO();
        Elements images = jsoupdoc.select("img.thumbimage");
        for (int i=0; i < images.size(); i++) {
            String uri = images.get(i).attr("src");
            URL url = new URL("http:" +uri);
            ReadableByteChannel rbc = Channels.newChannel(url.openStream());
            String ifn;
            int si0 = uri.lastIndexOf('/');
            int si1 = uri.substring(0, si0 - 1).lastIndexOf('/');
            if (uri.substring(si0).equals( "/" )) {
                ifn = uri.substring(si1+1, si0);
            } else {
                ifn = uri.substring(si0+1, uri.length());
            }
            FileOutputStream fos = new FileOutputStream(htmlpath + "\\image\\" + ifn);
            fos.getChannel().transferFrom(rbc, 0, 1 << 24);
            synchronized (DAO.daolock) {
                dao.update("insert into voc3 (word, sn1, sn2, sn3, sn4, type, imageurl, image) "
                        + "values(?, ?, ?, ?, ?, ?, ?, ?)");
                dao.setString(1, word);
                dao.setInt(2, i + 100); //Only for image
                dao.setInt(3, 0);
                dao.setInt(4, 0);
                dao.setInt(5, 0);
                dao.setInt(6, Ref.getType("image"));
                dao.setString(7, uri);
                dao.setString(8, ifn);
                dao.executeUpdate();
            }
        }
        synchronized (DAO.daolock) {
            dao.update("update voc set imaged = ? where word = ?");
            dao.setBoolean(1, true);
            dao.setString(2, word);
            dao.executeUpdate();
        }
        //doc
    }

    synchronized void getXed() throws Exception{
        Integer a = 0;
        synchronized (a) {
        //throw new UnsupportedOperationException("Not yet implemented");
        if (jsoupdoc == null) {
            openJsoupDoc();
            if (jsoupdoc == null) throw new Exception("Cannot find html source. Inconsistant status in DB...");
        }
        Elements es = jsoupdoc.select("span.tocnumber"); //tocnumber elements
        String r = null; //root tocnumber
        String s = "\n" + word + " getXed Started!\n";
        System.out.print(s);
        String[] toc = new String[es.size()];
        int n0 = 0, j = 0;
        int[] osn = {0, 0, 0, 0, 0, 0};
        int[] osnunfiltered = {0, 0, 0, 0, 0, 0}; 
        int[] lastosnunfiltered = {0, 0, 0, 0, 0, 0}; 
        int[] sn = {0, 0, 0, 0, 0, 0};
        int[] lastosn = {0, 0, 0, 0, 0, 0};
        int[] nextosn = {0, 0, 0, 0, 0, 0};
        int[] lastsn = {0, 0, 0, 0, 0, 0};
        boolean bincut = false, bref = false; //Actually cut started until last cut.
        int[] bcutsnroot = {0, 0, 0, 0, 0, 0};
        boolean b1 = false;
        loops:
        {
            for (int i = 0; i < es.size(); i++) {
                org.jsoup.nodes.Element e = es.get(i);
                String[] split = e.html().split("\\.");
                String tocid = "", tocidfull = "";
                String s6 = e.parent().attr("href");

                for (int i1=0; i1 < osn.length; i1++) {
                    lastosnunfiltered[i1] = osnunfiltered[i1];
                }

                for (int i1 = 0; i1 < Math.min(6, split.length); i1++) {
                    osnunfiltered[i1] = Integer.parseInt(split[i1]);
                }
                for (int i1 = split.length; i1 < 6; i1++) {
                    osnunfiltered[i1] = 0;
                }
/*
                String ps0 = "\nps0 Output OSNunfiltered now for ";
                ps0 = ps0 + e.html() + "," + e.parent().attr("href") + " :";
                for (int i1 = 0; i1 < 6; i1++) {
                    ps0 = ps0 + osnunfiltered[i1] + " , ";
                }
                System.out.print(ps0);
*/
                bref = Ref.hasRef(s6.indexOf("_")>0 ? s6.substring(1, s6.indexOf("_")) : s6.substring(1)); //Only the type in ref will be a mapped sn[] 
                if (!bref) {
                    newRef(s6.indexOf("_")>0 ? s6.substring(1, s6.indexOf("_")) : s6.substring(1)); //So far the only post processing and not necessary for runtime.
                }
                if (!bref && !bincut) { //Starting of cut
                    bincut = !bref;
                    for (int i1 = 0; i1 < osn.length; i1++) {
                        bcutsnroot[i1] = osnunfiltered[i1];
                    }
                    j = i;
                    continue;
                } 
                if (bincut) { //Check if current toc is the children of the bcutsnroot
                    boolean bsubnode = true; //Assumpt true
                    for (int i1 =0; i1 < cl(bcutsnroot); i1++) {
                        if ( bcutsnroot[i1] != osnunfiltered[i1]) {
                            bsubnode = false;
                            break;
                        }
                    }
                    if (bsubnode) {
                        continue; //cut
                    } else if (!bsubnode) {
                        if (!bref) {
                            bincut = true;
                            for (int i1 = 0; i1 < osn.length; i1 ++) {
                                bcutsnroot[i1] = osnunfiltered[i1];
                            }
                            j = i;
                            continue;
                        }
                    }
                }
                
                if (r != null) {
                    if (e.html().length() == r.length()
                            && !(e.html().equals(r)) ) {break loops;}
                    n0 = n0 + 1;
                    Boolean movetree = false;
                    for (int i2 = 0; i2 < osn.length; i2++) {
                        lastosn[i2] = osn[i2];
                        lastsn[i2] = sn[i2];
                    }
                    
                    for (int i2 = 0; i2 < osn.length; i2++) {
                        osn[i2] = osnunfiltered[i2];
                    }
/*
                String ps1 = "\nps1 Output osn now : ";
                for (int i1=0; i1<6; i1++) {
                    ps1 = ps1 + osn[i1] + ";";
                }
                ps1 = ps1;
                System.out.print(ps1);
*/                    
                    if (cl(lastosn) > cl(osn)) {
                        int up = 0;
                        up = cl(lastsn) - cl(lastosn) + cl(osn) - 1;
                        sn[up] = sn[up] + 1;
                        for (int i6 = up + 1; i6 < Math.min(6, osn.length); i6++) {
                            sn[i6] = 0; 
                        }
                    } else if (cl(lastosn) == cl(osn)) {
                        String s7 = es.get(i-1).parent().attr("href"); //Consider how to use j(the cycle index of last cut root)
                        String s8 = s7.indexOf("_") > 0 ? s7.substring(1, s7.indexOf("_")) : s7.substring(1);
                        if (s8.equalsIgnoreCase("Etymology") || s8.equalsIgnoreCase("Pronunciation")) {
                            movetree = true;
                        };
                        if (movetree) {
                            sn[cl(sn)] = 1;
                            for (int i2 = cl(sn) + 1; i2 < sn.length; i2 ++) {
                                sn[i2] = 0;
                            }
                        } else {
                            sn[cl(sn)-1] = sn[cl(sn)-1] + 1;
                        }
                    } else if (cl(lastosn) < cl(osn)) {
                        sn[cl(sn)] = 1;
                    }
                    s = "\n" + word + ":: tocnumber is " + e.html() + ". Href is " + e.parent().attr("href") + "\n";
                    System.out.print(s);
                    s = "osn :";
                    for (int i1 = 0; i1< 6; i1++) {
                        s = s + Integer.valueOf(osn[i1]).toString() + ",";
                    }
                    s = s + "\nsn  :";
                    for (int i1 = 0; i1< 6; i1 ++) {
                        s = s + Integer.valueOf(sn[i1]).toString() + ",";
                    }
                    System.out.print(s);
/*                    synchronized (DAO.daolock) {
                        try {
                            DAO dao = new DAO();
                            dao.update("insert into voc3test(word, sn1, sn2, sn3, sn4, sn5) values"
                                    + "(?, ?, ?, ?, ?, ?)");
                            dao.setString(1, word);
                            dao.setInt(2, sn[1]);
                            dao.setInt(3, sn[2]);
                            dao.setInt(4, sn[3]);
                            dao.setInt(5, sn[4]);
                            dao.setInt(6, sn[5]);
                            dao.executeUpdate();
                        } catch (Exception ex) {
                            System.out.print("\nException caught for voc3test at " + word + ":");
                            System.out.print(sn[0]); System.out.print(",");
                            System.out.print(sn[1]); System.out.print(",");
                            System.out.print(sn[2]); System.out.print(",");
                            System.out.print(sn[3]); System.out.print(",");
                            System.out.print(sn[4]); System.out.print(",");
                            System.out.print(sn[5]); System.out.print("....");
                        }
                    }
*/                    
                    tocidfull = e.parent().attr("href").substring(1);
                    tocid = tocidfull.indexOf("_") > 0 ? tocidfull.substring(0, tocidfull.indexOf("_")) : tocidfull.substring(0);
   
                    if (Ref.hasRef(tocid) && Ref.isEtym(tocid)) {
                        xEtym(tocidfull, sn.clone());
                    } else if (Ref.hasRef(tocid) && Ref.isPronun(tocid)) {
                        xPron(tocidfull, sn.clone());
                    } else if (Ref.hasRef(tocid) && Ref.getType(tocid) > 100 && Ref.getType(tocid) <= 1000) {
                        xMeaning(tocidfull, sn.clone());
                    } else if (Ref.hasRef(tocid) && Ref.isSynonym(tocid)) {
                        xSyn(tocidfull, sn.clone());
                    } else if (Ref.hasRef(tocid) && Ref.isAntonym(tocid)) {
                        xAnt(tocidfull, sn.clone());
                    } else if (!Ref.hasRef(tocid)) {
                        //Could never be executed now that it has been filtered by bcut indicator.
                        newRef(tocid); //Later have to check Referrence manually to decide if I want to extract it anyway.
                    }
                    DAO dao = new DAO();
                    dao.update("update voc set xed = true where word = ?");
                    dao.setString(1, word);
                    dao.executeUpdate();
                } else  { //r is null so far
                    String s9 = e.parent().attr("href");
                    String s10 = s9.indexOf("_") > 0 ? s9.substring(1, s9.indexOf("_")) : s9.substring(1);
                    if (s10.equals("English")) {
                        for (int i2 = 0; i2 < osn.length; i2++) {
                            osn[i2] = osnunfiltered[i2];
                        }
                        r = e.html();
                        sn[0] = sn[0] + 1;
                    }
                }
            } //End of outmost for
        } // End of label loops
        }
    }

    private void openJsoupDoc() {
        try {
            //throw new UnsupportedOperationException("Not yet implemented");
            File input = new File( htmlpath + "\\" + word + ".html");
            jsoupdoc = Jsoup.parse(input, "UTF-8");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(HGetter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException uee) {
            uee.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
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

    private void xEtym(String t, int[] sn) throws Exception{
        //throw new UnsupportedOperationException("Not yet implemented");
        if (jsoupdoc == null) {
            openJsoupDoc();
            if (jsoupdoc == null) throw new Exception("Cannot find html source. Inconsistant status in DB...");
        }
        org.jsoup.nodes.Element e = jsoupdoc.getElementById(t);
        org.jsoup.nodes.Element e0 = e.parent();
        String etym;
        l1:{
            while(e0 != null) {
                e0 = e0.nextElementSibling();
                if (e0 == null) break l1;
                org.jsoup.nodes.Document d = Jsoup.parse(e0.html(), "UTF-8");
                if (d == null) {
                    System.out.println("parse failed" + e0.html());
                }
                Elements es0 = d.select(".etyl"); //e0.select("etyl") does not work
                
                org.jsoup.nodes.Element e1;
                if (es0.size() > 0) {
                    e1 = es0.get(0); 
                    etym = Jsoup.parse(e1.html(), "UTF-8").text() + " " + Jsoup.parse(e1.nextElementSibling().html(), "UTF-8").text();
                    etym = etym.substring(0, Math.min(50, etym.length()));
                    synchronized (DAO.daolock) {
                        try {
                            DAO dao = new DAO();
                            dao.update("insert into voc3 (word, sn1, sn2, sn3, sn4, type, etym) values"
                                    + "(?, ?, ?, ?, ?, ?, ?)");
                            dao.setString(1, word);
                            dao.setInt(2, sn[1]);
                            dao.setInt(3, sn[2]);
                            dao.setInt(4, sn[3]);
                            dao.setInt(5, sn[4]);
                            dao.setInt(6, Ref.getType(t.contains("_") ? t.substring(0, t.indexOf("_")) : t));
                            dao.setString(7, etym);
                            dao.executeUpdate();
//                            dao.update("commit");
//                            dao.executeUpdate();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                    break l1;
                }
            }
        }
    }

    private void xPron(String t, int[] sn) throws Exception{
        //throw new UnsupportedOperationException("Not yet implemented");
        if (jsoupdoc == null) {
            openJsoupDoc();
            if (jsoupdoc == null) throw new Exception("Cannot find html source. Inconsistant status in DB...");
        }
        org.jsoup.nodes.Element e = jsoupdoc.getElementById(t);
        org.jsoup.nodes.Element e0 = e.parent();
        String pronunuk = "", pronunus = "", pronun = "";
        Boolean pronunukfound = false, pronunusfound = false, pronunfound = false;
        l1: {
            while (e0!=null) {
                e0 = e0.nextElementSibling();
                if (e0 == null) break; 
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
                            }
                            if ((pronunukfound && pronunusfound) || pronunfound) break;
                        }
                    }
                    break l1; // Upon first "ul" found, execute pronunciation search. And break at the end of search. Meaning second "ul" is not even in the loop
                }
            }
        }// end of l1 label block
        if (pronunukfound || pronunusfound || pronunfound) {
            try {
                DAO dao = new DAO();
                synchronized (DAO.daolock) {
                    dao.update("insert into voc3 (word, sn1, sn2, sn3, sn4, type, pronun, pronuk, pronus) values "
                            + "(?, ?, ?, ?, ?, ?, ?, ?, ?)");
                    dao.setString(1, word);
                    dao.setInt(2, sn[1]);
                    dao.setInt(3, sn[2]);
                    dao.setInt(4, sn[3]);
                    dao.setInt(5, sn[4]);
                    dao.setInt(6, Ref.getType(t.contains("_") ? t.substring(0, t.indexOf("_")) : t));
                    dao.setString(7, pronun);
                    dao.setString(8, pronunuk);
                    dao.setString(9, pronunus);
                    dao.executeUpdate();
                }
            } catch (Exception ex) {
                System.err.println("Exception caught in xPron");
            }
        }

    }

    private void xMeaning(String t, int[] sn) throws Exception{
        //throw new UnsupportedOperationException("Not yet implemented");
        if (jsoupdoc == null) {
            openJsoupDoc();
            if (jsoupdoc == null) throw new Exception("Cannot find html source. Inconsistant status in DB...");
        }
        System.out.print("\nNow in xMeaning!");
        org.jsoup.nodes.Element e = jsoupdoc.getElementById(t);
        org.jsoup.nodes.Element e0 = e.parent();
        String meaning = "";
        boolean b0 = false;
        while(e0!=null) {
            e0 = e0.nextElementSibling();
            if (e0==null) break; 
            if (e0.tagName().equals("ol")) {
                org.jsoup.select.Elements es0 = e0.children();
                for (int i = 0; i < es0.size(); i ++) {
                    //System.out.print("\nli:" + es0.get(i).html());
                    if (es0.get(i).tagName().equals("li")) {
//                        System.out.print("\nli:" + es0.get(i).html());
                        org.jsoup.nodes.Element e1 = es0.get(i);
                        if (e1.html().indexOf("<dl>") >= 0) { 
                            String limeaning = e1.html().substring(0, e1.html().indexOf("<dl>"));
                            if (meaning.length() < 800) 
                            meaning = meaning + Jsoup.parse(limeaning, "UTF-8").text() + "*";
                        } else {
                            String limeaning = e1.html();
                            if (meaning.length() < 800)
                            meaning = meaning + Jsoup.parse(limeaning, "UTF-8").text() + "*";
                        }
                    }
                }
                if (meaning.length() > 1) {
                    synchronized (DAO.daolock) {
                        try {
                            DAO dao = new DAO();
                            dao.update("insert into voc3 (word, sn1, sn2, sn3, sn4, type, meaning) values"
                                    + "(?, ?, ?, ?, ?, ?, ?)");
                            dao.setString(1, word);
                            dao.setInt(2, sn[1]);
                            dao.setInt(3, sn[2]);
                            dao.setInt(4, sn[3]);
                            dao.setInt(5, sn[4]);
                            dao.setInt(6, Ref.getType(t.contains("_") ? t.substring(0, t.indexOf("_")) : t));
                            dao.setString(7, meaning.substring(0, meaning.length()-1));
                            dao.executeUpdate();
                        } catch (Exception ex) {
                            //System.err.println("Exception caught in xMeaning");
                            ex.printStackTrace();
                        }
                    }
                }
                break; //Only search one ol element then break from while block.
            }
        }
    }

    private void newRef(String tocid) {
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