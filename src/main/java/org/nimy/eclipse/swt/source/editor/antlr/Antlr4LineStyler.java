package org.nimy.eclipse.swt.source.editor.antlr;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Token;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;

import com.google.common.base.Preconditions;

public class Antlr4LineStyler implements AntlrLineStyler {

	private Lexer lexer;
	protected ColorSchema colorSchema;
	protected FontSchema fontSchema;
	private StyleRange[] pageStyles;

	public Antlr4LineStyler() {
	}

	public void parse(final StyledText styleText) {
		Preconditions.checkState(lexer != null);
		lexer.setInputStream(new ANTLRInputStream(styleText.getText()));
		Token prevToken = null;
		Token token = lexer.nextToken();
		int line = 1;
		List<StyleRange> stylesList = new ArrayList<StyleRange>();
		while (token != null && token.getType() != -1) {
			iterateToken(token);
			if (line != token.getLine()) {
				line = token.getLine();
			}
			StyleRange style = new StyleRange(token.getStartIndex(), token.getText().length(), getColor(lexer, token, prevToken), null);
			int fontStyle = getFontStyle(token.getType());
			if (fontStyle != style.fontStyle) {
				style.fontStyle = fontStyle;
			}
			stylesList.add(style);
			prevToken = token;
			token = lexer.nextToken();

		}
		pageStyles = stylesList.toArray(new StyleRange[0]);
		styleText.setStyleRanges(pageStyles);
		styleText.redraw();
	}

	private int getFontStyle(int type) {
		Preconditions.checkState(fontSchema != null);
		return fontSchema.getFontStyle(type);
	}

	protected void iterateToken(Token token) {
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

}
