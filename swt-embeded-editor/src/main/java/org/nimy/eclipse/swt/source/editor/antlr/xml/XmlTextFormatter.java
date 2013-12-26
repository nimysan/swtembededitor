package org.nimy.eclipse.swt.source.editor.antlr.xml;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.nimy.eclipse.swt.source.editor.formatter.TextFormatter;
import org.nimy.eclipse.swt.source.editor.utils.Debug;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

public class XmlTextFormatter implements TextFormatter {
	public String format(String input) {
		if (input == null)
			return input;
		try {
			InputSource src = new InputSource(new StringReader(input));
			Node document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(src).getDocumentElement();
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.METHOD, "html");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
			DOMSource source = new DOMSource(document);
			StringWriter stringWriter = new StringWriter();
			StreamResult result = new StreamResult(stringWriter);
			transformer.transform(source, result);

			return stringWriter.toString();
		} catch (Exception e) {
			e.printStackTrace();
			Debug.error(e);
		}
		return input;
	}

}
