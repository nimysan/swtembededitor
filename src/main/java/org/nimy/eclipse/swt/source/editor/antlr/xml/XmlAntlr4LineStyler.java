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

	@Override
	public Color getColor(Lexer lexer, Token token, Token prev) {
		if (token.getType() == XMLLexer.Name && prev != null) {
			if (prev.getType() == XMLLexer.SLASH || prev.getType() == XMLLexer.OPEN) {
				return colorSchema.getColor(25);
			}
		}
		return colorSchema.getColor(token.getType());
	}
}
