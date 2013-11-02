package org.nimy.eclipse.swt.source.editor;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class SampleEditor {
	public static void main(String[] args) {
		Display display = new Display();
		Shell shell = new Shell(display);
		SimpleSourceComposite styledText = EditorBuilder.buildXmlEditor(shell, 2048);

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
