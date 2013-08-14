package org.louisvuitton.bundle;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.nio.charset.Charset;

import javax.jcr.ItemExistsException;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.ValueFormatException;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.nodetype.NoSuchNodeTypeException;
import javax.jcr.version.VersionException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class ParserXML{
	private PrintWriter out;
	private javax.jcr.Node tree;
	private javax.jcr.Node xmlNode;
	private Session session;
	
	
	
	public ParserXML(PrintWriter out, javax.jcr.Node tree,
			javax.jcr.Node xmlNode, Session session) {
		super();
		this.out = out;
		this.tree = tree;
		this.xmlNode = xmlNode;
		this.session = session;
	}
	
	public void launchParserXML() throws ParserConfigurationException, ValueFormatException, PathNotFoundException, RepositoryException, SAXException, IOException{
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    	dbf.setIgnoringElementContentWhitespace(true);
    	dbf.setCoalescing(true);
    	dbf.setIgnoringComments(true);
    	dbf.setValidating(false);
    	dbf.setNamespaceAware(true);
    	DocumentBuilder db = null;
    	db = dbf.newDocumentBuilder();
    	String xmlString = xmlNode.getNode("jcr:content").getProperty("jcr:data").getString();
    	String xmlStringUTF8 = new String(xmlString.getBytes("windows-1252"), Charset.forName("windows-1252"));
    	InputSource is = new InputSource();
	    is.setCharacterStream(new StringReader(xmlStringUTF8));
    	//InputStream is= xmlNode.getNode("jcr:content").getProperty("jcr:data").getBinary().getStream();
        Document doc = db.parse(is);
        Node xmlTag = doc.getFirstChild();
        if(xmlTag.getNodeName().equals("xml")){
	        if(xmlTag.hasChildNodes()){
	        	NodeList xmlTagChildren = xmlTag.getChildNodes();
	        	for(int i=0; i<xmlTagChildren.getLength(); i++){
	    	        Node xmlTagChild = xmlTagChildren.item(i);
	    	        if(xmlTagChild.getNodeType() != Node.TEXT_NODE){
			    	    String rootName= tagNameOrID(xmlTagChild);
			    	    if(tree.hasNode(rootName)){
			    	    	javax.jcr.Node rootNode = tree.getNode(rootName);
			    	    	rootNode.remove();
			    	    	session.save();
	    	        	}
			    	    javax.jcr.Node rootNode = tree.addNode(rootName,"nt:unstructured");
			    	    if(xmlTagChild.hasChildNodes()){
				    	     parse(rootNode, xmlTagChild);
			    	    }
	    	        }
	        	}
	        	out.println("TreeNodes created.");
	        }
        }
        else{
        	String rootName= tagNameOrID(xmlTag);
    	    if(!tree.hasNode(rootName)){
	    	    javax.jcr.Node rootNode = tree.addNode(rootName,"nt:unstructured");
	    	    if(xmlTag.hasChildNodes()){
		    	     parse(rootNode, xmlTag);
	    	    }
	    	    out.println("TreeNodes created.");
        	}
        	else{
        		out.println("L'arborescence dont "+rootName+" est le root existe déjà");
        	}
        }
	}
//	protected void doGet(SlingHttpServletRequest request,
//			
//            SlingHttpServletResponse response) throws ServletException,
//
//            IOException {
//		
//		String DESTINATIONFOLDER = "content/dam/treeTest";
//		String NODEXMLFILE = "var/dam/lv/testfolder/text/animation.xml";
//		
//		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
//    	dbf.setIgnoringElementContentWhitespace(true);
//    	dbf.setCoalescing(true);
//    	dbf.setIgnoringComments(true);
//    	DocumentBuilder db = null;
//    	PrintWriter out = response.getWriter();
//    	try {
//    	    db = dbf.newDocumentBuilder();
//    	    try {
//    	    	ResourceResolver req= request.getResourceResolver();
//		    	Session session = req.adaptTo(Session.class);
//		    	javax.jcr.Node root = session.getRootNode();
//		    	if(root.hasNode(DESTINATIONFOLDER)){
//					javax.jcr.Node tree = root.getNode(DESTINATIONFOLDER);
//					if(root.hasNode(NODEXMLFILE)){
//						javax.jcr.Node xmlNode= root.getNode(NODEXMLFILE);
//						InputStream is= xmlNode.getNode("jcr:content").getProperty("jcr:data").getBinary().getStream();
//		    	        Document doc = db.parse(is);
//		    	        Node xmlTag = doc.getFirstChild();
//		    	        if(xmlTag.getNodeName().equals("xml")){
//			    	        if(xmlTag.hasChildNodes()){
//			    	        	NodeList xmlTagChildren = xmlTag.getChildNodes();
//			    	        	for(int i=0; i<xmlTagChildren.getLength(); i++){
//					    	        Node xmlTagChild = xmlTagChildren.item(i);
//					    	        if(xmlTagChild.getNodeType() != Node.TEXT_NODE){
//							    	    String rootName= tagNameOrID(xmlTagChild);
//							    	    if(!tree.hasNode(rootName)){
//								    	    javax.jcr.Node rootNode = tree.addNode(rootName,"nt:unstructured");
//								    	    if(xmlTagChild.hasChildNodes()){
//									    	     parse(rootNode, xmlTagChild);
//								    	    }
//					    	        	}
//					    	        	else{
//					    	        		out.println("L'arborescence dont "+rootName+" est le root existe déjà");
//					    	        	}
//					    	        }
//			    	        	}
//			    	        	out.println("TreeNodes created.");
//			    	        }
//		    	        }
//		    	        else{
//		    	        	String rootName= tagNameOrID(xmlTag);
//				    	    if(!tree.hasNode(rootName)){
//					    	    javax.jcr.Node rootNode = tree.addNode(rootName,"nt:unstructured");
//					    	    if(xmlTag.hasChildNodes()){
//						    	     parse(rootNode, xmlTag);
//					    	    }
//					    	    out.println("TreeNodes created.");
//		    	        	}
//		    	        	else{
//		    	        		out.println("L'arborescence dont "+rootName+" est le root existe déjà");
//		    	        	}
//		    	        }
//		    	        session.save();
//						session.logout();
//					}
//					else{
//						out.println("Le fichier n'existe pas.");
//					}
//		    	}
//		    	else{
//		    		out.println("Le répertoire de destination n'existe pas.");
//		    	}
//    	    } catch (SAXException e) {
//    	    	e.printStackTrace();
//    	    } catch (IOException e) {
//    	    	e.printStackTrace();
//    	    } catch (RepositoryException e) {
//				e.printStackTrace();
//			} 
//    	} catch (ParserConfigurationException e1) {
//    		e1.printStackTrace();
//    	}
//    	out.flush();
//	    out.close();
//    }

//    protected void doPost(SlingHttpServletRequest request,
//
//            SlingHttpServletResponse response) throws ServletException,
//
//            IOException {
//    	
//    	//Récupération du contenu du textarea.
//    	
//    	String xml =new String(request.getParameter("xmlText").getBytes(),"UTF-8");
//    	
//    	if(!xml.isEmpty()){
//	    	//Permet d'instancier un document builder.
//	    	DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
//	    	dbf.setIgnoringElementContentWhitespace(true);
//	    	dbf.setCoalescing(true);
//	    	dbf.setIgnoringComments(true);
//	    	DocumentBuilder db = null;
//	    	PrintWriter out = response.getWriter();
//	    	try {
//	    	    db = dbf.newDocumentBuilder();
//	    	    //Lis le contenu du textarea.
//	    	    InputSource is = new InputSource();
//	    	    is.setCharacterStream(new StringReader(xml));
//	    	    //Create a connection to the Adobe Day CQ repository running on local host
//	    	    try {
//	    	    	ResourceResolver req= request.getResourceResolver();
//			    	Session session = req.adaptTo(Session.class);
//			    	javax.jcr.Node root = session.getRootNode();
//					javax.jcr.Node tree = root.getNode("content/dam/treeTest");
//	    	    	//Le text est parse de manière à l'interpréter comme un XML.
//	    	        Document doc = db.parse(is);
//	    	        Node xmlTag = doc.getFirstChild();
//	    	        if(xmlTag.getNodeName().equals("xml")){
//		    	        if(xmlTag.hasChildNodes()){
//		    	        	NodeList xmlTagChildren = xmlTag.getChildNodes();
//		    	        	for(int i=0; i<xmlTagChildren.getLength(); i++){
//				    	        Node xmlTagChild = xmlTagChildren.item(i);
//				    	        if(xmlTagChild.getNodeType() != Node.TEXT_NODE){
//						    	    String rootName= tagNameOrID(xmlTagChild);
//						    	    if(!tree.hasNode(rootName)){
//							    	    javax.jcr.Node rootNode = tree.addNode(rootName,"nt:unstructured");
//							    	    if(xmlTagChild.hasChildNodes()){
//								    	     parse(rootNode, xmlTagChild);
//							    	    }
//				    	        	}
//				    	        	else{
//				    	        		out.println("L'arborescence dont "+rootName+" est le root existe déjà");
//				    	        	}
//				    	        }
//		    	        	}
//		    	        	out.println("TreeNodes created.");
//		    	        }
//	    	        }
//	    	        else{
//	    	        	String rootName= tagNameOrID(xmlTag);
//			    	    if(!tree.hasNode(rootName)){
//				    	    javax.jcr.Node rootNode = tree.addNode(rootName,"nt:unstructured");
//				    	    if(xmlTag.hasChildNodes()){
//					    	     parse(rootNode, xmlTag);
//				    	    }
//				    	    out.println("TreeNodes created.");
//	    	        	}
//	    	        	else{
//	    	        		out.println("L'arborescence dont "+rootName+" est le root existe déjà");
//	    	        	}
//	    	        }
//	    	        session.save();
//					session.logout();
//	    	    } catch (SAXException e) {
//	    	    	e.printStackTrace();
//	    	    } catch (IOException e) {
//	    	    	e.printStackTrace();
//	    	    } catch (RepositoryException e) {
//					e.printStackTrace();
//				} 
//	    	} catch (ParserConfigurationException e1) {
//	    		e1.printStackTrace();
//	    	}
//	    	out.flush();
//		    out.close();
//    	}
//    	
//    }
    
    public void parse(javax.jcr.Node parent, Node docParent){
    	if(parent != null && docParent != null){
	    	if(docParent.hasChildNodes()){
	    		try {
		    		NodeList tmp= docParent.getChildNodes();
		    		if(!onlyContentInvisibleTag(tmp)){
			    		for(int i=0; i<tmp.getLength(); i++){
				    		Node tmpNode = tmp.item(i);
				    		if(tmpNode.getNodeType() != Node.TEXT_NODE){
				    			String tmpNodeName = tagNameOrID(tmpNode);
				    			javax.jcr.Node child = parent.addNode(tmpNodeName, "nt:unstructured");
				    			if(tmpNode.hasChildNodes()){
				    				parse(child, tmpNode);
				    			}
				    		}
			    		}
		    		}
		    		else{
		    			parent.setProperty("val", docParent.getTextContent());
		    		}
		    	} catch (ItemExistsException e) {
		    		out.println(e);
				} catch (PathNotFoundException e) {
					out.println(e);
				} catch (NoSuchNodeTypeException e) {
					out.println(e);
				} catch (LockException e) {
					out.println(e);
				} catch (VersionException e) {
					out.println(e);
				} catch (ConstraintViolationException e) {
					out.println(e);
				} catch (RepositoryException e) {
					out.println(e);
				}	
	    		
	    	}
    	}
    }
    
    //Fonction qui permet de vérifier si les enfants du noeud contient que des #text
    public boolean onlyContentInvisibleTag(NodeList list){
    	if(list != null){
	    	int i=0;
	    	while((i<list.getLength()) &&(list.item(i).getNodeType() == Node.TEXT_NODE)){
	    		i++;
	    	}
	    	if(i == list.getLength()){
	    		return true;
	    	}
	    	else{
	    		return false;
	    	}
    	}
    	else{
    		return false;
    	}
    }
    
    public String tagNameOrID(Node e){
    	if(e != null){
	    	if(e.hasAttributes()){
	    		NamedNodeMap attr = e.getAttributes();
	    		int i=0;
	    		boolean trouve= false;
	    		while(i<attr.getLength() && !trouve){
	    			trouve = attr.item(i).getNodeName().equals("id");
	    			i++;
	    		}
	    		if(trouve){
	    			return attr.item(i-1).getNodeValue();
	    		}
	    		else{
	    			return e.getNodeName();
	    		}
	    	}
	    	else{
	    		return e.getNodeName();
	    	}
    	}
    	else{
    		return "";
    	}
    }
}
