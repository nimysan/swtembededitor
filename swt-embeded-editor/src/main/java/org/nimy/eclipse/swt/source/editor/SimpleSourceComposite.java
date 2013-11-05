package org.nimy.eclipse.swt.source.editor;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.custom.Bullet;
import org.eclipse.swt.custom.ExtendedModifyEvent;
import org.eclipse.swt.custom.ExtendedModifyListener;
import org.eclipse.swt.custom.LineStyleEvent;
import org.eclipse.swt.custom.LineStyleListener;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GlyphMetrics;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.nimy.eclipse.swt.source.editor.antlr.AntlrLineStyler;
import org.nimy.eclipse.swt.source.editor.formatter.TextFormatter;
import org.nimy.eclipse.swt.source.editor.undo.UndoRedoContext;

import com.google.common.base.Preconditions;

public class SimpleSourceComposite extends Composite {
	private StyledText styledText;
	private final AntlrLineStyler lineStyler;
	private TextFormatter formatter;

	public SimpleSourceComposite(AntlrLineStyler lineStyler, Composite parent, int style) {
		super(parent, style);
		Preconditions.checkNotNull(lineStyler);
		this.lineStyler = lineStyler;
		createControls();
	}

	private void createControls() {
		this.styledText = new StyledText(this, 2560);
		Font font = new Font(getShell().getDisplay(), "Courier New", 12, 0);
		this.styledText.setFont(font);
		GridLayoutFactory.swtDefaults().numColumns(1).applyTo(this);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(this.styledText);
		this.styledText.addExtendedModifyListener(new ExtendedModifyListener() {
			public void modifyText(ExtendedModifyEvent event) {
				SimpleSourceComposite.this.lineStyler.parse(SimpleSourceComposite.this.styledText);
				SimpleSourceComposite.this.styledText.redraw();
			}
		});
		this.styledText.addLineStyleListener(new LineStyleListener() {
			public void lineGetStyle(LineStyleEvent event) {
				int lineNumber = SimpleSourceComposite.this.styledText.getLineAtOffset(event.lineOffset);
				event.bulletIndex = (lineNumber + 1);

				StyleRange style = new StyleRange();
				style.foreground = Display.getDefault().getSystemColor(9);
				style.background = Display.getDefault().getSystemColor(29);
				style.metrics = new GlyphMetrics(0, 0, Integer.toString(SimpleSourceComposite.this.styledText.getLineCount() + 1).length() * 12);

				event.bullet = new Bullet(2, style);

				event.styles = SimpleSourceComposite.this.lineStyler.getStylesPeyLine(lineNumber + 1, event.lineOffset, event.lineText.length());
			}
		});
		this.styledText.addKeyListener(new KeyListener() {
			public void keyReleased(KeyEvent e) {
				boolean isCtrl = (e.stateMask & 0x40000) > 0;

				@SuppressWarnings("unused")
				boolean isAlt = (e.stateMask & 0x10000) > 0;
				boolean isShift = (e.stateMask & 0x20000) > 0;
				if ((isCtrl) && (e.keyCode == 97)) {
					SimpleSourceComposite.this.styledText.selectAll();
				}
				if ((isCtrl) && (isShift) && ((e.keyCode == 102) || (e.keyCode == 70)))
					SimpleSourceComposite.this.format();
			}

			public void keyPressed(KeyEvent e) {
			}
		});
		UndoRedoContext.applyTo(this.styledText);
	}

	public void format() {
		if (getFormatter() != null) {
			String format = getFormatter().format(this.styledText.getText());
			if (format != null)
				setContent(format);
		}
	}

	public void setContent(String str) {
		if (str != null)
			this.styledText.setText(str);
	}

	public void setContent(String str, boolean format) {
		if (str != null) {
			this.styledText.setText(str);
			if (format)
				format();
		}
	}

	public StyledText getStyledText() {
		return this.styledText;
	}

	public TextFormatter getFormatter() {
		return this.formatter;
	}

	public void setFormatter(TextFormatter formatter) {
		this.formatter = formatter;
	}
}
