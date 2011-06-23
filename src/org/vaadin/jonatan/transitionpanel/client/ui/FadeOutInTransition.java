package org.vaadin.jonatan.transitionpanel.client.ui;

import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.Paintable;

public class FadeOutInTransition extends Transition {

	public FadeOutInTransition(VTransitionPanel parent, Paintable from, Paintable to) {
		super(parent, from, to);
	}

	@Override
	protected void onUpdate(double progress) {
		if (from == null) {
			fadeInNew(progress);
			return;
		}
		if (to == null) {
			fadeOutOld(progress);
			return;
		}
		if (progress < 0.5) {
			fadeOutOld(progress * 2);
		} else {
			fadeInNew((progress - 0.5) * 2);
		}
	}

	private void fadeOutOld(double progress) {
		((Widget) from).getElement().getStyle().setOpacity(1.0 - progress);
	}

	private void fadeInNew(double progress) {
		switchPaintablesOnce();
		((Widget) to).getElement().getStyle().setOpacity(progress);
	}
}
