package org.nimy.eclipse.editor.xml.grammar;

import com.sun.org.apache.xerces.internal.impl.dtd.DTDGrammar;
import com.sun.org.apache.xerces.internal.impl.dtd.XMLContentSpec;
import com.sun.org.apache.xerces.internal.impl.dtd.XMLElementDecl;
import com.sun.org.apache.xerces.internal.impl.xs.SchemaGrammar;
import com.sun.org.apache.xerces.internal.parsers.CachingParserPool;
import com.sun.org.apache.xerces.internal.parsers.DOMParser;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.util.XMLGrammarPoolImpl;
import com.sun.org.apache.xerces.internal.xni.grammars.Grammar;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarDescription;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarPool;
import com.sun.org.apache.xerces.internal.xs.XSElementDeclaration;
import com.sun.org.apache.xerces.internal.xs.XSNamedMap;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.apache.log4j.Logger;
import org.merlotxml.util.xml.GrammarDocument;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

public class ParseXML {
	private static final Logger logger = Logger.getLogger(ParseXML.class);
	public static final String DTDGRAMMAR = "DTD";
	public static final String SCHEMAGRAMMAR = "SCHEMA";
	private SymbolTable symbolTable;
	public static String CACHE_Realtive_PATH = "http://www.w3.org/TR/voicexml20/";

	public ParseXML() {
		this.symbolTable = null;
	}

	public GrammarDocument getGrammarDocument(String dtdfile) {
		return null;
	}

