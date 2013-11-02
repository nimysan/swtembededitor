package org.nimy.eclipse.swt.source.editor.antlr.xml;

import org.eclipse.swt.SWT;
import org.nimy.antlr4.xml.XMLLexer;
import org.nimy.eclipse.swt.source.editor.antlr.FontSchema;

public class XmlFontSchema implements FontSchema {

	@Override
	public int getFontStyle(int type) {
		switch (type) {
		case XMLLexer.STRING:
			return SWT.BOLD;
		default:
			return SWT.NORMAL;
		}
	}

}
