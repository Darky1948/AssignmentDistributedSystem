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

import org.apache.log4j.Logger;

import ki.types.ds.StreamInfo;
import se.umu.cs._5dv186.a1.client.DefaultStreamServiceClient;
import se.umu.cs._5dv186.a1.client.StreamServiceClient;
import se.umu.cs._5dv186.a1.client.StreamServiceDiscovery;

/**
 * @author Kristen Viguier ens17kvr
 */
public class Main {
	
	/** Handle stack output */
    private static final Logger LOG = Logger.getLogger(Main.class);
    
	public static final int DEFAULT_TIMEOUT = 1000;

	/**
	 * Main function. Given parameters allow to define which host which timeout with
	 * the given username.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
//		final String host = (args.length > 0) ? args[0] : "localhost";
		final int timeout = (args.length > 1) ? Integer.parseInt(args[1]) : DEFAULT_TIMEOUT;
		final String username = (args.length > 2) ? args[2] : "test";

		
		
		// We define the number of Threads that we are going to use. 1 is for sequential
		// high is for parallel.
		Integer[] threadsNumber = {1, 2, 4, 6, 8, 16, 32, 64, 128, 256 };
		
		String[] hosts = StreamServiceDiscovery.SINGLETON.findHosts(); 
		
		for (String host : hosts) {
			// Only one host to tests
			LOG.info("Test on the host " + host + " with " + timeout + " timeout for the user " + username);
		
			for (Integer tn : threadsNumber) {
				LOG.info("Number of threads use to fetch data : " + tn);
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

	}

	/**
	 * In this function we are going to fetches stream.
	 * 
	 * @param clients
	 * @param timeout
	 */
	private static void callHost(List<StreamServiceClient> clients, int timeout) {

		try {
			FrameAccessorFactoryImpl frameAccessorFactoryImpl = new FrameAccessorFactoryImpl();
			FrameAccessorImpl frameAccessorImpl = (FrameAccessorImpl) frameAccessorFactoryImpl
					.getFrameAccessor(listStreamServiceClientToArray(clients), "stream10");
			
			// Fetching the StreamInfo
			StreamInfo streamInfo = frameAccessorImpl.getStreamInfo();

			// Fecthing the frame
			long t1 = System.currentTimeMillis();
			for (int i = 0; i < streamInfo.getLengthInFrames(); i++) {
				LOG.trace("Frame :" + (i + 1) + "/" + streamInfo.getLengthInFrames());
				frameAccessorImpl.getFrame(i);
			}
			long t2 = System.currentTimeMillis();
			
			double totalTime = (t2 - t1) * 1.00 / 1000;
			
			LOG.info("(UDP) packet drop rate (per service) : " + frameAccessorImpl.getPerformanceStatisticImpl().getPacketDropRate(""));
            LOG.info("(average) packet latency (per service) : " + frameAccessorImpl.getPerformanceStatisticImpl().getPacketLatency(""));
            LOG.info("(average) frame throughput : " + frameAccessorImpl.getPerformanceStatistics().getFrameThroughput());
            // bandwith utilization for a host
            LOG.info("bandwidth utilization (total network footprint) : " + frameAccessorImpl.getPerformanceStatistics().getLinkBandwidth("") + " bps");
			LOG.info("Total amount of time : " +  totalTime + " s");
			
			StringBuilder resultat = new StringBuilder();
			resultat.append(frameAccessorImpl.getPerformanceStatisticImpl().getPacketDropRate("")).append(";");
			resultat.append(frameAccessorImpl.getPerformanceStatisticImpl().getPacketLatency("")).append(";");
			resultat.append(frameAccessorImpl.getPerformanceStatistics().getFrameThroughput()).append(";");
			resultat.append(frameAccessorImpl.getPerformanceStatistics().getLinkBandwidth("")).append(";");
			resultat.append(totalTime).append(";");
			
			LOG.info(resultat.toString());

		} catch (SocketTimeoutException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * list.toArray don't work so need to do that.
	 */
	private static StreamServiceClient[] listStreamServiceClientToArray(List<StreamServiceClient> clients) {
		StreamServiceClient[] serviceClients = new StreamServiceClient[clients.size()];
		
		for (int i = 0; i < serviceClients.length; i++) {
			serviceClients[i]= clients.get(i);
		}
		return serviceClients;
	}
	
}
