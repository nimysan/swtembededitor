package org.nimy.eclipse.editor.xml.ui;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.merlotxml.util.xml.GrammarComplexType;
import org.merlotxml.util.xml.GrammarDocument;

public class ChooseRootElementEditor extends Dialog {
	private GrammarDocument grammar = null;
	public static final String TITLE = "Choose Roor Element";
	private CCombo root = null;

	public ChooseRootElementEditor(Shell parentShell) {
		super(parentShell);
	}

	public ChooseRootElementEditor(IShellProvider parentShell) {
		super(parentShell);
	}

	protected Control createDialogArea(Composite parent) {
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;

		GridData gridData1 = new GridData();
		gridData1.horizontalAlignment = 4;
		gridData1.grabExcessHorizontalSpace = true;
		gridData1.verticalAlignment = 2;

		CLabel label = new CLabel(parent, 0);
		label.setText("Name:");
		this.root = new CCombo(parent, 2048);
		GrammarDocument g = getGrammar();
		if (g != null) {
			GrammarComplexType[] types = g.getTopLevelGrammarComplexTypes();
			String first = null;
			for (GrammarComplexType type : types) {
				if (first == null) {
					first = type.getName();
				}
				this.root.add(type.getName());
			}
			this.root.setText(first);
		} else {
			this.root.setText("New");
		}

		parent.setLayout(layout);
		return super.createDialogArea(parent);
	}

	protected void configureShell(Shell newShell) {
		newShell.setSize(300, 150);
		newShell.setText("Choose Roor Element");
		super.configureShell(newShell);
	}

	protected void okPressed() {
		CosEditor.rootElement = this.root.getText().trim();
		super.okPressed();
	}

	public GrammarDocument getGrammar() {
		return this.grammar;
	}

	public void setGrammar(GrammarDocument grammar) {
		this.grammar = grammar;
	}
}