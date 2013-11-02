package org.nimy.eclipse.swt.source.editor.antlr.xml;

import java.io.StringReader;

import javax.xml.parsers.DocumentBuilderFactory;

import org.nimy.eclipse.swt.source.editor.formatter.TextFormatter;
import org.nimy.eclipse.swt.source.editor.utils.Debug;
import org.w3c.dom.Node;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.InputSource;

public class XmlTextFormatter implements TextFormatter {
	public String format(String input) {
		if (input == null)
			return input;
		try {
			InputSource src = new InputSource(new StringReader(input));
			Node document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(src).getDocumentElement();
			Boolean keepDeclaration = Boolean.valueOf(input.startsWith("<?xml"));

			DOMImplementationRegistry registry = DOMImplementationRegistry.newInstance();
			DOMImplementationLS impl = (DOMImplementationLS) registry.getDOMImplementation("LS");
			LSSerializer writer = impl.createLSSerializer();

			writer.getDomConfig().setParameter("format-pretty-print", Boolean.TRUE);

			writer.getDomConfig().setParameter("xml-declaration", keepDeclaration);

			return writer.writeToString(document);
		} catch (Exception e) {
			Debug.error(e);
		}
		return input;
	}
}
