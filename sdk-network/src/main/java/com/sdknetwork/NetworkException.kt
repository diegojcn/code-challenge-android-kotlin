package com.sdknetwork

import retrofit2.HttpException
import java.lang.RuntimeException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

sealed class NetworkException(error: Throwable) : RuntimeException(error) {

    class NoNetworkException(error: Throwable) : NetworkException(error)

    class ServerUnreachableException(error: Throwable) : NetworkException(error)

    class HttpCallFailureException(error: Throwable) : NetworkException(error)

    class UnknownNetworkException(error: Throwable) : NetworkException(error)
}


fun handleError(error: Throwable): NetworkException {

    return when (error) {
        is SocketTimeoutException -> NetworkException.NoNetworkException(error)

        is UnknownHostException -> NetworkException.ServerUnreachableException(error)

        is HttpException -> NetworkException.HttpCallFailureException(error)

        else -> NetworkException.UnknownNetworkException(error)
    }
}

