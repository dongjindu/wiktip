/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.wiktionary;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;
import org.apache.fop.apps.FOUserAgent;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.MimeConstants;
import org.apache.fop.fonts.FontCollection;
import org.apache.fop.fonts.FontInfo;
import org.apache.fop.fonts.FontManager;

/**
 *
 * @author rose
 */
public class testFO {

    public static void main(String args[]) {
        System.gc();
        //test1();
        test2();
    }

    public static void test1() {

// Step 1: Construct a FopFactory
// (reuse if you plan to render multiple documents!)
        FopFactory fopFactory = FopFactory.newInstance();

// Step 2: Set up output stream.
// Note: Using BufferedOutputStream for performance reasons (helpful with FileOutputStreams).

        try {
            OutputStream out = new BufferedOutputStream(new FileOutputStream(new File("C:/Users/rose/Documents/NetBeansProjects/wiktionary/dict/fotest1.pdf")));
            // Step 3: Construct fop with desired output format
            Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, out);
            
            fopFactory.getFontManager().setFontBaseURL("file:///C:/Windows/Fonts");

            // Step 4: Setup JAXP using identity transformer
            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer transformer = factory.newTransformer(); // identity transformer

            // Step 5: Setup input and output for XSLT transformation
            // Setup input stream
            Source src = new StreamSource(new File("C:/Users/rose/Documents/NetBeansProjects/wiktionary/dict/fotest1.fo"));
            // Resulting SAX events (the generated FO) must be piped through to FOP
            Result res = new SAXResult(fop.getDefaultHandler());

            // Step 6: Start XSLT transformation and FOP processing
            transformer.transform(src, res);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //Clean-up
        }
    }

    private static void test2() {
        try {
            System.out.println("FOP ExampleXML2PDF\n");
            System.out.println("Preparing...");

            // Setup directories
            File baseDir = new File(".");
            File outDir = new File(baseDir, "dict");
            //outDir.mkdirs();

            // Setup input and output files
            File xmlfile = new File(baseDir, "dict/dictsmall.xml");
            File xsltfile = new File(baseDir, "dict/dictfo.xsl");
            File pdffile = new File(outDir, "dictsmall.pdf");

            System.out.println("Input: XML (" + xmlfile + ")");
            System.out.println("Stylesheet: " + xsltfile);
            System.out.println("Output: PDF (" + pdffile + ")");
            System.out.println();
            System.out.println("Transforming...");

            // configure fopFactory as desired
            FopFactory fopFactory = FopFactory.newInstance();
//            System.out.println("Font base url: " + fopFactory.getFontManager().getFontBaseURL().toString());
            
            FontManager fm = fopFactory.getFontManager();

            fopFactory.setUserConfig(new File("C:/Users/rose/Documents/NetBeansProjects/wiktionary/dict/fop-1.1/conf/fop.xconf"));
            //fm.getCacheFile();
           // fm.setFontBaseURL("file:///c:/Windows/Fonts");
            //System.out.println("Font base url after set to local windows font directory: " + fopFactory.getFontManager().getFontBaseURL().toString());
            //System.out.println("Sun's font base" + System.getProperty("com.sun.aas.instanceRoot"));
            FOUserAgent foUserAgent = fopFactory.newFOUserAgent();
            // configure foUserAgent as desired

            // Setup output
            OutputStream out = new java.io.FileOutputStream(pdffile);
            out = new java.io.BufferedOutputStream(out);

            try {
                // Construct fop with desired output format
                Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, foUserAgent, out);

                // Setup XSLT
                TransformerFactory factory = TransformerFactory.newInstance();
                Transformer transformer = factory.newTransformer(new StreamSource(xsltfile));

                // Set the value of a <param> in the stylesheet
                transformer.setParameter("versionParam", "2.0");

                // Setup input for XSLT transformation
                Source src = new StreamSource(xmlfile);

                // Resulting SAX events (the generated FO) must be piped through to FOP
                Result res = new SAXResult(fop.getDefaultHandler());

                // Start XSLT transformation and FOP processing
                transformer.transform(src, res);
            } finally {
                out.close();
            }
            System.out.println("Success!");
        } catch (Exception e) {
            e.printStackTrace(System.err);
            System.exit(-1);
        }
    }
        //throw new UnsupportedOperationException("Not yet implemented");
}
