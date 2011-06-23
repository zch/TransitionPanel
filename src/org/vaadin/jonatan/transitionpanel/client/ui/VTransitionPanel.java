package org.vaadin.jonatan.transitionpanel.client.ui;

import java.util.Set;

import org.vaadin.jonatan.transitionpanel.client.ui.VTransitionPanel.Interpolation;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.event.dom.client.DomEvent.Type;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.BrowserInfo;
import com.vaadin.terminal.gwt.client.Container;
import com.vaadin.terminal.gwt.client.Focusable;
import com.vaadin.terminal.gwt.client.Paintable;
import com.vaadin.terminal.gwt.client.RenderInformation;
import com.vaadin.terminal.gwt.client.RenderSpace;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.Util;
import com.vaadin.terminal.gwt.client.ui.ClickEventHandler;
import com.vaadin.terminal.gwt.client.ui.Icon;
import com.vaadin.terminal.gwt.client.ui.ShortcutActionHandler;
import com.vaadin.terminal.gwt.client.ui.ShortcutActionHandler.ShortcutActionHandlerOwner;
import com.vaadin.terminal.gwt.client.ui.TouchScrollDelegate;

/**
 * Client side widget which communicates with the server. Messages from the
 * server are shown as HTML and mouse clicks are sent to the server.
 */
