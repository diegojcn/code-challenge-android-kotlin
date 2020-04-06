package com.sdknetwork

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

object SDKNetwork {

    private lateinit var retrofitInstance: Retrofit

    fun setup(params: SDKNetworkParameters) {

        retrofitInstance = Retrofit.Builder()
                .addCallAdapterFactory(params.callAdapterFactory
                        ?: RxJava2CallAdapterFactory.create())
                .addConverterFactory(MoshiConverterFactory.create())
                .client(getClient(params))
                .baseUrl(params.getBaseUrl())
                .build()


    }

    private fun getClient(params: SDKNetworkParameters): OkHttpClient {
        val builder = OkHttpClient.Builder()
                .connectTimeout(params.connectTimeout ?: 10, TimeUnit.SECONDS)
                .readTimeout(params.readTimeout ?: 10, TimeUnit.SECONDS)
                .writeTimeout(params.writeTimeout ?: 10, TimeUnit.SECONDS)

        params.interceptors.forEach { builder.addInterceptor(it) }
        params.networkInterceptors.forEach { builder.addNetworkInterceptor(it) }

        return builder.build()
    }


    fun <T : Any> createService(api: Class<T>): T = retrofitInstance.create(api)

}