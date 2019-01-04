package neo4jDemo;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class RequestParser {
	/**
	 * Parses XACML access request to obtain data for evaluation
	 * 
	 * @param filename
	 */

	public Map<String, List<Attribute>> parseRequest(String filename) {
		Map<String, List<Attribute>> attributeMap = null;
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(new File(filename));

			Element request = doc.getDocumentElement();
			NodeList attributes_list = request.getElementsByTagName("Attributes");
			attributeMap = new HashMap<String, List<Attribute>>();
			for (int i = 0; i < attributes_list.getLength(); i++) {
				Element attrsElem = (Element) attributes_list.item(i);
				NodeList attribute_list = attrsElem.getElementsByTagName("Attribute");
				List<Attribute> list = new ArrayList<Attribute>();
				for (int j = 0; j < attribute_list.getLength(); j++) {
					Attribute attr = new Attribute();
					Element attributeElem = (Element) attribute_list.item(j);
					attr.setAttributeId(attributeElem.getAttribute("AttributeId"));
					attr.setIssuer(attributeElem.getAttribute("Issuer"));
					Element attrValue = (Element) attributeElem.getElementsByTagName("AttributeValue").item(0);
					attr.setDatatype(attrValue.getAttribute("DataType"));
					attr.setValue(attrValue.getTextContent());
					list.add(attr);
				}
				attributeMap.put(attrsElem.getAttribute("Category"), list);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return attributeMap;
	}
	
}
