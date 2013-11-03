package org.nimy.eclipse.editor.xml.ui;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.TreeItem;

public class AttributeEditor extends Dialog {
	public static final String TITLE = "Edit Attribute";
	private Text nameText = null;
	private Text valueText = null;
	private Entity entity = null;
	private TreeViewer tv = null;

	public AttributeEditor(Shell parent, Entity entity, TreeViewer tv) {
		super(parent);
		this.entity = entity;
		this.tv = tv;
	}

	protected Control createDialogArea(Composite parent) {
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;

		GridData gridData1 = new GridData();
		gridData1.horizontalAlignment = 4;
		gridData1.grabExcessHorizontalSpace = true;
		gridData1.verticalAlignment = 2;
		GridData gridData = new GridData();
		gridData.horizontalAlignment = 4;
		gridData.grabExcessHorizontalSpace = true;
		gridData.verticalAlignment = 2;

		CLabel label = new CLabel(parent, 0);
		label.setText("Name:");
		this.nameText = new Text(parent, 2048);
		this.nameText.setLayoutData(gridData1);
		this.nameText.setEditable(false);
		CLabel label1 = new CLabel(parent, 0);
		label1.setText("Value:");
		this.valueText = new Text(parent, 2048);
		this.valueText.setLayoutData(gridData);

		parent.setLayout(layout);

		initAttr();
		return super.createDialogArea(parent);
	}

	protected void configureShell(Shell newShell) {
		newShell.setSize(300, 150);
		newShell.setText("Edit Attribute");
		super.configureShell(newShell);
	}

	public Entity getEntity() {
		return this.entity;
	}

	public void setEntity(Entity entity) {
		this.entity = entity;
	}

	private void initAttr() {
		if (this.entity != null) {
			if (this.entity.getName() != null) {
				this.nameText.setText(this.entity.getName());
			}

			if (this.entity.getValue() != null)
				this.valueText.setText(this.entity.getValue());
		}
	}

	protected void okPressed() {
		this.entity.setName(this.nameText.getText().trim());
		this.entity.setValue(this.valueText.getText().trim());
		TreeItem[] trees = this.tv.getTree().getSelection();
		if (trees != null) {
			TreeItem ti = trees[0];
			if (ti != null) {
				ti.setText(new String[] { this.entity.getName(), this.entity.getValue() });
			}
		}
		super.okPressed();
	}

	public TreeViewer getTv() {
		return this.tv;
	}

	public void setTv(TreeViewer tv) {
		this.tv = tv;
	}
}