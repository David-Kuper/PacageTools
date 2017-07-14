package caculate;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XmlNode {
	Node node;
	NamedNodeMap attrs;
	NodeList childList;
	
	public String findNodeValue(String name) {
		if (attrs == null && attrs.getLength() <= 0) {
			throw new IllegalStateException("attrs must not be null and length > 0!");
		}
		String value;
		value = attrs.getNamedItem(name).getNodeValue();
		return value;
	}
	
	public void setNode(Node node) {
		this.node = node;
	}
	
	public void setAttrs(NamedNodeMap attrs) {
		this.attrs = attrs;
	}
	
	public void setChildList(NodeList childList) {
		this.childList = childList;
	}
	
	public NamedNodeMap getAttrs() {
		return attrs;
	}
	
	public NodeList getChildList() {
		return childList;
	}
	
	public Node getNode() {
		return node;
	}
	
	public boolean hasChildNode() {
		return childList != null && childList.getLength() > 0 ? true : false;
	}
	
	
}
