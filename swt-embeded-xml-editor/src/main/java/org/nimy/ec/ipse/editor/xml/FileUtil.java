package org.nimy.ec.ipse.editor.xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileUtil {
	public static void copyFile(String fileName, String fromDir, String toDir) throws IOException {
		copyFile(new File(fromDir + File.separator + fileName), new File(toDir + File.separator + fileName));
	}

	public static void copyFile(File from, File to) throws IOException {
		if (!from.canRead()) {
			throw new IOException("Cannot read file '" + from + "'.");
		}

		if ((to.exists()) && (!to.canWrite())) {
			throw new IOException("Cannot write to file '" + to + "'.");
		}

		FileInputStream fis = new FileInputStream(from);
		FileOutputStream fos = new FileOutputStream(to);

		byte[] buf = new byte[1024];
		int bytesLeft;
		while ((bytesLeft = fis.available()) > 0) {
			if (bytesLeft >= buf.length) {
				fis.read(buf);
				fos.write(buf);
			} else {
				byte[] smallBuf = new byte[bytesLeft];
				fis.read(smallBuf);
				fos.write(smallBuf);
			}
		}

		fos.close();
		fis.close();
	}

	public static InputStream getInputStream(File file, Class<?> c) throws FileNotFoundException {
		if (file != null) {
			try {
				return new FileInputStream(file);
			} catch (FileNotFoundException e) {
				String s = file.toString();
				int i = s.indexOf(File.separator);
				if (i >= 0) {
					s = s.substring(i);
					s = StringUtil.sReplace("\\", "/", s);
					InputStream rtn;
					if ((rtn = c.getResourceAsStream(s)) != null) {
						return rtn;
					}
				}
				throw e;
			}
		}
		return null;
	}
}