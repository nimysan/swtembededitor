package org.nimy.eclipse.editor.xml.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Menu;
import org.merlotxml.util.xml.GrammarComplexType;
import org.merlotxml.util.xml.GrammarDocument;
import org.merlotxml.util.xml.GrammarSimpleType;
import org.nimy.eclipse.editor.xml.icons.ImageIndex;
import org.nimy.eclipse.swt.source.editor.utils.UIResourceContext;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ViewActionsManager {

	public static final int INSERT_HERE = 1;
	public static final int INSERT_BEFORE = 2;
	public static final int INSERT_AFTRE = 3;
	private static final Logger logger = Logger.getLogger(ViewActionsManager.class);

	private IMenuManager menuManager = null;

	private boolean ingoreRootAttribute = false;

	private TreeViewer treeViewer = null;

	private Action removeAction = null;

	private Action editDoctypeAction = null;

	private Action editAttributeAction = null;

	private Action editProcessingInstructionAction = null;

	private Action itemUpAction = null;

	private Action itemDownAction = null;

	private GrammarDocument grammars = null;

	private static Document doc = null;

	public ViewActionsManager(TreeViewer viewer, GrammarDocument grammars, boolean ingoreRootAttribute) {
		this.treeViewer = viewer;
		this.grammars = grammars;
		this.ingoreRootAttribute = ingoreRootAttribute;
		logger.debug("Will init ActionManagers");
		initActions();
	}

	public void fillContextMenu(IMenuManager mgr) {
		if (mgr != null) {
			mgr.removeAll();
		}

		Menu menu = ((MenuManager) mgr).createContextMenu(this.treeViewer.getTree());
		this.treeViewer.getTree().setMenu(menu);

		TreeSelection selection = (TreeSelection) this.treeViewer.getSelection();
		if (selection == null) {
			IMenuManager sunmenu = new MenuManager("Add C&hild");
			fillChildSubMenu(sunmenu, null, 1);
			mgr.add(sunmenu);
		} else {
			Iterator<Entity> ir = selection.iterator();
			Entity first = null;
			int number = 0;
			boolean includeRoot = false;
			while (ir.hasNext()) {
				first = (Entity) ir.next();
				if (first.getParent() == null) {
					includeRoot = true;
				}
				number++;
			}
			if (number == 1) {
				Entity now = first;
				if (now.getType() == "DCOTYPE") {
					mgr.add(this.removeAction);
					mgr.add(this.editDoctypeAction);
				} else if (now.getType() == "Attribute") {
					mgr.add(this.removeAction);
					mgr.add(this.editAttributeAction);
				} else if (now.getType() == "CDATA Section") {
					mgr.add(this.removeAction);
				} else if (now.getType() == "Comment") {
					mgr.add(this.removeAction);
				} else if (now.getType() == "Element") {
					if (!includeRoot) {
						mgr.add(this.removeAction);
					}

					if ((first.getParent() != null) || (!this.ingoreRootAttribute)) {
						IMenuManager subAttriMenu = new MenuManager("Add A&ttribute");

						fillAttributeSubMenu(subAttriMenu, now);
						mgr.add(subAttriMenu);
					}
					IMenuManager subChildMenu = new MenuManager("Add &Child");
					fillChildSubMenu(subChildMenu, now, 1);
					mgr.add(subChildMenu);
					IMenuManager before = new MenuManager("Add &Before");
					fillAddBeforeMenu(before, now, 2);
					mgr.add(before);
					IMenuManager after = new MenuManager("Add &After");
					mgr.add(after);
					fillAddAfterMenu(after, now, 3);

					mgr.add(this.itemUpAction);
					mgr.add(this.itemDownAction);
				} else if (now.getType() == "#PCDATA") {
					mgr.add(this.removeAction);
				} else if (now.getType() == "Process Instruction") {
					mgr.add(this.removeAction);
					mgr.add(this.editProcessingInstructionAction);
				}

			} else if (!includeRoot) {
				mgr.add(this.removeAction);
			}
		}
	}

	private void initActions() {
		this.removeAction = new RemoveAction(this.treeViewer);
		this.editDoctypeAction = new EditDoctypeAction(this.treeViewer);
		this.editAttributeAction = new EditAttributeAction(this.treeViewer);
		this.editProcessingInstructionAction = new EditProcessingInstructionAction(this.treeViewer);
		this.itemUpAction = new ItemUpAction(this.treeViewer);
		this.itemDownAction = new ItemDownAction(this.treeViewer);
	}

	private void fillAddBeforeMenu(IMenuManager menu, Entity now, int insertType) {
		fillChildSubMenu(menu, now.getParent(), insertType);
	}

	private void fillAddAfterMenu(IMenuManager menu, Entity now, int insertType) {
		fillChildSubMenu(menu, now.getParent(), insertType);
	}

	private void fillAttributeSubMenu(IMenuManager menu, Entity now) {
		if (this.grammars != null) {
			logger.debug(now.getName() + " Attribute Menu init running...");
			GrammarComplexType type = this.grammars.getTopLevelGrammarComplexType(now.getName());

			if (type != null) {
				GrammarSimpleType[] children = type.getAttributes();
				int i = 0;
				for (int n = children.length; i < n; i++) {
					List<Entity> attrs = now.getChildren();
					if (attrs != null) {
						boolean isExisted = false;
						for (Entity a : attrs) {
							if (children[i].getName().equals(a.getName())) {
								isExisted = true;
								break;
							}
						}
						if (!isExisted)
							menu.add(new AddAttributesAction(this.treeViewer, children[i]));
					} else {
						menu.add(new AddAttributesAction(this.treeViewer, children[i]));
					}
				}

			} else {
				logger.debug("GrammarSimpleType is null");
				menu.add(new AddAttributesAction(this.treeViewer, null));
			}
		} else {
			logger.debug("Have no Grammar Document.");
			menu.add(new AddAttributesAction(this.treeViewer, null));
		}
	}

	private void fillChildSubMenu(IMenuManager menu, Entity now, int insertType) {
		if (this.grammars != null) {
			if (now != null) {
				logger.debug(now.getName() + " Menu init running...");
				GrammarComplexType type = this.grammars.getTopLevelGrammarComplexType(now.getName());

				if (type != null) {
					if (doc != null) {
						logger.debug("Assiant Document:" + doc);
						Element ele = doc.createElement(type.getName());
						GrammarComplexType[] children = type.getInsertableElements(ele);

						int i = 0;
						for (int n = children.length; i < n; i++) {
							menu.add(new AddElementAction(this.treeViewer, children[i], insertType));
						}
					}

					if (type.getIsSimpleContentAllowed()) {
						menu.add(new AddNodeAction(this.treeViewer, "#PCDATA", insertType));
					}
				} else {
					logger.debug("GrammarComplexType is null");
					menu.add(new AddElementAction(this.treeViewer, null, insertType));
				}
			}
		} else {
			logger.debug("Have no Grammar Document.");
			menu.add(new AddElementAction(this.treeViewer, null, insertType));
		}
		menu.add(new AddNodeAction(this.treeViewer, "Comment", insertType));

		menu.add(new AddNodeAction(this.treeViewer, "CDATA Section", insertType));

		menu.add(new AddNodeAction(this.treeViewer, "Process Instruction", insertType));
	}

	public TreeViewer getTreeViewer() {
		return this.treeViewer;
	}

	public void setTreeViewer(TreeViewer treeViewer) {
		this.treeViewer = treeViewer;
	}

	public IMenuManager getMenuManager() {
		return this.menuManager;
	}

	public void setMenuManager(IMenuManager menuManager) {
		this.menuManager = menuManager;
	}

	public Entity findFirstEntityInSelection(TreeViewer viewer) {
		List list = entitySelection(viewer);
		if (list != null) {
			return (Entity) list.get(0);
		}
		return null;
	}

	private List<Entity> entitySelection(TreeViewer treeViewer) {
		List temp = new ArrayList();
		TreeSelection selection = (TreeSelection) treeViewer.getSelection();
		if (selection != null) {
			Iterator i = selection.iterator();
			while (i.hasNext()) {
				temp.add((Entity) i.next());
			}
		}
		if (temp.size() <= 0) {
			temp = null;
		}
		return temp;
	}

	private int numEntitys(List<Entity> list) {
		int res = 0;
		if ((list != null) && (list.size() > 0)) {
			for (Entity e : list) {
				res += numEntitys(e.getChildren());
				res++;
			}
		}
		return res;
	}

	public GrammarDocument getGrammars() {
		return this.grammars;
	}

	public void setGrammars(GrammarDocument grammars) {
		this.grammars = grammars;
	}

	public Document getDoc() {
		return doc;
	}

	public void setDoc(Document doc) {
		this.doc = doc;
	}

	public boolean isIngoreRootAttribute() {
		return this.ingoreRootAttribute;
	}

	private void switchElementPosition(TreeViewer tv, int movePosition) {
		if (movePosition != 1 && movePosition != -1) {
			return;
		}
		TreeSelection ts = (TreeSelection) tv.getSelection();
		TreePath[] sel = ts.getPaths();
		List model;
		if (ts != null) {
			List<Entity> list = ViewActionsManager.this.entitySelection(tv);
			if (list != null) {
				this.logger.debug("TreeSelection : " + list);
				model = (List) tv.getInput();
				this.logger.debug("Data Model:" + model);
				Entity element = null;
				for (Entity e : list) {
					if (e != null && "Element".equals(e.getType())) {
						element = e;
						this.logger.debug("Move Entity :" + e);
						// only move one
						break;
					}
				}
				if (element != null) {
					list = element.getParent().getChildren();
					int currentIndex = list.indexOf(element);
					int swapIndex = currentIndex + movePosition;
					if (swapIndex >= 0 && swapIndex < list.size()) {
						Entity entity = list.get(swapIndex);
						if (entity != null && "Element".equals(entity.getType())) {
							// move up
							Collections.swap(list, currentIndex, swapIndex);
						}
					}
				}
			}
		}

		tv.refresh();
	}

	static {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder db = dbf.newDocumentBuilder();
			doc = db.newDocument();
		} catch (Exception e) {
			logger.error(e);
		}
	}

	private final class AddNodeAction extends Action {
		private final Logger logger = Logger.getLogger(ViewActionsManager.AddElementAction.class);

		private TreeViewer tv = null;

		private String type = null;

		private int insertType = 1;

		public AddNodeAction(TreeViewer v, String type, int insertType) {
			this.tv = v;
			this.type = type;
			this.insertType = insertType;

			setText(type);
			ImageRegistry ir = JFaceResources.getImageRegistry();
			setImageDescriptor(ir.getDescriptor(type));
		}

		public void run() {
			Entity base = ViewActionsManager.this.findFirstEntityInSelection(this.tv);

			List insertEle = base.getChildren();
			if (insertEle == null) {
				insertEle = new ArrayList();
			}
			Entity node = new Entity("");
			if (this.type.equals("Process Instruction")) {
				ProcessingInstructionEditor pe = new ProcessingInstructionEditor(this.tv.getTree().getShell(), node, this.tv);

				pe.open();
			}
			node.setType(this.type);

			if (this.insertType == 1) {
				node.setParent(base);
				insertEle.add(node);
				base.setChildren(insertEle);
			} else if (this.insertType == 2) {
				List list = null;
				if (base.getParent() == null)
					list = (List) this.tv.getInput();
				else {
					list = base.getParent().getChildren();
				}
				int index = list.indexOf(base);
				this.logger.debug("Insert at: " + index);
				list.add(index, node);
			} else if (this.insertType == 3) {
				List list = null;
				if (base.getParent() == null)
					list = (List) this.tv.getInput();
				else {
					list = base.getParent().getChildren();
				}
				int index = list.indexOf(base);
				this.logger.info("Insert at: " + index);
				list.add(index + 1, node);
			}

			this.logger.debug("Add a " + node.getName() + " Success!");

			this.tv.refresh();
		}

		public TreeViewer getTv() {
			return this.tv;
		}

		public void setTv(TreeViewer tv) {
			this.tv = tv;
		}

		public String getType() {
			return this.type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public int getInsertType() {
			return this.insertType;
		}

		public void setInsertType(int insertType) {
			this.insertType = insertType;
		}
	}

	private final class AddElementAction extends Action {
		private final Logger logger = Logger.getLogger(AddElementAction.class);
		public static final String NEW_ELEMENT = "New &Element";
		private TreeViewer tv = null;

		private GrammarComplexType grammar = null;

		private int insertType = 1;

		public AddElementAction(TreeViewer v, GrammarComplexType grammar, int insertType) {
			this.tv = v;
			this.grammar = grammar;
			this.insertType = insertType;
			setImageDescriptor(JFaceResources.getImageRegistry().getDescriptor("Element"));

			if (grammar != null)
				setText("&" + grammar.getName());
			else
				setText("New &Element");
		}

		public void run() {
			Entity ele = null;
			if (this.grammar == null) {
				InputDialog in = new InputDialog(this.tv.getTree().getShell(), "Element name", "Element:", null, null);

				if (in.open() == 0) {
					String x = in.getValue();
					if ((x != null) && (!x.equals("")))
						ele = new Entity(x);
				} else {
					MessageDialog.openWarning(this.tv.getTree().getShell(), "Element edit", "The element name is empty");
				}
			} else {
				ele = new Entity(this.grammar.getName());

				List requiredAttrs = null;
				GrammarSimpleType[] attrs = this.grammar.getAttributes();
				if ((attrs != null) && (attrs.length > 0)) {
					for (GrammarSimpleType a : attrs) {
						if (a.getIsRequired()) {
							Entity attr = new Entity(a.getName());
							attr.setType("Attribute");
							this.logger.debug("Required Attribute: " + attr.getName());

							attr.setValue(a.getDefaultValue());
							if (requiredAttrs == null) {
								requiredAttrs = new ArrayList();
							}
							requiredAttrs.add(attr);
						}

					}

				}

				ele.setChildren(requiredAttrs);
			}

			ele.setType("Element");

			Entity base = ViewActionsManager.this.findFirstEntityInSelection(this.tv);

			List insertEle = base.getChildren();
			if (insertEle == null) {
				insertEle = new ArrayList();
			}
			if (this.insertType == 1) {
				ele.setParent(base);
				insertEle.add(ele);
				base.setChildren(insertEle);
			} else if (this.insertType == 2) {
				List list = null;
				if (base.getParent() == null)
					list = (List) this.tv.getInput();
				else {
					list = base.getParent().getChildren();
				}
				int index = list.indexOf(base);
				list.add(index, ele);

				ele.setParent(base.getParent());
			} else if (this.insertType == 3) {
				List list = null;
				if (base.getParent() == null)
					list = (List) this.tv.getInput();
				else {
					list = base.getParent().getChildren();
				}
				int index = list.indexOf(base);
				this.logger.debug("Insert at: " + index);
				list.add(index + 1, ele);
				ele.setParent(base.getParent());
			}

			this.logger.debug("Input has all--" + ViewActionsManager.this.numEntitys((List) this.tv.getInput()) + "-- entitys");

			this.tv.refresh();
		}

		public TreeViewer getTv() {
			return this.tv;
		}

		public void setTv(TreeViewer tv) {
			this.tv = tv;
		}

		public GrammarComplexType getGrammar() {
			return this.grammar;
		}

		public void setGrammar(GrammarComplexType grammar) {
			this.grammar = grammar;
		}

		public int getInsertType() {
			return this.insertType;
		}

		public void setInsertType(int insertType) {
			this.insertType = insertType;
		}
	}

	private final class AddAttributesAction extends Action {
		private final Logger logger = Logger.getLogger(AddAttributesAction.class);

		private TreeViewer tv = null;

		private GrammarSimpleType type = null;

		public AddAttributesAction(TreeViewer v, GrammarSimpleType type) {
			this.tv = v;
			this.type = type;
			if (type != null) {
				setText("&" + type.getName());
				if (type.getIsRequired()) {
					setImageDescriptor(JFaceResources.getImageRegistry().getDescriptor("Attribute Required"));

					setToolTipText("Required");
				} else {
					setImageDescriptor(JFaceResources.getImageRegistry().getDescriptor("Attribute"));
				}
			} else {
				setText("&New Attribute...");
				setImageDescriptor(JFaceResources.getImageRegistry().getDescriptor("Attribute"));
			}
		}

		public void run() {
			try {
				this.logger.debug("Do Add Attribute action");
				Entity base = ViewActionsManager.this.findFirstEntityInSelection(this.tv);

				Entity newAttr = null;
				if (this.type != null) {
					newAttr = new Entity(this.type.getName());
					newAttr.setType("Attribute");
				} else {
					newAttr = new Entity("");
					newAttr.setType("Attribute");
					AttributeEditor ae = new AttributeEditor(this.tv.getTree().getShell(), newAttr, this.tv);

					ae.open();
				}

				List<Entity> attrs = base.getChildren();
				if (attrs == null) {
					attrs = new ArrayList();
					attrs.add(newAttr);
				} else {
					int index = 0;
					for (Entity e : attrs) {
						if (e.getType() == "Attribute") {
							index++;
						}
					}
					attrs.add(index, newAttr);
				}
				base.setChildren(attrs);
				this.tv.refresh();
			} catch (Exception e) {
				MessageDialog.openError(this.tv.getTree().getShell(), "Error", e.getMessage());
			}
		}

		public TreeViewer getTv() {
			return this.tv;
		}

		public void setTv(TreeViewer tv) {
			this.tv = tv;
		}
	}

	private final class EditProcessingInstructionAction extends Action {
		private final Logger logger = Logger.getLogger(EditProcessingInstructionAction.class);

		private TreeViewer tv = null;

		public EditProcessingInstructionAction(TreeViewer v) {
			this.tv = v;
			setText("Edit Processing Instruction...");
		}

		public void run() {
			this.logger.debug("Editing Processing Instruction");
			Entity now = ViewActionsManager.this.findFirstEntityInSelection(this.tv);
			ProcessingInstructionEditor pe = new ProcessingInstructionEditor(this.tv.getTree().getShell(), now, this.tv);

			pe.setEdit(true);
			pe.open();
		}

		public TreeViewer getTv() {
			return this.tv;
		}

		public void setTv(TreeViewer tv) {
			this.tv = tv;
		}
	}

	private final class EditAttributeAction extends Action {
		private final Logger logger = Logger.getLogger(EditAttributeAction.class);

		private TreeViewer tv = null;

		public EditAttributeAction(TreeViewer v) {
			this.tv = v;
			setText("Edit Attribute...");
		}

		public void run() {
			this.logger.debug("Edit Attribute running...");
			Entity attr = ViewActionsManager.this.findFirstEntityInSelection(this.tv);

			if (attr != null)
				this.logger.debug(attr.getName() + " : " + attr.getValue());
			else {
				this.logger.debug("attr is null");
			}
			AttributeEditor ae = new AttributeEditor(this.tv.getTree().getShell(), attr, this.tv);

			ae.open();
		}

		public TreeViewer getTv() {
			return this.tv;
		}

		public void setTv(TreeViewer tv) {
			this.tv = tv;
		}
	}

	private final class EditDoctypeAction extends Action {
		private final Logger logger = Logger.getLogger(EditDoctypeAction.class);

		private TreeViewer tv = null;

		public EditDoctypeAction(TreeViewer v) {
			this.tv = v;
			setText("Edit DOCTYPE...");
		}

		public void run() {
			this.logger.debug("Edit DocType...");
		}

	}

	@SuppressWarnings("unused")
	private final class ItemUpAction extends Action {
		private final Logger logger = Logger.getLogger(ItemUpAction.class);
		private TreeViewer tv = null;

		public ItemUpAction(TreeViewer v) {
			this.tv = v;
			setText("Move U&p");
			setImageDescriptor(UIResourceContext.getInstance().getImageDescriptor(ImageIndex.class, "full/dtool16/doubleArrowUp.png"));
		}

		public void run() {
			switchElementPosition(getTv(), -1);
		}

		public TreeViewer getTv() {
			return this.tv;
		}

		public void setTv(TreeViewer tv) {
			this.tv = tv;
		}
	}

	@SuppressWarnings("unused")
	private final class ItemDownAction extends Action {
		private final Logger logger = Logger.getLogger(ItemDownAction.class);
		private TreeViewer tv = null;

		public ItemDownAction(TreeViewer v) {
			this.tv = v;
			setText("Move Dow&n");
			setImageDescriptor(UIResourceContext.getInstance().getImageDescriptor(ImageIndex.class, "full/dtool16/doubleArrowDown.png"));
		}

		public void run() {
			switchElementPosition(getTv(), 1);
		}

		public TreeViewer getTv() {
			return this.tv;
		}

		public void setTv(TreeViewer tv) {
			this.tv = tv;
		}
	}

	private final class RemoveAction extends Action {
		private final Logger logger = Logger.getLogger(RemoveAction.class);

		private TreeViewer tv = null;

		public RemoveAction(TreeViewer v) {
			this.tv = v;
			setText("Re&move");
		}

		public void run() {
			TreeSelection ts = (TreeSelection) this.tv.getSelection();
			TreePath[] sel = ts.getPaths();
			List model;
			if (ts != null) {
				List<Entity> list = ViewActionsManager.this.entitySelection(this.tv);
				if (list != null) {
					this.logger.debug("TreeSelection : " + list);
					model = (List) this.tv.getInput();
					this.logger.debug("Data Model:" + model);
					for (Entity e : list) {
						this.logger.debug("Remove Result :" + del(e, model));
					}
				}
			}

			this.tv.remove(sel);
		}

		private boolean del(Entity des, List<Entity> source) {
			if ((source != null) && (des != null)) {
				Iterator it = source.iterator();
				boolean res = false;
				while (it.hasNext()) {
					Entity e = (Entity) it.next();
					if (e.equals(des)) {
						it.remove();
						res = true;
					} else {
						res = del(des, e.getChildren());
					}
				}
				return res;
			}
			return false;
		}

		public TreeViewer getTv() {
			return this.tv;
		}

		public void setTv(TreeViewer tv) {
			this.tv = tv;
		}
	}
}