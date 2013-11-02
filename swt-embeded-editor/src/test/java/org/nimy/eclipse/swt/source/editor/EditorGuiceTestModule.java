package org.nimy.eclipse.swt.source.editor;

import org.nimy.eclipse.swt.source.editor.antlr.ColorSchema;
import org.nimy.eclipse.swt.source.editor.antlr.FontSchema;
import org.nimy.eclipse.swt.source.editor.antlr.xml.XmlColorSchema;
import org.nimy.eclipse.swt.source.editor.antlr.xml.XmlFontSchema;

import com.google.guiceberry.GuiceBerryEnvMain;
import com.google.guiceberry.GuiceBerryModule;
import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

public class EditorGuiceTestModule extends AbstractModule {

	@Override
	protected void configure() {
		install(new GuiceBerryModule());
		bind(GuiceBerryEnvMain.class).to(PizzaAppMain.class);
		bind(ColorSchema.class).annotatedWith(Names.named("xmlColorSchema")).to(XmlColorSchema.class);
		bind(FontSchema.class).annotatedWith(Names.named("xmlFontSchema")).to(XmlFontSchema.class);
	}

	static class PizzaAppMain implements GuiceBerryEnvMain {
		public void run() {
		}
	}
}