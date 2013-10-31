package org.nimy.eclipse.swt.source.editor;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.Bullet;
import org.eclipse.swt.custom.LineStyleEvent;
import org.eclipse.swt.custom.LineStyleListener;
import org.eclipse.swt.custom.ST;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GlyphMetrics;
import org.eclipse.swt.widgets.Composite;
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
		Font font = new Font(getShell().getDisplay(), "Courier New", 16, SWT.BOLD);
		styledText.setFont(font);
		styledText.addLineStyleListener(new LineStyleListener() {
			public void lineGetStyle(LineStyleEvent event) {
				// Set the line number
				event.bulletIndex = styledText.getLineAtOffset(event.lineOffset);
				// Set the style, 12 pixles wide for each digit
				StyleRange style = new StyleRange();
				style.metrics = new GlyphMetrics(0, 0, Integer.toString(styledText.getLineCount() + 1).length() * 16);
				// Create and set the bullet
				event.bullet = new Bullet(ST.BULLET_NUMBER, style);
			}
		});
		styledText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				lineStyler.parse(styledText);
			}
		});
		GridLayoutFactory.swtDefaults().numColumns(1).applyTo(this);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(styledText);
	}
}
