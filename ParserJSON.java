package org.louisvuitton.bundle;

import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import javax.jcr.ItemExistsException;
import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.ValueFormatException;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.version.VersionException;

import org.apache.sling.commons.json.JSONArray;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.JSONObject;


public class ParserJSON{
	
	private PrintWriter out;
	private Node tree;
	private Node jsonNode;
	private Session session;
	
	public ParserJSON(PrintWriter out, Node tree, Node jsonNode, Session session) {
		super();
		this.out = out;
		this.tree = tree;
		this.jsonNode = jsonNode;
		this.session = session;
	}
	
	public void launchParserJSON() throws ValueFormatException, PathNotFoundException, RepositoryException, JSONException, UnsupportedEncodingException{
		String jsonText=new String(jsonNode.getNode("jcr:content").getProperty("jcr:data").getString().getBytes("UTF-8"), "UTF-8");
		JSONObject doc = new JSONObject(jsonText);
		out.println(doc.toString(5));
		parseJSON(doc, tree);
		out.println("TreeNodes created.");
	}
	
//	protected void doGet(SlingHttpServletRequest request,
//			
//            SlingHttpServletResponse response) throws ServletException,
//
//            IOException {
//	
//	String DESTINATIONFOLDER = "content/dam/treeTest";
//	String NODEJSONFILE = "var/dam/lv/testfolder/text/animation.json";
//	
//	try {
//		PrintWriter out = response.getWriter();
//		ResourceResolver req= request.getResourceResolver();
//    	Session session = req.adaptTo(Session.class);
//    	Node root = session.getRootNode();
//    	if(root.hasNode(DESTINATIONFOLDER)){
//    		Node tree = root.getNode(DESTINATIONFOLDER);
//			if(root.hasNode(NODEJSONFILE)){
//				Node jsonNode= root.getNode(NODEJSONFILE);
//				String jsonText=jsonNode.getNode("jcr:content").getProperty("jcr:data").getString();
//				JSONObject doc = new JSONObject(jsonText);
//				out.println(doc.toString(5));
//				parseJSON(doc, tree);
//				out.println("TreeNodes created.");
//			}
//			else{
//				out.println("le fichier n'existe pas.");
//			}
//    	}
//    	else{
//    		out.println("le répertoire de destination n'existe pas.");
//    	}
//		session.save();
//		session.logout();
//		out.flush();
//	    out.close();
//	} catch (PathNotFoundException e) {
//		// TODO Auto-generated catch block
//		e.printStackTrace();
//	} catch (RepositoryException e) {
//		// TODO Auto-generated catch block
//		e.printStackTrace();
//	} catch (JSONException e) {
//		// TODO Auto-generated catch block
//		e.printStackTrace();
//	}
//	
//}

	public void parseJSON(JSONObject doc, Node parent){
			try {
				JSONArray listElements = doc.names();
				for(int i=0; i<listElements.length(); i++){
					if(parent.hasNode(listElements.getString(i))){
						Node elemJCR = parent.getNode(listElements.getString(i));
						elemJCR.remove();
						session.save();
					}
					Node elemJCR = parent.addNode(listElements.getString(i), "nt:unstructured");
					Object currentElem = doc.get(listElements.getString(i));
					if(elementIsAJSONArray(currentElem)){
						JSONArray current = jsonArrayData(doc, listElements, i);
						for(int j=0; j<current.length(); j++){
							JSONObject nextDoc = current.getJSONObject(j);
							parseJSON(nextDoc, elemJCR);
						}
					}
					else{
						String current = stringData(doc, listElements, i);
						elemJCR.setProperty("val", current);
					}
				}
			
			} catch (ItemExistsException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (PathNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (VersionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ConstraintViolationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (LockException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (RepositoryException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				out.println(e);
			} 
	
	}
	
	public boolean elementIsAJSONArray(Object elem){
		boolean res= false;
		if(elem != null){
			String typeClassElem = elem.getClass().getSimpleName();
			if(typeClassElem.equals("JSONArray")){
				res = true;
			}
		}
		return res;
	}
	
	public JSONArray jsonArrayData(JSONObject doc, JSONArray names, int index) throws JSONException{
		JSONArray res =  null;
		if(doc != null && names!=null && index>-1 && index<names.length()){
			res = doc.getJSONArray(names.getString(index));
		}
		return res;
	}
	
	public String stringData(JSONObject doc, JSONArray names, int index) throws JSONException{
		String res = "";
		if(doc != null && names!=null && index>-1 && index<names.length()){
			res = doc.getString(names.getString(index));
		}
		return res;
	}
	
}
