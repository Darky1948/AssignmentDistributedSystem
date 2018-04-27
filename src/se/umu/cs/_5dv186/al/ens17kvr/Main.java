/**
 * 
 */
package se.umu.cs._5dv186.al.ens17kvr;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import ki.types.ds.StreamInfo;
import se.umu.cs._5dv186.a1.client.DefaultStreamServiceClient;
import se.umu.cs._5dv186.a1.client.StreamServiceClient;

/**
 * @author Kristen Viguier ens17kvr
 */
public class Main {
	
	/** Handle stack output */
    private static final Logger LOG = Logger.getLogger(Main.class);
    
	public static final int DEFAULT_TIMEOUT = 1000;
	
	/** The location of the generated CSV files */
	private static final String FILE_LOCATION = "resources/";
	
	/** Contains all the values for packet drop rate. */
	private static List<String> packetDropRate = new ArrayList<>();
	
	/** Contains all the values for packet latency. */
	private static List<String> packetLatency = new ArrayList<>();
	
	/** Contains all the values for frame throughput. */
	private static List<String> frameThroughput = new ArrayList<>();
	
	/** Contains all the values for bandwidth utilization. */
	private static List<String> bandwidthUtilization = new ArrayList<>();
	
	/** Contains all the values for amout of time taken. */
	private static List<String> amountOfTime = new ArrayList<>();
	
	/** We define the number of Threads that we are going to use. 1 is for sequential higher is for parallelization. */
	private static Integer[] threadsNumber = {1, 2, 4, 6, 8, 16, 32, 64, 128, 256 };
	
	/**
	 * Main function. Given parameters allow to define which host which timeout with
	 * the given username.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
//		final String host = (args.length > 0) ? args[0] : "localhost";
//		final int timeout = (args.length > 1) ? Integer.parseInt(args[1]) : DEFAULT_TIMEOUT;
		final String username = (args.length > 2) ? args[2] : "test";

		
//		String[] hosts = StreamServiceDiscovery.SINGLETON.findHosts(); 
		String[] hosts = {"dobby.cs.umu.se", "bellatrix.cs.umu.se", "harry.cs.umu.se"};
		
		// TODO a delete juste pour le test
		int[] timeouts = {1000, 750, 500};
		
		for (int timeout : timeouts) {
			for (String host : hosts) {
				// Only one host to tests
				LOG.info("Test on the host " + host + " with " + timeout + " timeout for the user " + username);
			
				for (Integer tn : threadsNumber) {
					LOG.info("Number of threads use to fetch data : " + tn);
					List<StreamServiceClient> clients = new ArrayList<>();
		
					try {
		
						for (int i = 0; i < tn; i++) {
							StreamServiceClient client = DefaultStreamServiceClient.bind(host, timeout, username);
							
							clients.add(client);
						}
		
						callHost(clients, timeout);
		
					} catch (SocketException | UnknownHostException e) {
						e.printStackTrace();
					}
		
				}
				
				// Generate the CSV for the Host and given timeout
				generatedCSVByHost(host, timeout);
				// Reinit the list
				reinitList();
			}
		}
	}

	/**
	 * Reinitialize the list that contains data for the CSV file.
	 */
	private static void reinitList() {
		packetDropRate = new ArrayList<>();
		packetLatency = new ArrayList<>();
		frameThroughput = new ArrayList<>();
		bandwidthUtilization = new ArrayList<>();
		amountOfTime = new ArrayList<>();
	}

