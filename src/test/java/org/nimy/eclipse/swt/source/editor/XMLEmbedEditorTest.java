package org.nimy.eclipse.swt.source.editor;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
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
		verify(text, times(1)).setStyleRanges((StyleRange[]) any());
	}

	@Test(expected = IllegalStateException.class)
	public void testParseWithoutLexer() {
		when(text.getText()).thenReturn("<abc>test</abc>");
		styler.parse(text);
		verify(text, times(1)).setStyleRanges((StyleRange[]) any());
	}

	@Test
	public void testGetColorForToken() {
		// assertNotNull(styler.getColor());
	}
}
