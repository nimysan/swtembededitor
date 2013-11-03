package org.nimy.eclipse.editor.xml.ui;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public abstract interface IXmlModel {
	public abstract Document getDocument();
	public abstract void setDocument();
	public abstract void insertElement(Element paramElement);
	public abstract void updateDocument();
	public abstract boolean isDirty();
	public abstract void setDirty(boolean paramBoolean);
	public abstract boolean save(String paramString);
}