	/**
	 * In this function we are going to fetches stream.
	 * 
	 * @param clients
	 * @param timeout
	 */
	private static void callHost(List<StreamServiceClient> clients, int timeout) {

		try {
			FrameAccessorFactoryImpl frameAccessorFactoryImpl = new FrameAccessorFactoryImpl();
			FrameAccessorImpl frameAccessorImpl = (FrameAccessorImpl) frameAccessorFactoryImpl
					.getFrameAccessor(listStreamServiceClientToArray(clients), "stream10");
			
			// Fetching the StreamInfo
			StreamInfo streamInfo = frameAccessorImpl.getStreamInfo();
			frameAccessorImpl.setStreamInfo(streamInfo);
			
			// Fecthing the frame
			long t1 = System.currentTimeMillis();
			for (int i = 0; i < streamInfo.getLengthInFrames(); i++) {
				LOG.trace("Frame :" + (i + 1) + "/" + streamInfo.getLengthInFrames());
				frameAccessorImpl.getFrame(i);
			}
			long t2 = System.currentTimeMillis();
			
			double totalTime = (t2 - t1) * 1.00 / 1000;
			
			LOG.info("(UDP) packet drop rate (per service) : " + frameAccessorImpl.getPerformanceStatisticImpl().getPacketDropRate(""));
            LOG.info("(average) packet latency (per service) : " + frameAccessorImpl.getPerformanceStatisticImpl().getPacketLatency(""));
            LOG.info("(average) frame throughput : " + frameAccessorImpl.getPerformanceStatistics().getFrameThroughput());
            // bandwith utilization for a host
            LOG.info("bandwidth utilization (total network footprint) : " + frameAccessorImpl.getPerformanceStatistics().getLinkBandwidth("") + " bps");
			LOG.info("Total amount of time : " +  totalTime + " s");
			
			// We store values to generate our CSV file
			packetDropRate.add(String.valueOf(frameAccessorImpl.getPerformanceStatisticImpl().getPacketDropRate("")));
			packetLatency.add(String.valueOf(frameAccessorImpl.getPerformanceStatisticImpl().getPacketLatency("")));
			frameThroughput.add(String.valueOf(frameAccessorImpl.getPerformanceStatistics().getFrameThroughput()));
			bandwidthUtilization.add(String.valueOf(frameAccessorImpl.getPerformanceStatistics().getLinkBandwidth("")));
			amountOfTime.add(String.valueOf(totalTime));
			

		} catch (SocketTimeoutException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * list.toArray don't work so need to do that.
	 */
	private static StreamServiceClient[] listStreamServiceClientToArray(List<StreamServiceClient> clients) {
		StreamServiceClient[] serviceClients = new StreamServiceClient[clients.size()];
		
		for (int i = 0; i < serviceClients.length; i++) {
			serviceClients[i]= clients.get(i);
		}
		return serviceClients;
	}
	
	/**
	 * This function generate a CSV file for the current host and contains all the data of performance metrics.
	 * @param host
	 * 			This is the name of the current host
	 * @param timeout 
	 * 			This is the timeout that we defined to fetch the blocks.
	 */
	private static void generatedCSVByHost(String host, int timeout) {
		StringBuilder data = new StringBuilder();
				
		getCsvData(host, timeout, data, threadsNumber.length);
		
		// Create the CSV File
		// Create the file each time
		StringBuilder fileName = new StringBuilder(FILE_LOCATION);
		fileName.append(host).append("_").append(timeout).append(".csv");
		
		File file = new File(fileName.toString());
		
		String pathFull = file.getPath();

		BufferedWriter bwFull = null;
		FileWriter fwFull = null;
				
		try {
			fwFull = new FileWriter(pathFull);
			bwFull = new BufferedWriter(fwFull);
			
			// Writing content
			if(data != null) {
				bwFull.write(data.toString());
			}
			LOG.info("File created");
		} catch (IOException e) {
			LOG.error("Error occured during file creation " + e.getMessage());
		} finally {
			try {
				if (bwFull != null) {
					bwFull.close();
				}
				if (fwFull != null) {
					fwFull.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	/**
	 * Transform all the data performance metrics under csv format to generate some EXCEL (make life easier).
	 * 
	 * @param host
	 * 			name of the host.
	 * @param timeout
	 * 			the timeout setted for the test.
	 * @param data
	 * 			contains all the data under csv format.
	 * @param lengthThread
	 * 			length of the thread arrays defined.
	 */
	private static void getCsvData(String host, int timeout, StringBuilder data, int lengthThread) {
		// "header" of the csv
		data.append(host).append(" - ").append(timeout).append(";\n\n\n");
		
		// Drop rate packet
		data.append("Packet drop rate").append(";\n");
		
		for (int i = 0; i < lengthThread; i++) {
			data.append(packetDropRate.get(i)).append(";");
		}
		
		data.append("\n").append(getCsvThreadData()).append("\n");
		
		// Packet latency
		data.append("Packet latency").append(";\n");
		
		for (int i = 0; i < lengthThread; i++) {
			data.append(packetLatency.get(i)).append(";");
		}
		
		data.append("\n").append(getCsvThreadData()).append("\n");
		
		// Frame per second
		data.append("Frame per second").append(";\n");
		
		for (int i = 0; i < lengthThread; i++) {
			data.append(frameThroughput.get(i)).append(";");
		}
		
		data.append("\n").append(getCsvThreadData()).append("\n");

		
		// bandwidth
		data.append("Bandwidth Utilization").append(";\n");
		
		for (int i = 0; i < lengthThread; i++) {
			data.append(bandwidthUtilization.get(i)).append(";");
		}
		
		data.append("\n").append(getCsvThreadData()).append("\n");
		
		// Amount of time
		
		data.append("Amount of time").append(";\n");
		
		for (int i = 0; i < lengthThread; i++) {
			data.append(amountOfTime.get(i)).append(";");
		}
		
		data.append("\n").append(getCsvThreadData()).append("\n");
		
	}
	
	/**
	 * Return the value of the defined array thead number under csv format.
	 * @return String	
	 * 			Return the value of the array threads on csv format
	 */
	private static String getCsvThreadData() {
		StringBuilder data = new StringBuilder();
		
		for (int i = 0; i < threadsNumber.length; i++) {
			data.append(threadsNumber[i]).append(";");
		}
		
		return data.append("\n").toString();
	}

	/**
	 * @return the packetDropRate
	 */
	public List<String> getPacketDropRate() {
		return packetDropRate;
	}


	/**
	 * @return the packetLatency
	 */
	public List<String> getPacketLatency() {
		return packetLatency;
	}


	/**
	 * @return the frameThroughput
	 */
	public List<String> getFrameThroughput() {
		return frameThroughput;
	}


	/**
	 * @return the bandwidthUtilization
	 */
	public List<String> getBandwidthUtilization() {
		return bandwidthUtilization;
	}


	/**
	 * @return the amountOfTime
	 */
	public static List<String> getAmountOfTime() {
		return amountOfTime;
	}

	/**
	 * @param amountOfTime the amountOfTime to set
	 */
	public static void setAmountOfTime(List<String> amountOfTime) {
		Main.amountOfTime = amountOfTime;
	}

	/**
	 * @param packetDropRate the packetDropRate to set
	 */
	public static void setPacketDropRate(List<String> packetDropRate) {
		Main.packetDropRate = packetDropRate;
	}

	/**
	 * @param packetLatency the packetLatency to set
	 */
	public static void setPacketLatency(List<String> packetLatency) {
		Main.packetLatency = packetLatency;
	}

	/**
	 * @param frameThroughput the frameThroughput to set
	 */
	public static void setFrameThroughput(List<String> frameThroughput) {
		Main.frameThroughput = frameThroughput;
	}

	/**
	 * @param bandwidthUtilization the bandwidthUtilization to set
	 */
	public static void setBandwidthUtilization(List<String> bandwidthUtilization) {
		Main.bandwidthUtilization = bandwidthUtilization;
	}
	
}