public class VTransitionPanel extends SimplePanel implements Container,
		ShortcutActionHandlerOwner, Focusable {

	public static final String CLICK_EVENT_IDENTIFIER = "click";
	public static final String CLASSNAME = "v-panel";

	ApplicationConnection client;

	String id;

	private final Element captionNode = DOM.createDiv();

	private final Element captionText = DOM.createSpan();

	private Icon icon;

	private final Element bottomDecoration = DOM.createDiv();

	private final Element contentNode = DOM.createDiv();

	private Element errorIndicatorElement;

	private String height;

	private Paintable layout;

	ShortcutActionHandler shortcutHandler;

	private String width = "";

	private Element geckoCaptionMeter;

	private int scrollTop;

	private int scrollLeft;

	private RenderInformation renderInformation = new RenderInformation();

	private int borderPaddingHorizontal = -1;

	private int borderPaddingVertical = -1;

	private int captionPaddingHorizontal = -1;

	private int captionMarginLeft = -1;

	private boolean rendering;

	private int contentMarginLeft = -1;

	private String previousStyleName;

	private Transition transition = null;

	private ClickEventHandler clickEventHandler = new ClickEventHandler(this,
			CLICK_EVENT_IDENTIFIER) {

		@Override
		protected <H extends EventHandler> HandlerRegistration registerHandler(
				H handler, Type<H> type) {
			return addDomHandler(handler, type);
		}
	};
	private TouchScrollDelegate touchScrollDelegate;
	private int duration;
	private TransitionType transitionType;
	private Interpolation interpolation;

	public VTransitionPanel() {
		super();
		DivElement captionWrap = Document.get().createDivElement();
		captionWrap.appendChild(captionNode);
		captionNode.appendChild(captionText);

		captionWrap.setClassName(CLASSNAME + "-captionwrap");
		captionNode.setClassName(CLASSNAME + "-caption");
		contentNode.setClassName(CLASSNAME + "-content");
		bottomDecoration.setClassName(CLASSNAME + "-deco");

		getElement().appendChild(captionWrap);

		/*
		 * Make contentNode focusable only by using the setFocus() method. This
		 * behaviour can be changed by invoking setTabIndex() in the serverside
		 * implementation
		 */
		contentNode.setTabIndex(-1);

		getElement().appendChild(contentNode);

		getElement().appendChild(bottomDecoration);
		setStyleName(CLASSNAME);
		DOM.sinkEvents(getElement(), Event.ONKEYDOWN);
		DOM.sinkEvents(contentNode, Event.ONSCROLL | Event.TOUCHEVENTS);
		contentNode.getStyle().setProperty("position", "relative");
		getElement().getStyle().setProperty("overflow", "hidden");
		addHandler(new TouchStartHandler() {
			public void onTouchStart(TouchStartEvent event) {
				getTouchScrollDelegate().onTouchStart(event);
			}
		}, TouchStartEvent.getType());
	}

	/**
	 * Sets the keyboard focus on the Panel
	 * 
	 * @param focus
	 *            Should the panel have focus or not.
	 */
	public void setFocus(boolean focus) {
		if (focus) {
			getContainerElement().focus();
		} else {
			getContainerElement().blur();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.vaadin.terminal.gwt.client.Focusable#focus()
	 */
	public void focus() {
		setFocus(true);
	}

	@Override
	protected Element getContainerElement() {
		return contentNode;
	}

	private void setCaption(String text) {
		DOM.setInnerHTML(captionText, text);
	}

	public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
		rendering = true;
		if (!uidl.hasAttribute("cached")) {

			// Handle caption displaying and style names, prior generics.
			// Affects size
			// calculations

			// Restore default stylenames
			contentNode.setClassName(CLASSNAME + "-content");
			bottomDecoration.setClassName(CLASSNAME + "-deco");
			captionNode.setClassName(CLASSNAME + "-caption");
			boolean hasCaption = false;
			if (uidl.hasAttribute("caption")
					&& !uidl.getStringAttribute("caption").equals("")) {
				setCaption(uidl.getStringAttribute("caption"));
				hasCaption = true;
			} else {
				setCaption("");
				captionNode.setClassName(CLASSNAME + "-nocaption");
			}

			// Add proper stylenames for all elements. This way we can prevent
			// unwanted CSS selector inheritance.
			if (uidl.hasAttribute("style")) {
				final String[] styles = uidl.getStringAttribute("style").split(
						" ");
				final String captionBaseClass = CLASSNAME
						+ (hasCaption ? "-caption" : "-nocaption");
				final String contentBaseClass = CLASSNAME + "-content";
				final String decoBaseClass = CLASSNAME + "-deco";
				String captionClass = captionBaseClass;
				String contentClass = contentBaseClass;
				String decoClass = decoBaseClass;
				for (int i = 0; i < styles.length; i++) {
					captionClass += " " + captionBaseClass + "-" + styles[i];
					contentClass += " " + contentBaseClass + "-" + styles[i];
					decoClass += " " + decoBaseClass + "-" + styles[i];
				}
				captionNode.setClassName(captionClass);
				contentNode.setClassName(contentClass);
				bottomDecoration.setClassName(decoClass);

			}
		}
		// Ensure correct implementation
		if (client.updateComponent(this, uidl, false)) {
			rendering = false;
			return;
		}

		clickEventHandler.handleEventHandlerRegistration(client);

		this.client = client;
		id = uidl.getId();

		setIconUri(uidl, client);

		handleError(uidl);

		duration = uidl.getIntAttribute("duration");
		transitionType = TransitionType.valueOf(uidl
				.getStringAttribute("transition"));
		interpolation = Interpolation.valueOf(uidl
				.getStringAttribute("interpolation"));

		// Render content
		renderContent(uidl);

		// We may have actions attached to this panel
		if (uidl.getChildCount() > 1) {
			final int cnt = uidl.getChildCount();
			for (int i = 1; i < cnt; i++) {
				UIDL childUidl = uidl.getChildUIDL(i);
				if (childUidl.getTag().equals("actions")) {
					if (shortcutHandler == null) {
						shortcutHandler = new ShortcutActionHandler(id, client);
					}
					shortcutHandler.updateActionMap(childUidl);
				}
			}
		}

		if (uidl.hasVariable("scrollTop")
				&& uidl.getIntVariable("scrollTop") != scrollTop) {
			scrollTop = uidl.getIntVariable("scrollTop");
			contentNode.setScrollTop(scrollTop);
			// re-read the actual scrollTop in case invalid value was set
			// (scrollTop != 0 when no scrollbar exists, other values would be
			// caught by scroll listener), see #3784
			scrollTop = contentNode.getScrollTop();
		}

		if (uidl.hasVariable("scrollLeft")
				&& uidl.getIntVariable("scrollLeft") != scrollLeft) {
			scrollLeft = uidl.getIntVariable("scrollLeft");
			contentNode.setScrollLeft(scrollLeft);
			// re-read the actual scrollTop in case invalid value was set
			// (scrollTop != 0 when no scrollbar exists, other values would be
			// caught by scroll listener), see #3784
			scrollLeft = contentNode.getScrollLeft();
		}

		// Must be run after scrollTop is set as Webkit overflow fix re-sets the
		// scrollTop
		runHacks(false);

		// And apply tab index
		if (uidl.hasVariable("tabindex")) {
			contentNode.setTabIndex(uidl.getIntVariable("tabindex"));
		}

		rendering = false;

	}

	private void renderContent(UIDL uidl) {
		final UIDL layoutUidl = uidl.getChildUIDL(0);
		final Paintable newLayout = client.getPaintable(layoutUidl);
		newLayout.updateFromUIDL(layoutUidl, client);
		if (newLayout != layout) {
			performTransitionTo(newLayout);
		}
	}

	private void performTransitionTo(final Paintable newLayout) {
		if (transition != null) {
			transition.cancel();
		}
		if (transitionType == TransitionType.NONE) {
			replaceLayoutWith(newLayout);
		} else {
			transition = transitionType.getInstance(this, layout, newLayout);
			transition.setInterpolationMode(interpolation);
			transition.run(duration);
		}
	}

	public void replaceLayoutWith(Paintable newLayout) {
		if (layout != null) {
			client.unregisterPaintable(layout);
		}
		setWidget((Widget) newLayout);
		layout = newLayout;
		Util.notifyParentOfSizeChange(layout, false);
	}

	@Override
	public void setStyleName(String style) {
		if (!style.equals(previousStyleName)) {
			super.setStyleName(style);
			detectContainerBorders();
			previousStyleName = style;
		}
	}

	private void handleError(UIDL uidl) {
		if (uidl.hasAttribute("error")) {
			if (errorIndicatorElement == null) {
				errorIndicatorElement = DOM.createSpan();
				DOM.setElementProperty(errorIndicatorElement, "className",
						"v-errorindicator");
				DOM.sinkEvents(errorIndicatorElement, Event.MOUSEEVENTS);
				sinkEvents(Event.MOUSEEVENTS);
			}
			DOM.insertBefore(captionNode, errorIndicatorElement, captionText);
		} else if (errorIndicatorElement != null) {
			DOM.removeChild(captionNode, errorIndicatorElement);
			errorIndicatorElement = null;
		}
	}

	private void setIconUri(UIDL uidl, ApplicationConnection client) {
		final String iconUri = uidl.hasAttribute("icon") ? uidl
				.getStringAttribute("icon") : null;
		if (iconUri == null) {
			if (icon != null) {
				DOM.removeChild(captionNode, icon.getElement());
				icon = null;
			}
		} else {
			if (icon == null) {
				icon = new Icon(client);
				DOM.insertChild(captionNode, icon.getElement(), 0);
			}
			icon.setUri(iconUri);
		}
	}

	public void runHacks(boolean runGeckoFix) {
		if (BrowserInfo.get().isIE6() && width != null && !width.equals("")) {
			/*
			 * IE6 requires overflow-hidden elements to have a width specified
			 * so we calculate the width of the content and caption nodes when
			 * no width has been specified.
			 */
			/*
			 * Fixes #1923 VPanel: Horizontal scrollbar does not appear in IE6
			 * with wide content
			 */

			/*
			 * Caption must be shrunk for parent measurements to return correct
			 * result in IE6
			 */
			DOM.setStyleAttribute(captionNode, "width", "1px");

			int parentPadding = Util.measureHorizontalPaddingAndBorder(
					getElement(), 0);

			int parentWidthExcludingPadding = getElement().getOffsetWidth()
					- parentPadding;

			Util.setWidthExcludingPaddingAndBorder(captionNode,
					parentWidthExcludingPadding - getCaptionMarginLeft(), 26,
					false);

			int contentMarginLeft = getContentMarginLeft();

			Util.setWidthExcludingPaddingAndBorder(contentNode,
					parentWidthExcludingPadding - contentMarginLeft, 2, false);

		}

		if ((BrowserInfo.get().isIE() || BrowserInfo.get().isFF2())
				&& (width == null || width.equals(""))) {
			/*
			 * IE and FF2 needs width to be specified for the root DIV so we
			 * calculate that from the sizes of the caption and layout
			 */
			int captionWidth = captionText.getOffsetWidth()
					+ getCaptionMarginLeft() + getCaptionPaddingHorizontal();
			int layoutWidth = ((Widget) layout).getOffsetWidth()
					+ getContainerBorderWidth();
			int width = layoutWidth;
			if (captionWidth > width) {
				width = captionWidth;
			}

			if (BrowserInfo.get().isIE7()) {
				Util.setWidthExcludingPaddingAndBorder(captionNode, width
						- getCaptionMarginLeft(), 26, false);
			}

			super.setWidth(width + "px");
		}

		if (runGeckoFix && BrowserInfo.get().isGecko()) {
			// workaround for #1764
			if (width == null || width.equals("")) {
				if (geckoCaptionMeter == null) {
					geckoCaptionMeter = DOM.createDiv();
					DOM.appendChild(captionNode, geckoCaptionMeter);
				}
				int captionWidth = DOM.getElementPropertyInt(captionText,
						"offsetWidth");
				int availWidth = DOM.getElementPropertyInt(geckoCaptionMeter,
						"offsetWidth");
				if (captionWidth == availWidth) {
					/*
					 * Caption width defines panel width -> Gecko based browsers
					 * somehow fails to float things right, without the
					 * "noncode" below
					 */
					setWidth(getOffsetWidth() + "px");
				} else {
					DOM.setStyleAttribute(captionNode, "width", "");
				}
			}
		}

		client.runDescendentsLayout(this);

		Util.runWebkitOverflowAutoFix(contentNode);

	}

	@Override
	public void onBrowserEvent(Event event) {
		super.onBrowserEvent(event);

		final Element target = DOM.eventGetTarget(event);
		final int type = DOM.eventGetType(event);
		if (type == Event.ONKEYDOWN && shortcutHandler != null) {
			shortcutHandler.handleKeyboardEvent(event);
			return;
		}
		if (type == Event.ONSCROLL) {
			int newscrollTop = DOM.getElementPropertyInt(contentNode,
					"scrollTop");
			int newscrollLeft = DOM.getElementPropertyInt(contentNode,
					"scrollLeft");
			if (client != null
					&& (newscrollLeft != scrollLeft || newscrollTop != scrollTop)) {
				scrollLeft = newscrollLeft;
				scrollTop = newscrollTop;
				client.updateVariable(id, "scrollTop", scrollTop, false);
				client.updateVariable(id, "scrollLeft", scrollLeft, false);
			}
		} else if (captionNode.isOrHasChild(target)) {
			if (client != null) {
				client.handleTooltipEvent(event, this);
			}
		}
	}

	protected TouchScrollDelegate getTouchScrollDelegate() {
		if (touchScrollDelegate == null) {
			touchScrollDelegate = new TouchScrollDelegate(contentNode);
		}
		return touchScrollDelegate;

	}

	@Override
	public void setHeight(String height) {
		this.height = height;
		super.setHeight(height);
		if (height != null && !"".equals(height)) {
			final int targetHeight = getOffsetHeight();
			int containerHeight = targetHeight
					- captionNode.getParentElement().getOffsetHeight()
					- bottomDecoration.getOffsetHeight()
					- getContainerBorderHeight();
			if (containerHeight < 0) {
				containerHeight = 0;
			}
			DOM.setStyleAttribute(contentNode, "height", containerHeight + "px");
		} else {
			DOM.setStyleAttribute(contentNode, "height", "");
		}
		if (!rendering) {
			runHacks(true);
		}
	}

	private int getCaptionMarginLeft() {
		if (captionMarginLeft < 0) {
			detectContainerBorders();
		}
		return captionMarginLeft;
	}

	private int getContentMarginLeft() {
		if (contentMarginLeft < 0) {
			detectContainerBorders();
		}
		return contentMarginLeft;
	}

	private int getCaptionPaddingHorizontal() {
		if (captionPaddingHorizontal < 0) {
			detectContainerBorders();
		}
		return captionPaddingHorizontal;
	}

	private int getContainerBorderHeight() {
		if (borderPaddingVertical < 0) {
			detectContainerBorders();
		}
		return borderPaddingVertical;
	}

	@Override
	public void setWidth(String width) {
		if (this.width.equals(width)) {
			return;
		}

		this.width = width;
		super.setWidth(width);
		if (!rendering) {
			runHacks(true);

			if (height.equals("")) {
				// Width change may affect height
				Util.updateRelativeChildrenAndSendSizeUpdateEvent(client, this);
			}

		}
	}

	private int getContainerBorderWidth() {
		if (borderPaddingHorizontal < 0) {
			detectContainerBorders();
		}
		return borderPaddingHorizontal;
	}

	private void detectContainerBorders() {
		DOM.setStyleAttribute(contentNode, "overflow", "hidden");

		borderPaddingHorizontal = Util.measureHorizontalBorder(contentNode);
		borderPaddingVertical = Util.measureVerticalBorder(contentNode);

		DOM.setStyleAttribute(contentNode, "overflow", "auto");

		captionPaddingHorizontal = Util.measureHorizontalPaddingAndBorder(
				captionNode, 26);

		captionMarginLeft = Util.measureMarginLeft(captionNode);
		contentMarginLeft = Util.measureMarginLeft(contentNode);

	}

	public boolean hasChildComponent(Widget component) {
		if (component != null && component == layout) {
			return true;
		} else {
			return false;
		}
	}

	public void replaceChildComponent(Widget oldComponent, Widget newComponent) {
		// TODO This is untested as no layouts require this
		if (oldComponent != layout) {
			return;
		}

		// FadeTransition ft = new FadeTransition(this, (Paintable)
		// oldComponent,
		// (Paintable) newComponent);
		// ft.run(2000);

		setWidget(newComponent);
		layout = (Paintable) newComponent;
	}

	public RenderSpace getAllocatedSpace(Widget child) {
		int w = 0;
		int h = 0;

		if (width != null && !width.equals("")) {
			w = getOffsetWidth() - getContainerBorderWidth();
			if (w < 0) {
				w = 0;
			}
		}

		if (height != null && !height.equals("")) {
			h = contentNode.getOffsetHeight() - getContainerBorderHeight();
			if (h < 0) {
				h = 0;
			}
		}

		return new RenderSpace(w, h, true);
	}

	public boolean requestLayout(Set<Paintable> child) {
		// content size change might cause change to its available space
		// (scrollbars)
		client.handleComponentRelativeSize((Widget) layout);
		if (height != null && height != "" && width != null && width != "") {
			/*
			 * If the height and width has been specified the child components
			 * cannot make the size of the layout change
			 */
			return true;
		}
		runHacks(false);
		return !renderInformation.updateSize(getElement());
	}

	public void updateCaption(Paintable component, UIDL uidl) {
		// NOP: layouts caption, errors etc not rendered in Panel
	}

	@Override
	protected void onAttach() {
		super.onAttach();
		detectContainerBorders();
	}

	public ShortcutActionHandler getShortcutActionHandler() {
		return shortcutHandler;
	}

	private static enum TransitionType {
		FADE_OUT_IN, FADE_OUT, FADE_IN, SLIDE_UP, SLIDE_DOWN, NONE;

		public Transition getInstance(VTransitionPanel parent, Paintable from,
				Paintable to) {
			switch (this) {
			case FADE_OUT_IN:
				return new FadeOutInTransition(parent, from, to);
			case FADE_OUT:
				return new FadeOutTransition(parent, from, to);
			case FADE_IN:
				return new FadeOutInTransition(parent, null, to);
			case SLIDE_UP:
				return new SlideUpTransition(parent, from, to);
			case SLIDE_DOWN:
				return new SlideDownTransition(parent, from, to);
			case NONE:
			default:
				return null;
			}
		}
	}

	protected enum Interpolation {
		LINEAR, COS, EXPONENTIAL, CIRCULAR, BOUNCE, ELASTIC, QUAD, CUBIC, QUART, QUINT;
	}
}
