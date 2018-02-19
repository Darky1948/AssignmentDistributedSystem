package se.umu.cs._5dv186.al.ens17kvr;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

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
	private PerformanceStatisticImpl performanceStatisticImpl;

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
		this.performanceStatisticImpl = new PerformanceStatisticImpl();
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
		this.performanceStatisticImpl = new PerformanceStatisticImpl();
	}

	@Override
	public Frame getFrame(int i) throws IOException, SocketTimeoutException {
		
		// Fetch again the streamInfo
		StreamInfo streamInfo = getStreamInfo();
		
		FrameImpl frameImpl = new FrameImpl();
		
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
