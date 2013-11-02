package org.nimy.eclipse.swt.source.editor.antlr;

import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Token;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;

public abstract interface AntlrLineStyler {
	public abstract void parse(StyledText paramStyledText);

	public abstract Lexer getLexer();

	public abstract Color getColor(Lexer paramLexer, Token paramToken1, Token paramToken2);

	public abstract StyleRange[] getStylesPeyLine(int paramInt1, int paramInt2, int paramInt3);
}
