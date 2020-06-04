package com.example.nonoshow;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Bytes32;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.abi.datatypes.generated.Uint32;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.admin.Admin;
import org.web3j.protocol.admin.methods.response.PersonalUnlockAccount;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthGetTransactionReceipt;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.core.methods.response.Web3ClientVersion;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.objectweb.asm.commons.GeneratorAdapter.AND;

/*
* 함수사용  참고해보기
* https://sabarada.tistory.com/21?category=800571
* */
public class EthereumService {

    private static String url = "http://10.0.2.2:7545";  /*test넷 url*/
    static String contractAddress = MyApplication.Companion.getContextForList().getResources().getString(R.string.smartContractAddress);
    private static String address = MyApplication.Companion.getContextForList().getResources().getString(R.string.myWalletAddress); /*wallet*/
    private static String password = MyApplication.Companion.getContextForList().getResources().getString(R.string.myPrivateKey);
    private Web3ClientVersion  clientVersion;
    static Admin admin = Admin.build(new HttpService(url));
    static Web3j web3j = Web3j.build(new HttpService(url));
    private static BigInteger gasLimit= BigInteger.valueOf(20_000_000_000L);
    // gas price
    private static BigInteger gasPrice = BigInteger.valueOf(4300000);
    private static Credentials credentials = Credentials.create(password);
    private static BigInteger ACOUNT_UNLOCK_DURATION = BigInteger.valueOf(5000);
    /****/
    public static Credentials getCredential(){
        BigInteger privateKeyInBT = new BigInteger(password, 16);
        ECKeyPair aPair = ECKeyPair.create(privateKeyInBT);
        Credentials aCredential = Credentials.create(aPair);
        address = aCredential.getAddress();
        Log.i("address : ", address);
        return aCredential;
    }



    /****/

    public static void getEthClientVersionSync() throws Exception
    {
        Web3ClientVersion web3ClientVersion = web3j.web3ClientVersion().send();
        System.out.println(web3ClientVersion.getWeb3ClientVersion());
    }

