package org.nimy.eclipse.editor.xml.grammar;

public class NoDTDException extends Exception {
	private static final long serialVersionUID = 9144738070105003657L;

	public NoDTDException() {
	}

	public NoDTDException(String message) {
		super(message);
	}

	public NoDTDException(Throwable cause) {
		super(cause);
	}

	public NoDTDException(String message, Throwable cause) {
		super(message, cause);
	}
}