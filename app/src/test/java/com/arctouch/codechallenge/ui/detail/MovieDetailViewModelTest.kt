package com.arctouch.codechallenge.ui.detail

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.arctouch.codechallenge.api.TmdbApi
import com.arctouch.codechallenge.model.Movie
import com.arctouch.codechallenge.model.MovieRequest
import com.arctouch.codechallenge.util.RxTestSchedulers
import io.mockk.every
import io.mockk.mockk
import io.reactivex.Single
import io.reactivex.schedulers.TestScheduler
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import java.util.*

class MovieDetailViewModelTest {


    @get:Rule
    val instantTaskExecutorRule: TestRule = InstantTaskExecutorRule()

    private val api = mockk<TmdbApi>()

    private val testSchedulers = TestScheduler()

    private val rxTestSchedulers = RxTestSchedulers(testSchedulers)

    private lateinit var viewModel: MovieDetailViewModel

    private fun createList(numberOfInvoices: Int): List<Movie> {
        val invoicesStub = mutableListOf<Movie>()
        for (i in 1..numberOfInvoices) {
            invoicesStub.add(getMovieStub())
        }
        return invoicesStub
    }

    fun getMovieStub(): Movie =
            Movie(1,
                    "",
                    "",
                    listOf(),
                    listOf(),
                    "",
                    "",
                    "")

    @Before
    fun setUp() {
        viewModel = MovieDetailViewModel(api, rxTestSchedulers)
    }

    @Test
    fun shouldHaveData_whenLoadUpcomingMovies() {
        val request = mockk<MovieRequest>()

        every {
            request.id
        } returns 1

        every {
            request.language
        } returns "pt_BR"

        every {
            request.apiKey
        } returns "1234"

        val map: HashMap<String, Any> = hashMapOf(
                "api_key" to "1234",
                "language" to "pt_BR")
        every {
            request.getMapParameters()
        } returns map

        val response = mockk<Movie>()

        every {
            response.title
        } returns "title"

        every {
            response.id
        } returns 1

        every {
            response.posterPath
        } returns "posterPath"

        every {
            response.overview
        } returns "overview"

        every {
            response.backdropPath
        } returns "backdropPath"

        every {
            response.genreIds
        } returns listOf()

        every {
            response.genres
        } returns listOf()

        every {
            response.releaseDate
        } returns "17-17-2017"

        every {
            api.movie(1, map)
        } returns Single.just(response)

        viewModel.loadMovie(request)

        testSchedulers.triggerActions()

        assertNotEquals(viewModel.movieData.value, null)

    }


}

