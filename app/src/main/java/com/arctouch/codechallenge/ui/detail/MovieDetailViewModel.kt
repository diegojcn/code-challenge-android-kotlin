package com.arctouch.codechallenge.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.arctouch.codechallenge.api.TmdbApi
import com.arctouch.codechallenge.model.MovieRequest
import com.arctouch.codechallenge.ui.model.MovieView
import com.arctouch.codechallenge.util.MovieImageUrlBuilder
import com.sdknetwork.NetworkException
import com.sdknetwork.handleError
import com.arctouch.codechallenge.util.rx.RxSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy

class MovieDetailViewModel(private val api: TmdbApi,
                           private val schedulers: RxSchedulers) : ViewModel() {

    private val _movieData = MutableLiveData<MovieView>()

    val movieData: LiveData<MovieView>
        get() = _movieData

    private val _error = MutableLiveData<NetworkException>()

    val error: LiveData<NetworkException>
        get() = _error

    private val composite = CompositeDisposable()

    private val builder = MovieImageUrlBuilder()

    fun loadMovie(request: MovieRequest) {

        api.movie(request.id, request.getMapParameters())
                .subscribeOn(schedulers.io())
                .observeOn(schedulers.ui())
                .map {
                    MovieView(it, builder)
                }
                .subscribeBy(
                        onSuccess = {
                            _movieData.postValue(it)
                        },
                        onError = {
                            _error.postValue(handleError(it))
                        })
                .addTo(composite)
    }

    fun clear() {
        composite.clear()
    }

}