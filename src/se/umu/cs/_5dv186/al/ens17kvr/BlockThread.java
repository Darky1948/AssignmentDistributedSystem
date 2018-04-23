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
	Queue<BlockXY> droppedBlocks = new ConcurrentLinkedQueue<BlockXY>();

	/**
	 * To know which block we want to fecth.
	 */
	private BlockXY blockXY;
	
	
	/**
	 * Constructor with these given parameters.
	 * @param frame
	 * @param client
	 * @param performanceStatisticImpl
	 * @param block 
	 */
	public BlockThread(Frame frame, PerformanceStatisticImpl performanceStatisticImpl, StreamServiceClient client, BlockXY blockXY) {
		this.frame = frame;
		this.performanceStatisticImpl = performanceStatisticImpl;
		this.client = client;
		this.blockXY = blockXY;
	}

	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	public void run() {
		long latency1 = System.currentTimeMillis();
		fetchBlock(blockXY);
		long latency2 = System.currentTimeMillis();
		
		performanceStatisticImpl.computeTotalLatency(latency2 - latency1);
		
		BlockXY dropped = null;

		while((dropped = droppedBlocks.poll()) != null) {
			
			latency1 = System.currentTimeMillis();
			fetchBlock(blockXY);
			latency2 = System.currentTimeMillis();
			
			performanceStatisticImpl.computeTotalLatency(latency2 - latency1);
		}
		
	}
	
	
	/**
	 * Function fetch all the needed blocks according the parameters.
	 * @param blockXY
	 */
	private void fetchBlock(BlockXY blockXY) {
		try {
			fetchedBlocks.add(frame.getBlock(blockXY.getX(), blockXY.getY()));
			performanceStatisticImpl.incrementPackageReceived();
			System.out.println("The block was received successfully !");
		}catch (IOException e) {
			// Here it means we have a dropped packet
			System.out.println("A drop packed happened !");
			performanceStatisticImpl.incrementPackageDropped();
			blockXY.incrementCounterTries();
			if(blockXY.getCounterTries() < BlockXY.NUMBER_OF_TRIES) {
				droppedBlocks.add(blockXY);
			} else {
				// Handling infinity loop for packets that drop every time
				System.err.println("The block : " + blockXY.getX() + "-" + blockXY.getY() + " can't be reached.");
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
	 * @return the blockXY
	 */
	public BlockXY getBlockXY() {
		return blockXY;
	}

	/**
	 * @param blockXY the blockXY to set
	 */
	public void setBlockXY(BlockXY blockXY) {
		this.blockXY = blockXY;
	}
	
}
