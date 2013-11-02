package org.nimy.ec.ipse.editor.xml;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ManifestMaker {
	public static String MANIFEST_DEF_SPEC_TITLE = "";
	public static String MANIFEST_DEF_SPEC_VERS = "";
	public static String MANIFEST_DEF_SPEC_VEND = "ChannelPoint, Inc.";
	public static String MANIFEST_DEF_IMPL_TITLE = "";
	public static String MANIFEST_DEF_IMPL_VERS = "";
	public static String MANIFEST_DEF_IMPL_VEND = "ChannelPoint, Inc.";
	public static final String MANIFEST_VERSION = "Manifest-Version: ";
	public static final String REQUIRED_VERSION = "Required-Version: ";
	public static final String NAME = "Name: ";
	public static final String SEALED = "Sealed: ";
	public static final String SPECIFICATION_TITLE = "Specification-Title: ";
	public static final String SPECIFICATION_VERSION = "Specification-Version: ";
	public static final String SPECIFICATION_VENDOR = "Specification-Vendor: ";
	public static final String IMPL_TITLE = "Implementation-Title: ";
	public static final String IMPL_VERSION = "Implementation-Version: ";
	public static final String IMPL_VENDOR = "Implementation-Vendor: ";
	public static final String MAIN_CLASS = "Main-Class: ";
	public static final String CLASS_PATH = "Class-Path: ";
	protected String _manifestVersion = "1.0";
	protected String _spectitle = MANIFEST_DEF_SPEC_TITLE;
	protected String _specvers = MANIFEST_DEF_SPEC_VERS;
	protected String _specvend = MANIFEST_DEF_SPEC_VEND;
	protected String _impltitle = MANIFEST_DEF_IMPL_TITLE;
	protected String _implvers = MANIFEST_DEF_IMPL_VERS;
	protected String _implvend = MANIFEST_DEF_IMPL_VEND;
	protected String _mainclass = null;
	protected String _classpath = null;

	protected String _manifestFilename = null;

	public static void main(String[] args) {
		ManifestMaker maker = new ManifestMaker(args);
		maker.run();
	}

	public static void printUsage(PrintStream out) {
		out.println("ManifestMaker usage:");
		out.println("  ManifestMaker [options] <manifestfile>");
		out.println("  Options:");

		out.println("           -st <spectitle>   Specification title");
		out.println("           -sv <specversion> Specification version");
		out.println("           -sc <specvendor>  Specification vendor (company)");
		out.println("           -it <impltitle>   Implementation title");
		out.println("           -iv <implversion> Implementation version");
		out.println("           -ic <implvendor>  Implementation vendor (company)");
		out.println("           -mc <mainclass>   Main class");
		out.println("           -cp <classpath>   Classpath additions");
	}

	public ManifestMaker(String[] args) {
		try {
			for (int i = 0; i < args.length; i++) {
				String arg = args[i];
				if (arg.startsWith("-")) {
					if (arg.equals("-st")) {
						setSpecTitle(getNextArg(args, i++));
					} else if (arg.equals("-sv")) {
						setSpecVersion(getNextArg(args, i++));
					} else if (arg.equals("-sc")) {
						setSpecVendor(getNextArg(args, i++));
					} else if (arg.equals("-it")) {
						setImplTitle(getNextArg(args, i++));
					} else if (arg.equals("-iv")) {
						setImplVersion(getNextArg(args, i++));
					} else if (arg.equals("-ic")) {
						setImplVendor(getNextArg(args, i++));
					} else if (arg.equals("-mc")) {
						setMainClass(getNextArg(args, i++));
					} else if (arg.equals("-cp")) {
						setClassPath(getNextArg(args, i++));
					} else {
						throw new Exception("Unknown option: " + arg);
					}
				} else {
					if (i + 1 < args.length) {
						throw new Exception("Unknown argument: " + arg);
					}

					this._manifestFilename = arg;
				}
			}

		} catch (Exception ex) {
			System.err.println(ex.getMessage());
			printUsage(System.err);
			System.exit(-1);
		}
	}

	public static String getNextArg(String[] args, int i) throws Exception {
		if (i + 1 < args.length) {
			String arg = args[i];
			String narg = args[(i + 1)];
			if (narg.startsWith("-")) {
				throw new Exception(arg + " requires an argument");
			}
			return args[(i + 1)];
		}
		return null;
	}

	public void setSpecTitle(String s) {
		this._spectitle = s;
	}

	public void setSpecVersion(String s) {
		this._specvers = s;
	}

	public void setSpecVendor(String s) {
		this._specvend = s;
	}

	public void setImplTitle(String s) {
		this._impltitle = s;
	}

	public void setImplVersion(String s) {
		this._implvers = s;
	}

	public void setImplVendor(String s) {
		this._implvend = s;
	}

	public void setMainClass(String s) {
		this._mainclass = s;
	}

	public void setClassPath(String s) {
		this._classpath = s;
	}

	public void run() {
		try {
			PrintStream out;
			if (this._manifestFilename != null) {
				File f = new File(this._manifestFilename);
				if (f.exists()) {
					throw new Exception("Will not overwrite existing file: " + this._manifestFilename);
				}
				f.createNewFile();
				out = new PrintStream(new FileOutputStream(f));
			} else {
				out = System.out;
			}

			if ((this._implvers == null) || (this._implvers.trim().equals(""))) {
				this._implvers = getBuildID();
			}

			print(out, "Manifest-Version: ", this._manifestVersion);
			print(out, "Specification-Title: ", this._spectitle);
			print(out, "Specification-Version: ", this._specvers);
			print(out, "Specification-Vendor: ", this._specvend);
			print(out, "Implementation-Title: ", this._impltitle);
			print(out, "Implementation-Version: ", this._implvers);
			print(out, "Implementation-Vendor: ", this._implvend);
			print(out, "Main-Class: ", this._mainclass);
			print(out, "Class-Path: ", this._classpath);
		} catch (Exception ex) {
			System.err.println(ex.getMessage());
			System.exit(-1);
		}
	}

	protected void print(PrintStream out, String header, String value) {
		if ((value != null) && (!value.trim().equals("")))
			out.println(header + value);
	}

	public static String getBuildID() {
		Date d = new Date();
		SimpleDateFormat f = new SimpleDateFormat("yyyyMMdd-HHmmss-z");
		String date = f.format(d);
		String hostname;
		try {
			hostname = InetAddress.getLocalHost().getHostName();
		} catch (Exception ex) {
			hostname = "unknown";
		}
		String username = System.getProperty("user.name");
		String os = System.getProperty("os.name");
		String version = System.getProperty("os.version");
		String arch = System.getProperty("os.arch");
		String BUILDID = date + " (" + username + "@" + hostname + " [" + os + " " + version + " " + arch + "] )";
		return BUILDID;
	}
}