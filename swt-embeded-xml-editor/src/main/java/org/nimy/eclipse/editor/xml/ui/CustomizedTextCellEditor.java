package org.nimy.eclipse.editor.xml.ui;

import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.widgets.Composite;

public class CustomizedTextCellEditor extends TextCellEditor {
	public CustomizedTextCellEditor(Composite parent) {
		super(parent);
	}

	protected void doSetValue(Object value) {
		if (value == null)
			return;
		super.doSetValue(value);
	}
}