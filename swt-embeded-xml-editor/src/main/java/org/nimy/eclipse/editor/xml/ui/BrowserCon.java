package org.nimy.eclipse.editor.xml.ui;


import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.nimy.eclipse.editor.xml.icons.ImageIndex;
import org.nimy.eclipse.swt.source.editor.utils.UIResourceContext;

public class BrowserCon extends Composite {
	private TreeViewer treeViewer = null;
	private Tree tree = null;
	private XmlDesignEditor view = null;

	public BrowserCon(Shell parent, int style) {
		super(parent, style);
		initialize();
	}

	private void initialize() {
		this.tree = new Tree(this, 68354);

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
		treeColumn.setImage(UIResourceContext.getInstance().getImage(ImageIndex.class, "full/obj16/proinst_obj.gif"));

		TreeColumn treeColumn1 = new TreeColumn(this.tree, 65536);
		treeColumn1.setWidth(200);
		treeColumn1.setText("version=\"1.0\" encoding=\"UTF-8\"");

		this.treeViewer.setContentProvider(new ContentProvider());
		this.treeViewer.setLabelProvider(new LabelProvider());
		if ((this.view != null) && (this.view.getTreeViewer().getInput() != null)) {
			this.treeViewer.setInput(this.view.getTreeViewer().getInput());
			this.treeViewer.expandAll();
		}
	}

	public TreeViewer getTreeViewer() {
		return this.treeViewer;
	}

	public void setTreeViewer(TreeViewer tv) {
		this.treeViewer = tv;
	}

	public XmlDesignEditor getView() {
		return this.view;
	}

	public void setView(XmlDesignEditor view) {
		this.view = view;
	}

	public void update() {
		if ((this.view != null) && (this.view.getTreeViewer().getInput() != null)) {
			this.treeViewer.setInput(this.view.getTreeViewer().getInput());
			this.treeViewer.expandAll();
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

					return JFaceResources.getImageRegistry().get(c.getType());
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
					if (c.getName().equals("#text")) {
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
				if (c.getChildren() != null) {
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