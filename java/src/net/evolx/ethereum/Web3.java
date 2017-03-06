package net.evolx.ethereum;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

/**
 *
 * @author evolplus
 */
public class Web3 {

	private static final String[] NO_PARAMS = {};

	private final String url;
	private final AtomicLong apiSeq;
	public final EthereumAPI eth;
	public final NetworkAPI net;

	private abstract class EthereumObject {

		private final JSONObject jsonData;

		protected EthereumObject(JSONObject jsonData) {
			this.jsonData = jsonData;
		}

		protected long getLong(String key) {
			return toDecimal(jsonData.get(key).toString());
		}

		protected int getInt(String key) {
			return (int) toDecimal(jsonData.get(key).toString());
		}

		protected String getString(String key) {
			return jsonData.get(key).toString();
		}

		protected Object getObject(String key) {
			return jsonData.get(key);
		}
	}

	public final class BlockInfo extends EthereumObject {

		protected BlockInfo(JSONObject jsonData) {
			super(jsonData);
		}

		public int getBlockNumber() {
			return getInt("number");
		}

		public long getDifficulty() {
			return getLong("difficulty");
		}

		public String getExtraData() {
			return getString("extraData");
		}

		public int getGasLimit() {
			return getInt("gasLimit");
		}

		public int getGasUsed() {
			return getInt("gasLimit");
		}

		public String getHash() {
			return getString("hash");
		}

		public String getLogsBloom() {
			return getString("logsBloom");
		}

		public String getMiner() {
			return getString("miner");
		}

		public String getMixHash() {
			return getString("mixHash");
		}

		public long getNonce() {
			return getLong("nonce");
		}

		public String getParentHash() {
			return getString("parentHash");
		}

		public String getReceiptsRoot() {
			return getString("receiptsRoot");
		}

		public String getSha3Uncles() {
			return getString("sha3Uncles");
		}

		public String getStateRoot() {
			return getString("stateRoot");
		}

		public int getSize() {
			return getInt("size");
		}

		public long getTimestamp() {
			return getLong("timestamp");
		}

		public long getTotalDifficulty() {
			return getLong("totalDifficulty");
		}

		public String getTransactionsRoot() {
			return getString("transactionsRoot");
		}

		public TransactionInfo[] getTransactions() {
			Object obj = getObject("transactions");
			if (obj instanceof JSONArray) {
				JSONArray a = (JSONArray) obj;
				TransactionInfo[] rs = new TransactionInfo[a.size()];
				for (int i = 0; i < a.size(); i++) {
					rs[i] = new TransactionInfo((JSONObject) a.get(i));
				}
				return rs;
			}
			return null;
		}

		public String[] getUncles() {
			Object obj = getObject("uncles");
			if (obj instanceof JSONArray) {
				JSONArray a = (JSONArray) obj;
				String[] rs = new String[a.size()];
				for (int i = 0; i < a.size(); i++) {
					rs[i] = a.get(i).toString();
				}
				return rs;
			}
			return null;
		}
	}

	public final class TransactionInfo extends EthereumObject {

		public TransactionInfo(JSONObject jsonData) {
			super(jsonData);
		}

		public String getBlockHash() {
			return getString("blockHash");
		}

		public int getBlockNumber() {
			return getInt("blockNumber");
		}

		public String getFrom() {
			return getString("from");
		}

		public String getTo() {
			return getString("to");
		}

		public int getGas() {
			return getInt("gas");
		}

		public long getGasPrice() {
			return getLong("gasPrice");
		}

		public String getHash() {
			return getString("hash");
		}

		public String getInput() {
			return getString("input");
		}

		public long getNonce() {
			return getLong("nonce");
		}

		public long getValue() {
			return getLong("value");
		}

		public int getTransactionIndex() {
			return getInt("transactionIndex");
		}
	}

	public final class TransactionReceipt extends EthereumObject {

		public TransactionReceipt(JSONObject jsonData) {
			super(jsonData);
		}

		public String getBlockHash() {
			return getString("blockHash");
		}

		public int getBlockNumber() {
			return getInt("blockNumber");
		}

		public int getGasUsed() {
			return getInt("gasUsed");
		}

		public String getTransactionHash() {
			return getString("transactionHash");
		}

		public int getTransactionIndex() {
			return getInt("transactionIndex");
		}

