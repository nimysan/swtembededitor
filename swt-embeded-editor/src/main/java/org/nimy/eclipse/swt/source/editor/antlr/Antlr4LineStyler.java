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
import org.nimy.eclipse.swt.source.editor.utils.Debug;

import com.google.common.base.Preconditions;

public class Antlr4LineStyler implements AntlrLineStyler {
	private Lexer lexer;
	protected ColorSchema colorSchema;
	protected FontSchema fontSchema;
	private Map<Integer, StyleRange[]> stylesPeyLines;

	public Antlr4LineStyler() {
		this.stylesPeyLines = new TreeMap<Integer, StyleRange[]>();
	}

	public void parse(StyledText styleText) {
		Preconditions.checkState(this.lexer != null);
		this.lexer.setInputStream(new ANTLRInputStream(styleText.getText()));
		Token prevToken = null;
		Token token = this.lexer.nextToken();
		int line = 1;
		List<StyleRange> stylesListLine = new ArrayList<StyleRange>();
		while ((token != null) && (token.getType() != -1)) {
			Debug.debug(printToken(token));
			if (line < token.getLine()) {
				addLineStyles(line, stylesListLine);
				line = token.getLine();
				stylesListLine.clear();
			}
			if (token.getType() != 6) {
				StyleRange style = new StyleRange(token.getStartIndex(), token.getText().length(), getColor(this.lexer, token, prevToken), null);
				int fontStyle = getFontStyle(token.getType());
				if (fontStyle != style.fontStyle) {
					style.fontStyle = fontStyle;
				}
				stylesListLine.add(style);
			}
			prevToken = token;
			token = this.lexer.nextToken();
			addLineStyles(line, stylesListLine);
		}
	}

	private void addLineStyles(int line, List<StyleRange> stylesListLine) {
		if (!stylesListLine.isEmpty()) {
			StyleRange[] lineStyles = (StyleRange[]) stylesListLine.toArray(new StyleRange[0]);
			this.stylesPeyLines.put(Integer.valueOf(line), lineStyles);
		}
	}

	private String printToken(Token token) {
		if (token == null)
			return "Token[null]";
		return String.format("[Line %1d # Text %2s # start: %3d - %4d]", new Object[] { Integer.valueOf(token.getLine()), "\r\n".equals(token.getText()) ? "NL" : token.getText(), Integer.valueOf(token.getStartIndex()), Integer.valueOf(token.getStopIndex()) });
	}

	private int getFontStyle(int type) {
		Preconditions.checkState(this.fontSchema != null);
		return this.fontSchema.getFontStyle(type);
	}

	public Color getColor(Lexer lexer, Token token, Token prev) {
		Preconditions.checkState(this.colorSchema != null);
		return this.colorSchema.getColor(token.getType());
	}

	public Lexer getLexer() {
		return this.lexer;
	}

	public void prepareLexer(Class<? extends Lexer> lexerClass) {
		Preconditions.checkNotNull(lexerClass);
		try {
			Constructor<?> constructor = lexerClass.getConstructor(new Class[] { CharStream.class });
			this.lexer = ((Lexer) constructor.newInstance(new Object[] { new ANTLRInputStream() }));
		} catch (Exception e) {
			throw new IllegalStateException("Can not initialize a Lexer from " + lexerClass);
		}
	}

	public ColorSchema getColorSchema() {
		return this.colorSchema;
	}

	public void setColorSchema(ColorSchema colorSchema) {
		this.colorSchema = colorSchema;
	}

	public FontSchema getFontSchema() {
		return this.fontSchema;
	}

	public void setFontSchema(FontSchema fontSchema) {
		this.fontSchema = fontSchema;
	}

	public StyleRange[] getStylesPeyLine(int lineNumber, int lineStart, int length) {
		try {
			for (Integer key : this.stylesPeyLines.keySet()) {
				Debug.debug(key);
				Debug.debug(printStyleRanles((StyleRange[]) this.stylesPeyLines.get(key)));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		StyleRange[] ranges = (StyleRange[]) this.stylesPeyLines.get(Integer.valueOf(lineNumber));
		if (ranges != null) {
			return ranges;
		}
		int index = lineNumber;
		while ((ranges == null) && (index >= 0)) {
			ranges = (StyleRange[]) this.stylesPeyLines.get(Integer.valueOf(index--));
		}
		if (ranges != null) {
			StyleRange singleStyle = ranges[(ranges.length - 1)];
			StyleRange lineStyle = (StyleRange) singleStyle.clone();
			lineStyle.start = lineStart;
			lineStyle.length = length;
			return new StyleRange[] { lineStyle };
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
				if (r != null)
					builder.append(String.format("Start %1d - %2d in color: %3s", new Object[] { Integer.valueOf(r.start), Integer.valueOf(r.length), r.foreground }) + " # ");
				else {
					builder.append("Range null");
				}
			}
			Debug.debug(builder.toString());
		}
		Debug.debug(">>>");
		return null;
	}
}
