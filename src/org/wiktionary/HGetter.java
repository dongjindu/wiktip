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

public class HGetter {
    private String word;
    private static HashMap<String, Object> hm;
    private Document doc;
    private String path;
    
    public HGetter(String pword, HashMap<String, Object> phm) {
        if (hm == null) { hm = phm; }
        word = pword;
        path = ((JTextField) hm.get("htmldir")).getText();
    }
    public void getHtml() throws Exception {
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        Result output = new StreamResult(new File(path + "\\" + word + ".html"));
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
        if (doc == null) {
            openDoc();
            if (doc == null) {throw new Exception("Cannot find html source. Inconsistant status in DB...");}
        }
        doc.get
        URL website = new URL("http://www.website.com/information.asp");
        ReadableByteChannel rbc = Channels.newChannel(website.openStream());
        FileOutputStream fos = new FileOutputStream("information.html");
        fos.getChannel().transferFrom(rbc, 0, 1 << 24);
        //doc
    }

    void getXed() throws Exception{
        //throw new UnsupportedOperationException("Not yet implemented");
        if (doc == null) {
            openDoc();
            if (doc == null) {throw new Exception("Cannot find html source. Inconsistant status in DB...");}
        }
    }

    private void openDoc() {
        try {
            //throw new UnsupportedOperationException("Not yet implemented");
            doc = Res.loadXMLFromFile(new File(path + "\\" + word + ".html"));
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