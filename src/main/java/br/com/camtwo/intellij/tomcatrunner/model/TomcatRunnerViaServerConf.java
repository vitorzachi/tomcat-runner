package br.com.camtwo.intellij.tomcatrunner.model;

import com.intellij.execution.ExecutionException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Configure Runner for mapping contexts in server.xml file.
 *
 * @author Vitor Zachi Junior
 * @since 11/05/16.
 */
public class TomcatRunnerViaServerConf {

    public void configure(Path tomcatInstallationPath, Modules modules) throws ExecutionException {
        Path confFolder = tomcatInstallationPath.resolve("conf");
        try {
            if (Files.exists(confFolder)) {
                Path serverXml = confFolder.resolve("server.xml");

                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                org.w3c.dom.Document doc = builder.parse(serverXml.toUri().toString());
                XPathFactory xPathfactory = XPathFactory.newInstance();
                XPath xpath = xPathfactory.newXPath();
                XPathExpression expr = xpath.compile("/Server/Service[@name='Catalina']/Engine[@name='Catalina']/Host");
                XPathExpression exprContext = xpath.compile
                        ("/Server/Service[@name='Catalina']/Engine[@name='Catalina']/Host/Context");

                Node hostNode = (Node) expr.evaluate(doc, XPathConstants.NODE);
                NodeList nodeList = (NodeList) exprContext.evaluate(doc, XPathConstants.NODESET);

                if (nodeList != null && nodeList.getLength() > 0) {
                    for (int i = 0; i < nodeList.getLength(); i++) {
                        Node node = nodeList.item(i);
                        node.getParentNode().removeChild(node);
                    }
                }

                for (Module module : modules.getModules()) {
//                    if (module.isInternal()) {

//                    } else {
                    Element element = doc.createElement("Context");
                    element.setAttribute("docBase", module.getDocumentBase());
                    element.setAttribute("path", module.getContextNormalized());
                    element.setAttribute("reloadable", Boolean.toString(module.isReloadable()));
                    hostNode.appendChild(element);
//                    }
                }
                Source source = new DOMSource(doc);
                StreamResult result = new StreamResult(new OutputStreamWriter(new FileOutputStream(serverXml.toFile()),
                        "UTF-8"));
                Transformer xformer = TransformerFactory.newInstance().newTransformer();
                xformer.transform(source, result);
            } else {
                throw new ExecutionException("Conf folder in Tomcat installation is missing!");
            }
        } catch (IOException e) {
            throw new ExecutionException(e);
        } catch (Exception e) {
            throw new ExecutionException(e);
        }

    }

}
