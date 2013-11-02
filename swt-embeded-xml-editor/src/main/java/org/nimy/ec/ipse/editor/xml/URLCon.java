package org.nimy.ec.ipse.editor.xml;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

public class URLCon {
	public String downloadData(String url) {
		String gr = null;
		try {
			String lineseparator = System.getProperty("line.separator");
			URL u = new URL(url);
			InputStream in = u.openStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			StringBuilder sb = new StringBuilder();
			String temp = null;
			while ((temp = br.readLine()) != null) {
				sb.append(temp);
				sb.append(lineseparator);
			}
			gr = sb.toString();
			if (gr.length() <= 0)
				gr = null;
		} catch (MalformedURLException mal) {
			mal.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		return gr;
	}
}