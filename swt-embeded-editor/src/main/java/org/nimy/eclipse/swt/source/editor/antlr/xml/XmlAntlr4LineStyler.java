package org.nimy.eclipse.swt.source.editor.antlr.xml;

import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Token;
import org.eclipse.swt.graphics.Color;
import org.nimy.antlr4.xml.XMLLexer;
import org.nimy.eclipse.swt.source.editor.antlr.Antlr4LineStyler;

public class XmlAntlr4LineStyler extends Antlr4LineStyler {
	public XmlAntlr4LineStyler() {
		prepareLexer(XMLLexer.class);
	}

	public Color getColor(Lexer lexer, Token token, Token prev) {
		if ((token.getType() == 16) && (prev != null) && ((prev.getType() == 13) || (prev.getType() == 7))) {
			return this.colorSchema.getColor(25);
		}

		return this.colorSchema.getColor(token.getType());
	}
}
