package org.nimy.eclipse.editor.xml.ui;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.StatusLineManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabFolder2Adapter;
import org.eclipse.swt.custom.CTabFolderEvent;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.merlotxml.util.xml.GrammarComplexType;
import org.merlotxml.util.xml.GrammarDocument;
import org.merlotxml.util.xml.GrammarSimpleType;
import org.nimy.eclipse.editor.xml.grammar.CacheGrammar;
import org.nimy.eclipse.editor.xml.icons.ImageIndex;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.sun.org.apache.xerces.internal.parsers.DOMParser;

public class CosEditor extends ApplicationWindow {
	private static final Logger logger = Logger.getLogger(CosEditor.class);

	public static String rootElement = null;
	public static final String NAME = "Cosmact XML";
	private CacheGrammar cache = CacheGrammar.getInstance();

	private CTabFolder editArea = null;

	public CosEditor(Shell parentShell) {
		super(parentShell);
		addMenuBar();
		addToolBar(0);
		addStatusLine();
	}

	public static void main(String[] args) {
		CosEditor editor = new CosEditor(null);
		editor.setBlockOnOpen(true);
		editor.open();
		Display.getCurrent().dispose();
	}

	protected Control createContents(Composite parent) {
		createEditor(parent);
		return super.createContents(parent);
	}

	private void createEditor(Composite parent) {
		this.editArea = new CTabFolder(getShell(), 2048);
		this.editArea.addCTabFolder2Listener(new CTabFolder2Adapter() {
			public void close(CTabFolderEvent event) {
				event.doit = false;
				CTabItem file = (CTabItem) event.item;
				int x = MessageDialogExtend.openYNC(CosEditor.this.editArea.getShell(), "Save Resource", file.getText() + " has been modified.Save changes?");

				if (x == 0) {
					CosEditor.this.save(null);
					event.doit = true;
					event.item.dispose();
				} else if (x == 1) {
					event.doit = true;
					event.item.dispose();
				}
			}
		});
	}

	void openFile() {
		FileDialog locate = new FileDialog(getShell(), 8192);

		locate.setFilterExtensions(new String[] { "*.xml", "*.vxml", "*.*" });
		String xmllocate = locate.open();
		if (xmllocate != null) {
			DOMParser parser = new DOMParser();
			Document doc = null;
			String systemID = null;
			GrammarDocument grammar = null;
			try {
				parser.parse(new InputSource(new FileInputStream(new File(xmllocate))));
				doc = parser.getDocument();
				if ((doc != null) && (doc.getDoctype() != null)) {
					systemID = doc.getDoctype().getSystemId();
					logger.info("Find Schema:" + systemID);
					// TODO how to get the root element?
					grammar = this.cache.cacheGrammar(systemID, "vxml");
				}
				try {
					openNewView(xmllocate, grammar, doc);
				} catch (ViewException ve) {
					logger.error(ve);
				}
			} catch (FileNotFoundException fnfe) {
				logger.error(fnfe);
			} catch (IOException ioe) {
				logger.error(ioe);
			} catch (SAXException saxe) {
				logger.error(saxe);
			}
		}
	}

	void closeFile() {
		CTabItem item = this.editArea.getSelection();
		closeItem(item);
	}

	private void closeItem(CTabItem item) {
		if (item != null) {
			XmlDesignEditor x = (XmlDesignEditor) item.getControl();
			String e = null;
			if (x.getXmlpath() != null)
				e = " to " + x.getXmlpath();
			else {
				e = "";
			}
			int res = MessageDialogExtend.openYNC(getShell(), "Close File", "Save " + item.getText() + e + "?");
			if (res == 0) {
				save(null);
				item.dispose();
			} else if (res == 1) {
				item.dispose();
			}
		}
	}

	void closeAll() {
		CTabItem[] items = this.editArea.getItems();
		if (items != null)
			;
	}

	void saveAll() {
		CTabItem[] items = this.editArea.getItems();
		if (items != null)
			for (CTabItem item : items)
				saveItem(item);
	}

