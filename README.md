# evolx-web3j

Java based JSON-RPC client for go-ethereum (geth).

This library uses Apache HttpClient to communicate with geth server via HTTP and use JSON.Simple for JSON parsing.

## Example

### Create new instance of Web3

```java
    Web3 web3 = new Web3("http://localhost:8545");
```

### Get current Blockchain height

```java
    int blockHeight = web3.getBlockNumber();
```

### Get information of a blockHeight

```java
   Web3.BlockInfo blockInfo = web3.eth.getBlock(1234); // get BlockInfo by block number
```

or

```java
    Web3.BlockInfo blockInfo = web3.eth.getBlock("0x5873a7dc5e3e646e97d6b673f011b6d36cb1090dd526c5ada2d512a01e584076"); // get BlockInfo by block hash
```

or 

```java
    Web3.BlockInfo blockInfo = web3.eth.getBlock("latest"); // get BlockInfo by a tag
```