package org.nimy.eclipse.swt.source.editor.antlr;

import org.antlr.v4.runtime.Lexer;
import org.eclipse.swt.custom.StyledText;

public interface AntlrLineStyler<T extends Lexer> {

	public void parse(final StyledText styleText);

	public T getLexer();
}