		public int getCumulativeGasUsed() {
			return getInt("cumulativeGasUsed");
		}

		public String getContractAddress() {
			return getString("contractAddress");
		}
	}

	public final class EthereumAPI {

		protected EthereumAPI() {
		}

		public BlockInfo getBlock(int blockNumber) throws IOException {
			JSONObject obj = (JSONObject) callRPC("eth_getBlockByNumber", new String[]{String.format("\"0x%x\"", blockNumber), "true"});
			if (obj == null) {
				return null;
			}
			return new BlockInfo(obj);
		}

		public BlockInfo getBlock(String hashOrTag) throws IOException {
			boolean isTag = hashOrTag.equals("earliest") || hashOrTag.equals("latest") || hashOrTag.equals("pending");
			JSONObject obj = (JSONObject) callRPC(isTag ? "eth_getBlockByNumber" : "eth_getBlockByHash", new String[]{String.format("\"%s\"", hashOrTag), "true"});
			if (obj == null) {
				return null;
			}
			return new BlockInfo(obj);
		}

		public TransactionReceipt getTransactionReceipt(String hash) throws IOException {
			JSONObject obj = (JSONObject) callRPC("eth_getTransactionReceipt", new String[]{String.format("\"%s\"", hash)});
			if (obj == null) {
				return null;
			}
			return new TransactionReceipt(obj);
		}

		public int getBlockNumber() throws IOException {
			return (int) toDecimal(callRPC("eth_blockNumber", NO_PARAMS).toString());
		}

		public int getProtocolVersion() throws IOException {
			return (int) toDecimal(callRPC("eth_protocolVersion", NO_PARAMS).toString());
		}

		public boolean isSyncing() throws IOException {
			Object result = callRPC("eth_syncing", NO_PARAMS);
			return result instanceof JSONObject;
		}

		public String getCoinbase() throws IOException {
			return getConfig("eth_coinbase");
		}

		public boolean isMining() throws IOException {
			Object result = callRPC("eth_mining", NO_PARAMS);
			return result instanceof JSONObject;
		}

		public long getHashrate() throws IOException {
			return toDecimal(callRPC("eth_hashrate", NO_PARAMS));
		}

		public long getGasPrice() throws IOException {
			return toDecimal(callRPC("eth_gasPrice", NO_PARAMS));
		}

		public String[] getAccounts() throws IOException {
			Object result = callRPC("eth_accounts", NO_PARAMS);
			if (!(result instanceof JSONArray)) {
				return null;
			}
			JSONArray a = (JSONArray) result;
			String[] accounts = new String[a.size()];
			for (int i = 0; i < accounts.length; i++) {
				accounts[i] = a.get(i).toString();
			}
			return accounts;
		}

		public long getBalance(String address) throws IOException {
			return toDecimal(callRPC("eth_getBalance", new String[]{address, "latest"}));
		}

		public long getBalance(String address, String tag) throws IOException {
			return toDecimal(callRPC("eth_getBalance", new String[]{address, tag}));
		}

		public long getBalance(String address, int blockNumber) throws IOException {
			return toDecimal(callRPC("eth_getBalance", new String[]{address, String.valueOf(blockNumber)}));
		}

		public String getStorageAt(String address, long pos, int blockNumber) throws IOException {
			return getStorageAt(address, pos, String.valueOf(blockNumber));
		}

		public String getStorageAt(String address, long pos, String block) throws IOException {
			Object result = callRPC("eth_getStorageAt", new String[]{address, String.valueOf(pos), block});
			if (result instanceof String) {
				return (String) result;
			}
			return null;
		}

		public int getTransactionCount(String address, int blockNumber) throws IOException {
			return getTransactionCount(address, String.valueOf(blockNumber));
		}

		public int getTransactionCount(String address, String block) throws IOException {
			return (int) toDecimal(callRPC("eth_getTransactionCount", new String[]{address, block}));
		}

		public int getBlockTransactionCountByHash(String hash) throws IOException {
			return (int) toDecimal(callRPC("eth_getBlockTransactionCountByHash", new String[]{hash}));
		}

		public int getBlockTransactionCountByNumber(String tagOrNumber) throws IOException {
			return (int) toDecimal(callRPC("eth_getBlockTransactionCountByNumber", new String[]{tagOrNumber}));
		}

