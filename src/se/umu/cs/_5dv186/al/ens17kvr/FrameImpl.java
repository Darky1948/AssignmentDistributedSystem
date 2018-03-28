package se.umu.cs._5dv186.al.ens17kvr;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import ki.types.ds.Block;
import se.umu.cs._5dv186.a1.client.FrameAccessor.Frame;

/**
 * Frame class represents a set of blocks.
 * 
 * @author Kristen Viguier ens17kvr
 *
 */
public class FrameImpl implements Frame {
	
	List<List<Block>> blocks;
	
	/**
	 * Constructor {@link FrameImpl}.
	 * @param x
	 * @param y
	 */
	public FrameImpl(int x, int y) {
		blocks = new ArrayList<>();

		for (int i = 0; i < y; i++) {
			List<Block> temp = new ArrayList<>();
			for (int j = 0; j < x; j++) {
				temp.add(new Block(null));
			}
			blocks.add(temp);
		}
	}

	@Override
	public Block getBlock(int x, int y) throws IOException, SocketTimeoutException {
		return blocks.get(x).get(y);
	}

}
