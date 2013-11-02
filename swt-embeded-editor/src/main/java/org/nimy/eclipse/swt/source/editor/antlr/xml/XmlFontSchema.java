package org.nimy.eclipse.swt.source.editor.antlr.xml;

import org.nimy.eclipse.swt.source.editor.antlr.FontSchema;

public class XmlFontSchema implements FontSchema {
	public int getFontStyle(int type) {
		switch (type) {
		case 15:
			return 1;
		}
		return 0;
	}
}
