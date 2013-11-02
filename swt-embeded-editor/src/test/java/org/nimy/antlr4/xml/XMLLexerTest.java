package org.nimy.antlr4.xml;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

public class XMLLexerTest {

	@Test
	public void testLexerExist() throws ClassNotFoundException {
		Class<?> forName = Class.forName("org.nimy.antlr4.xml.XMLLexer");
		assertNotNull(forName);
	}
}
