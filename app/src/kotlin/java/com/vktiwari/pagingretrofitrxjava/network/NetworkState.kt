package com.vktiwari.pagingretrofitrxjava.network

class NetworkState {

    enum class Status {
        RUNNING,
        SUCCESS,
        FAILED
    }

    private var status: Status? = null
    private var message: String?=null

    constructor(status: Status, message: String) {
        this.status = status
        this.message = message
    }

    constructor(status: Status){
        this.status = status
    }

    companion object {
        val LOADED: NetworkState = NetworkState(Status.SUCCESS);
        val LOADING = NetworkState(Status.RUNNING, "Loading please wait");

        fun error(message: String?): NetworkState {
            return NetworkState(Status.FAILED, message ?: "Something went wrong , try again")
        }
    }

    fun getStatus(): Status? {
        return status
    }

    fun getMessage(): String? {
        return message
    }

}