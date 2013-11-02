package org.nimy.eclipse.editor.xml.ui;

import com.sun.org.apache.xerces.internal.parsers.DOMParser;
import java.io.StringReader;
import org.apache.log4j.Logger;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.merlotxml.util.xml.GrammarComplexType;
import org.merlotxml.util.xml.GrammarDocument;
import org.merlotxml.util.xml.GrammarSimpleType;
import org.nimy.eclipse.editor.xml.grammar.CacheGrammar;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

@SuppressWarnings("restriction")
public class Editor extends Composite {
	private static final Logger logger = Logger.getLogger(Editor.class);

	private SashForm sashForm = null;
	private XmlDesignEditor xmlTreeView = null;
	private IXmlModel model = null;
	private GrammarDocument grammars = null;
	private BrowserCon browser = null;

	public Editor(Composite parent, int style) {
		super(parent, style);
		initialize();
	}

	private void initialize() {
		CacheGrammar cg = CacheGrammar.getInstance();
		this.grammars = cg.cacheGrammar("c:\\VoiceXml2.0.dtd", "vxml");

		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		setLayout(gridLayout);
		createSashForm();
		setSize(new Point(400, 500));
	}

	private void createSashForm() {
		GridData gridData = new GridData();
		gridData.horizontalAlignment = 4;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.verticalAlignment = 4;
		this.sashForm = new SashForm(this, 0);
		this.sashForm.setLayoutData(gridData);
		createXmlTreeView();
		createActionPalette();
		int[] p = { 90, 10 };
		this.sashForm.setWeights(p);
	}

	private void createXmlTreeView() {
		this.xmlTreeView = new XmlDesignEditor(this.sashForm, 2048, this.grammars);

		this.xmlTreeView.setMenuManager(new MenuManager());
		StringBuilder sb = new StringBuilder();
		sb.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
		sb.append("<vxml");
		sb.append(" ");

		GrammarComplexType g = this.grammars.getTopLevelGrammarComplexType("vxml");

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
		Document doc = null;
		try {
			parser.parse(new InputSource(new StringReader(sb.toString())));
			doc = parser.getDocument();
			this.xmlTreeView.setDoc(doc);
			this.xmlTreeView.updateView();
		} catch (Exception saxe) {
			logger.error(saxe);
		}
	}

	private void createActionPalette() {
		Composite composite = new Composite(this.sashForm, 0);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		layout.makeColumnsEqualWidth = true;
		composite.setLayout(layout);

		GridData gridData = new GridData();
		gridData.horizontalAlignment = 2;

		Button save = new Button(composite, 0);
		save.setText("&Save");
		save.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
			}

			public void widgetSelected(SelectionEvent e) {
				Editor.this.xmlTreeView.saveModel(null);
				Editor.this.browser.update();
			}
		});
		save.setLayoutData(gridData);

		this.browser = new BrowserCon(getShell(), 2048);
		this.browser.setView(this.xmlTreeView);
		GridData gridData1 = new GridData();
		gridData1.horizontalAlignment = 2;
		gridData1.grabExcessHorizontalSpace = true;
		gridData1.grabExcessVerticalSpace = true;
		gridData1.verticalAlignment = 4;
		this.browser.setLayoutData(gridData1);

		composite.setVisible(true);
	}

	public IXmlModel getModel() {
		return this.model;
	}

	public void setModel(IXmlModel model) {
		this.model = model;
	}

	public GrammarDocument getGrammars() {
		return this.grammars;
	}

	public void setGrammars(GrammarDocument grammars) {
		this.grammars = grammars;
	}
}