package net.evolx.ethereum;

import java.io.IOException;

/**
 *
 * @author evolplus
 */
public class EthereumUtils {

	public static final double WEI_TO_ETH = 1E-18;

	public static double getAverageNetworkHashrate(Web3 web3, int numOfLatestBlocks) throws IOException {
		Web3.BlockInfo next = web3.eth.getBlock("latest");
		double sumDiff = 0;
		long lastBlockTimetamp = next.getTimestamp();
		for (int i = 0; i < numOfLatestBlocks; i++) {
			sumDiff += next.getDifficulty();
			next = web3.eth.getBlock(next.getBlockNumber() - 1);
		}
		return sumDiff / (lastBlockTimetamp - next.getTimestamp());
	}
}
