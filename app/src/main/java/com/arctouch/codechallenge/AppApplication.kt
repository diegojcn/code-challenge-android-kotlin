package com.arctouch.codechallenge

import android.app.Application
import com.arctouch.codechallenge.api.TmdbApi
import com.arctouch.codechallenge.ui.detail.MovieDetailViewModel
import com.arctouch.codechallenge.ui.home.HomeViewModel
import com.arctouch.codechallenge.util.rx.Schedulers
import com.sdknetwork.SDKNetwork
import com.sdknetwork.SDKNetworkParameters
import okhttp3.Interceptor
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import java.net.URL

class AppApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startSDKNetwork()

        startDependencieInjector()
    }

    private fun startSDKNetwork() {

        val logging: HttpLoggingInterceptor?
        val value = hashSetOf<Interceptor>()

        if (BuildConfig.DEBUG) {
            logging = HttpLoggingInterceptor()
            logging.level = HttpLoggingInterceptor.Level.BODY
            value.add(logging)
        }

        SDKNetwork.setup(SDKNetworkParameters(url = URL(TmdbApi.URL), interceptors = value))
    }

    private fun startDependencieInjector() {

        startKoin {

            androidContext(baseContext)

        }

        loadModule()
    }

    override fun onTerminate() {
        super.onTerminate()

        stopKoin()
    }

    private fun loadModule() {

        val module = module {
            single { SDKNetwork.createService(TmdbApi::class.java) }
            viewModel { HomeViewModel(get(), Schedulers()) }
            viewModel { MovieDetailViewModel(get(), Schedulers()) }
        }

        loadKoinModules(module)
    }


}