package org.nimy.eclipse.swt.source.editor.antlr;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Token;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.nimy.antlr4.xml.XMLLexer;
import org.nimy.eclipse.swt.source.editor.utils.Debug;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.google.inject.name.Named;

public class Antlr4LineStyler implements AntlrLineStyler {

	private Lexer lexer;

	@Inject
	@Named("xmlColorSchema")
	protected ColorSchema colorSchema;

	@Inject
	@Named("xmlFontSchema")
	protected FontSchema fontSchema;
	private Map<Integer, StyleRange[]> stylesPeyLines;

	public Antlr4LineStyler() {
		stylesPeyLines = new TreeMap<Integer, StyleRange[]>();
	}

	public void parse(final StyledText styleText) {
		Preconditions.checkState(lexer != null);
		lexer.setInputStream(new ANTLRInputStream(styleText.getText()));
		Token prevToken = null;
		Token token = lexer.nextToken();
		int line = 1;
		List<StyleRange> stylesListLine = new ArrayList<StyleRange>();
		while (token != null && token.getType() != -1) {
			Debug.debug(printToken(token));
			if (line < token.getLine()) {
				addLineStyles(line, stylesListLine);
				line = token.getLine();
				stylesListLine.clear();
			}
			if (token.getType() != XMLLexer.SEA_WS) {
				StyleRange style = new StyleRange(token.getStartIndex(), token.getText().length(), getColor(lexer, token, prevToken), null);
				int fontStyle = getFontStyle(token.getType());
				if (fontStyle != style.fontStyle) {
					style.fontStyle = fontStyle;
				}
				stylesListLine.add(style);
			}
			prevToken = token;
			token = lexer.nextToken();
			addLineStyles(line, stylesListLine);
		}
	}

	private void addLineStyles(int line, List<StyleRange> stylesListLine) {
		if (!stylesListLine.isEmpty()) {
			StyleRange[] lineStyles = stylesListLine.toArray(new StyleRange[0]);
			stylesPeyLines.put(line, lineStyles);
		}
	}

	private String printToken(Token token) {
		if (token == null)
			return "Token[null]";
		return String.format("[Line %1d # Text %2s # start: %3d - %4d]", token.getLine(), "\r\n".equals(token.getText()) ? "NL" : token.getText(), token.getStartIndex(), token.getStopIndex());
	}

	private int getFontStyle(int type) {
		Preconditions.checkState(fontSchema != null);
		return fontSchema.getFontStyle(type);
	}

	@Override
	public Color getColor(final Lexer lexer, final Token token, final Token prev) {
		Preconditions.checkState(colorSchema != null);
		return colorSchema.getColor(token.getType());
	}

	public Lexer getLexer() {
		return lexer;
	}

	public void prepareLexer(Class<? extends Lexer> lexerClass) {
		Preconditions.checkNotNull(lexerClass);
		try {
			@SuppressWarnings("unchecked")
			Constructor<Lexer> constructor = (Constructor<Lexer>) lexerClass.getConstructor(CharStream.class);
			this.lexer = constructor.newInstance(new ANTLRInputStream());
		} catch (Exception e) {
			throw new IllegalStateException("Can not initialize a Lexer from " + lexerClass);
		}
	}

	public ColorSchema getColorSchema() {
		return colorSchema;
	}

	public void setColorSchema(ColorSchema colorSchema) {
		this.colorSchema = colorSchema;
	}

	public FontSchema getFontSchema() {
		return fontSchema;
	}

	public void setFontSchema(FontSchema fontSchema) {
		this.fontSchema = fontSchema;
	}

	@Override
	public StyleRange[] getStylesPeyLine(int lineNumber, int lineStart, int length) {
		try {
			for (Integer key : stylesPeyLines.keySet()) {
				Debug.debug(key);
				Debug.debug(printStyleRanles(stylesPeyLines.get(key)));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		StyleRange[] ranges = stylesPeyLines.get(lineNumber);
		if (ranges != null) {
			return ranges;
		} else {
			int index = lineNumber;
			while (ranges == null && index >= 0) {
				ranges = stylesPeyLines.get(index--);
			}
			if (ranges != null) {
				StyleRange singleStyle = ranges[ranges.length - 1];
				StyleRange lineStyle = (StyleRange) singleStyle.clone();
				lineStyle.start = lineStart;
				lineStyle.length = length;
				return new StyleRange[] { lineStyle };
			}
		}
		return null;
	}

	private Integer printStyleRanles(StyleRange[] styleRanges) {
		Debug.debug("===");
		if (styleRanges == null) {
			Debug.debug("[null]");
		} else {
			StringBuilder builder = new StringBuilder();
			for (StyleRange r : styleRanges) {
				if (r != null) {
					builder.append(String.format("Start %1d - %2d in color: %3s", r.start, r.length, r.foreground) + " # ");
				} else {
					builder.append("Range null");
				}
			}
			Debug.debug(builder.toString());
		}
		Debug.debug(">>>");
		return null;
	}
}
