package org.vaadin.jonatan.transitionpanel.client.ui;

import org.vaadin.jonatan.transitionpanel.client.ui.VTransitionPanel.Interpolation;

import com.google.gwt.animation.client.Animation;
import com.vaadin.terminal.gwt.client.Paintable;

public abstract class Transition extends Animation {

	private boolean switched;
	protected VTransitionPanel parent;
	protected Paintable from;
	protected Paintable to;
	private Interpolation interpolate;

	public Transition(VTransitionPanel parent, Paintable from, Paintable to) {
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

	public void setInterpolationMode(Interpolation interpolation) {
		this.interpolate = interpolation;
	}

	@Override
	protected double interpolate(double progress) {
		switch (interpolate) {
		case EXPONENTIAL:
			return Math.pow(2, 8 * (progress - 1));
		case CIRCULAR:
			return 1 - Math.sin(Math.acos(progress));
		case BOUNCE:
			double value;
			for (int a = 0, b = 1; true; a += b, b /= 2){
				if (progress >= (7 - 4 * a) / 11){
					value = b * b - Math.pow((11 - 6 * a - 11 * progress) / 4, 2);
					break;
				}
			}
			return value;
		case LINEAR:
			return progress;
		case QUAD:
			return Math.pow(progress, 2);
		case CUBIC:
			return Math.pow(progress, 3);
		case QUART:
			return Math.pow(progress, 4);
		case QUINT:
			return Math.pow(progress, 5);
		case COS:
		default:
			return super.interpolate(progress);
		}
	}

}
