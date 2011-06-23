package org.vaadin.jonatan.transitionpanel.demo;

import org.vaadin.jonatan.transitionpanel.TransitionPanel;

import com.vaadin.Application;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;

@SuppressWarnings("serial")
public class TransitionpanelApplication extends Application {
	private VerticalLayout first;
	private VerticalLayout second;
	
	@Override
	public void init() {
		Window mainWindow = new Window("Transitionpanel Application");
		
		first = new VerticalLayout();
		first.addComponent(new Label("First!!"));
		
		second = new VerticalLayout();
		second.addComponent(new Label("Second!!"));

		final TransitionPanel tp = new TransitionPanel("Transitions!");
		tp.setContent(first);
		tp.setTransition(TransitionPanel.Transition.FADE_OUT_IN);
		mainWindow.addComponent(tp);

		Button switchContent = new Button("Switch content", new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				if (tp.getContent() == first) {
					tp.setContent(second);
				} else {
					tp.setContent(first);
				}
			}
		});
		mainWindow.addComponent(switchContent);

		setMainWindow(mainWindow);
	}

}
