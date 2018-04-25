package se.umu.cs._5dv186.al.ens17kvr;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Queue;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import ki.types.ds.StreamInfo;
import se.umu.cs._5dv186.a1.client.FrameAccessor;
import se.umu.cs._5dv186.a1.client.StreamServiceClient;

/**
 * @author Kristen Viguier ens17kvr
 *
 */
public class FrameAccessorImpl implements FrameAccessor {
	
	/**
	 * The name of the stream.
	 */
	private String stream;

	/**
	 * The list of clients service.
	 */
	private ArrayList<StreamServiceClient> clients;

	/**
	 * Our performance statistic object.
	 */
	private PerformanceStatisticImpl performanceStatisticImpl = new PerformanceStatisticImpl();
	
	/**
	 * Constructor for one host.
	 * 
	 * @param client
	 * @param stream
	 */
	public FrameAccessorImpl(StreamServiceClient client, String stream) {
		this.clients = new ArrayList<>();
		this.clients.add(client);
		this.stream = stream;
	}

	/**
	 * Constructor for 4 hosts.
	 * 
	 * @param client
	 * @param stream
	 */
	public FrameAccessorImpl(StreamServiceClient[] client, String stream) {
		this.clients = new ArrayList<>();

		for (StreamServiceClient streamServiceClient : client) {
			this.clients.add(streamServiceClient);
		}
		this.stream = stream;
	}

	@Override
	public Frame getFrame(int i) {
		
		// Init the Queue to work with.
		Queue<BlockXY> blockXYs = new ConcurrentLinkedQueue<BlockXY>();
		
		//for (int x = 0; x < streamInfo.getHeightInBlocks(); x++) {
		for (int x = 0; x < 5; x++) {
			//for (int y = 0; y < streamInfo.getWidthInBlocks(); y++) {
			for (int y = 0; y < 10; y++) {
				blockXYs.add(new BlockXY(x, y));
			}
		}
		
		List<Thread> threads = new ArrayList<>();
		
		// Compute the amount of time to fetch all blocks that made the frame at the ith index.
		long t1 = System.currentTimeMillis();
		
		for (StreamServiceClient client : clients) {
			Frame frame = new FrameImpl(client, stream, i);
			
			Thread blockxyRetrievement = new BlockThread(frame, performanceStatisticImpl, client, blockXYs);
			blockxyRetrievement.start();
			threads.add(blockxyRetrievement); // To know when thread has completed its task
		}
		
		for(Thread thread : threads) {
			try {
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		long t2 = System.currentTimeMillis();
		
		performanceStatisticImpl.computeTotalTime(t2 - t1);
		performanceStatisticImpl.incrementFrameNb();
		
		// TODO faire un nouveau perf statistic pour chacun des threads
		
		return null;
	}

	@Override
	public PerformanceStatistics getPerformanceStatistics() {
		return performanceStatisticImpl;
	}

	@Override
	public StreamInfo getStreamInfo() throws IOException, SocketTimeoutException {
		for (StreamServiceClient client : clients) {
			StreamInfo[] streams = client.listStreams();

			for (StreamInfo streamInfo : streams) {
				if (streamInfo.getName().equals(this.stream)) {
					return streamInfo;
				}
			}
		}

		return null;
	}

	/**
	 * @return the stream
	 */
	public String getStream() {
		return stream;
	}

	/**
	 * @param stream
	 *            the stream to set
	 */
	public void setStream(String stream) {
		this.stream = stream;
	}

	/**
	 * @return the clients
	 */
	public ArrayList<StreamServiceClient> getClients() {
		return clients;
	}

	/**
	 * @param clients
	 *            the clients to set
	 */
	public void setClients(ArrayList<StreamServiceClient> clients) {
		this.clients = clients;
	}

	/**
	 * @return the performanceStatisticImpl
	 */
	public PerformanceStatisticImpl getPerformanceStatisticImpl() {
		return performanceStatisticImpl;
	}

	/**
	 * @param performanceStatisticImpl
	 *            the performanceStatisticImpl to set
	 */
	public void setPerformanceStatisticImpl(PerformanceStatisticImpl performanceStatisticImpl) {
		this.performanceStatisticImpl = performanceStatisticImpl;
	}

}
