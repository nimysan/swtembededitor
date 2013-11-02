package org.nimy.eclipse.swt.source.editor.antlr;

import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Token;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;

public interface AntlrLineStyler {

	public void parse(final StyledText styleText);

	public Lexer getLexer();

	public Color getColor(final Lexer lexer, final Token token, final Token prev);

	public StyleRange[] getStylesPeyLine(int lineNumber, int lineStart, int length);

}
