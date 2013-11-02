package org.nimy.eclipse.editor.xml.grammar;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringReader;

import org.apache.log4j.Logger;
import org.merlotxml.util.xml.GrammarDocument;
import org.merlotxml.util.xml.xerces.DTDGrammarDocumentImpl;
import org.merlotxml.util.xml.xerces.SchemaGrammarDocumentImpl;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import com.sun.org.apache.xerces.internal.parsers.CachingParserPool;
import com.sun.org.apache.xerces.internal.parsers.DOMParser;
import com.sun.org.apache.xerces.internal.parsers.XMLGrammarPreparser;
import com.sun.org.apache.xerces.internal.util.DefaultErrorHandler;
import com.sun.org.apache.xerces.internal.util.XMLGrammarPoolImpl;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.grammars.Grammar;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarPool;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
import com.sun.org.apache.xerces.internal.xni.parser.XMLParseException;

public class CacheGrammar {
	private static final Logger logger = Logger.getLogger(CacheGrammar.class);
	public static final String TYPE_DTD = "DTD";
	public static final String TYPE_SCHEMA = "Schema";
	public static final String GRAMMAR_POOL_IMPL_CLASS = "com.sun.org.apache.xerces.internal.util.XMLGrammarPoolImpl";
	public static final String GRAMMAR_POOL_PROPERTY = "http://apache.org/xml/properties/internal/grammar-pool";
	public static final String NAMESPACES_FEATURE_ID = "http://xml.org/sax/features/namespaces";
	public static final String VALIDATION_FEATURE_ID = "http://xml.org/sax/features/validation";
	private DOMParser parser = null;

	private XMLGrammarPool pool = null;

	private static CacheGrammar instance = null;

	private CacheGrammar() {
		CachingParserPool config = new CachingParserPool();

		this.pool = new XMLGrammarPoolImpl();
		this.parser = config.createDOMParser();
		this.parser.setErrorHandler(new CosmosHandler());
		try {
			this.parser.setProperty("http://apache.org/xml/properties/internal/grammar-pool", this.pool);

			this.parser.setFeature("http://apache.org/xml/features/validation/dynamic", true);

			this.parser.setFeature("http://xml.org/sax/features/validation", true);
			this.parser.setFeature("http://apache.org/xml/features/validation/schema", true);
		} catch (SAXException saxe) {
			saxe.printStackTrace();
		}
	}

	public static CacheGrammar getInstance() {
		if (instance == null) {
			instance = new CacheGrammar();
		}
		return instance;
	}

	public GrammarDocument cacheGrammar(String grammarPath, final String rootXmlElement) {
		logger.info("grammarPath" + grammarPath);
		if (grammarPath.endsWith(".dtd")) {
			return analyzeDTDOrSchemaGrammar(cacheDTDGrammar(grammarPath, rootXmlElement), "DTD");
		}
		if (grammarPath.endsWith(".xsd")) {
			return analyzeDTDOrSchemaGrammar(new Grammar[] { cacheSchemaGrammar(grammarPath) }, "Schema");
		}

		return null;
	}

	private Grammar[] cacheDTDGrammar(final String dtdfile, final String rootElement) {
		try {
			StringBuilder db = new StringBuilder();
			db.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
			db.append("<!DOCTYPE " + rootElement + " SYSTEM \"" + dtdfile + "\">");
			db.append("<" + rootElement + "/>");
			String xmlstring = db.toString();
			StringReader sr = new StringReader(xmlstring);
			this.pool = ((XMLGrammarPool) this.parser.getProperty("http://apache.org/xml/properties/internal/grammar-pool"));
			this.pool.clear();
			this.parser.parse(new InputSource(sr));
			Grammar[] grammars = this.pool.retrieveInitialGrammarSet("http://www.w3.org/TR/REC-xml");
			if (grammars != null) {
				return grammars;
			}
			return null;
		} catch (Exception e) {
			logger.error(e);
		}
		return null;
	}

