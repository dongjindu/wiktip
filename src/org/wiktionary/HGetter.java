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

    void getXed() throws Exception{
        //throw new UnsupportedOperationException("Not yet implemented");
        if (jsoupdoc == null) {
            openJsoupDoc();
            if (jsoupdoc == null) throw new Exception("Cannot find html source. Inconsistant status in DB...");
        }
        Elements es = jsoupdoc.select("span.tocnumber"); //tocnumber elements
        String roottocnumber = null;
        String[][] toc = new String[3][es.size()];
        int n0 = 0;
        int sn1 = 0, sn2 = 0, sn3 = 0, sn4 = 0;
        loops:
        {
            for (int i = 0; i < es.size(); i++) {
                org.jsoup.nodes.Element e = es.get(i);
                Elements e1s = es.get(i).siblingElements();
                //tocnumbers[i] = es.get(i).
                if (roottocnumber != null) {
                    if (es.get(i).html().substring(0,2).equals(roottocnumber + ".")) {
                        n0 = n0 + 1;
                        toc[0][n0] = es.get(i).html();
                        if () {
                            
                        }
                    }
                }
                for (int i1 = 0; i1 < e1s.size(); i1++) {
                    if (roottocnumber == null && e1s.get(i1).attr("class").equals("toctext")
                            && e1s.get(i1).html().equalsIgnoreCase("English")) {
                        roottocnumber = es.get(i).html();
                        break;
                    }
                    if (roottocnumber != null) {
                        if (e1s.get(i1).html().equalsIgnoreCase("Pronunciation")
                                && e1s.get(i1).html().equals("")){
                            
                        }
                    }
                }
            }
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