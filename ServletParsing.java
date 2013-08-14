package org.louisvuitton.bundle;

import java.io.IOException;
import java.io.PrintWriter;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.servlet.ServletException;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.commons.json.JSONException;
import org.xml.sax.SAXException;

/** * @scr.service interface="javax.servlet.Servlet"

 * @scr.component immediate="true" metatype="no"

 * @scr.property name="service.description" value="my servlet parser"

 * @scr.property name="service.vendor" value="Day"
 
 * @scr.property name="sling.servlet.paths" value="/test/servletParsing"
 
 * @scr.property name="sling.servlet.methods" values.0="GET" values.1="POST"

 */

public class ServletParsing extends SlingAllMethodsServlet{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected void doGet(SlingHttpServletRequest request,
			
            SlingHttpServletResponse response) throws ServletException,

            IOException {
		
		String DESTINATION_FOLDER = "content/dam/treeTest";
		String NODE_FILE_FOLDER = "var/dam/lv/testfolder/text/";
		String PARAMETER_URL = "parsedFile";
		
		PrintWriter out = response.getWriter();
		String fichier = request.getParameter(PARAMETER_URL);
		try {
			if(fichier!=null && !fichier.isEmpty()){
				ResourceResolver req= request.getResourceResolver();
		    	Session session = req.adaptTo(Session.class);
		    	Node root = session.getRootNode();
		    	Node tree = root.getNode(DESTINATION_FOLDER);
		    	out.println("Nom Fichier: "+fichier);
				if(root.hasNode(NODE_FILE_FOLDER+fichier)){
					Node nodeFichier = root.getNode(NODE_FILE_FOLDER+fichier);
					String[] nomFichierElements = fichier.split("\\.");
					String extensionFichier = nomFichierElements[nomFichierElements.length-1].toLowerCase();
					if(extensionFichier.equals("xml")){
						out.println("Vous lancez un parser XML.");
						ParserXML parserXML = new ParserXML(out, tree, nodeFichier, session);
						parserXML.launchParserXML();
					}
					else if(extensionFichier.equals("json")){
						out.println("Vous lancez un parser JSON.");
						ParserJSON parserJSON = new ParserJSON(out, tree, nodeFichier, session);
						parserJSON.launchParserJSON();
					}
					else{
						out.println("L'extension du fichier n'est pas connu.");
					}
				}
				else{
					out.println("Le fichier n'est pas dans le répertoire.");
				}
				session.save();
				session.logout();
			}
			else{
				out.println("Le paramètre parsedFile est vide.");
			}
			
		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			out.println(e);
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			out.println(e);
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			out.println(e);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			out.println(e);
		}
		out.flush();
	    out.close();
	}

}
