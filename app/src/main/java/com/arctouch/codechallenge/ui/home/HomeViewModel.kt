package com.arctouch.codechallenge.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.arctouch.codechallenge.api.TmdbApi
import com.arctouch.codechallenge.data.Cache
import com.arctouch.codechallenge.model.Movie
import com.arctouch.codechallenge.model.UpcomingMoviesRequest
import com.sdknetwork.NetworkException
import com.sdknetwork.handleError
import com.arctouch.codechallenge.util.rx.RxSchedulers
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy


class HomeViewModel(private val api: TmdbApi,
                    private val schedulers: RxSchedulers) : ViewModel() {

    private val _moviesWithGenresData = MutableLiveData<List<Movie>>()

    val moviesWithGenresData: LiveData<List<Movie>>
        get() = _moviesWithGenresData

    private val _error = MutableLiveData<NetworkException>()

    val error: LiveData<NetworkException>
        get() = _error

    private val compositeUpcomingMovies = CompositeDisposable()

    private val compositeGenres = CompositeDisposable()

    private var totalPagesApi = Int.MAX_VALUE

    fun hasMorePages(page: Long) = page < totalPagesApi

    fun loadGenres() {
        api.genres(TmdbApi.API_KEY, TmdbApi.DEFAULT_LANGUAGE)
                .subscribeOn(schedulers.io())
                .observeOn(schedulers.ui())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                        onSuccess = {
                            Cache.cacheGenres(it.genres)
                        },
                        onError = {
                            _error.postValue(handleError(it))
                        })
                .addTo(compositeGenres)
    }

    fun loadUpcomingMovies(upcomingMoviesRequest: UpcomingMoviesRequest) {
        api.upcomingMovies(upcomingMoviesRequest.getMapParameters())
                .subscribeOn(schedulers.io())
                .observeOn(schedulers.ui())
                .map {
                    totalPagesApi = it.totalPages
                    it.results.map { movie ->
                        movie.copy(genres = Cache.genres.filter { movie.genreIds?.contains(it.id) == true })
                    }
                }
                .subscribeBy(
                        onSuccess = {
                            _moviesWithGenresData.postValue(it)
                        },
                        onError = {
                            _error.postValue(handleError(it))
                        })
                .addTo(compositeUpcomingMovies)
    }

    fun clear() {
        compositeUpcomingMovies.clear()
        compositeGenres.clear()
    }

}