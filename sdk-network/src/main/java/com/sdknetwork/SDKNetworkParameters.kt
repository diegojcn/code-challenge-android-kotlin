package com.sdknetwork

import okhttp3.Interceptor
import retrofit2.CallAdapter
import java.net.URL

data class SDKNetworkParameters(
        val url: URL,
        val interceptors: Set<Interceptor> = emptySet(),
        val networkInterceptors: Set<Interceptor> = emptySet(),
        val headers: HashMap<String, String> = HashMap(),
        val callAdapterFactory: CallAdapter.Factory? = null,
        val connectTimeout: Long? = null,
        val readTimeout: Long? = null,
        val writeTimeout: Long? = null
) {
    fun getBaseUrl() = "${url.protocol}://${url.host}${url.path}"
}