package net.gnisio.example.chat.client;

import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import net.gnisio.client.wrapper.PushClass;
import net.gnisio.shared.PushEventType;
import net.gnisio.shared.SocketIOService;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("socket.io")
public interface GreetingService extends SocketIOService {
	
	public enum PushEvent implements PushEventType {
		@PushClass(String.class)  TEST_EVENT,
		@PushClass(Integer.class) TEST_EVENT_1
	}
	
	String greetServer(String name) throws IllegalArgumentException;
}
