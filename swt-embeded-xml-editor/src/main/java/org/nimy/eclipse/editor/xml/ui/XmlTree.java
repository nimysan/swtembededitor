package org.nimy.eclipse.editor.xml.ui;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.nimy.eclipse.swt.source.editor.utils.UIResourceContext;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XmlTree {
	public static final String ELEMENT = "element";
	public static final String ATTRIBUTE = "attribute";
	public static final String COMMENT = "comment";
	private Shell sShell = null;
	private Tree tree = null;
	private ImageRegistry ir = null;

	public static void main(String[] args) {
		Display display = Display.getDefault();
		XmlTree thisClass = new XmlTree();
		thisClass.createSShell();
		thisClass.sShell.open();

		while (!thisClass.sShell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}

	private void createSShell() {
		initIR();

		GridData gridData = new GridData();
		gridData.horizontalAlignment = 4;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.verticalAlignment = 4;
		this.sShell = new Shell();
		this.sShell.setText("Shell");
		this.sShell.setSize(new Point(400, 500));
		this.sShell.setLayout(new GridLayout());
		this.tree = new Tree(this.sShell, 0);
		this.tree.setHeaderVisible(true);
		this.tree.setLayoutData(gridData);
		this.tree.setLinesVisible(true);
		TreeColumn treeColumn = new TreeColumn(this.tree, 0);
		treeColumn.setWidth(200);
		treeColumn.setText("?=? xml");
		TreeColumn treeColumn1 = new TreeColumn(this.tree, 0);
		treeColumn1.setWidth(200);
		treeColumn1.setText("version=\"1.0\" encoding=\"UTF-8\"");

		fillContent(parseXML("c:\\com.vxml"), this.tree);
	}

	private void fillContent(Document xml, Tree parent) {
		Node root = xml.getFirstChild();
		TreeItem ti = new TreeItem(parent, 0);
		ti.setImage(this.ir.get("element"));
		ti.setText(new String[] { root.getNodeName(), root.getNodeValue() });

		NamedNodeMap nnm = root.getAttributes();
		int i = 0;
		for (int n = nnm.getLength(); i < n; i++) {
			Node x = nnm.item(i);
			TreeItem y = new TreeItem(ti, 0);
			y.setImage(this.ir.get("attribute"));
			y.setText(new String[] { x.getNodeName(), x.getNodeValue() });
		}

		NodeList nl = root.getChildNodes();
		for (int n = nl.getLength(); i < n; i++) {
			Node x = nl.item(i);
			fillNode(x, ti);
		}
	}

	private void fillNode(Node des, TreeItem parent) {
		if (des.getNodeType() != 3) {
			TreeItem ti = new TreeItem(parent, 0);
			if (des.getNodeType() == 8) {
				ti.setImage(this.ir.get("comment"));
				ti.setText(new String[] { "", des.getNodeValue() });
			} else {
				ti.setImage(this.ir.get("element"));
				ti.setText(new String[] { des.getNodeName(), des.getNodeValue() });
			}

			NamedNodeMap nnm = des.getAttributes();
			if ((nnm != null) && (nnm.getLength() > 0)) {
				int i = 0;
				for (int n = nnm.getLength(); i < n; i++) {
					Node x = nnm.item(i);
					TreeItem y = new TreeItem(ti, 0);
					y.setImage(this.ir.get("attribute"));
					y.setText(new String[] { x.getNodeName(), x.getNodeValue() });
				}

			}

			NodeList nl = des.getChildNodes();
			if ((nl != null) && (nl.getLength() > 0)) {
				int i = 0;
				for (int n = nl.getLength(); i < n; i++) {
					Node x = nl.item(i);

					fillNode(x, ti);
				}
			}
		}
	}

	public Document parseXML(String url) {
		Document xml = null;
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

			factory.setValidating(false);

			DocumentBuilder docBuilder = factory.newDocumentBuilder();
			xml = docBuilder.parse(url);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return xml;
	}

	private void initIR() {
		this.ir = new ImageRegistry();
		this.ir.put("element", UIResourceContext.getInstance().getImage(XmlTree.class, "element.ico"));
		this.ir.put("attribute", UIResourceContext.getInstance().getImage(XmlTree.class, "attribute.ico"));
		this.ir.put("comment", UIResourceContext.getInstance().getImage(XmlTree.class, "comment.ico"));
	}
}