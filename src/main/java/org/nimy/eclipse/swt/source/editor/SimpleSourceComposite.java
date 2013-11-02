package org.nimy.eclipse.swt.source.editor;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.Bullet;
import org.eclipse.swt.custom.ExtendedModifyEvent;
import org.eclipse.swt.custom.ExtendedModifyListener;
import org.eclipse.swt.custom.LineStyleEvent;
import org.eclipse.swt.custom.LineStyleListener;
import org.eclipse.swt.custom.ST;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GlyphMetrics;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.nimy.eclipse.swt.source.editor.antlr.AntlrLineStyler;

import com.google.common.base.Preconditions;

public class SimpleSourceComposite extends Composite {
	private StyledText styledText;
	private final AntlrLineStyler lineStyler;

	public SimpleSourceComposite(final AntlrLineStyler lineStyler, Composite parent, int style) {
		super(parent, style);
		Preconditions.checkNotNull(lineStyler);
		this.lineStyler = lineStyler;
		createControls();
	}

	private void createControls() {
		styledText = new StyledText(this, SWT.V_SCROLL | SWT.BORDER);
		Font font = new Font(getShell().getDisplay(), "Courier New", 12, SWT.NORMAL);
		styledText.setFont(font);
		GridLayoutFactory.swtDefaults().numColumns(1).applyTo(this);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(styledText);
		styledText.addExtendedModifyListener(new ExtendedModifyListener() {
			@Override
			public void modifyText(ExtendedModifyEvent event) {
				lineStyler.parse(styledText);
				styledText.redraw();
			}
		});
		styledText.addLineStyleListener(new LineStyleListener() {
			public void lineGetStyle(LineStyleEvent event) {
				// Set the line number
				int lineNumber = styledText.getLineAtOffset(event.lineOffset);
				event.bulletIndex = lineNumber;
				// Set the style, 12 pixles wide for each digit
				StyleRange style = new StyleRange();
				style.foreground = Display.getDefault().getSystemColor(SWT.COLOR_BLUE);
				style.background = Display.getDefault().getSystemColor(SWT.COLOR_INFO_BACKGROUND);
				style.metrics = new GlyphMetrics(0, 0, Integer.toString(styledText.getLineCount() + 1).length() * 12);
				// Create and set the bullet
				event.bullet = new Bullet(ST.BULLET_NUMBER, style);
				// line number from 0
				event.styles = lineStyler.getStylesPeyLine(lineNumber + 1, event.lineOffset, event.lineText.length());
			}
		});
	}

	public void setContent(final String str) {
		if (str != null) {
			this.styledText.setText(str);
		}
	}

	public StyledText getStyledText() {
		return styledText;
	}

}
