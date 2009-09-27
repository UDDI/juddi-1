package org.apache.juddi.portlets.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.Widget;

public class MenuBarPanel extends FlowPanel implements ClickListener {

	public static final String PUBLISHER = "publisher";
	public static final String SUBSCRIPTION = "subscription";
	public static final Images images = (Images) GWT.create(Images.class);
	PushButton saveButton = new PushButton();
	PushButton newButton = new PushButton();
	PushButton deleteButton = new PushButton();
	String context = PUBLISHER;

	public MenuBarPanel(String context) {
		
		this.context = context;
		HorizontalPanel pushPanel = new HorizontalPanel();
	    pushPanel.setSpacing(7);

		newButton.setHTML(images.create().getHTML());
		newButton.setStyleName(("portlet-form-button"));
		newButton.addClickListener(this);
		pushPanel.add(newButton);
		
		saveButton.setHTML(images.save().getHTML());
		saveButton.setStyleName(("portlet-form-button"));
		saveButton.addClickListener(this);
		pushPanel.add(saveButton);
		
		deleteButton.setHTML(images.delete().getHTML());
		deleteButton.setStyleName(("portlet-form-button"));
		deleteButton.addClickListener(this);
		pushPanel.add(deleteButton);
		
		add(pushPanel);
		
	}

	public void onClick(Widget sender) {
		if (sender == saveButton) {
			if (PUBLISHER.equals(context)) JUDDIPublisher.getInstance().savePublisher();
			else if (SUBSCRIPTION.equals(context)) UDDISubscription.getInstance().saveSubscription();
		} else if (sender == newButton) {
			if (PUBLISHER.equals(context)) JUDDIPublisher.getInstance().newPublisher();
			else if (SUBSCRIPTION.equals(context)) UDDISubscription.getInstance().newSubscription();
		} else if (sender == deleteButton) {
			if (PUBLISHER.equals(context)) JUDDIPublisher.getInstance().deletePublisher();
			else if (SUBSCRIPTION.equals(context)) UDDISubscription.getInstance().deleteSubscription();
		}
		
	}
}