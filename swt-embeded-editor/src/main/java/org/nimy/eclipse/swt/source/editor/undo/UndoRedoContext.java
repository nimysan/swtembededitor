package org.nimy.eclipse.swt.source.editor.undo;

import java.util.Stack;

import org.eclipse.swt.custom.ExtendedModifyEvent;
import org.eclipse.swt.custom.ExtendedModifyListener;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;

import com.google.common.base.Preconditions;

public class UndoRedoContext implements KeyListener, ExtendedModifyListener {
	private StyledText editor;
	private UndoRedoStack<ExtendedModifyEvent> stack;
	private boolean isUndo;
	private boolean isRedo;

	public static final void applyTo(StyledText editor) {
		Preconditions.checkArgument(editor != null);
		new UndoRedoContext(editor);
	}

	private UndoRedoContext(StyledText editor) {
		editor.addExtendedModifyListener(this);
		editor.addKeyListener(this);
		this.editor = editor;
		this.stack = new UndoRedoStack<ExtendedModifyEvent>();
	}

	public void keyPressed(KeyEvent e) {
		boolean isCtrl = (e.stateMask & 0x40000) > 0;
		boolean isAlt = (e.stateMask & 0x10000) > 0;
		if ((isCtrl) && (!isAlt)) {
			boolean isShift = (e.stateMask & 0x20000) > 0;
			if ((!isShift) && (e.keyCode == 122))
				undo();
			else if (((!isShift) && (e.keyCode == 121)) || ((isShift) && (e.keyCode == 122)))
				redo();
		}
	}

	public void keyReleased(KeyEvent e) {
	}

	public void modifyText(ExtendedModifyEvent event) {
		if (this.isUndo) {
			this.stack.pushRedo(event);
		} else {
			this.stack.pushUndo(event);
			if (!this.isRedo)
				this.stack.clearRedo();
		}
	}

	private void undo() {
		if (this.stack.hasUndo()) {
			this.isUndo = true;
			revertEvent((ExtendedModifyEvent) this.stack.popUndo());
			this.isUndo = false;
		}
	}

	private void redo() {
		if (this.stack.hasRedo()) {
			this.isRedo = true;
			revertEvent((ExtendedModifyEvent) this.stack.popRedo());
			this.isRedo = false;
		}
	}

	private void revertEvent(ExtendedModifyEvent event) {
		this.editor.replaceTextRange(event.start, event.length, event.replacedText);

		this.editor.setSelectionRange(event.start, event.replacedText.length());
	}

	private static class UndoRedoStack<T> {
		private Stack<T> undo;
		private Stack<T> redo;

		public UndoRedoStack() {
			this.undo = new Stack<T>();
			this.redo = new Stack<T>();
		}

		public void pushUndo(T delta) {
			this.undo.add(delta);
		}

		public void pushRedo(T delta) {
			this.redo.add(delta);
		}

		public T popUndo() {
			return undo.pop();
		}

		public T popRedo() {
			return this.redo.pop();
		}

		public void clearRedo() {
			this.redo.clear();
		}

		public boolean hasUndo() {
			return !this.undo.isEmpty();
		}

		public boolean hasRedo() {
			return !this.redo.isEmpty();
		}
	}
}