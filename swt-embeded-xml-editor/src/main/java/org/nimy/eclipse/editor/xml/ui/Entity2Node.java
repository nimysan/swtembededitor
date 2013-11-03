package org.nimy.eclipse.editor.xml.ui;

import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

@SuppressWarnings("unchecked")
public class Entity2Node {
	private static final Logger logger = Logger.getLogger(Entity2Node.class);

	@SuppressWarnings("rawtypes")
	public static Entity node2Entity(Node node, Entity parent) {
		if (node != null) {
			logger.debug("--->>" + node.getNodeName() + " running");

			if ((node.getNodeType() == 3) && (node.getNodeValue().trim().equals(""))) {
				logger.debug(node.getNodeName() + "|  |" + node.getNodeValue());
				return null;
			}

			Entity entity = new Entity(node.getNodeName());

			entity.setValue(node.getNodeValue());

			switch (node.getNodeType()) {
			case 7:
				entity.setType("Process Instruction");
				break;
			case 4:
				entity.setType("CDATA Section");
				break;
			case 8:
				entity.setType("Comment");
				break;
			case 3:
				entity.setType("#PCDATA");
				break;
			case 10:
				entity.setType("DCOTYPE");
				break;
			case 1:
				entity.setType("Element");
				break;
			case 2:
				entity.setType("Attribute");
				break;
			case 5:
			case 6:
			case 9:
			default:
				entity.setType("Element");
			}

			entity.setParent(parent);

			if (entity.getType() == "Element") {
				if ((node.getChildNodes() != null) || (node.getAttributes() != null)) {
					List children = null;

					NamedNodeMap nnm = node.getAttributes();
					if (nnm != null) {
						int n = nnm.getLength();
						logger.debug("Attribute Will add " + n + "--->");
						for (int i = 0; i < n; i++) {
							Node x = nnm.item(i);
							if (children == null) {
								children = new ArrayList();
							}
							children.add(node2Entity(x, entity));
						}

					}

					NodeList childs = node.getChildNodes();
					if (childs != null) {
						logger.debug("Child Elements Will add " + childs.getLength() + "--->");
						int i = 0;
						for (int n = childs.getLength(); i < n; i++) {
							Node now = childs.item(i);
							if (node2Entity(now, entity) == null) {
								logger.debug("Child is null");
							} else {
								if (children == null) {
									children = new ArrayList();
								}
								children.add(node2Entity(now, entity));
							}
						}

					}

					entity.setChildren(children);
				} else {
					entity.setChildren(null);
				}

			}

			if (entity.getType() == "DCOTYPE") {
				entity.setName("DOCTYPE");
				DocumentType dt = (DocumentType) node;
				StringBuilder sb = new StringBuilder();
				sb.append(dt.getName());
				sb.append(" ");
				if (dt.getPublicId() != null) {
					sb.append("PUBLIC");
					sb.append(" ");
					sb.append(dt.getPublicId());
					sb.append(" ");
				}
				if (dt.getSystemId() != null) {
					sb.append("SYSTEM");
					sb.append(" ");
					sb.append(dt.getSystemId());
				}

				entity.setValue(sb.toString().trim());
			}

			return entity;
		}

		return null;
	}

	public static Node entity2Node(Entity entity, Document doc) {
		if (entity != null) {
			if (entity.getType() == "Element") {
				Element element = doc.createElement(entity.getName());

				List<Entity> entityList = entity.getChildren();
				if ((entityList != null) && (entityList.size() > 0)) {
					for (Entity child : entityList) {
						if (child.getType() == "Attribute") {
							element.setAttribute(child.getName(), child.getValue());
						} else {
							element.appendChild(entity2Node(child, doc));
						}
					}
				}

				return element;
			}
			if (entity.getType() == "CDATA Section")
				return doc.createCDATASection(entity.getValue());
			if (entity.getType() == "Comment")
				return doc.createComment(entity.getValue());
			if (entity.getType() == "#PCDATA")
				return doc.createTextNode(entity.getValue());
			if (entity.getType() == "Process Instruction") {
				return doc.createProcessingInstruction(entity.getName(), entity.getValue());
			}
			return null;
		}

		return null;
	}

	public static Document entitys2Doc(List<Entity> data) {
		if ((data == null) || (data.size() <= 0)) {
			return null;
		}
		Document doc = null;
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder builder = dbf.newDocumentBuilder();
			doc = builder.newDocument();

			for (Entity e : data) {
				if (e.getType() != "DCOTYPE") {
					doc.appendChild(entity2Node(e, doc));
				}
			}
			return doc;
		} catch (ParserConfigurationException pce) {
			logger.error("Document Builder error.", pce);
		}
		return null;
	}
}