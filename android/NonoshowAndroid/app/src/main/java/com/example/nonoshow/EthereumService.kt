package com.example.nonoshow

import android.annotation.SuppressLint
import android.util.Log
import com.example.nonoshow.MyApplication.Companion.contextForList
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.DefaultBlockParameterName
import org.web3j.protocol.core.methods.response.EthGetBalance
import org.web3j.utils.Convert
import java.util.concurrent.ExecutionException
import org.web3j.protocol.core.methods.response.Web3ClientVersion
import java.math.BigInteger


/** *
 * 1. ABI와 SmartContract가져오기
 * 2. 가져온 ContractAddress로 web3j생성
 * 3. 생성한 web3j의 버전컨트롤
 * 4. 함수사용  참고해보기
 * https://sabarada.tistory.com/21?category=800571
 *
 * eth_call 의 Protocol
 *
 * Parameters
 * Object - The transaction call object
 * from: DATA, 20 Bytes - (optional) The address the transaction is sent from.
 * to: DATA, 20 Bytes - The address the transaction is directed to.
 * gas: QUANTITY - (optional) Integer of the gas provided for the transaction execution. eth_call consumes zero gas, but this parameter may be needed by some executions.
 * gasPrice: QUANTITY - (optional) Integer of the gasPrice used for each paid gas
 * value: QUANTITY - (optional) Integer of the value sent with this transaction
 * data: DATA - (optional) Hash of the method signature and encoded parameters. For details see Ethereum Contract ABI
 * QUANTITY|TAG - integer block number, or the string "latest", "earliest" or "pending", see the default block parameter
 *
 * Returns
 * DATA - the return value of executed contract.
 *
 * **/

class EthereumServiceKt {
    companion object {
        private val url = "https://ropsten.infura.io/v3/4b06de7f86264b748a0e78ed57222891"   /*test넷 url*/
        val contractAddress = contextForList!!.resources.getString(R.string.smartContractAddress)
        private val address = "0xea45fBC8C70f6785C485cB535112ca793E4c6c54"  /*wallet*/
        private var clientVersion : Web3ClientVersion? = null
        fun getBalance(web3j : Web3j,address : String) : BigInteger {
            var ethGetBalance: EthGetBalance?
            var result : BigInteger? = null
            try {
                ethGetBalance =
                    web3j.ethGetBalance(address, DefaultBlockParameterName.LATEST).sendAsync()
                        .get()
                val wei = ethGetBalance!!.balance

                result = Convert.fromWei(wei.toString(), Convert.Unit.ETHER).toBigInteger()
                Log.i("balance", result.toString())
            } catch (e: InterruptedException) {
                e.printStackTrace()
                Log.i("err","stackTrace")
            } catch (e: ExecutionException) {
                e.printStackTrace()
                Log.i("err","executionException")
            }
            return result!!
        }

        /*동기화 요청*/
        @SuppressLint("CheckResult")
        private fun synchronousRequest(url : String) : Web3j{   /*client version을 동기화 시킨다.*/
            val web3j = Web3jFactory.build(url)
            var web3ClientVersion: Web3ClientVersion?
            try {
                web3ClientVersion = web3j.web3ClientVersion().sendAsync().get()
                clientVersion = web3ClientVersion
                Log.d("Test", web3ClientVersion!!.web3ClientVersion)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            } catch (e: ExecutionException) {
                e.printStackTrace()
            }

            return web3j
        }
/*
        fun functionCall() {

            val web3j: Web3j? = JsonRpc2_0Web3j(HttpService(url))
            val inputParameters: List<Type<*>> = Arrays.asList<Uint8>(Uint8(param))
            val typeReference = object : TypeReference<Type<*>>() {

            }
            val outputParameters = Arrays.asList<TypeReference<*>>()

            val function = Function("setValue", inputParameters, outputParameters)
            val encodedFunction = FunctionEncoder.encode(function)

            var ethCall: EthSendTransaction? = null
            try {
                //Nonce
                val ethGetTransactionCount = web3j!!.ethGetTransactionCount(
                    url, DefaultBlockParameterName.LATEST
                ).sendAsync().get()
                val nonce = ethGetTransactionCount.transactionCount
                Log.i("info", "==NONCE:$nonce")

                //Run
                ethCall = web3j.ethSendTransaction(
                    Transaction.createFunctionCallTransaction(
                        url,
                        nonce, //or nullL
                        this.getBalance(web3j,address), //gasPrice
                        BigInteger.valueOf(27000), //gasLimit
                        contractAddress,
                        encodedFunction
                    )
                ).sendAsync().get()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            val transactionHash = ethCall!!.transactionHash
        }

*/
        fun custSignUp(/*phoneNumber : String, name : String,
                                 id : String, pw : String,
                                 age: Uint32*/){
            val web3j = synchronousRequest(url) /*web3를 생성하면서 버전동기화*/
            var result: String? = null


            web3j.shutdown()


        }

        fun callMethodCompSignUp(/*companyName : String, id : String,
                                 pw : String, address : String,
                                 phoneNumber : String*/){
            val web3j = synchronousRequest(url)
            //val contract :  Contract = Contract.deployRemoteCall(web3j)
        }
    }
}