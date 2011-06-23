package org.vaadin.jonatan.transitionpanel.demo;

import java.util.EnumSet;

import org.vaadin.jonatan.transitionpanel.TransitionPanel;
import org.vaadin.jonatan.transitionpanel.TransitionPanel.Transition;

import com.vaadin.Application;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

@SuppressWarnings("serial")
public class TransitionpanelApplication extends Application {
	private VerticalLayout first;
	private VerticalLayout second;
	
	@Override
	public void init() {
		Window mainWindow = new Window("Transitionpanel Application");
		
		first = new VerticalLayout();
		first.addComponent(new Label("First!!<br><br><br>foo", Label.CONTENT_XHTML));
		
		second = new VerticalLayout();
		second.addComponent(new Label("Second!!<br><br><br>bar", Label.CONTENT_XHTML));

		final TransitionPanel tp = new TransitionPanel("Transitions!");
		tp.setContent(first);
		tp.setTransition(TransitionPanel.Transition.FADE_OUT_IN);
		mainWindow.addComponent(tp);

		ComboBox transitionSelect = new ComboBox();
		for (TransitionPanel.Transition t : EnumSet.allOf(TransitionPanel.Transition.class)) {
			transitionSelect.addItem(t);
		}
		transitionSelect.addListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				tp.setTransition((Transition) event.getProperty().getValue());
			}
		});
		transitionSelect.setNullSelectionAllowed(false);
		transitionSelect.select(TransitionPanel.Transition.NONE);
		mainWindow.addComponent(transitionSelect);
		
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
