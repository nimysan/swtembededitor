package org.nimy.ec.ipse.editor.xml;


public class StringUtil2 {
	public static boolean blankString(String str) {
		if (str == null) {
			return true;
		}
		String result = str.trim();
		System.out.println("Result |" + result);
		if ((result == null) || (result.length() <= 0)) {
			return true;
		}
		return false;
	}
}