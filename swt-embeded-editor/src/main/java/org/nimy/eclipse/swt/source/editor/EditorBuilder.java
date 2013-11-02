package org.nimy.eclipse.swt.source.editor;

import org.eclipse.swt.widgets.Composite;
import org.nimy.eclipse.swt.source.editor.antlr.Antlr4LineStyler;
import org.nimy.eclipse.swt.source.editor.antlr.xml.XmlAntlr4LineStyler;
import org.nimy.eclipse.swt.source.editor.antlr.xml.XmlColorSchema;
import org.nimy.eclipse.swt.source.editor.antlr.xml.XmlFontSchema;
import org.nimy.eclipse.swt.source.editor.antlr.xml.XmlTextFormatter;

public final class EditorBuilder {
	public static final SimpleSourceComposite buildXmlEditor(Composite parent, int style) {
		Antlr4LineStyler lineStyler = new XmlAntlr4LineStyler();
		lineStyler.setColorSchema(new XmlColorSchema());
		lineStyler.setFontSchema(new XmlFontSchema());

		SimpleSourceComposite editor = new SimpleSourceComposite(lineStyler, parent, 2048);
		editor.setFormatter(new XmlTextFormatter());
		return editor;
	}
}
