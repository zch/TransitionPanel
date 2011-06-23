package org.vaadin.jonatan.transitionpanel;

import com.vaadin.Application;
import com.vaadin.ui.*;

public class TransitionpanelApplication extends Application {
	@Override
	public void init() {
		Window mainWindow = new Window("Transitionpanel Application");
		Label label = new Label("Hello Vaadin user");
		mainWindow.addComponent(label);
		setMainWindow(mainWindow);
	}

}
