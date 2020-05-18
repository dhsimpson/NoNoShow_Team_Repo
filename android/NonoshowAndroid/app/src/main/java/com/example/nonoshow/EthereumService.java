package com.example.nonoshow;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import org.json.JSONArray;
import org.json.JSONObject;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Bytes32;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.abi.datatypes.generated.Uint32;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthCall;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.core.methods.response.Web3ClientVersion;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
/*
* 함수사용  참고해보기
* https://sabarada.tistory.com/21?category=800571
* */
public class EthereumService {

    private static String url = "https://ropsten.infura.io/v3/4b06de7f86264b748a0e78ed57222891";  /*test넷 url*/
    static String contractAddress = MyApplication.Companion.getContextForList().getResources().getString(R.string.smartContractAddress);
    private static String address = "0xea45fBC8C70f6785C485cB535112ca793E4c6c54";  /*wallet*/
    private Web3ClientVersion  clientVersion;
    static Web3j web3j = Web3j.build(new HttpService(url));
    public void getEthClientVersionSync() throws Exception
    {

        Web3ClientVersion web3ClientVersion = web3j.web3ClientVersion().send();
        System.out.println(web3ClientVersion.getWeb3ClientVersion());
    }

    public static BigInteger getBalance() {
        EthGetBalance ethGetBalance = null;
        BigInteger result = null;
        try {
            ethGetBalance =
                    web3j.ethGetBalance(address, DefaultBlockParameterName.LATEST).sendAsync().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
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
    private static String from;
    public static void custLogIn(String id,  String pw)
    {
        try {
            from = custLogInDetail(id, pw);
        } catch (IOException | ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }
    public static String custLogInDetail(String id, String pw) throws IOException, ExecutionException, InterruptedException {

        // 1. 호출하고자 하는 function 세팅[functionName, parameters]
        Function function = new Function("custLogIn",
                Arrays.asList(convertStringToBytes32(id),convertStringToBytes32(pw)),
                Arrays.asList(new TypeReference<Uint256>() {}));

        // 2. ethereum을 function 변수로 통해 호출
        return ethCall(function);
    }
    public static String ethCall(Function function) throws IOException {

        //3. transaction 제작
        Transaction transaction = Transaction.createEthCallTransaction(from, contractAddress,
                FunctionEncoder.encode(function));

        //4. ethereum 호출후 결과 가져오기
        EthCall ethCall = web3j.ethCall(transaction, DefaultBlockParameterName.LATEST).send();

        //5. 결과값 decode
        List<Type> decode = FunctionReturnDecoder.decode(ethCall.getResult(),
                function.getOutputParameters());

        System.out.println("ethCall.getResult() = " + ethCall.getResult());
        System.out.println("getValue = " + decode.get(0).getValue());
        System.out.println("getType = " + decode.get(0).getTypeAsString());

        return (String)decode.get(0).getValue();
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