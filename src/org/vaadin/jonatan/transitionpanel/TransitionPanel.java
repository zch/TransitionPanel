package org.vaadin.jonatan.transitionpanel;

import com.vaadin.terminal.PaintException;
import com.vaadin.terminal.PaintTarget;
import com.vaadin.ui.Panel;

/**
 * Server side component for the VTransitionPanel widget.
 */
@SuppressWarnings("serial")
@com.vaadin.ui.ClientWidget(org.vaadin.jonatan.transitionpanel.client.ui.VTransitionPanel.class)
public class TransitionPanel extends Panel {

	public static enum Transition {
		FADE_OUT_IN, FADE_OUT, FADE_IN, SLIDE_UP, SLIDE_DOWN, NONE;
	}

	private int duration = 1000;
	private Transition transition = Transition.NONE;

	public TransitionPanel(String caption) {
		super(caption);
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public void setTransition(Transition transition) {
		this.transition = transition;
	}

	@Override
	public void paintContent(PaintTarget target) throws PaintException {
		super.paintContent(target);
		target.addAttribute("duration", duration);
		target.addAttribute("transition", transition.name());
	}
}
