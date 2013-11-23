package org.nimy.ec.ipse.editor.xml.exception;

public class CommonXmlException extends Exception {

	private static final long serialVersionUID = 4965354084806667985L;
	private final String xmlContent;

	public CommonXmlException(final Exception thrown, final String message, final String xmlContent) {
		super("[Xml content]:\r\n" + xmlContent + "\r\n" + message, thrown);
		this.xmlContent = xmlContent;
	}

	public String getXmlContent() {
		return xmlContent;
	}

}
