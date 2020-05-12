package com.example.nonoshow

import android.util.Log
import org.web3j.protocol.core.DefaultBlockParameterName
import org.web3j.protocol.core.methods.response.EthGetBalance
import org.web3j.utils.Convert
import java.util.concurrent.ExecutionException

class EthereumServiceKt {
    companion object {
        private val url = "https://ropsten.infura.io/v3/4b06de7f86264b748a0e78ed57222891"   /*무료로 제공받은 녀석 test용url*/
        private val contractAddress = "************************************"
        private val address = "*********************************************"

        fun setConnectEther() {
            Log.i("ether", "start")
            val web3j = Web3jFactory.build(url)
            Log.i("ether", "continue")
            var result: String? = null
            var ethGetBalance: EthGetBalance? = null
            try {
                ethGetBalance =
                    web3j.ethGetBalance(contractAddress, DefaultBlockParameterName.LATEST).sendAsync()
                        .get()
                val wei = ethGetBalance!!.balance

                result = Convert.fromWei(wei.toString(), Convert.Unit.ETHER).toString()
                Log.i("balance", result)
            } catch (e: InterruptedException) {
                e.printStackTrace()
                Log.i("err","stackTrace")
            } catch (e: ExecutionException) {
                e.printStackTrace()
                Log.i("err","executionException")
            }
        }
    }
        /*val web3j: Web3j? = JsonRpc2_0Web3j(HttpService(url))
        val inputParameters : List<Type<*>> = Arrays.asList<Uint8>(Uint8(param))
        val typeReference = object : TypeReference<Type<*>>() {

        }
        val outputParameters = Arrays.asList<TypeReference<*>>()

        val function = Function("setValue", inputParameters, outputParameters)
        val encodedFunction = FunctionEncoder.encode(function)

        var ethCall: EthSendTransaction? = null
        try {
            //Nonce
            val ethGetTransactionCount = web3j.ethGetTransactionCount(
                url, DefaultBlockParameterName.LATEST
            ).sendAsync().get()
            val nonce = ethGetTransactionCount.transactionCount
            Log.i("info", "==NONCE:$nonce")

            //Run
            ethCall = web3j.ethSendTransaction(
                Transaction.createFunctionCallTransaction(
                    url,
                    nonce, //or nullL
                    this.getGasPrice(), //gasPrice
                    BigInteger.valueOf(27000), //gasLimit
                    contractAddress,
                    encodedFunction
                )
            ).sendAsync().get()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val transactionHash = ethCall!!.transactionHash
    }*/
}