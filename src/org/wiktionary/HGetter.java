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
    private String htmlpath;
    private org.jsoup.nodes.Document jsoupdoc;
    
    public HGetter(String pword, HashMap<String, Object> phm) {
        if (hm == null) { hm = phm; }
        word = pword;
        htmlpath = ((JTextField) hm.get("htmldir")).getText();
    }
    public void getHtml() throws Exception {
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
    }

    void getImageAndCount() throws Exception {
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
                dao.setInt(6, 3);
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
        //throw new UnsupportedOperationException("Not yet implemented");
        if (jsoupdoc == null) {
            openJsoupDoc();
            if (jsoupdoc == null) throw new Exception("Cannot find html source. Inconsistant status in DB...");
        }
        Elements es = jsoupdoc.select("span.tocnumber"); //tocnumber elements
        String r = null; //root tocnumber
        String s = "", s1 = "";
        String[] toc = new String[es.size()];
        int n0 = 0;
        int[] osn = {0, 0, 0, 0, 0, 0};
        int[] sn = {0, 0, 0, 0, 0, 0};
        int[] lastosn = {0, 0, 0, 0, 0, 0};
        int[] nextosn = {0, 0, 0, 0, 0, 0};
        int[] lastsn = {0, 0, 0, 0, 0, 0};
        loops:
        {
            for (int i = 0; i < es.size(); i++) {
                org.jsoup.nodes.Element e = es.get(i);
                String[] split = e.html().split("\\.");
                String[] nextsplit;
                String tocid = "", tocidfull = "";
                Elements e1s = e.siblingElements();
                /*for (int i2 = 0; i2 < e1s.size(); i2++) {
                    if (e1s.get(i2).attr("class").equals("toctext")) {
                        toc[i] = e1s.get(i2).html();
                        break;
                    }
                }*/

                //tocnumbers[i] = es.get(i).
                if (r != null) {
                    if (e.html().length() == r.length()
                            && !(e.html().equals(r)) ) {break loops;}
                    n0 = n0 + 1;
                    Boolean movetree = false;
                    s = "\n" +  e.html() + "::"; //This are the toc numbers to be processed.
                    for (int i2 = 0; i2 < Math.min(6, osn.length); i2++) {
                        lastosn[i2] = osn[i2];
                        lastsn[i2] = sn[i2];
                    }
                    //s = s + Integer.valueOf(sn[0]).toString() + " is sn[0] now.";
                    //s = s + Integer.valueOf(sn[1]).toString() + " is sn[1] now.\n";   
                    
                    if (n0 == 1 ) {
                        for (int i2=0; i2<Math.min(6, split.length); i2++) {
                            osn[i2] = Integer.parseInt(split[i2]);
                        }
                        s = s+ "osn were assigned to zero for indices : ";
                        for (int i2=split.length; i2<6; i2++) {
                            osn[i2] = 0;
                            s = s + Integer.valueOf(i2).toString() + ",";
                        }
                        s = s + ";::";
                    } else if (n0 > 1 ) {
                        for (int i2=0; i2<Math.min(6, osn.length); i2++) {
                            osn[i2] = nextosn[i2];
                        }
                    }
                    if (i < es.size() - 1) {
                        nextsplit = es.get(i+1).html().split("\\.");
                        for (int i2=0; i2<Math.min(osn.length, nextsplit.length); i2++) {
                            nextosn[i2] = Integer.parseInt(nextsplit[i2]);
                        }
                        for (int i2=nextsplit.length; i2<6; i2++) {
                            nextosn[i2] = 0;
                        }
                    }
                    
                    for (int i2 = 0; i2<Math.min(6, osn.length); i2++) {
                        sn[i2] = lastsn[i2];
                    }
                    if (cl(lastosn) > cl(osn)) {
                        int up = 0;
                        up = cl(lastsn) - cl(lastosn) + cl(osn) - 1;
                        sn[up] = sn[up] + 1;
                        for (int i6 = up + 1; i6 < Math.min(6, osn.length); i6++) {
                            sn[i6] = 0; 
                        }
                    } else if (cl(lastosn) == cl(osn)) {
                        Elements e1sl = es.get(i - 1).siblingElements();
                        for (int i6 = 0; i6 < e1s.size(); i6++) {
                            if (e1sl.get(i6).attr("class").equals("toctext")) {
                                if (e1sl.get(i6).html().length() > 8) {
                                       if (e1sl.get(i6).html().substring(0, 9).equalsIgnoreCase("Etymology")
                                        || e1sl.get(i6).html().substring(0, 9).equalsIgnoreCase("Pronuncia")) 
                                       {movetree = true;
                                            break;
                                       }
                                }
                            }
                        }
                        if (movetree) {
                            sn[cl(sn)] = 1;
                            for (int i2 = cl(sn) + 1; i2 < sn.length; i2 ++) {
                                sn[i2] = 0;
                            }
                        } else {
                            System.out.print("Array index (should minus 1):");
                            System.out.println(cl(sn));
                            sn[cl(sn)-1] = sn[cl(sn)-1] + 1;
                        }
                    } else if (cl(lastosn) < cl(osn)) {
                        sn[cl(sn)] = 1;
                    }
                    s = s + word + ":: tocnumber is " + e.html() + ". Title is " + toc[i] + "\n";
                    s = s + "osn :";
                    for (int i1 = 0; i1< 6; i1++) {
                        s = s + Integer.valueOf(osn[i1]).toString() + ",";
                    }
                    s = s + "\nsn  :";
                    for (int i1 = 0; i1< 6; i1 ++) {
                        s = s + Integer.valueOf(sn[i1]).toString() + ",";
                    }
                    System.out.print(s);
                } else  { //r is null now
                    for (int i1 = 0; i1 < e1s.size(); i1++) {
                        if (r == null && e1s.get(i1).attr("class").equals("toctext")
                                && e1s.get(i1).html().equalsIgnoreCase("English")) {
                            r = e.html();
                            for (int i2 = 0; i2 < Math.min(6, split.length); i2++) {
                                osn[i2] = Integer.parseInt(split[i2]);
                            }
                            sn[0] = sn[0] + 1;
                            break;
                        }
                    }
                }
                tocidfull = e.parent().attr("href");
                if (tocid.contains("_")) {
                    tocid = tocidfull.substring(1, tocidfull.indexOf("_"));
                } else {
                    tocid = tocidfull.substring(1);
                }
                if (tocid.equalsIgnoreCase("Etymology")) {
                        getEtym(tocidfull, sn);
                } else if (tocid.equalsIgnoreCase("Pronunciation")) {
                        getPron(tocidfull, sn);
                } else if (tocid.equalsIgnoreCase("Verb")
                        || tocid.equalsIgnoreCase("Noun")
                        || tocid.equalsIgnoreCase("Pronoun")
                        || tocid.equalsIgnoreCase("Determiner")
                        || tocid.equalsIgnoreCase("Conjunction")
                        || tocid.equalsIgnoreCase("Adjective")
                        || tocid.equalsIgnoreCase("Adverb")
                        || tocid.equalsIgnoreCase("Article")
                        || tocid.equalsIgnoreCase("Interjection")
                        || tocid.equalsIgnoreCase("Numeral") 
                        || tocid.equalsIgnoreCase("Particle")
                        || tocid.equalsIgnoreCase("Preposition")) {
                    getMeaning(tocidfull, sn);
                }
            } //End of outmost for
        } // End of label loops
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

    private void getEtym(String tocid, int[] sn) {
        //throw new UnsupportedOperationException("Not yet implemented");
    }

    private void getPron(String tocid, int[] sn) {
        //throw new UnsupportedOperationException("Not yet implemented");
    }

    private void getMeaning(String tocid, int[] sn) {
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