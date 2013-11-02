package org.nimy.eclipse.swt.source.editor;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

import org.antlr.v4.runtime.Token;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;
import org.nimy.antlr4.xml.XMLLexer;
import org.nimy.eclipse.swt.source.editor.antlr.Antlr4LineStyler;

import com.google.guiceberry.junit4.GuiceBerryRule;
import com.google.inject.Inject;

public class XMLEmbedEditorTest {

	@Rule
	public final GuiceBerryRule guiceBerry = new GuiceBerryRule(AppGuiceBerryEnv.class);

	@Inject
	private Antlr4LineStyler styler;

	StyledText text = Mockito.mock(StyledText.class);

	@Test
	public void testInitialize() {
		assertNotNull(styler);
		styler.prepareLexer(XMLLexer.class);
		assertNotNull(styler.getLexer());
	}

	@Test
	public void testParse() {
		styler.prepareLexer(XMLLexer.class);
		when(text.getText()).thenReturn("<abc>test</abc>");
		assertNotNull(styler.getLexer());
		styler.parse(text);
	}

	@Test(expected = IllegalStateException.class)
	public void testParseWithoutLexer() {
		when(text.getText()).thenReturn("<abc>test</abc>");
		styler.parse(text);
		Assert.assertNotNull(styler.getStylesPeyLine(1, 0, 0));
	}

	@Test
	public void testGetColorForToken() {
		Token token = Mockito.mock(Token.class);
		when(token.getType()).thenReturn(XMLLexer.CDATA);
		Color color = styler.getColor(Mockito.mock(XMLLexer.class), token, null);
		assertNotNull(color);
	}
}
