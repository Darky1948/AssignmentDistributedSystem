package se.umu.cs._5dv186.al.ens17kvr;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.List;

import ki.types.ds.Block;
import ki.types.ds.StreamInfo;
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
		// TODO comment gérer le socketTimeoutException
		/*
		 * java.net.SocketTimeoutException: Receive timed out at
		 * java.net.DualStackPlainDatagramSocketImpl.socketReceiveOrPeekData(Native
		 * Method) at java.net.DualStackPlainDatagramSocketImpl.receive0(Unknown Source)
		 * at java.net.AbstractPlainDatagramSocketImpl.receive(Unknown Source) at
		 * java.net.DatagramSocket.receive(Unknown Source) at
		 * se.umu.cs._5dv186.a1.Transceiver.receive(Transceiver.java:62) at
		 * se.umu.cs._5dv186.a1.client.DefaultStreamServiceClient.getBlock(
		 * DefaultStreamServiceClient.java:78) at
		 * se.umu.cs._5dv186.al.ens17kvr.FrameImpl.getBlock(FrameImpl.java:49) at
		 * se.umu.cs._5dv186.al.ens17kvr.FrameAccessorImpl.getFrame(FrameAccessorImpl.
		 * java:76) at se.umu.cs._5dv186.al.ens17kvr.Main.callHost(Main.java:87) at
		 * se.umu.cs._5dv186.al.ens17kvr.Main.main(Main.java:53)
		 */
		return this.serviceClient.getBlock(streamName, frame, x, y);
	}

}
