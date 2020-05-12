package com.example.nonoshow;

import android.util.Log;

import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint8;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.JsonRpc2_0Web3j;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Convert;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class EthereumService {
    private String url = "html://127.0.0.1:7545";
    private String contractAddress = "0x884829e64A5D52653A84d1822650A61eb2E7aA7A";
    private String address = "0x9f9177a5fCf6AA5b7B5Da852fD8b6154c93cF7E8";
    void setConnectEther() {
        Web3j web3j = new JsonRpc2_0Web3j(new HttpService(contractAddress));
        String result = null;
        EthGetBalance ethGetBalance = null;
        try {
            ethGetBalance = web3j.ethGetBalance(address, DefaultBlockParameterName.LATEST).sendAsync().get();
            BigInteger wei = ethGetBalance.getBalance();

            result = Convert.fromWei(wei.toString(), Convert.Unit.ETHER).toString();
            Log.i("balance", result);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
/**
    void setConnectEther()
    {
        Web3j web3j = new JsonRpc2_0Web3j(new HttpService(contractAddress));
        List<Type> inputParameters = Arrays.asList(new Uint8(param));
        TypeReference<?> typeReference = new TypeReference<Type>() {
        };
        List<TypeReference<?>> outputParameters = Arrays.asList();

        Function function = new Function("setValue", inputParameters, outputParameters);
        String encodedFunction = FunctionEncoder.encode(function);

        EthSendTransaction ethCall = null;
        try {
            //Nonce
            EthGetTransactionCount ethGetTransactionCount = web3j.ethGetTransactionCount(
                    url, DefaultBlockParameterName.LATEST).sendAsync().get();
            BigInteger nonce = ethGetTransactionCount.getTransactionCount();
            Log.i("info","==NONCE:" + nonce);

            //Run
            ethCall = web3j.ethSendTransaction(
                    Transaction.createFunctionCallTransaction(
                            url,
                            nonce, //or nullL
                            this.getGasPrice(), //gasPrice
                            BigInteger.valueOf(27000), //gasLimit
                            contractAddress,
                            encodedFunction)
            ).sendAsync().get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        String transactionHash = ethCall.getTransactionHash();
    }**/
}
