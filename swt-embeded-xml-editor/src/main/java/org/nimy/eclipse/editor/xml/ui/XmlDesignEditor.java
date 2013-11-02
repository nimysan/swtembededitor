package org.nimy.eclipse.editor.xml.ui;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.merlotxml.util.xml.GrammarDocument;
import org.nimy.eclipse.editor.xml.icons.ImageIndex;
import org.nimy.eclipse.swt.source.editor.EditorBuilder;
import org.nimy.eclipse.swt.source.editor.SimpleSourceComposite;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.sun.org.apache.xerces.internal.parsers.DOMParser;
import com.sun.org.apache.xml.internal.serialize.DOMSerializerImpl;
import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

@SuppressWarnings("unchecked")
public class XmlDesignEditor extends Composite {
	public static final String TREE_COLUMN_1 = "name";
	public static final String TREE_COLUMN_2 = "value";
	private boolean ingoreRootAttibute = false;

	private ViewActionsManager actions = null;

	private Document doc = null;

	private String xmlpath = null;

	private String rootElement = null;

	private static final Logger logger = Logger.getLogger(XmlDesignEditor.class);

	private Tree tree = null;

	private TreeViewer treeViewer = null;

	private IMenuManager menuManager = null;

	private GrammarDocument grammars = null;

	static ImageRegistry ir = null;
	private CTabFolder modeSwitcher;
	private SimpleSourceComposite xmlSourceEditor;

	public XmlDesignEditor(Composite parent, int style, GrammarDocument grammars) {
		super(parent, style);
		this.grammars = grammars;
		initialize();
		this.menuManager = new MenuManager();
	}

	public XmlDesignEditor(Composite parent, int style, GrammarDocument grammars, boolean ingoreRootAttribute) {
		super(parent, style);
		this.grammars = grammars;
		this.ingoreRootAttibute = ingoreRootAttribute;
		initialize();
		this.menuManager = new MenuManager();
	}

