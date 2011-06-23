package org.vaadin.jonatan.transitionpanel.client.ui;

import com.vaadin.terminal.gwt.client.Paintable;

public class FadeOutTransition extends FadeOutInTransition {

	private final VTransitionPanel parent;
	private final Paintable to;

	public FadeOutTransition(VTransitionPanel parent, Paintable from,
			Paintable to) {
		super(parent, from, null);
		this.parent = parent;
		this.to = to;
	}
	

	@Override
	protected void onComplete() {
		super.onComplete();
		parent.replaceLayoutWith(to);
	}

}
