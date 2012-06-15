package net.gnisio.example.chat.server;

import net.gnisio.example.chat.client.GreetingService;
import net.gnisio.example.chat.shared.FieldVerifier;
import net.gnisio.server.AbstractRemoteService;

/**
 * The server side implementation of the RPC service.
 */
public class GreetingServiceImpl extends AbstractRemoteService implements
		GreetingService {

	public GreetingServiceImpl(String gwtAppLocation) {
		super(gwtAppLocation);
	}

	public String greetServer(String input) throws IllegalArgumentException {
		// Verify that the input is valid. 
		if (!FieldVerifier.isValidName(input)) {
			// If the input is not valid, throw an IllegalArgumentException back to
			// the client.
			throw new IllegalArgumentException(
					"Name must be at least 4 characters long");
		}

		// Publish message for allconnected users
		onMessage(input, "/");
		
		return "Hello, " + input + "!<br><br>I am running ";
	}

	@Override
	public void onClientConnected() {
		addSubscriber("/");
	}

	@Override
	public void onClientDisconnected() {
		removeSubscriber();
	}

	@Override
	public String onMessage(String message, String node) {
		publishMessage( getMethod("onMessage", String.class), message, node);
		return null;
	}
}
