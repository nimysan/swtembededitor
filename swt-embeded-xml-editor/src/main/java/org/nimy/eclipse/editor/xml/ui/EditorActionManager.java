package org.nimy.eclipse.editor.xml.ui;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.nimy.eclipse.editor.xml.icons.ImageIndex;
import org.nimy.eclipse.swt.source.editor.utils.UIResourceContext;

public class EditorActionManager {
	private CosEditor editor = null;

	private static EditorActionManager instance = null;

	private NewFile newFile = null;

	private OpenFile openFile = null;

	private CloseFile closeFile = null;

	private CloseAllFile closeAllFile = null;

	private SaveFile saveFile = null;

	private SaveAllFile saveAllFile = null;

	private SaveAsFile saveAsFile = null;

	private Exit exit = null;

	private EditorActionManager() {
		initActions();
	}

	private void initActions() {
		this.newFile = new NewFile();
		this.openFile = new OpenFile();
		this.closeFile = new CloseFile();
		this.closeAllFile = new CloseAllFile();
		this.saveFile = new SaveFile();
		this.saveAllFile = new SaveAllFile();
		this.saveAsFile = new SaveAsFile();
		this.exit = new Exit();
	}

	public static EditorActionManager getInstance(CosEditor editor) {
		if (instance == null) {
			instance = new EditorActionManager();
		}
		instance.setEditor(editor);
		return instance;
	}

	public void fillMenuBar(IMenuManager menu) {
		MenuManager fileMenu = new MenuManager("&File");
		fileMenu.add(this.newFile);
		fileMenu.add(this.openFile);
		fileMenu.add(new Separator());
		fileMenu.add(this.closeFile);
		fileMenu.add(this.closeAllFile);
		fileMenu.add(new Separator());
		fileMenu.add(this.saveFile);
		fileMenu.add(this.saveAllFile);
		fileMenu.add(this.saveAsFile);
		fileMenu.add(new Separator());
		fileMenu.add(this.exit);
		menu.add(fileMenu);
	}

	public void fillToolBar(IToolBarManager tool) {
		tool.add(this.newFile);
		tool.add(new Separator());
		tool.add(this.saveFile);
		tool.add(this.saveAllFile);
	}

	public CosEditor getEditor() {
		return this.editor;
	}

	public void setEditor(CosEditor editor) {
		this.editor = editor;
	}

	private final class Exit extends Action {
		public Exit() {
			setText("E&xit");
		}

		public void run() {
			if (MessageDialog.openQuestion(EditorActionManager.this.editor.getShell(), "Cosmact XML", "Confirm to exit editor?")) {
				EditorActionManager.this.editor.saveAll();
				System.exit(0);
			}
		}
	}

	private final class CloseAllFile extends Action {
		public CloseAllFile() {
			setText("Close &All@Ctrl+Shift+W");
		}

		public void run() {
			EditorActionManager.this.editor.closeAll();
		}
	}

	private final class CloseFile extends Action {
		public CloseFile() {
			setText("&Close@Ctrl+W");
		}

		public void run() {
			EditorActionManager.this.editor.closeFile();
		}
	}

	private final class SaveAsFile extends Action {
		public SaveAsFile() {
			setText("Save &As...");
			setImageDescriptor(UIResourceContext.getInstance().getImageDescriptor(ImageIndex.class, "full/wizardtool/saveas_edit.png"));
		}

		public void run() {
			FileDialog locate = new FileDialog(EditorActionManager.this.editor.getShell(), 8192);

			locate.setFilterExtensions(new String[] { "*.xml", "*.vxml", "*.*" });

			String xmllocate = locate.open();
			if (xmllocate != null)
				EditorActionManager.this.editor.save(xmllocate);
		}
	}

	private final class SaveAllFile extends Action {
		public SaveAllFile() {
			setText("Sav&e All@Ctrl+Shift+S");
			setImageDescriptor(UIResourceContext.getInstance().getImageDescriptor(ImageIndex.class, "full/wizardtool/saveall_edit.png"));
		}

		public void run() {
			EditorActionManager.this.editor.saveAll();
		}
	}

	private final class SaveFile extends Action {
		public SaveFile() {
			setText("&Save@Ctrl+S");
			setImageDescriptor(UIResourceContext.getInstance().getImageDescriptor(ImageIndex.class, "full/wizardtool/save_edit.png"));
		}

		public void run() {
			EditorActionManager.this.editor.save(null);
		}
	}

	private final class OpenFile extends Action {
		public OpenFile() {
			setText("Open file&...");
		}

		public void run() {
			EditorActionManager.this.editor.openFile();
		}
	}

	private final class NewFile extends Action {
		public NewFile() {
			setText("&New@Alt+Shift+N");
			setImageDescriptor(UIResourceContext.getInstance().getImageDescriptor(ImageIndex.class, "full/wizardtool/newfile_wiz.png"));
		}

		public void run() {
			EditorActionManager.this.editor.newFile();
		}
	}
}