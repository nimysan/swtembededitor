package org.nimy.eclipse.swt.source.editor.antlr;

import org.eclipse.swt.SWT;
import org.nimy.antlr4.xml.XMLLexer;

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
