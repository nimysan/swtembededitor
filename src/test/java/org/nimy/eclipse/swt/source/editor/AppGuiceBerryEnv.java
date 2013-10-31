package org.nimy.eclipse.swt.source.editor;

import com.google.guiceberry.GuiceBerryEnvMain;
import com.google.guiceberry.GuiceBerryModule;
import com.google.inject.AbstractModule;

public class AppGuiceBerryEnv extends AbstractModule {

	@Override
	protected void configure() {
		install(new GuiceBerryModule());
		bind(GuiceBerryEnvMain.class).to(PizzaAppMain.class);
	}

	static class PizzaAppMain implements GuiceBerryEnvMain {
		public void run() {
		}
	}
}