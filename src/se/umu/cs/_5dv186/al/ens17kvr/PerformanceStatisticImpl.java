package se.umu.cs._5dv186.al.ens17kvr;

import se.umu.cs._5dv186.a1.client.FrameAccessor.PerformanceStatistics;

/**
 * @author Kristen Viguier ens17kvr
 *
 */
public class PerformanceStatisticImpl implements PerformanceStatistics {
	/*
	 * Packet Drop Rate : amountOfDroppedPacket / TotalAmountOfPackets Packet
	 * Latency : total time / packet received 
	 * Frame Throughput : total frames / total time 
	 * Link bandwidth : 3 (because pixels is r, b, g) * 8 (for each size of 8) * (16*16) (because a block is 16x16) * package received / totalTime
	 * 
	 * Bandwidth utilization : is the total amount of link bandwidth
	 */
	
	private double totalTime = 0d;
	private double packageReceived = 0d;
	private double packageDropped = 0d;
	private double nbFrame = 0d;
	private double totalLatency = 0d;
	
	@Override
	public double getBandwidthUtilization() {
		double additionLinkBdw = 0;

		// Somme des host linkbandwidth
		// additionLinkBdw += getLinkBandwidth(hostStatistics.hostName);
		//

		return additionLinkBdw;
	}

	@Override
	public double getFrameThroughput() {
		return nbFrame / (totalTime / 1000);
	}

	@Override
	public double getLinkBandwidth(String arg0) {
		return (3 * 8) * (16 * 16) * (packageReceived / (totalTime / 1000));
	}

	@Override
	public double getPacketDropRate(String arg0) {
		// Packet Drop Rate : amountOfDroppedPacket / TotalAmountOfPackets
		return packageDropped / (packageReceived + packageDropped);
	}

	@Override
	public double getPacketLatency(String arg0) {
		// Packet Latency : total time / packet received
		return totalLatency  / packageReceived;
	}
	
	/**
	 * This function compute the total amount of time.
	 * @param time
	 */
	public synchronized void computeTotalTime(double time) {
		totalTime += time;
	}
	
	/**
	 * This function compute the total amount of Latency.
	 * @param time
	 */
	public synchronized void computeTotalLatency(double time) {
		totalLatency += time;
	}
	
	/**
	 * Increment the number of frame rendered.
	 */
	public synchronized void incrementFrameNb() {
		this.nbFrame++;
	}
	
	/**
	 * Increment the number of package received.
	 */
	public synchronized void incrementPackageReceived() {
		this.packageReceived++;
	}
	
	/**
	 * Increment the number of package dropped.
	 */
	public synchronized void incrementPackageDropped() {
		this.packageDropped++;
	}

}
