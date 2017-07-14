package caculate;
import java.util.*;
import java.io.File;
import java.io.IOException;

import javax.xml.parsers.*;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

public class XmlBuilder {
		
		public static XmlNode parseXML(String fileName) {
			Document document = null;
			try {
				DocumentBuilder db = DocumentBuilderFactory.newInstance()
						.newDocumentBuilder();
				document = db.parse(new File(fileName));
			    Element root = document.getDocumentElement();
			    XmlNode xmlObject = new XmlNode();
			    xmlObject.attrs = root.getAttributes();
			    xmlObject.childList = root.getChildNodes();
			    xmlObject.node = root;
			    
			    return xmlObject;
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
		
		public static String readNodeAttrValue(Node node,String attrName) {
			if (node == null || attrName == null) {
				throw new NullPointerException();
			}
			String value = null ;
			try {
				value = node.getAttributes().getNamedItem(attrName).getNodeValue();
			} catch (Exception e) {
				// TODO: handle exception
			}
			
			return value;
		}
		
		public static List<Node> readNode(String fileName,String nodeName){
			Document document = null;
			try {
				DocumentBuilder db = DocumentBuilderFactory.newInstance()
						.newDocumentBuilder();
				document = db.parse(new File(fileName));
			    Element root = document.getDocumentElement();
			    NodeList list = root.getElementsByTagName(nodeName);// 获得page元素
				List<Node> nodes = new ArrayList<>();
				for (int i = 0; i < list.getLength(); i++) {
					if (nodeName.equals(list.item(i).getNodeName())) {
						nodes.add(list.item(i));
					}
				}
				return nodes;
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
}		