	public Document validateXmlWithNoDOCTYPE(String dtd, String xmlfile) {
		Document doc = null;
		if (this.parser != null) {
			try {
				StringBuilder db = new StringBuilder();
				db.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
				db.append("<!DOCTYPE vxml SYSTEM \"" + dtd + "\">");
				db.append("<vxml/>");
				String xmlstring = db.toString();
				StringReader sr = new StringReader(xmlstring);
				this.parser.parse(new InputSource(sr));

				File file = new File(xmlfile);
				FileInputStream fr = new FileInputStream(file);
				InputSource source = new InputSource(fr);
				this.parser.parse(source);
				doc = this.parser.getDocument();
			} catch (Exception e) {
				logger.error(e);
			}
		}

		return doc;
	}

	private Grammar cacheSchemaGrammar(String schemaLocation) {
		XMLGrammarPreparser preparser = new XMLGrammarPreparser();

		preparser.registerPreparser("http://www.w3.org/2001/XMLSchema", null);

		if (this.pool != null) {
			this.pool.clear();
		}
		preparser.setProperty("http://apache.org/xml/properties/internal/grammar-pool", this.pool);

		preparser.setFeature("http://xml.org/sax/features/namespaces", true);
		preparser.setFeature("http://xml.org/sax/features/validation", true);
		preparser.setErrorHandler(new CosmosXMLErrorHandler());
		try {
			return preparser.preparseGrammar("http://www.w3.org/2001/XMLSchema", new XMLInputSource(null, schemaLocation, null));
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		return null;
	}

	private GrammarDocument analyzeDTDOrSchemaGrammar(Grammar[] grammars, String type) {
		GrammarDocument grammarDocument = null;
		if (grammars != null) {
			if (type.equals("DTD")) {
				if (grammars.length > 0) {
					logger.debug("DTDGrammars: " + grammars.length);
					grammarDocument = new DTDGrammarDocumentImpl(grammars);
				}
			} else if (type.equals("Schema")) {
				logger.debug("SchemaGrammars: " + grammars.length);
				grammarDocument = new SchemaGrammarDocumentImpl(grammars);
			}
		}

		return grammarDocument;
	}

	public DOMParser getParser() {
		return this.parser;
	}

	public XMLGrammarPool getPool() {
		return this.pool;
	}

	class CosmosXMLErrorHandler extends DefaultErrorHandler {
		CosmosXMLErrorHandler() {
		}

		public void error(String domain, String key, XMLParseException ex) throws XNIException {
			System.out.println("SystemId->" + ex.getBaseSystemId() + " Domain ->" + domain + "  key->" + key + "  line:" + ex.getLineNumber() + " cloumn:" + ex.getColumnNumber());
		}

		public void fatalError(String domain, String key, XMLParseException ex) throws XNIException {
			System.out.println("SystemId->" + ex.getBaseSystemId() + " Domain ->" + domain + "  key->" + key + "  line:" + ex.getLineNumber() + " cloumn:" + ex.getColumnNumber());
		}

		public void warning(String domain, String key, XMLParseException ex) throws XNIException {
			System.out.println("SystemId->" + ex.getBaseSystemId() + " Domain ->" + domain + "  key->" + key + "  line:" + ex.getLineNumber() + " cloumn:" + ex.getColumnNumber());
		}
	}

	class CosmosHandler extends DefaultHandler {
		CosmosHandler() {
		}

		public void error(SAXParseException e) throws SAXException {
			printlnException(e);
		}

		public void fatalError(SAXParseException e) throws SAXException {
			printlnException(e);
		}

		public InputSource resolveEntity(String publicId, String systemId) throws IOException, SAXException {
			return super.resolveEntity(publicId, systemId);
		}

		public void warning(SAXParseException e) throws SAXException {
			printlnException(e);
		}

		private void printlnException(SAXParseException e) {
			StringBuilder sb = new StringBuilder();
			sb.append("[Error] Line: ");
			sb.append(e.getLineNumber());
			sb.append(" Column: ");
			sb.append(e.getColumnNumber());
			sb.append(" ");
			sb.append(e.getMessage());
			System.err.println(sb.toString());
		}
	}
}