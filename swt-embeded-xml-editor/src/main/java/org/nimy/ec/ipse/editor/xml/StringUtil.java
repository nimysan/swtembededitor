package org.nimy.ec.ipse.editor.xml;

public class StringUtil {
	public static final int RECURSION_THRESHOLD = 10;

	public static String sReplace(String search, String replace, String source) {
		String origSource = new String(source);

		int spot = source.indexOf(search);
		String returnString;
		if (spot > -1)
			returnString = "";
		else
			returnString = source;
		while (spot > -1) {
			if (spot == source.length() + 1) {
				returnString = returnString.concat(source.substring(0, source.length() - 1).concat(replace));
				source = "";
			} else if (spot > 0) {
				returnString = returnString.concat(source.substring(0, spot).concat(replace));
				source = source.substring(spot + search.length(), source.length());
			} else {
				returnString = returnString.concat(replace);
				source = source.substring(spot + search.length(), source.length());
			}
			spot = source.indexOf(search);
		}
		if (!source.equals(origSource)) {
			return returnString.concat(source);
		}

		return returnString;
	}

	public static String lookupKeysInString(String str, KeyFinder finder) {
		return lookupKeysInString(str, 0, finder);
	}

	public static String lookupKeysInString(String str, int recurselvl, KeyFinder finder) {
		if (recurselvl > 10) {
			throw new RuntimeException("Recursion Threshold reached");
		}

		char[] sb = str.toCharArray();
		int len = sb.length;

		StringBuffer newsb = null;

		int lastKeyEnd = 0;

		for (int i = 0; i < len; i++) {
			char c = sb[i];
			if ((c == '{') && (i + 2 < len) && (sb[(i + 1)] == '%')) {
				int endkey = -1;
				StringBuffer key = new StringBuffer();
				for (int j = i + 2; (j + 1 < len) && (endkey < 0); j++) {
					if ((sb[j] == '%') && (sb[(j + 1)] == '}')) {
						endkey = j - 1;
					} else {
						key.append(sb[j]);
					}
				}
				if (endkey > 0) {
					String val = finder.lookupString(key.toString());
					String s = lookupKeysInString(val, recurselvl + 1, finder);
					if (s != null) {
						if (newsb == null) {
							newsb = new StringBuffer(len);
							for (int k = 0; k < i; k++)
								newsb.append(sb[k]);
						} else {
							for (int k = lastKeyEnd + 1; k < i; k++) {
								newsb.append(sb[k]);
							}
						}
						newsb.append(s);
						i = endkey + 2;
						lastKeyEnd = i;
					}
				}
			}
		}

		if ((lastKeyEnd == 0) && (newsb == null)) {
			return str;
		}
		if ((lastKeyEnd > 0) && (lastKeyEnd + 1 < len)) {
			for (int k = lastKeyEnd + 1; k < len; k++) {
				newsb.append(sb[k]);
			}
		}
		return newsb.toString();
	}

	public static abstract interface KeyFinder {
		public abstract String lookupString(String paramString);
	}
}