	public Document parseByDomStyle(String file) {
		Document res = null;
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder builder = dbf.newDocumentBuilder();
			res = builder.parse(new File(file));
		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} catch (SAXException saxe) {
			saxe.printStackTrace();
		}
		return res;
	}

	public XMLGrammarPool parseXMLtoGeneralGrammars(InputSource is) {
		XMLGrammarPool pool = null;
		DOMParser parser = null;
		try {
			CachingParserPool config = new CachingParserPool();
			XMLGrammarPool pp = new XMLGrammarPoolImpl();
			parser = config.createDOMParser();
			parser.setProperty("http://apache.org/xml/properties/internal/grammar-pool", pp);

			parser.setFeature("http://apache.org/xml/features/validation/dynamic", true);

			parser.setFeature("http://xml.org/sax/features/validation", true);
			parser.setFeature("http://apache.org/xml/features/validation/schema", true);

			parser.setFeature("http://xml.org/sax/features/namespaces", true);
			this.symbolTable = new SymbolTable();
			parser.setProperty("http://apache.org/xml/properties/internal/symbol-table", this.symbolTable);
			parser.setErrorHandler(new MyErrorHandler());
			parser.setEntityResolver(new MyErrorHandler());
			parser.parse(is);
			pool = (XMLGrammarPool) parser.getProperty("http://apache.org/xml/properties/internal/grammar-pool");
		} catch (SAXException e) {
			logger.debug("error in setting up validation feature");
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		return pool;
	}

	public void parseXMLGrammar(XMLGrammarPool pool, String grammarType) {
		if (grammarType.equals("DTD")) {
			Grammar[] gs = pool.retrieveInitialGrammarSet("http://www.w3.org/TR/REC-xml");

			if ((gs != null) && (gs.length > 0)) {
				for (Grammar g : gs) {
					DTDGrammar dg = (DTDGrammar) g;
					XMLGrammarDescription desc = g.getGrammarDescription();
					print(desc);
					int index = dg.getFirstElementDeclIndex();
					while (index != -1) {
						logger.debug("************************" + index + " begin ************************");

						XMLElementDecl xd = new XMLElementDecl();
						XMLContentSpec contentSpec = new XMLContentSpec();
						boolean found = dg.getElementDecl(index, xd);
						boolean hasContent = dg.getContentSpec(index, contentSpec);

						if (found) {
							logger.debug("Parser Declare!......");
							logger.debug("QName : " + xd.name + " <--->  QType : " + type2String(xd.type));
						}

						if (hasContent) {
							logger.debug("Parser ContentSpec!......");
							Object v = contentSpec.value;
							Object ov = contentSpec.otherValue;
							if ((v instanceof String))
								logger.debug("Value:" + v);
							else {
								logger.debug("Parse failure");
							}
							if ((ov instanceof Node)) {
								Node node = (Node) ov;
								logger.debug("OtherValue:" + node.getLocalName());
							} else {
								logger.debug("OV:::" + ov);
							}
						}
						logger.debug("************************" + index + " over ************************");

						index = dg.getNextElementDeclIndex(index);
					}
				}
			}
		} else if (grammarType.equals("SCHEMA")) {
			Grammar[] gs1 = pool.retrieveInitialGrammarSet("http://www.w3.org/2001/XMLSchema");

			if ((gs1 != null) && (gs1.length > 0)) {
				logger.debug("Schema Grammar:");
				for (Grammar g : gs1) {
					XMLGrammarDescription desc = g.getGrammarDescription();
					print(desc);
					SchemaGrammar sg = (SchemaGrammar) g;

					XSNamedMap topLevelElementDecls = sg.getComponents((short) 2);

					for (int i = 0; i < topLevelElementDecls.getLength(); i++) {
						XSElementDeclaration elementDecl = (XSElementDeclaration) topLevelElementDecls.item(i);

						logger.debug("QName : " + elementDecl.getName() + "<--->QType:" + elementDecl.getType());
					}
				}
			}
		} else {
			logger.debug("Have not any Grammar declare!");
		}
	}

	public static String type2String(short type) {
		switch (type) {
		case 0:
			return "ANY";
		case 3:
			return "CHILDREN";
		case 1:
			return "EMPTY";
		case 2:
			return "MIXED";
		case 4:
			return "SIMPLE";
		}
		return "UNKNOWN";
	}

	private void print(XMLGrammarDescription xmlgd) {
		logger.debug("BaseSystemId " + xmlgd.getBaseSystemId());
		logger.debug("ExpandedSystemId() " + xmlgd.getExpandedSystemId());
		logger.debug("GrammarType " + xmlgd.getGrammarType());
		logger.debug("GrammarType " + xmlgd.getGrammarType());
		logger.debug("Namespace " + xmlgd.getNamespace());
		logger.debug("PublicId " + xmlgd.getPublicId());
	}

	public void parseBySAXStyle(String file) {
		SAXParserFactory sf = SAXParserFactory.newInstance();
		try {
			sf.setValidating(true);
			SAXParser parser = sf.newSAXParser();
			XMLReader xr = parser.getXMLReader();
			xr.setContentHandler(new MyErrorHandler());
			xr.setErrorHandler(new MyErrorHandler());
			xr.parse(file);
		} catch (SAXException saxe) {
			saxe.printStackTrace();
		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	public void parserDTD(String dtdfile) {
	}

	public SymbolTable getSymbolTable() {
		return this.symbolTable;
	}

	public void setSymbolTable(SymbolTable symbolTable) {
		this.symbolTable = symbolTable;
	}

	class MyErrorHandler extends DefaultHandler {
		private final Logger logger = Logger.getLogger(MyErrorHandler.class);

		MyErrorHandler() {
		}

		public void error(SAXParseException exception) throws SAXException {
			this.logger.debug("Error at col:" + exception.getColumnNumber() + " line:" + exception.getLineNumber() + " " + exception.getMessage());

			this.logger.debug("Public ID:" + exception.getPublicId() + "System ID:" + exception.getSystemId());
		}

		public void warning(SAXParseException exception) throws SAXException {
			this.logger.debug("Warn at col:" + exception.getColumnNumber() + " line:" + exception.getLineNumber() + " " + exception.getMessage());

			this.logger.debug("Public ID:" + exception.getPublicId() + "System ID:" + exception.getSystemId());
		}

		public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
			this.logger.debug(uri + " " + localName + " " + qName);
			super.startElement(uri, localName, qName, attributes);
		}

		public void endElement(String uri, String localName, String qName) throws SAXException {
			this.logger.debug(uri + " " + localName + " " + qName);
			super.endElement(uri, localName, qName);
		}

		public void characters(char[] ch, int start, int length) throws SAXException {
			char[] use = new char[length];
			for (int i = 0; i < length; start++) {
				use[i] = ch[start];

				i++;
			}

			this.logger.debug(new String(use));
			super.characters(ch, start, length);
		}

		public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
			this.logger.debug("ignoreableWhitespace " + ch.toString());
			super.ignorableWhitespace(ch, start, length);
		}

		public void notationDecl(String name, String publicId, String systemId) throws SAXException {
			this.logger.debug("Notation Declare:" + name + " " + publicId + " " + systemId);

			super.notationDecl(name, publicId, systemId);
		}

		public void fatalError(SAXParseException exception) throws SAXException {
			this.logger.debug("Fatal at col:" + exception.getColumnNumber() + " line:" + exception.getLineNumber() + " " + exception.getMessage());

			this.logger.debug("Public ID:" + exception.getPublicId() + "System ID:" + exception.getSystemId());

			throw exception;
		}

		public void processingInstruction(String target, String data) throws SAXException {
			this.logger.debug("ProcessingInstruceion Target" + target + " DATA:" + data);

			super.processingInstruction(target, data);
		}

		public void skippedEntity(String name) throws SAXException {
			this.logger.debug("SkipingEntity :" + name);
			super.skippedEntity(name);
		}

		public void unparsedEntityDecl(String name, String publicId, String systemId, String notationName) throws SAXException {
			this.logger.debug("UnparseEntityDeclare Name" + name + " " + publicId + " " + systemId + " " + notationName);

			super.unparsedEntityDecl(name, publicId, systemId, notationName);
		}

		public InputSource resolveEntity(String publicId, String systemId) throws IOException, SAXException {
			this.logger.debug("Resolve Entity-->PUBLIC ID:" + publicId + "   SYSTEM ID: " + systemId);

			return new InputSource(new FileInputStream(new File(systemId.substring(8))));
		}

		public String filterPath(String path) {
			String sep = "///";
			if (path.indexOf("///") != -1) {
				return path.substring(path.indexOf(sep) + sep.length());
			}
			return path;
		}

		public String[] separaPath(String path) {
			String[] res = new String[2];
			int sep = path.lastIndexOf("/");
			if (sep == -1) {
				return null;
			}
			res[0] = path.substring(0, sep + 1);
			res[1] = path.substring(sep + 1);
			return res;
		}
	}
}