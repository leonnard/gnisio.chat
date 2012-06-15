package net.gnisio.example.chat.client;

import net.gnisio.client.wrapper.Push;
import net.gnisio.shared.SocketIOService;

import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("greet")
public interface GreetingService extends SocketIOService {
	String greetServer(String name) throws IllegalArgumentException;
	
	@Push String onMessage(String message, String node);
}
