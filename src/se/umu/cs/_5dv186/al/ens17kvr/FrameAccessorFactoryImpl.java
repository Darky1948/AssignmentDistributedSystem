package se.umu.cs._5dv186.al.ens17kvr;

import se.umu.cs._5dv186.a1.client.FrameAccessor;
import se.umu.cs._5dv186.a1.client.FrameAccessor.Factory;
import se.umu.cs._5dv186.a1.client.StreamServiceClient;

/**
 * @author Kristen Viguier ens17kvr
 *
 */
public class FrameAccessorFactoryImpl implements Factory {

	@Override
	public FrameAccessor getFrameAccessor(StreamServiceClient arg0, String arg1) {
		FrameAccessorImpl frameAccessorImpl = new FrameAccessorImpl(arg0, arg1);
		return frameAccessorImpl;
	}

	@Override
	public FrameAccessor getFrameAccessor(StreamServiceClient[] arg0, String arg1) {
		FrameAccessorImpl frameAccessorImpl = new FrameAccessorImpl(arg0, arg1);
		return frameAccessorImpl;
	}
	
}
