package se.umu.cs._5dv186.al.ens17kvr;

import ki.types.ds.Pixel;

public class BlockXY extends ki.types.ds.Block {
	/**
	 * Represent the abscisse.
	 */
	private int x;
	
	/**
	 * Represent the ordonnate.
	 */
	private int y;
	
	/**
	 * Represent the number of try to fetch a dropped block.
	 */
	public static final int NUMBER_OF_TRIES = 10; 
	
	/**
	 * Count the number of tries for a current block.
	 */
	private int counterTries = 0;

	public BlockXY(Pixel[] pixels) {
		super(pixels);
	}
	
	/**
	 * @param x
	 * @param y
	 */
	public BlockXY(int x, int y) {
		super(new Pixel[5]);
		this.x = x;
		this.y = y;
	}



	/**
	 * @return the x
	 */
	public int getX() {
		return x;
	}

	/**
	 * @param x the x to set
	 */
	public void setX(int x) {
		this.x = x;
	}

	/**
	 * @return the y
	 */
	public int getY() {
		return y;
	}

	/**
	 * @param y the y to set
	 */
	public void setY(int y) {
		this.y = y;
	}

	/**
	 * @return the counterTries
	 */
	public int getCounterTries() {
		return counterTries;
	}

	/**
	 * This function increment the counter tries.
	 */
	public void incrementCounterTries() {
		this.counterTries++;
	}

	
}
