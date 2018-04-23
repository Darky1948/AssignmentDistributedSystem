package se.umu.cs._5dv186.al.ens17kvr;

import java.io.IOException;
import java.util.List;

import ki.types.ds.Block;
import se.umu.cs._5dv186.a1.client.StreamServiceClient;
import se.umu.cs._5dv186.a1.client.FrameAccessor.Frame;

/**
 * Frame class represents a set of blocks.
 * 
 * @author Kristen Viguier ens17kvr
 *
 */
public class FrameImpl implements Frame {

	/**
	 * A frame is a set of ordered set of blocks.
	 */
	List<List<Block>> blocks;

	/**
	 * The service client.
	 */
	StreamServiceClient serviceClient;

	/**
	 * The stream name.
	 */
	String streamName;

	/**
	 * The frame position.
	 */
	int frame;

	public FrameImpl(StreamServiceClient serviceClient, String streamName, int frame) {
		this.serviceClient = serviceClient;
		this.streamName = streamName;
		this.frame = frame;
	}

	@Override
	public Block getBlock(int x, int y) throws IOException {
		return this.serviceClient.getBlock(streamName, frame, x, y);
	}

}
