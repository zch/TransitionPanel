package org.vaadin.jonatan.transitionpanel.demo;

import java.util.EnumSet;

import org.vaadin.jonatan.transitionpanel.TransitionPanel;
import org.vaadin.jonatan.transitionpanel.TransitionPanel.Interpolation;
import org.vaadin.jonatan.transitionpanel.TransitionPanel.TransitionType;

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
		tp.setTransitionType(TransitionPanel.TransitionType.FADE_OUT_IN);
		mainWindow.addComponent(tp);

		ComboBox transitionSelect = new ComboBox();
		for (TransitionType t : EnumSet.allOf(TransitionType.class)) {
			transitionSelect.addItem(t);
		}
		transitionSelect.addListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				tp.setTransitionType((TransitionType) event.getProperty().getValue());
			}
		});
		transitionSelect.setNullSelectionAllowed(false);
		transitionSelect.select(TransitionType.NONE);
		mainWindow.addComponent(transitionSelect);
		
		ComboBox interpolateSelect = new ComboBox();
		for (Interpolation t : EnumSet.allOf(Interpolation.class)) {
			interpolateSelect.addItem(t);
		}
		interpolateSelect.addListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				tp.setInterpolation((Interpolation) event.getProperty().getValue());
			}
		});
		interpolateSelect.setNullSelectionAllowed(false);
		interpolateSelect.select(Interpolation.COS);
		mainWindow.addComponent(interpolateSelect);

		
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
