package org.nimy.eclipse.editor.xml.ui;

public class ViewException extends Exception {
	private static final long serialVersionUID = 7170852281880591743L;

	public ViewException() {
	}

	public ViewException(String message) {
		super(message);
	}

	public ViewException(Throwable cause) {
		super(cause);
	}

	public ViewException(String message, Throwable cause) {
		super(message, cause);
	}
}