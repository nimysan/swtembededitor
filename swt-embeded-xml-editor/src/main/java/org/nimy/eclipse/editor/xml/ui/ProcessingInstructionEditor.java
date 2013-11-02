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

public class ProcessingInstructionEditor extends Dialog {
	public static final String TITLE = "Processing Instruction";
	private Text targetText = null;
	private Text dataText = null;
	private Entity entity = null;
	private TreeViewer tv = null;
	private boolean isEdit = false;

	public ProcessingInstructionEditor(Shell parent, Entity entity, TreeViewer tv) {
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
		label.setText("Target:");
		this.targetText = new Text(parent, 2048);
		this.targetText.setLayoutData(gridData1);
		CLabel label1 = new CLabel(parent, 0);
		label1.setText("Data:");
		this.dataText = new Text(parent, 2048);
		this.dataText.setLayoutData(gridData);

		parent.setLayout(layout);

		initAttr();
		return super.createDialogArea(parent);
	}

	protected void configureShell(Shell newShell) {
		newShell.setSize(300, 150);
		newShell.setText("Processing Instruction");
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
			this.targetText.setText(this.entity.getName());
			if (this.entity.getValue() != null)
				this.dataText.setText(this.entity.getValue());
		}
	}

	protected void okPressed() {
		this.entity.setName(this.targetText.getText().trim());
		this.entity.setValue(this.dataText.getText().trim());
		if (this.isEdit) {
			TreeItem[] trees = this.tv.getTree().getSelection();
			if (trees != null) {
				TreeItem ti = trees[0];
				if (ti != null) {
					ti.setText(new String[] { this.entity.getName(), this.entity.getValue() });
				}
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

	public boolean isEdit() {
		return this.isEdit;
	}

	public void setEdit(boolean isEdit) {
		this.isEdit = isEdit;
	}
}