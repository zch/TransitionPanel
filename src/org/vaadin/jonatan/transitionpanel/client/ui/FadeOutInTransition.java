package org.vaadin.jonatan.transitionpanel.client.ui;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.Paintable;

public class FadeOutInTransition extends Animation {

	private final VTransitionPanel parent;
	private final Paintable from;
	private final Paintable to;
	private boolean switched;

	public FadeOutInTransition(VTransitionPanel parent, Paintable from, Paintable to) {
		this.parent = parent;
		this.from = from;
		this.to = to;
		switched = false;
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
		if (!switched) {
			switched = true;
			parent.replaceLayoutWith(to);
		}
		((Widget) to).getElement().getStyle().setOpacity(progress);
	}
}
