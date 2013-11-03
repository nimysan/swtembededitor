package org.nimy.ec.ipse.editor.xml;

import java.io.PrintStream;
import java.io.PrintWriter;

public class WrapperException extends Exception {
	private static final long serialVersionUID = 621534818426578931L;
	protected Exception realException;

	public WrapperException(String message) {
		super(message);
	}

	public WrapperException(Exception realException) {
		super(realException.getMessage());
		this.realException = realException;
	}

	public WrapperException(Exception realException, String appendMsg) {
		super(realException.getMessage() + " " + appendMsg);
		this.realException = realException;
	}

	public Exception getRealException() {
		return this.realException;
	}

	public void printStackTrace() {
		super.printStackTrace();

		if (this.realException != null) {
			System.err.println("-------------------\nWrapped Exception:");
			this.realException.printStackTrace();
		}
	}

	public void printStackTrace(PrintStream s) {
		s.println("Wrapped Exception:");
		this.realException.printStackTrace(s);
		super.printStackTrace(s);
	}

	public void printStackTrace(PrintWriter w) {
		w.println("Wrapped Exception:");
		this.realException.printStackTrace(w);
		super.printStackTrace(w);
	}
}