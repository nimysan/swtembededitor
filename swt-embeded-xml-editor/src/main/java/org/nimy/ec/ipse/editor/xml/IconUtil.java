package org.nimy.ec.ipse.editor.xml;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.swing.Icon;
import javax.swing.ImageIcon;

public class IconUtil {
	public static Icon getIcon(String name, ClassLoader classLoader) {
		ByteArrayOutputStream byteOut = new ByteArrayOutputStream();

		BufferedInputStream iconIn = new BufferedInputStream(classLoader.getResourceAsStream(name));
		try {
			int i;
			while ((i = iconIn.read()) > -1) {
				byteOut.write(i);
			}
			return new ImageIcon(byteOut.toByteArray());
		} catch (IOException e) {
		}
		return null;
	}
}