		public int getBlockTransactionCountByNumber(int blockNumber) throws IOException {
			return getBlockTransactionCountByNumber(String.valueOf(blockNumber));
		}

		public int getUncleCountByBlockHash(String hash) throws IOException {
			return (int) toDecimal(callRPC("eth_getUncleCountByBlockHash", new String[]{hash}));
		}

		public int getUncleCountByNumber(String tagOrNumber) throws IOException {
			return (int) toDecimal(callRPC("eth_getUncleCountByBlockNumber", new String[]{tagOrNumber}));
		}

		public int getUncleCountByNumber(int blockNumber) throws IOException {
			return getUncleCountByNumber(String.valueOf(blockNumber));
		}

		public String getCode(String address, String tagOrNumber) throws IOException {
			Object result = callRPC("eth_getCode", new String[]{address, tagOrNumber});
			if (result instanceof String) {
				return (String) result;
			}
			return null;
		}

		public String getCode(String address, int blockNumber) throws IOException {
			return getCode(address, String.valueOf(blockNumber));
		}

		public String sign(String address, String message) throws IOException {
			Object result = callRPC("eth_sign", new String[]{address, message});
			if (result instanceof String) {
				return (String) result;
			}
			return null;
		}
	}

	public final class NetworkAPI {

		protected NetworkAPI() {

		}

		public String getVersion() throws IOException {
			return getConfig("net_version");
		}

		public boolean isListening() throws IOException {
			Object result = callRPC("net_listening", NO_PARAMS);
			if (result instanceof Boolean) {
				return (Boolean) result;
			}
			return false;
		}

		public int getPeerCount() throws IOException {
			Object result = callRPC("net_peerCount", NO_PARAMS);
			return (int) toDecimal(result);
		}
	}

	public Web3(String url) {
		this.url = url;
		this.eth = new EthereumAPI();
		this.net = new NetworkAPI();
		apiSeq = new AtomicLong(0);
	}

	public String call(String method, String[] params) throws IOException {
		HttpPost post = new HttpPost(url);
		StringBuilder sb = new StringBuilder("{\"jsonrpc\":\"2.0\",\"method\":\"");
		sb.append(method);
		sb.append("\",\"params\":[");
		for (String param : params) {
			sb.append(param);
			sb.append(',');
		}
		if (params.length > 0) {
			sb.setCharAt(sb.length() - 1, ']');
		} else {
			sb.append(']');
		}
		sb.append(",\"id\":");
		sb.append(apiSeq.incrementAndGet());
		sb.append('}');
		String json = sb.toString();
		post.setEntity(new ByteArrayEntity(json.getBytes()));
		HttpClient client = new DefaultHttpClient();
		HttpResponse resp = client.execute(post);
		sb.setLength(0);
		try (BufferedReader br = new BufferedReader(new InputStreamReader(resp.getEntity().getContent()))) {
			String s;
			while ((s = br.readLine()) != null) {
				sb.append(s);
			}
		}
		return sb.toString();
	}

	private Object callRPC(String method, String[] params) throws IOException {
		JSONObject obj;
		try {
			obj = (JSONObject) JSONValue.parse(call(method, params));
			return obj.get("result");
		} catch (Exception ex) {
			return null;
		}
	}

	private String getConfig(String method) throws IOException {
		Object result = callRPC(method, NO_PARAMS);
		if (result instanceof String) {
			return (String) result;
		}
		return null;
	}

	public long toDecimal(Object obj) {
		if (obj instanceof Integer) {
			return (Integer) obj;
		}
		if (obj instanceof Long) {
			return (Long) obj;
		}
		String text = obj.toString();
		if (text.startsWith("0x")) {
			return Long.valueOf(text.substring(2), 16);
		}
		return Long.valueOf(text);
	}

	public String getClientVersion() throws IOException {
		return getConfig("web3_clientVersion");
	}

	public String sha3(String data) throws IOException {
		Object result = callRPC("web3_sha3", new String[]{data});
		if (result instanceof String) {
			return (String) result;
		}
		return null;
	}

	public boolean isAddress(String address) {
		if (address.startsWith("0x")) {
			address = address.substring(2);
		}
		return address.matches("[0-9a-fA-F]{20}");
	}

	public String toHex(Object value) {
		return String.format("0x%x", toDecimal(value));
	}
}
