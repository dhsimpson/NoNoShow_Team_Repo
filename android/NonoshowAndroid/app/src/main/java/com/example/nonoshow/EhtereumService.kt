package com.example.nonoshow

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import com.example.nonoshow.MyApplication.Companion.contextForList
import network.pocket.eth.PocketEth
import org.web3j.abi.datatypes.generated.Uint32
import org.web3j.crypto.Wallet
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.DefaultBlockParameterName
import org.web3j.protocol.core.methods.response.EthGetBalance
import org.web3j.utils.Convert
import java.util.concurrent.ExecutionException
import org.web3j.protocol.core.methods.response.Web3ClientVersion



class EthereumServiceKt {
    companion object {
        var wallet : Wallet? = null
        var appContext : Context? = null
        var pocketEth : PocketEth? = null
        private val url = "https://ropsten.infura.io/v3/4b06de7f86264b748a0e78ed57222891"   /*test넷 url*/
        val contractAddress = contextForList!!.resources.getString(R.string.smartContractAddress)
        private val address = "0xea45fBC8C70f6785C485cB535112ca793E4c6c54"  /*wallet*/
        private var clientVersion : Web3ClientVersion? = null
        //val contract : Contract = Contract(contractAddress)
        fun getBalance() {
            val web3j = Web3jFactory.build(url)
            var result: String? = null
            var ethGetBalance: EthGetBalance? = null
            try {
                ethGetBalance =
                    web3j.ethGetBalance("address", DefaultBlockParameterName.LATEST).sendAsync()
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
            web3j.shutdown()
        }

        fun callMethodCustSignUp(phoneNumber : String, name : String,
                                         id : String, pw : String,
                                         age: Uint32){
            val web3j = synchronousRequest(url)
            var result: String? = null


            web3j.shutdown()




        }

        fun callMethodCompSignUp(companyName : String, id : String,
                                         pw : String, address : String,
                                         phoneNumber : String){
            val web3j = synchronousRequest(url)
            //val contract :  Contract = Contract.deployRemoteCall(web3j)
        }

        /*동기화 요청*/
        @SuppressLint("CheckResult")
        private fun synchronousRequest(url : String) : Web3j{   /*client version을 동기화 시킨다.*/
            val web3j = Web3jFactory.build(url)
            var web3ClientVersion: Web3ClientVersion? = null
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