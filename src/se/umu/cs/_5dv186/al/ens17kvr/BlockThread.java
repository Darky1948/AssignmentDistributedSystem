package se.umu.cs._5dv186.al.ens17kvr;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.log4j.Logger;

import ki.types.ds.Block;
import se.umu.cs._5dv186.a1.client.FrameAccessor.Frame;
import se.umu.cs._5dv186.a1.client.StreamServiceClient;

public class BlockThread extends Thread {
	
	/** Handle stack output */
    private static final Logger LOG = Logger.getLogger(BlockThread.class);
    
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
	Queue<BlockXY> droppedBlocks = new ConcurrentLinkedQueue<BlockXY>();

	/**
	 * Contains the Queue of block to work with (frame).
	 */
	private Queue<BlockXY> blockXYs;
	
	
	/**
	 * Constructor with these given parameters.
	 * @param frame
	 * @param client
	 * @param performanceStatisticImpl
	 * @param block 
	 */
	public BlockThread(Frame frame, PerformanceStatisticImpl performanceStatisticImpl, StreamServiceClient client, Queue<BlockXY> blockXYs) {
		this.frame = frame;
		this.performanceStatisticImpl = performanceStatisticImpl;
		this.client = client;
		this.blockXYs = blockXYs;
	}

	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	public void run() {

		while(blockXYs.poll() != null) {
			// Compute the time to fetch one block
			long latency1 = System.currentTimeMillis();
			fetchBlock(blockXYs.poll());
			long latency2 = System.currentTimeMillis();
			performanceStatisticImpl.computeTotalLatency(latency2 - latency1);
		}
		
		// Try to fetch the dropped blocks.
		while(droppedBlocks.poll() != null) {
			// Compute the time to fetch one dropped block
			long latency1 = System.currentTimeMillis();
			fetchBlock(droppedBlocks.poll());
			long latency2 = System.currentTimeMillis();
			
			performanceStatisticImpl.computeTotalLatency(latency2 - latency1);
		}
	}
	
	
	/**
	 * Function fetch all the needed blocks according the parameters.
	 * @param blockXY
	 */
	private void fetchBlock(BlockXY blockXY) {
		try {
			if(blockXY != null) {
				fetchedBlocks.add(frame.getBlock(blockXY.getX(), blockXY.getY()));
				performanceStatisticImpl.incrementPackageReceived();
				LOG.trace("The block was received successfully ! (PID = " +  this.getId() + ")");
			}
		}catch (IOException e) {
			// Here it means we have a dropped packet
			LOG.trace("A drop packed happened !");
			
			performanceStatisticImpl.incrementPackageDropped();
			blockXY.incrementCounterTries();
			
			if(blockXY.getCounterTries() < BlockXY.NUMBER_OF_TRIES) {
				droppedBlocks.add(blockXY);
			} else {
				// Handling infinity loop for packets that drop every time
				LOG.trace("The block : " + blockXY.getX() + "-" + blockXY.getY() + " can't be reached. (PID = " +  this.getId() + ")");
			}
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

	/**
	 * @return the blockXYs
	 */
	public Queue<BlockXY> getBlockXYs() {
		return blockXYs;
	}

	/**
	 * @param blockXYs the blockXYs to set
	 */
	public void setBlockXYs(Queue<BlockXY> blockXYs) {
		this.blockXYs = blockXYs;
	}

}
