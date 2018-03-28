/**
 * 
 */
package se.umu.cs._5dv186.al.ens17kvr;

import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import ki.types.ds.StreamInfo;
import se.umu.cs._5dv186.a1.client.DefaultStreamServiceClient;
import se.umu.cs._5dv186.a1.client.StreamServiceClient;

/**
 * @author Kristen Viguier ens17kvr
 */
public class Main {

	public static final int DEFAULT_TIMEOUT = 1000;

	/**
	 * Main function. Given parameters allow to define which host which timeout with
	 * the given username.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		final String host = (args.length > 0) ? args[0] : "localhost";
		final int timeout = (args.length > 1) ? Integer.parseInt(args[1]) : DEFAULT_TIMEOUT;
		final String username = (args.length > 2) ? args[2] : "test";

		// Only one host to tests
		System.out.println("Test on the host " + host + " with " + timeout + " timeout for the user " + username);

		// We define the number of Threads that we are going to use. 1 is for sequential
		// high is for parallel.
		Integer[] threadsNumber = { 1, 2, 4, 8, 16, 32, 64, 128, 256 };

		for (Integer tn : threadsNumber) {
			
			List<StreamServiceClient> clients = new ArrayList<>();
			
			try {
				
				for (int i = 0; i < tn; i++) {
					StreamServiceClient client = DefaultStreamServiceClient.bind(host, timeout, username);
					clients.add(client);
				}
				
				callHost(clients, timeout);
				
			} catch (SocketException | UnknownHostException e) {
				e.printStackTrace();
			}
			
		}

	}

	/**
	 * In this function we are going to fetches stream.
	 * @param clients
	 * @param timeout
	 */
	private static void callHost(List<StreamServiceClient> clients, int timeout) {
		
		try {
			FrameAccessorFactoryImpl frameAccessorFactoryImpl = new FrameAccessorFactoryImpl();
			FrameAccessorImpl frameAccessorImpl = (FrameAccessorImpl) frameAccessorFactoryImpl.getFrameAccessor((StreamServiceClient[]) clients.toArray(), "stream10");
			
			// Fetching the StreamInfo
			StreamInfo streamInfo = frameAccessorImpl.getStreamInfo();
			
			// Fecthing the frame
			for (int i = 0; i < streamInfo.getLengthInFrames(); i++) {
				frameAccessorImpl.getFrame(i);
			}
			
		} catch (SocketTimeoutException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}

}
