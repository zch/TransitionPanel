package org.vaadin.jonatan.transitionpanel.client.ui;

import com.google.gwt.animation.client.Animation;
import com.vaadin.terminal.gwt.client.Paintable;

public abstract class Transition extends Animation {

	private boolean switched;
	protected VTransitionPanel parent;
	protected  Paintable from;
	protected Paintable to;

	public Transition(VTransitionPanel parent, Paintable from,
			Paintable to) {
		this.parent = parent;
		this.from = from;
		this.to = to;
		switched = false;
	}

	public void switchPaintablesOnce() {
		if (!switched) {
			switched = true;
			parent.replaceLayoutWith(to);
		}
	}

}
