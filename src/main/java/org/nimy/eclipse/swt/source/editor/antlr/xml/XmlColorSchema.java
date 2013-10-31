package org.nimy.eclipse.swt.source.editor.antlr.xml;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.nimy.antlr4.xml.XMLLexer;
import org.nimy.eclipse.swt.source.editor.antlr.ColorSchema;

public class XmlColorSchema implements ColorSchema {

	@Override
	public Color getColor(int type) {
		switch (type) {
		case XMLLexer.CDATA:
			return Display.getDefault().getSystemColor(SWT.COLOR_DARK_MAGENTA);
		case XMLLexer.COMMENT:
			return Display.getDefault().getSystemColor(SWT.COLOR_DARK_GREEN);
		case XMLLexer.EQUALS:
			return Display.getDefault().getSystemColor(SWT.COLOR_GRAY);
		case XMLLexer.CLOSE:
			return Display.getDefault().getSystemColor(SWT.COLOR_DARK_RED);
		case XMLLexer.OPEN:
			return Display.getDefault().getSystemColor(SWT.COLOR_DARK_RED);
		case XMLLexer.SLASH:
			return Display.getDefault().getSystemColor(SWT.COLOR_DARK_RED);
		case XMLLexer.SLASH_CLOSE:
			return Display.getDefault().getSystemColor(SWT.COLOR_DARK_RED);
		case XMLLexer.XMLDeclOpen:
			return Display.getDefault().getSystemColor(SWT.COLOR_RED);
		case XMLLexer.Name:
			return Display.getDefault().getSystemColor(SWT.COLOR_DARK_MAGENTA);
		case XMLLexer.STRING:
			return Display.getDefault().getSystemColor(SWT.COLOR_BLUE);
		case XMLLexer.PI:
			return Display.getDefault().getSystemColor(SWT.COLOR_RED);
		case XMLLexer.DTD:
			return Display.getDefault().getSystemColor(SWT.COLOR_DARK_BLUE);
		case XMLLexer.TEXT:
			return Display.getDefault().getSystemColor(SWT.COLOR_BLACK);
		case XMLLexer.CharRef:
			return Display.getDefault().getSystemColor(SWT.COLOR_RED);
		case XMLLexer.SPECIAL_CLOSE:
			return Display.getDefault().getSystemColor(SWT.COLOR_RED);
		case 25:
			return Display.getDefault().getSystemColor(SWT.COLOR_RED);
		default:
			return Display.getDefault().getSystemColor(SWT.COLOR_BLACK);
		}
	}

}
