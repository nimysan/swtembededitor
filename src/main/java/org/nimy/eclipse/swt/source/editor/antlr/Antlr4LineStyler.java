package org.nimy.eclipse.swt.source.editor.antlr;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Token;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.nimy.eclipse.swt.source.editor.antlr.xml.XmlColorSchema;

import com.google.common.base.Preconditions;

public class Antlr4LineStyler<T extends Lexer> implements AntlrLineStyler<T>, ColorSchema {
	private T lexer;
	private ColorSchema schema = new XmlColorSchema();
	private Map<Integer, StyleRange[]> lineStyles;
	private StyleRange[] pageStyles;

	public Antlr4LineStyler() {
	}

	public void parse(final StyledText styleText) {
		Preconditions.checkState(lexer != null);
		lexer.setInputStream(new ANTLRInputStream(styleText.getText()));
		Token nextToken = lexer.nextToken();
		int line = 1;
		List<StyleRange> stylesPerLine = new ArrayList<StyleRange>();
		List<StyleRange> stylesList = new ArrayList<StyleRange>();
		lineStyles = new HashMap<Integer, StyleRange[]>();
		while (nextToken != null && nextToken.getType() != -1) {
			iterateToken(nextToken);
			if (line != nextToken.getLine()) {
				lineStyles.put(line, stylesPerLine.toArray(new StyleRange[0]));
				stylesPerLine.clear();
				line = nextToken.getLine();
			}
			StyleRange style = new StyleRange(nextToken.getStartIndex(), nextToken.getText().length(), getColor(nextToken.getType()), null);
			stylesList.add(style);
			stylesPerLine.add(style);
			nextToken = lexer.nextToken();
		}
		pageStyles = stylesList.toArray(new StyleRange[0]);
		styleText.setStyleRanges(pageStyles);
		styleText.redraw();
	}

	protected void iterateToken(Token nextToken) {
	}

	@Override
	public Color getColor(int type) {
		return schema.getColor(type);
	}

	public T getLexer() {
		return lexer;
	}

	public void setLexerClass(Class<T> lexerClass) {
		Preconditions.checkNotNull(lexerClass);
		try {
			Constructor<T> constructor = lexerClass.getConstructor(CharStream.class);
			this.lexer = constructor.newInstance(new ANTLRInputStream());
		} catch (Exception e) {
			e.printStackTrace();
			throw new IllegalStateException("Can not initialize a Lexer from " + lexerClass);
		}
	}

}
