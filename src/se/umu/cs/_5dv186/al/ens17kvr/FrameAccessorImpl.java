package se.umu.cs._5dv186.al.ens17kvr;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import ki.types.ds.Block;
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
		
		// Fetch again the streamInfo
		List<Block> fetchedBlocks = new ArrayList<>();
		
		for (StreamServiceClient client : clients) {
			
			long t1 = System.currentTimeMillis();
			Frame frame = new FrameImpl(client, stream, i);
			
			//for (int x = 0; x < streamInfo.getHeightInBlocks(); x++) {
			for (int x = 0; x < 1; x++) {
				//for (int y = 0; y < streamInfo.getWidthInBlocks(); y++) {
				for (int y = 0; y < 10; y++) {
					long latency1 = System.currentTimeMillis();
					try {
						fetchedBlocks.add(frame.getBlock(x, y));
						performanceStatisticImpl.incrementPackageReceived();
						System.out.println("The block was received successfully !");
					}catch (IOException e) {
						// Here it means we have a dropped packet
						System.out.println("A drop packed happened !");
						performanceStatisticImpl.incrementPackageDropped();
						// TODO how to retrieved the lost pack?
					}
					long latency2 = System.currentTimeMillis();
					
					performanceStatisticImpl.computeTotalLatency(latency2 - latency1);
					
					System.out.println("block : " + x + " " + y);
				}
			}
			
			long t2 = System.currentTimeMillis();
			
			performanceStatisticImpl.computeTotalTime(t2 - t1);
			performanceStatisticImpl.incrementFrameNb();
		}
		
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
