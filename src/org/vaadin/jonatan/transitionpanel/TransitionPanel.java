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

	public static enum TransitionType {
		FADE_OUT_IN, FADE_OUT, FADE_IN, SLIDE_UP, SLIDE_DOWN, NONE;
	}
	
	public enum Interpolation {
		LINEAR, COS, EXPONENTIAL, CIRCULAR, BOUNCE, ELASTIC, QUAD, CUBIC, QUART, QUINT;
	}


	private int duration = 1000;
	private TransitionType transitionType = TransitionType.NONE;
	private Interpolation interpolation = Interpolation.COS;

	public TransitionPanel(String caption) {
		super(caption);
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public void setTransitionType(TransitionType transitionType) {
		this.transitionType = transitionType;
	}

	public void setInterpolation(Interpolation interpolation) {
		this.interpolation = interpolation;
	}

	@Override
	public void paintContent(PaintTarget target) throws PaintException {
		super.paintContent(target);
		target.addAttribute("duration", duration);
		target.addAttribute("transition", transitionType.name());
		target.addAttribute("interpolation", interpolation.name());
	}
}
