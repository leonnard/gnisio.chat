package net.gnisio.example.chat.client;

import net.gnisio.shared.SocketIOServiceAsync;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface GreetingServiceAsync extends SocketIOServiceAsync {
	
	void greetServer(String input, AsyncCallback<String> callback)
			throws IllegalArgumentException;
}
