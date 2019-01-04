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
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import DBUtils.GraphNode;
import DBUtils.Relationship;

public class PolicyParser {
	/**
	 * Transforms the XACML request into Neo4J nodes
	 * 
	 * @param filename
	 * @return
	 */
	public void parsePolicy(String filename) {
		try {

			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(new File(filename));
			filename = filename.replaceAll("\\\\", "/");
			if (doc.getElementsByTagName("PolicySet").item(0) != null) {
				Element policySet = (Element) doc.getElementsByTagName("PolicySet").item(0);
				Map<String, String> policySetMap = new HashMap<String, String>();
				policySetMap.put("PolicySetId", policySet.getAttribute("PolicySetId"));
				policySetMap.put("filename", filename);
				GraphNode.createNode("PolicySet", policySetMap);
				NodeList policies = policySet.getElementsByTagName("Policy");
				for (int i = 0; i < policies.getLength(); i++) {
					Element policyElement = (Element) policies.item(i);
					Map<String, String> policyMap = policyHandler(policyElement, filename);
					Relationship.createRelationship("CONTAINS", "PolicySet", "Policy", policySetMap, policyMap);
				}
			} else {
				Element policyElement = (Element) doc.getElementsByTagName("Policy").item(0);
				policyHandler(policyElement, filename);

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public String[] stripAttribute(String str) {
		String tmp[] = str.split(":");
		int size = tmp.length;
		return new String[] { tmp[size - 2], tmp[size - 1] };
	}

	public Map<String, String> policyHandler(Element policyElement, String filename) {
		Map<String, String> policyMap = new HashMap<String, String>();
		policyMap.put("PolicyId", policyElement.getAttribute("PolicyId"));
		System.out.println(policyElement.getAttribute("PolicyId"));
		policyMap.put("filename", filename);
		GraphNode.createNode("Policy", policyMap);
		NodeList targets = policyElement.getElementsByTagName("Target");
		if (targets != null) {
			for (int i = 0; i < targets.getLength(); i++) {
				Element target = (Element) policyElement.getElementsByTagName("Target").item(i);
				if (target.hasChildNodes()) {
					Map<String, String> targetMap = new HashMap<String, String>();
					String id = Long.toString(System.currentTimeMillis());
					targetMap.put("id", id);
					GraphNode.createNode("Target", targetMap);
					Relationship.createRelationship("ASSOCIATED_WITH", "Policy", "Target", policyMap, targetMap);
					targetHandler(target, targetMap);
				}
			}
		}
		String strSad = "", strEad = "";
		NodeList env = policyElement.getElementsByTagName("Apply");
		if (env != null) {
			Element environment = (Element) env.item(0);
			if (environment != null) {
				NodeList id = environment.getElementsByTagName("SubjectAttributeDesignator");
				for (int j = 0; j < id.getLength(); j++) {
					Element sad = (Element) id.item(j);
					if (!strSad.equals(sad.getAttribute("AttributeId"))) {
						Map<String, String> envMap = new HashMap<String, String>();
						envMap.put("AttributeValue", sad.getAttribute("AttributeId"));
						GraphNode.createNode("Environment", envMap);
						Relationship.createRelationship("DEFINED_BY", "Policy", "Environment", policyMap, envMap);
						strSad = sad.getAttribute("AttributeId");
					}

				}
				NodeList ed = environment.getElementsByTagName("EnvironmentAttributeDesignator");
				for (int j=0; j < ed.getLength(); j++) {
					Element ead = (Element) ed.item(j);
					if (!strEad.equals(ead.getAttribute("AttributeId"))) {
						Map<String, String> envMap = new HashMap<String, String>();
						envMap.put("AttributeValue", ead.getAttribute("AttributeId"));
						GraphNode.createNode("Environment", envMap);
						Relationship.createRelationship("DEFINED_BY", "Policy", "Environment", policyMap, envMap);
						strEad = ead.getAttribute("AttributeId");
					}
				}
				
			}

		}
		return policyMap;
	}

	public void targetHandler(Element targetElement, Map<String, String> targetMap) {
		if (targetElement.hasChildNodes()) {
			NodeList list = targetElement.getChildNodes();
			for (int i = 0; i < list.getLength(); i++) {
				if (list.item(i).getNodeName().equals("Resources")) {
					Map<String, String> resourceMap = resourceHandler((Element) list.item(i));
					Relationship.createRelationship("DEFINED_BY", "Target", "Resource", targetMap, resourceMap);
				}/* else if (list.item(i).getNodeName().equals("Actions")) {
					NodeList actions = ((Element) list.item(i)).getElementsByTagName("Action");
					for (int j = 0; j < actions.getLength(); j++) {
						Map<String, String> actionMap = actionHandler((Element) actions.item(j));
						Relationship.createRelationship("DEFINED_BY", "Target", "Action", targetMap, actionMap);
					}
				}*/ else if (list.item(i).getNodeName().equals("Subjects")) {
					Map<String, String> actionMap = subjectHandler((Element) list.item(i));
					Relationship.createRelationship("DEFINED_BY", "Target", "Subject", targetMap, actionMap);
				}
			}
		}

	}

	public Map<String, String> resourceHandler(Element resource) {
		Map<String, String> resourceMap = new HashMap<String, String>();
		Element attributeValue = (Element) resource.getElementsByTagName("AttributeValue").item(0);
		resourceMap.put("AttributeValue", attributeValue.getTextContent());
		GraphNode.createNode("Resource", resourceMap);
		return resourceMap;
	}

	public Map<String, String> actionHandler(Element action) {
		Map<String, String> actionMap = new HashMap<String, String>();
		Element attributeValue = (Element) action.getElementsByTagName("AttributeValue").item(0);
		actionMap.put("AttributeValue", attributeValue.getTextContent());
		GraphNode.createNode("Action", actionMap);
		return actionMap;
	}

	public Map<String, String> subjectHandler(Element subject) {
		Map<String, String> subjectMap = new HashMap<String, String>();
		Element attributeValue = (Element) subject.getElementsByTagName("AttributeValue").item(0);
		subjectMap.put("AttributeValue", attributeValue.getTextContent());
		GraphNode.createNode("Subject", subjectMap);
		return subjectMap;
	}

	public Map<String, String> environmentHandler(Element environment) {
		Map<String, String> actionMap = new HashMap<String, String>();
		Element actionMatch = (Element) environment.getElementsByTagName("ActionMatch").item(0);
		actionMap.put("MatchId", actionMatch.getAttribute("MatchId"));
		Element attributeValue = (Element) actionMatch.getElementsByTagName("AttributeValue").item(0);
		actionMap.put("DataType", attributeValue.getAttribute("DataType"));
		actionMap.put("AttributeValue", attributeValue.getTextContent());
		Element resourceAttributeDesignator = (Element) actionMatch.getElementsByTagName("ResourceAttributeDesignator")
				.item(0);
		actionMap.put("AttributeId", resourceAttributeDesignator.getAttribute("AttributeId"));
		GraphNode.createNode("Action", actionMap);
		return actionMap;
	}

}
