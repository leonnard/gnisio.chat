package net.gnisio.example.chat.client;

import net.gnisio.client.event.SIOConnectedEvent;
import net.gnisio.client.event.SIOConnectedEvent.SIOConnectedHandler;
import net.gnisio.client.event.SIOConnectionFailedEvent;
import net.gnisio.client.event.SIOConnectionFailedEvent.SIOConnectionFailedHandler;
import net.gnisio.client.event.SIODisconnectEvent;
import net.gnisio.client.event.SIODisconnectEvent.SIODisconnectHandler;
import net.gnisio.client.event.SIOReconnectFailedEvent;
import net.gnisio.client.event.SIOReconnectFailedEvent.SIOReconnectFailedHandler;
import net.gnisio.client.event.SIOReconnectedEvent;
import net.gnisio.client.event.SIOReconnectedEvent.SIOReconnectedHandler;
import net.gnisio.client.event.SIOReconnectingEvent;
import net.gnisio.client.event.SIOReconnectingEvent.SIOReconnectingHandler;
import net.gnisio.client.wrapper.SocketIOClient;
import net.gnisio.example.chat.shared.FieldVerifier;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Gnisio_example_chat implements EntryPoint, SIOConnectedHandler,
		SIODisconnectHandler, SIOReconnectedHandler, SIOReconnectingHandler, SIOConnectionFailedHandler,
		SIOReconnectFailedHandler  {
	/**
	 * The message displayed to the user when the server cannot be reached or
	 * returns an error.
	 */
	private static final String SERVER_ERROR = "An error occurred while "
			+ "attempting to contact the server. Please check your network "
			+ "connection and try again.";

	/**
	 * Create a remote service proxy to talk to the server-side Greeting service.
	 */
	private final GreetingServiceAsync greetingService = GWT
			.create(GreetingService.class);

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		final Button sendButton = new Button("Send");
		final TextBox nameField = new TextBox();
		nameField.setText("GWT User");
		final Label errorLabel = new Label();

		// We can add style names to widgets
		sendButton.addStyleName("sendButton");

		// Add the nameField and sendButton to the RootPanel
		// Use RootPanel.get() to get the entire body element
		RootPanel.get("nameFieldContainer").add(nameField);
		RootPanel.get("sendButtonContainer").add(sendButton);
		RootPanel.get("errorLabelContainer").add(errorLabel);

		// Focus the cursor on the name field when the app loads
		nameField.setFocus(true);
		nameField.selectAll();

		// Create the popup dialog box
		final DialogBox dialogBox = new DialogBox();
		dialogBox.setText("Remote Procedure Call");
		dialogBox.setAnimationEnabled(true);
		final Button closeButton = new Button("Close");
		// We can set the id of a widget by accessing its Element
		closeButton.getElement().setId("closeButton");
		final Label textToServerLabel = new Label();
		final HTML serverResponseLabel = new HTML();
		VerticalPanel dialogVPanel = new VerticalPanel();
		dialogVPanel.addStyleName("dialogVPanel");
		dialogVPanel.add(new HTML("<b>Sending name to the server:</b>"));
		dialogVPanel.add(textToServerLabel);
		dialogVPanel.add(new HTML("<br><b>Server replies:</b>"));
		dialogVPanel.add(serverResponseLabel);
		dialogVPanel.setHorizontalAlignment(VerticalPanel.ALIGN_RIGHT);
		dialogVPanel.add(closeButton);
		dialogBox.setWidget(dialogVPanel);

		// Add a handler to close the DialogBox
		closeButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				dialogBox.hide();
				sendButton.setEnabled(true);
				sendButton.setFocus(true);
			}
		});

		// Create a handler for the sendButton and nameField
		class MyHandler implements ClickHandler, KeyUpHandler {
			/**
			 * Fired when the user clicks on the sendButton.
			 */
			public void onClick(ClickEvent event) {
				sendNameToServer();
			}

			/**
			 * Fired when the user types in the nameField.
			 */
			public void onKeyUp(KeyUpEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					sendNameToServer();
				}
			}

			/**
			 * Send the name from the nameField to the server and wait for a response.
			 */
			private void sendNameToServer() {
				// First, we validate the input.
				errorLabel.setText("");
				String textToServer = nameField.getText();
				if (!FieldVerifier.isValidName(textToServer)) {
					errorLabel.setText("Please enter at least four characters");
					return;
				}

				// Then, we send the input to the server.
				sendButton.setEnabled(false);
				textToServerLabel.setText(textToServer);
				serverResponseLabel.setText("");
				
				greetingService.greetServer(textToServer,
						new AsyncCallback<String>() {
							public void onFailure(Throwable caught) {
								// Show the RPC error message to the user
								dialogBox
										.setText("Remote Procedure Call - Failure");
								serverResponseLabel
										.addStyleName("serverResponseLabelError");
								serverResponseLabel.setHTML(SERVER_ERROR);
								dialogBox.center();
								closeButton.setFocus(true);
							}

							public void onSuccess(String result) {
								dialogBox.setText("Remote Procedure Call");
								serverResponseLabel
										.removeStyleName("serverResponseLabelError");
								serverResponseLabel.setHTML(result);
								dialogBox.center();
								closeButton.setFocus(true);
							}
						});
			}
		}

		greetingService.onMessage(null, null, new AsyncCallback<String>() {
			@Override
			public void onFailure(Throwable caught) {
			}

			@Override
			public void onSuccess(String result) {
				dialogBox.setText("PUSHED message");
				serverResponseLabel
						.removeStyleName("serverResponseLabelError");
				serverResponseLabel.setHTML(result);
				dialogBox.center();
				closeButton.setFocus(true);
			}
		});
		
		// Add listener for connection
		SocketIOClient.getInstance().addSIOConnectedHandler(this);
		SocketIOClient.getInstance().addSIODisconnectHandler(this);
		SocketIOClient.getInstance().addSIOReconnectedHandler(this);
		SocketIOClient.getInstance().addSIOReconnectingHandler(this);
		SocketIOClient.getInstance().addSIOConnectionFailedHandler(this);
		SocketIOClient.getInstance().addSIOReconnectFailedHandler(this);
		
		// Add a handler to send the name to the server
		MyHandler handler = new MyHandler();
		sendButton.addClickHandler(handler);
		nameField.addKeyUpHandler(handler);
	}

	@Override
	public void onSIOReconnectFailed(SIOReconnectFailedEvent event) {
		GWT.log("Reconnection failed: "+event);
	}

	@Override
	public void onSIOConnectionFailed(SIOConnectionFailedEvent event) {
		GWT.log("Connection failed: "+event);
	}

	@Override
	public void onSIOReconnecting(SIOReconnectingEvent event) {
		GWT.log("Reconnecting...: "+event);
	}

	@Override
	public void onSIOReconnected(SIOReconnectedEvent event) {
		GWT.log("Reconnected: "+event);
	}

	@Override
	public void onSIODisconnect(SIODisconnectEvent event) {
		GWT.log("Disconnected: "+event);
	}

	@Override
	public void onSIOConnected(SIOConnectedEvent event) {
		GWT.log("Connected: "+event);
	}
}
