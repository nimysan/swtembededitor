package org.nimy.eclipse.swt.source.editor;

import static org.junit.Assert.assertNotNull;

import org.junit.Rule;
import org.junit.Test;
import org.nimy.antlr4.xml.XMLLexer;
import org.nimy.eclipse.swt.source.editor.antlr.Antlr4LineStyler;

import com.google.guiceberry.junit4.GuiceBerryRule;
import com.google.inject.Inject;

public class BaseTest {

	@Rule
	public final GuiceBerryRule guiceBerry = new GuiceBerryRule(AppGuiceBerryEnv.class);

	@Inject
	private Antlr4LineStyler<XMLLexer> styler;

	@Test
	public void testInitialize() {
		assertNotNull(styler);
		styler.setLexerClass(XMLLexer.class);
		assertNotNull(styler.getLexer());
	}
}
