package org.vaadin.jonatan.transitionpanel.client.ui;

import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.Paintable;
import com.vaadin.terminal.gwt.client.Util;

public class SlideUpTransition extends Transition {

	public SlideUpTransition(VTransitionPanel parent, Paintable from,
			Paintable to) {
		super(parent, from, to);

		((Widget) from).getElement().getStyle().setPosition(Position.RELATIVE);
		((Widget) to).getElement().getStyle().setPosition(Position.RELATIVE);
	}

	@Override
	protected void onUpdate(double progress) {
		Element fromElement = ((Widget) from).getElement();
		Element toElement = ((Widget) to).getElement();
		if (progress < 0.5) {
			fromElement.getStyle().setTop(
					-progress * 2 * fromElement.getOffsetHeight(), Unit.PX);
			Util.notifyParentOfSizeChange(from, false);
		} else {
			switchPaintablesOnce();
			toElement.getStyle().setTop(
					-(1 - (progress - 0.5) * 2) * toElement.getOffsetHeight(),
					Unit.PX);
			Util.notifyParentOfSizeChange(to, false);
		}
	}

	@Override
	protected void onComplete() {
		super.onComplete();
		DOM.setStyleAttribute(((Widget) to).getElement(), "height", "");
		((Widget) from).getElement().getStyle().setPosition(Position.STATIC);
		((Widget) to).getElement().getStyle().setPosition(Position.STATIC);
	}
}
