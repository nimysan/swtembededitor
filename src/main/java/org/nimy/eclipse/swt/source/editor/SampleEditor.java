package org.nimy.eclipse.swt.source.editor;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.nimy.eclipse.swt.source.editor.antlr.Antlr4LineStyler;
import org.nimy.eclipse.swt.source.editor.antlr.XmlFontSchema;
import org.nimy.eclipse.swt.source.editor.antlr.xml.XmlAntlr4LineStyler;
import org.nimy.eclipse.swt.source.editor.antlr.xml.XmlColorSchema;

public class SampleEditor {
	public static void main(String[] args) {
		final Display display = new Display();
		final Shell shell = new Shell(display);
		Antlr4LineStyler lineStyler = new XmlAntlr4LineStyler();
		lineStyler.setColorSchema(new XmlColorSchema());
		lineStyler.setFontSchema(new XmlFontSchema());
		final SimpleSourceComposite styledText = new SimpleSourceComposite(lineStyler, shell, SWT.BORDER);
		// styledText.setBounds(10, 10, 500, 100);
		GridLayoutFactory.swtDefaults().numColumns(1).applyTo(shell);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(styledText);
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		display.dispose();
	}
}