	private void initialize() {
		modeSwitcher = new CTabFolder(this, SWT.BOTTOM);
		createTreeViewMode(modeSwitcher);
		createSourceViewMode(modeSwitcher);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(modeSwitcher);
		modeSwitcher.setSimple(false);
		modeSwitcher.setUnselectedCloseVisible(true);
		modeSwitcher.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				CTabItem selection = modeSwitcher.getSelection();
				if (selection.getData("type") == "SOURCE") {
					xmlSourceEditor.setContent(transferToPlainSource(), true);
				} else if (selection.getData("type") == "DESIGN") {
					String plainSource = xmlSourceEditor.getStyledText().getText();
					try {
						Object generalInputFromString = generalInputFromString(plainSource);
						treeViewer.setInput(generalInputFromString);
					} catch (Exception e) {
						e.printStackTrace();// TODO how to handle this
											// exception?
					}
				}

			}
		});
	}

	private void createSourceViewMode(CTabFolder mode) {
		xmlSourceEditor = EditorBuilder.buildXmlEditor(mode, SWT.NONE);
		CTabItem xmlSourceViewItem = new CTabItem(mode, SWT.NONE);
		xmlSourceViewItem.setData("type", "SOURCE");
		xmlSourceViewItem.setText("Source");
		xmlSourceViewItem.setControl(xmlSourceEditor);
	}

	private void createTreeViewMode(CTabFolder mode) {
		CTabItem xmlTreeViewItem = new CTabItem(mode, SWT.NONE);
		xmlTreeViewItem.setText("Design");
		xmlTreeViewItem.setData("type", "DESIGN");
		this.tree = new Tree(mode, 68354);
		xmlTreeViewItem.setControl(this.tree);
		this.treeViewer = new TreeViewer(this.tree);
		GridData gridData = new GridData();
		gridData.horizontalAlignment = 4;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.verticalAlignment = 4;
		setSize(new Point(400, 500));
		setLayout(new GridLayout());
		this.tree = this.treeViewer.getTree();
		this.tree.setHeaderVisible(true);
		this.tree.setLayoutData(gridData);
		this.tree.setLinesVisible(true);
		TreeColumn treeColumn = new TreeColumn(this.tree, 65536);

		treeColumn.setWidth(200);
		treeColumn.setText("xml");
		treeColumn.setImage(ImageDescriptor.createFromFile(ImageIndex.class, "full/obj16/proinst_obj.gif").createImage());

		TreeColumn treeColumn1 = new TreeColumn(this.tree, 65536);
		treeColumn1.setWidth(200);
		treeColumn1.setText("version=\"1.0\" encoding=\"UTF-8\"");

		this.treeViewer.setContentProvider(new ContentProvider());
		this.treeViewer.setLabelProvider(new LabelProvider());

		initCellEditor();

		this.treeViewer.setCellModifier(new CellModifier());

		this.treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				XmlDesignEditor.logger.debug("---------------- Change Selection Over");
			}
		});
		this.treeViewer.getTree().addMouseListener(new MouseListener() {
			public void mouseDoubleClick(MouseEvent e) {
				TreeSelection selection = (TreeSelection) XmlDesignEditor.this.treeViewer.getSelection();

				XmlDesignEditor.this.treeViewer.setSelection(selection);
			}

			public void mouseDown(MouseEvent e) {
			}

			public void mouseUp(MouseEvent e) {
				if (e.button == 3) {
					XmlDesignEditor.this.actions.fillContextMenu(XmlDesignEditor.this.menuManager);
				}
			}
		});
		this.actions = new ViewActionsManager(this.treeViewer, this.grammars, this.ingoreRootAttibute);
	}

	private void initCellEditor() {
		String[] columnProperties = { "name", "value" };
		this.treeViewer.setColumnProperties(columnProperties);

		CellEditor[] cellEditors = new CellEditor[3];
		cellEditors[0] = null;
		cellEditors[1] = new CustomizedTextCellEditor(this.treeViewer.getTree());
		this.treeViewer.setCellEditors(cellEditors);
	}

	public void updateView() {
		modeSwitcher.setSelection(0);
		if (this.doc != null) {
			this.treeViewer.setInput(doc2TreeItem(this.doc));
			this.treeViewer.refresh();
		}
	}

	public Object generalInputFromXmlFile(String xmlpath) {
		DOMParser parser = new DOMParser();
		try {
			parser.parse(xmlpath);
			this.doc = parser.getDocument();
			return doc2TreeItem(this.doc);
		} catch (IOException ioe) {
			logger.error("Input error.", ioe);
			return null;
		} catch (SAXException saxe) {
			logger.error("Input error.", saxe);
		}
		return null;
	}

	public Object generalInputFromString(String xmlString) {
		if (xmlString != null) {
			StringReader sr = new StringReader(xmlString);
			DOMParser parser = new DOMParser();
			try {
				parser.parse(new InputSource(sr));
				this.doc = parser.getDocument();
				logger.debug("Parse ok");
				return doc2TreeItem(this.doc);
			} catch (IOException ioe) {
				logger.error("Input error.", ioe);
				return null;
			} catch (SAXException saxe) {
				logger.error("Input error.", saxe);
				return null;
			}
		}
		return null;
	}

	@SuppressWarnings("rawtypes")
	public List<Entity> doc2TreeItem(Document doc) {
		NodeList nl = doc.getChildNodes();
		if (nl != null) {
			List obj = null;
			int i = 0;
			for (int n = nl.getLength(); i < n; i++) {
				if (obj == null) {
					obj = new ArrayList();
				}
				obj.add(Entity2Node.node2Entity(nl.item(i), null));
			}

			return obj;
		}
		return null;
	}

	@SuppressWarnings("rawtypes")
	public boolean saveModel(String filepath) {
		if (filepath != null) {
			this.xmlpath = filepath;
		} else if (this.xmlpath == null) {
			FileDialog locate = new FileDialog(getShell(), 8192);

			locate.setFilterExtensions(new String[] { "*.xml", "*.*" });
			this.xmlpath = locate.open();
		}

		List input = (List) this.treeViewer.getInput();
		if (input != null) {
			try {
				Document doc = Entity2Node.entitys2Doc(input);
				XMLSerializer serializer = new XMLSerializer();
				serializer.setOutputFormat(new OutputFormat("", "UTF-8", true));
				FileWriter writer = new FileWriter(new File(this.xmlpath));
				serializer.setOutputCharStream(writer);
				serializer.serialize(doc);
				logger.debug("Save to " + this.xmlpath + " sucess.");
				return true;
			} catch (Exception e) {
				logger.error(e);
				return false;
			}
		}
		return false;
	}

	@SuppressWarnings("rawtypes")
	public String saveToString() {
		String result = null;
		List input = (List) this.treeViewer.getInput();
		if (input != null) {
			try {
				Document doc = Entity2Node.entitys2Doc(input);
				DOMSerializerImpl serializer = new DOMSerializerImpl();
				result = serializer.writeToString(doc);
			} catch (Exception e) {
				logger.error(e);
			}
		}
		logger.debug("Xml String--->" + result);
		return result;
	}

	public ViewActionsManager getActions() {
		return this.actions;
	}

	public void setActions(ViewActionsManager actions) {
		this.actions = actions;
	}

	public IMenuManager getMenuManager() {
		return this.menuManager;
	}

	public void setMenuManager(IMenuManager menuManager) {
		this.menuManager = menuManager;
	}

	public GrammarDocument getGrammars() {
		return this.grammars;
	}

	public void setGrammars(GrammarDocument grammars) {
		this.grammars = grammars;
	}

	public Document getDoc() {
		return this.doc;
	}

	public void setDoc(Document doc) {
		this.doc = doc;
	}

	/**
	 * Read current input in design view to source view
	 * 
	 * @return
	 */
	public String transferToPlainSource() {
		return saveToString();
	}

	public TreeViewer getTreeViewer() {
		return this.treeViewer;
	}

	public void setTreeViewer(TreeViewer treeViewer) {
		this.treeViewer = treeViewer;
	}

	public String getRootElement() {
		return this.rootElement;
	}

	public void setRootElement(String rootElement) {
		this.rootElement = rootElement;
	}

	public String getXmlpath() {
		return this.xmlpath;
	}

	public void setXmlpath(String xmlpath) {
		this.xmlpath = xmlpath;
	}

	public boolean isIngoreRootAttibute() {
		return this.ingoreRootAttibute;
	}

	public void setIngoreRootAttibute(boolean ingoreRootAttibute) {
		this.ingoreRootAttibute = ingoreRootAttibute;
	}

	static {
		ir = JFaceResources.getImageRegistry();
		ir.put("Process Instruction", ImageDescriptor.createFromFile(ImageIndex.class, "full/obj16/proinst_obj.gif"));

		ir.put("Attribute", ImageDescriptor.createFromFile(ImageIndex.class, "full/obj16/attribute_obj.gif"));

		ir.put("CDATA Section", ImageDescriptor.createFromFile(ImageIndex.class, "full/obj16/cdatasection.gif"));

		ir.put("Comment", ImageDescriptor.createFromFile(ImageIndex.class, "full/obj16/comment_obj.gif"));

		ir.put("DCOTYPE", ImageDescriptor.createFromFile(ImageIndex.class, "full/obj16/doctype.gif"));

		ir.put("Element", ImageDescriptor.createFromFile(ImageIndex.class, "full/obj16/element_obj.gif"));

		ir.put("#PCDATA", ImageDescriptor.createFromFile(ImageIndex.class, "full/obj16/text.gif"));

		ir.put("Attribute Required", ImageDescriptor.createFromFile(ImageIndex.class, "full/obj16/att_req_obj.gif"));
	}

	class CellModifier implements ICellModifier {
		private Entity hereEntity = null;

		CellModifier() {
		}

		public boolean canModify(Object element, String property) {
			if (property.equals("TREE_COLUMN_1")) {
				return false;
			}
			if ((element instanceof Entity)) {
				Entity n = (Entity) element;
				if ((n.getType() == "Element") || (n.getType() == "DCOTYPE")) {
					return false;
				}
				return true;
			}

			return false;
		}

		public Object getValue(Object element, String property) {
			if (property.equals("name")) {
				return null;
			}
			XmlDesignEditor.logger.debug("Element :" + element + " Property:" + property);
			if ((element instanceof Entity)) {
				Entity n = (Entity) element;
				this.hereEntity = n;
				return n.getValue() == null ? "" : n.getValue();
			}
			if ((element instanceof TreeItem)) {
				TreeItem item = (TreeItem) element;
				return item.getText(1);
			}
			return "";
		}

		public void modify(Object element, String property, Object value) {
			if (((element instanceof TreeItem)) && (property.equals("value"))) {
				String val = (String) value;
				TreeItem li = (TreeItem) element;
				if (!li.getText(1).equals(val)) {
					if (getHereEntity() != null) {
						getHereEntity().setValue(val);
					}

					li.setText(1, val);
				}
			}
		}

		public Entity getHereEntity() {
			return this.hereEntity;
		}

		public void setHereEntity(Entity hereEntity) {
			this.hereEntity = hereEntity;
		}
	}

	class LabelProvider implements ITableLabelProvider {
		private final Logger logger = Logger.getLogger(LabelProvider.class);

		LabelProvider() {
		}

		public Image getColumnImage(Object element, int columnIndex) {
			if (columnIndex == 0) {
				if ((element instanceof Entity)) {
					Entity c = (Entity) element;
					this.logger.debug("Fill iamge" + c.getName() + " Node.");

					return XmlDesignEditor.ir.get(c.getType());
				}
				return null;
			}

			return null;
		}

		public String getColumnText(Object element, int columnIndex) {
			switch (columnIndex) {
			case 0:
				if ((element instanceof Entity)) {
					Entity c = (Entity) element;
					this.logger.debug("Fill " + c.getName() + " Node.");
					if ((c.getName().equals("#text")) || (c.getType().equals("CDATA Section")) || (c.getType().equals("Comment")) || (c.getType().equals("#PCDATA"))) {
						return null;
					}
					return c.getName();
				}

				break;
			case 1:
				if ((element instanceof Entity)) {
					Entity c = (Entity) element;
					return c.getValue();
				}

				break;
			}

			return null;
		}

		public void addListener(ILabelProviderListener listener) {
		}

		public void dispose() {
		}

		public boolean isLabelProperty(Object element, String property) {
			return false;
		}

		public void removeListener(ILabelProviderListener listener) {
		}
	}

	class ContentProvider implements ITreeContentProvider {
		private final Logger logger = Logger.getLogger(ContentProvider.class);

		ContentProvider() {
		}

		public Object[] getChildren(Object parentElement) {
			if ((parentElement instanceof Entity)) {
				Entity c = (Entity) parentElement;
				this.logger.debug("Get " + c.getName() + " ' Children.");
				if ((c.getChildren() != null) && (c.getChildren().size() > 0)) {
					return c.getChildren().toArray();
				}
				return null;
			}

			return null;
		}

		public Object getParent(Object element) {
			if ((element instanceof Entity)) {
				Entity c = (Entity) element;
				this.logger.debug("Get " + c.getName() + " ' Parent.");
				return c.getParent();
			}
			return null;
		}

		public boolean hasChildren(Object element) {
			if ((element instanceof Entity)) {
				Entity c = (Entity) element;
				this.logger.debug("Get " + c.getName() + " ' Parent.");
				if (c.getChildren() == null) {
					return false;
				}
				return true;
			}

			return false;
		}

		@SuppressWarnings("rawtypes")
		public Object[] getElements(Object inputElement) {
			List list = (List) inputElement;
			return list.toArray();
		}

		public void dispose() {
			this.logger.debug("Dispose");
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}
	}
}