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
		/*
		 * Frame par second
		 */
		int numberOfFrame = 0;
		int totalTime = 0;
		return numberOfFrame / (totalTime / 1000);
	}

	@Override
	public double getLinkBandwidth(String arg0) {
		// TODO
		double packagesReceived = 0d;
		double totalTime = 0d;
		return (3 * 8) * (16 * 16) * (packagesReceived / (totalTime / 1000));
	}

	@Override
	public double getPacketDropRate(String arg0) {
		// Packet Drop Rate : amountOfDroppedPacket / TotalAmountOfPackets
		return 0;
	}

	@Override
	public double getPacketLatency(String arg0) {
		// Packet Latency : total time / packet received
		return 0;
	}

}