	void newFile() {
		InputDialog ip = new InputDialog(getShell(), "New XML", "File Nmae:", null, null);

		ip.open();
		String xmlfile = ip.getValue();
		if (!xmlfile.endsWith(".xml")) {
			xmlfile = xmlfile + ".xml";
		}

		FileDialog dtdChoose = new FileDialog(getShell(), 4096);

		dtdChoose.setFilterExtensions(new String[] { "*.dtd", "*.xsd", "*.*" });
		String file = dtdChoose.open();

		// how to get the default root element? TODO
		GrammarDocument grammar = this.cache.cacheGrammar(file, "vxml");
		logger.info("DTD file location at---" + file);
		Document doc = null;
		if (grammar != null) {
			ChooseRootElementEditor ce = new ChooseRootElementEditor(getShell());
			ce.setGrammar(grammar);
			ce.open();
			logger.debug("select root element :" + rootElement);
			StringBuilder sb = new StringBuilder();
			sb.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
			sb.append("<" + rootElement);
			sb.append(" ");

			GrammarComplexType g = grammar.getTopLevelGrammarComplexType(rootElement);

			if (g != null) {
				GrammarSimpleType[] attrs = g.getAttributes();
				if (attrs != null) {
					for (GrammarSimpleType st : attrs) {
						if (st.getIsRequired()) {
							sb.append(st.getName());
							sb.append("=");
							if ((st.getDefaultValue() != null) && (!st.getDefaultValue().equals(""))) {
								sb.append(st.getDefaultValue());
							} else
								sb.append("\"\"");

							sb.append(" ");
						}
					}
				}
			}
			sb.append("/>");
			DOMParser parser = new DOMParser();
			try {
				parser.parse(new InputSource(new StringReader(sb.toString())));
				doc = parser.getDocument();
			} catch (Exception saxe) {
				logger.error(saxe);
			}

		}

		try {
			openNewView(xmlfile, grammar, doc);
			logger.info(xmlfile + " " + grammar + " " + doc);
		} catch (ViewException ve) {
			logger.error(ve);
		}
	}

	private void openNewView(String fileLocation, GrammarDocument grammar, Document doc) throws ViewException {
		if ((fileLocation != null) && (doc != null)) {
			CTabItem xml = new CTabItem(this.editArea, 64);
			XmlDesignEditor view = new XmlDesignEditor(this.editArea, 2048, grammar);
			view.setXmlpath(fileLocation);
			view.setDoc(doc);
			view.updateView();
			xml.setText(fileLocation);
			xml.setControl(view);
			this.editArea.setFocus();
			this.editArea.setSelection(xml);
		} else {
			MessageDialog.openError(getShell(), "Open XML View", "Some Error occured when open a view");
			logger.debug(fileLocation + " " + grammar + " " + doc);
		}
	}

	void save(String filepath) {
		XmlDesignEditor view = (XmlDesignEditor) this.editArea.getSelection().getControl();
		view.saveModel(filepath);
	}

	void saveItem(CTabItem item) {
		if (item != null) {
			XmlDesignEditor view = (XmlDesignEditor) item.getControl();
			view.saveModel(null);
		}
	}

	protected StatusLineManager createStatusLineManager() {
		StatusLineManager statusLineManager = new StatusLineManager();
		statusLineManager.setCancelEnabled(true);
		statusLineManager.setMessage("Cosmact Xml Editor");
		return statusLineManager;
	}

	protected void configureShell(Shell shell) {
		shell.setSize(600, 500);
		shell.setText("Cosmact XML");
		shell.setImage(ImageDescriptor.createFromFile(ImageIndex.class, "XMLFile.gif").createImage());

		super.configureShell(shell);
	}

	protected MenuManager createMenuManager() {
		MenuManager menu = new MenuManager();
		EditorActionManager.getInstance(this).fillMenuBar(menu);
		return menu;
	}

	protected ToolBarManager createToolBarManager(int style) {
		ToolBarManager toolBarManager = new ToolBarManager(style);
		EditorActionManager.getInstance(this).fillToolBar(toolBarManager);
		return toolBarManager;
	}
}