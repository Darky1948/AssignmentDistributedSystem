package se.umu.cs._5dv186.al.ens17kvr;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import ki.types.ds.Block;
import se.umu.cs._5dv186.a1.client.FrameAccessor.Frame;
import se.umu.cs._5dv186.a1.client.StreamServiceClient;

public class BlockThread extends Thread {
	
	/**
	 * The current frame.
	 */
	private Frame frame;
	
	/**
	 * Performance statistic.
	 */
	private PerformanceStatisticImpl performanceStatisticImpl;
	
	/**
	 * The service client.
	 */
	private StreamServiceClient client;
	
	/**
	 * Fetch again the streamInfo 
	 */
	List<Block> fetchedBlocks = Collections.synchronizedList(new ArrayList<>());
	
	/**
	 * Will store the dropped blocks
	 */
	Queue<Block> droppedBlocks = new ConcurrentLinkedQueue<Block>();
	
	
	/**
	 * Constructor with these given parameters.
	 * @param frame
	 * @param client
	 * @param performanceStatisticImpl
	 */
	public BlockThread(Frame frame, PerformanceStatisticImpl performanceStatisticImpl, StreamServiceClient client) {
		this.frame = frame;
		this.performanceStatisticImpl = performanceStatisticImpl;
		this.client = client;
	}

	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	public void run() {
		long latency1 = System.currentTimeMillis();
		fetchBlock(0, 0);
		long latency2 = System.currentTimeMillis();
		
		performanceStatisticImpl.computeTotalLatency(latency2 - latency1);
		
		
		// Handle the dropped packet
		// TODO add time safety to avoid infinte loops
		Block dropped = null;
		
		while((dropped = droppedBlocks.poll()) != null) {
			latency1 = System.currentTimeMillis();
			fetchBlock(0, 0);
			latency2 = System.currentTimeMillis();
			
			performanceStatisticImpl.computeTotalLatency(latency2 - latency1);
		}
		
		
	}
	
	
	/**
	 * Function fetch all the needed blocks according the parameters.
	 * @param x
	 * @param y
	 */
	private void fetchBlock(int x, int y) {
		try {
			fetchedBlocks.add(frame.getBlock(x, y));
			performanceStatisticImpl.incrementPackageReceived();
			System.out.println("The block was received successfully !");
		}catch (IOException e) {
			// Here it means we have a dropped packet
			System.out.println("A drop packed happened !");
			performanceStatisticImpl.incrementPackageDropped();
			droppedBlocks.add(e) // TODO bloqué :/
		}
	}

	/**
	 * @return the frame
	 */
	public Frame getFrame() {
		return frame;
	}

	/**
	 * @param frame the frame to set
	 */
	public void setFrame(Frame frame) {
		this.frame = frame;
	}

	/**
	 * @return the client
	 */
	public StreamServiceClient getClient() {
		return client;
	}

	/**
	 * @param client the client to set
	 */
	public void setClient(StreamServiceClient client) {
		this.client = client;
	}
	
	
}
