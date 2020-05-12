package com.example.nonoshow

import org.web3j.protocol.Web3j
import org.web3j.protocol.Web3jService
import org.web3j.protocol.core.JsonRpc2_0Web3j
import org.web3j.protocol.http.HttpService

import java.util.concurrent.ScheduledExecutorService

public class Web3jFactory {

    /**
     * Construct a new Web3j instance.
     *
     * @param endpoint web3j service instance - i.e. HTTP or IPC
     * @return new Web3j instance
     */
    companion object {

        fun build(endpoint : String): Web3j
        {
            return JsonRpc2_0Web3j(HttpService (endpoint))
        }

        /**
         * Construct a new Web3j instance.
         *
         * @param web3jService web3j service instance - i.e. HTTP or IPC
         * @param pollingInterval polling interval for responses from network nodes
         * @param scheduledExecutorService executor service to use for scheduled tasks.
         *                                 <strong>You are responsible for terminating this thread
         *                                 pool</strong>
         * @return new Web3j instance
         */
        fun build (web3jService : Web3jService,pollingInterval : Long,
        scheduledExecutorService : ScheduledExecutorService) : Web3j
        {
            return JsonRpc2_0Web3j(web3jService, pollingInterval, scheduledExecutorService)
        }
    }
}

/*
* public class Web3jFactory {

    /**
     * Construct a new Web3j instance.
     *
     * @param endpoint web3j service instance - i.e. HTTP or IPC
     * @return new Web3j instance
     */
    public static Web3j build(String endpoint) {
        return Web3j.build(new HttpService(endpoint));
    }

    /**
     * Construct a new Web3j instance.
     *
     * @param web3jService web3j service instance - i.e. HTTP or IPC
     * @param pollingInterval polling interval for responses from network nodes
     * @param scheduledExecutorService executor service to use for scheduled tasks.
     *                                 <strong>You are responsible for terminating this thread
     *                                 pool</strong>
     * @return new Web3j instance
     */
    public static Web3j build(Web3jService web3jService, long pollingInterval,
                              ScheduledExecutorService scheduledExecutorService) {
        return Web3j.build(web3jService, pollingInterval, scheduledExecutorService);
    }
}
* */