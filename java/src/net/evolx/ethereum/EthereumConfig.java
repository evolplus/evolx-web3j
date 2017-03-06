package net.evolx.ethereum;

/**
 *
 * @author evolplus
 */
public class EthereumConfig {
	public static final double WEI_TO_ETH = 1E-18;

	public static int getRewardAmount(int blockNumber) {
		if (blockNumber < 3700000) {
			return 5;
		} else if (blockNumber < 5000000) {
			return 4;
		} else if (blockNumber < 7000000) {
			return 3;
		} else {
			return 2;
		}
	}
}
