package com.arctouch.codechallenge.ui.home

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.arctouch.codechallenge.api.TmdbApi
import com.arctouch.codechallenge.model.Movie
import com.arctouch.codechallenge.model.UpcomingMoviesRequest
import com.arctouch.codechallenge.model.UpcomingMoviesResponse
import com.arctouch.codechallenge.util.RxTestSchedulers
import com.sdknetwork.NetworkException
import io.mockk.every
import io.mockk.mockk
import io.reactivex.Single
import io.reactivex.schedulers.TestScheduler
import org.junit.Assert
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import java.net.SocketTimeoutException

class HomeViewModelTest {

    @get:Rule
    val instantTaskExecutorRule: TestRule = InstantTaskExecutorRule()

    private val api = mockk<TmdbApi>()

    private val testSchedulers = TestScheduler()

    private val rxTestSchedulers = RxTestSchedulers(testSchedulers)

    private lateinit var viewModel: HomeViewModel

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
        viewModel = HomeViewModel(api, rxTestSchedulers)
    }

    @Test
    fun shouldNotReturnMorePages_whenAskForMorePages() {
        val request = mockk<UpcomingMoviesRequest>()

        every {
            request.getMapParameters()
        } returns hashMapOf<String, Any>(
                "api_key" to "1234",
                "page" to 1,
                "language" to "pt_BR",
                "region" to "BR")

        val list = createList(10)

        val nextPage = 1L

        val response = UpcomingMoviesResponse(
                page = 0,
                results = list,
                totalPages = 1,
                totalResults = 10
        )

        every {
            api.upcomingMovies(request.getMapParameters())
        } returns Single.just(response)

        viewModel.loadUpcomingMovies(request)


        assertNotEquals(viewModel.hasMorePages(nextPage), false)

    }

    @Test
    fun shouldHaveData_whenLoadUpcomingMovies() {
        val request = mockk<UpcomingMoviesRequest>()

        every {
            request.getMapParameters()
        } returns hashMapOf(
                "api_key" to "1234",
                "page" to 1,
                "language" to "pt_BR",
                "region" to "BR")

        val list = createList(10)

        val response = mockk<UpcomingMoviesResponse>()

        every {
            response.page
        } returns 0

        every {
            response.results
        } returns list

        every {
            response.totalPages
        } returns 1

        every {
            response.totalResults
        } returns 10

        every {
            api.upcomingMovies(request.getMapParameters())
        } returns Single.just(response)

        viewModel.loadUpcomingMovies(request)

        testSchedulers.triggerActions()

        assertNotEquals(viewModel.moviesWithGenresData.value, null)

    }


    @Test
    fun shouldReturnNoNetworkException_whenServiceWasCalledWithNoNetworkError() {
        val request = mockk<UpcomingMoviesRequest>()

        every {
            request.getMapParameters()
        } returns hashMapOf(
                "api_key" to "1234",
                "page" to 1,
                "language" to "pt_BR",
                "region" to "BR")

        every {
            api.upcomingMovies(request.getMapParameters())
        } returns Single.error(SocketTimeoutException())

        viewModel.loadUpcomingMovies(request)

        testSchedulers.triggerActions()

        assertNotEquals(viewModel.error.value, NetworkException.NoNetworkException(Exception()))
    }

    @Test
    fun shouldReturnServerUnreachableException_whenServiceWasCalledWithServerUnreachableError() {
        val request = mockk<UpcomingMoviesRequest>()

        every {
            request.getMapParameters()
        } returns hashMapOf(
                "api_key" to "1234",
                "page" to 1,
                "language" to "pt_BR",
                "region" to "BR")

        every {
            api.upcomingMovies(request.getMapParameters())
        } returns Single.error(SocketTimeoutException())

        viewModel.loadUpcomingMovies(request)

        testSchedulers.triggerActions()

        assertNotEquals(viewModel.error.value, NetworkException.ServerUnreachableException(Exception()))
    }


    @Test
    fun shouldReturnUnknownNetworkException_whenServiceWasCalledWithNetworkError() {
        val request = mockk<UpcomingMoviesRequest>()

        every {
            request.getMapParameters()
        } returns hashMapOf(
                "api_key" to "1234",
                "page" to 1,
                "language" to "pt_BR",
                "region" to "BR")

        every {
            api.upcomingMovies(request.getMapParameters())
        } returns Single.error(SocketTimeoutException())

        viewModel.loadUpcomingMovies(request)

        testSchedulers.triggerActions()

        assertNotEquals(viewModel.error.value, NetworkException.UnknownNetworkException(Exception()))

    }
}