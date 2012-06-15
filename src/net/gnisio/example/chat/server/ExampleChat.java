package net.gnisio.example.chat.server;

import net.gnisio.server.AbstractGnisioServer;
import net.gnisio.server.AbstractRemoteService;
import net.gnisio.server.SessionsStorage;
import net.gnisio.server.clients.ClientsStorage;
import net.gnisio.server.processors.RequestProcessorsCollection;
import net.gnisio.server.processors.StaticContentProcessor;

public class ExampleChat extends AbstractGnisioServer {

	public static void main(String[] args) {
		try {
			new ExampleChat().start(3001);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void createRequestProcessors(
			RequestProcessorsCollection requestProcessors) {
		requestProcessors.addProcessor(new StaticContentProcessor("./war"), "/*");
	}

	@Override
	protected AbstractRemoteService createRemoteService(
			SessionsStorage sessionsStorage, ClientsStorage clientsStorage) {
		return new GreetingServiceImpl("./war");
	}

}