    public static BigInteger getBalance() throws Exception {
        getEthClientVersionSync();


        EthGetBalance ethGetBalance = null;
        BigInteger result = null;
        try {
            ethGetBalance =
                    web3j.ethGetBalance(address, DefaultBlockParameterName.LATEST).sendAsync().get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        assert ethGetBalance != null;
        BigInteger wei = ethGetBalance.getBalance();
        result = Convert.fromWei(wei.toString(), Convert.Unit.ETHER).toBigInteger();
        Log.i("balance", result.toString());
        return result;
    }
/**
 * {
 * 		"inputs": [
 *                        {
 * 				"internalType": "string",
 * 				"name": "phoneNumber",
 * 				"type": "string"
 *            },
 *            {
 * 				"internalType": "string",
 * 				"name": "name",
 * 				"type": "string"
 *            },
 *            {
 * 				"internalType": "string",
 * 				"name": "id",
 * 				"type": "string"
 *            },
 *            {
 * 				"internalType": "string",
 * 				"name": "pw",
 * 				"type": "string"
 *            },
 *            {
 * 				"internalType": "uint32",
 * 				"name": "age",
 * 				"type": "uint32"
 *            }
 * 		],
 * 		"name": "custSignUp",
 * 		"outputs": [
 *            {
 * 				"internalType": "bool",
 * 				"name": "",
 * 				"type": "bool"
 *            }
 * 		],
 * 		"stateMutability": "nonpayable",
 * 		"type": "function"
 * 	}
 */
    public static void custLogIn(String id,  String pw)
    {
        try {
            //getEthClientVersionSync();
            System.out.println("******************");
            custLogInDetail(id,pw);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public static String custLogInDetail(String id, String pw) throws IOException, ExecutionException, InterruptedException {

        // 1. 호출하고자 하는 function 세팅[functionName, parameters]
        Function function = new Function("custLogIn",
                Arrays.asList(convertStringToBytes32(id),convertStringToBytes32(pw)),
                Arrays.asList(new TypeReference<Uint256>(){}));

        // 2. ethereum을 function 변수로 통해 호출
        return ethCall(function);
    }
    public static String ethCall(Function function) throws IOException, ExecutionException, InterruptedException {

        //3. transaction 제작
        Transaction transaction = Transaction.createEthCallTransaction(address, contractAddress,
                FunctionEncoder.encode(function));

        //4. ethereum 호출후 결과 가져오기

        org.web3j.protocol.core.methods.response.EthCall ethCall =  web3j.ethCall(transaction, DefaultBlockParameterName.LATEST).sendAsync().get();


        //5. 결과값 decode
        String value = ethCall.getValue();
        System.out.println(value);
        List<Type> decode = FunctionReturnDecoder.decode(ethCall.getResult(),
                function.getOutputParameters());

        System.out.println("ethCall.getResult() = " + ethCall.getResult());
        System.out.println("getValue = " + decode.get(0).getValue());
        System.out.println("getType = " + decode.get(0).getTypeAsString());
        Number number = 0;
/**        String valueBool = "UNKNOWN";
        boolean bool = decode.isEmpty();
        if(bool){
            valueBool = "TRUE";
        }
        else    valueBool = "FALSE";


        System.out.println("getValue = " + decode.indexOf(number));
        number = 1;
        System.out.println("getValue = " + decode.indexOf(number));
        number = 2;
        System.out.println("getValue = " + decode.indexOf(number));
*/
        return "";
    }
    private static String callSmartContractFunction(
            Function function, String contractAddress) throws Exception {

        String encodedFunction = FunctionEncoder.encode(function);

        org.web3j.protocol.core.methods.response.EthCall response = web3j.ethCall(
                Transaction.createEthCallTransaction(
                        address, contractAddress, encodedFunction),
                DefaultBlockParameterName.LATEST)
                .sendAsync().get();

        return response.getValue();
    }


    public static void custSignUp(String name,String id,String pw,int ageOrAddress,String phoneNumber)
    {
        try {
            getEthClientVersionSync();
            custSignUpDetail(name, id, pw, ageOrAddress, phoneNumber);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static String custSignUpDetail(String name,String id,String pw,int ageOrAddress,String phoneNumber) throws IOException, ExecutionException, InterruptedException {

        // 1. 호출하고자 하는 function 세팅[functionName, parameters]
        Function function = new Function("custSingUp",
                Arrays.asList(convertStringToBytes32(phoneNumber),convertStringToBytes32(name),convertStringToBytes32(id),convertStringToBytes32(pw),new Uint32(ageOrAddress)),
                Arrays.asList(new TypeReference<Bool>(){}));

        // 2. ethereum을 function 변수로 통해 호출
        return ethCall(function);
    }


    public static void custSignUpDetail2(String name,String id,String pw,int ageOrAddress,String phoneNumber)throws IOException, ExecutionException, InterruptedException{
        Function function = new Function("custSingUp",
                Arrays.asList(convertStringToBytes32(phoneNumber),convertStringToBytes32(name),convertStringToBytes32(id),convertStringToBytes32(pw),new Uint32(ageOrAddress)),
                Arrays.asList(new TypeReference<Bool>(){}));

        // 2. sendTransaction
        String txHash = ethSendTransaction(function);

        // 7. getReceipt
        Log.i("getReceipt","try");
        TransactionReceipt receipt = getReceipt(txHash);
        Log.i("getReceipt","" +receipt);
        System.out.println("receipt = " + receipt);
    }

    public static String ethSendTransaction(Function function)
            throws IOException, InterruptedException, ExecutionException {

        // 3. Account Lock 해제
        BigInteger unlockDuration = BigInteger.valueOf(60L);

        try {
            PersonalUnlockAccount personalUnlockAccount = admin.personalUnlockAccount(address, password, unlockDuration).send();
            Boolean isUnlocked = personalUnlockAccount.accountUnlocked();
            System.out.println("account unlock " + isUnlocked);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (true/*personalUnlockAccount.accountUnlocked()*/) { // unlock 일때

            //4. account에 대한 nonce값 가져오기.
            EthGetTransactionCount ethGetTransactionCount = admin.ethGetTransactionCount(
                    address, DefaultBlockParameterName.LATEST).sendAsync().get();

            BigInteger nonce = ethGetTransactionCount.getTransactionCount();
            System.out.println(nonce);

            //5. Transaction값 제작
            Transaction transaction = Transaction.createFunctionCallTransaction(address, nonce,
                    Transaction.DEFAULT_GAS,
                    null, contractAddress,
                    FunctionEncoder.encode(function));

            // 6. ethereum Call
            EthSendTransaction ethSendTransaction = admin.ethSendTransaction(transaction).send();

            // transaction에 대한 transaction Hash값 얻기.
            String transactionHash = ethSendTransaction.getTransactionHash();

            // ledger에 쓰여지기 까지 기다리기.
            Thread.sleep(5000);

            return transactionHash;
        }
        else {
            throw new PersonalLockException("check ethereum personal Lock");
        }
    }
    public static TransactionReceipt getReceipt(String transactionHash) throws IOException {

        //8. transaction Hash를 통한 receipt 가져오기.
        EthGetTransactionReceipt transactionReceipt = admin.ethGetTransactionReceipt(transactionHash).send();

        if(transactionReceipt.getTransactionReceipt().isPresent())
        {
            // 9. 결과확인
            System.out.println("transactionReceipt.getResult().getContractAddress() = " +
                    transactionReceipt.getResult());
        }
        else
        {
            System.out.println("transaction complete not yet");
        }

        return transactionReceipt.getResult();
    }
    private static class PersonalLockException extends RuntimeException
    {
        public PersonalLockException(String msg)
        {
            super(msg);
        }
    }

    // String to 64 length HexString (equivalent to 32 Hex lenght)
    public static String asciiToHex(String asciiValue)
    {
        char[] chars = asciiValue.toCharArray();
        StringBuffer hex = new StringBuffer();
        for (int i = 0; i < chars.length; i++)
        {
            hex.append(Integer.toHexString((int) chars[i]));
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return hex.toString() + "".join("", Collections.nCopies(32 - (hex.length()/2), "00"));
        }
        else    return "";
    }

    public static Bytes32 convertStringToBytes32(String str){
        byte[] StringInByte = Numeric.hexStringToByteArray(asciiToHex(str));
        return new Bytes32(StringInByte);
    }
}