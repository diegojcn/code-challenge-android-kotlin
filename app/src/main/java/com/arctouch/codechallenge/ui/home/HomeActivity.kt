package com.arctouch.codechallenge.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.arctouch.codechallenge.R
import com.arctouch.codechallenge.api.TmdbApi
import com.arctouch.codechallenge.model.Movie
import com.arctouch.codechallenge.model.UpcomingMoviesRequest
import com.arctouch.codechallenge.ui.detail.MovieDetailActivity
import com.arctouch.codechallenge.ui.listeners.EndlessRecyclerViewScrollListener
import com.arctouch.codechallenge.ui.listeners.OnItemClickListener
import com.arctouch.codechallenge.ui.listeners.RecyclerItemClickListener
import kotlinx.android.synthetic.main.home_activity.*
import org.koin.android.ext.android.inject


private const val START_PAGE = 1L
private const val PAGE_SIZE = 10
const val MOVIE_ID = "movie_id"

class HomeActivity : AppCompatActivity() {

    private val viewModel: HomeViewModel by inject()

    private val adapter: HomeAdapter by lazy { HomeAdapter() }

    private lateinit var endlessRecyclerViewScrollListener: EndlessRecyclerViewScrollListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_activity)

        viewModel.loadGenres()
        loadData(START_PAGE)

        startRecyclerView()
        subscribeToLiveData()

    }

    private fun loadData(page: Long) {
        with(viewModel) {
            if (hasMorePages(page)) {
                loadUpcomingMovies(
                        UpcomingMoviesRequest(
                                apiKey = TmdbApi.API_KEY,
                                page = page,
                                region = TmdbApi.DEFAULT_REGION,
                                language = TmdbApi.DEFAULT_LANGUAGE
                        ))
            }
        }

    }

    private fun subscribeToLiveData() {

        with(viewModel) {

            moviesWithGenresData.observe(this@HomeActivity, Observer {
                adapter.update(it)
                progressBar.visibility = View.GONE
            })

            error.observe(this@HomeActivity, Observer {
                Toast.makeText(this@HomeActivity, getString(R.string.unexpected_error), Toast.LENGTH_SHORT)
                        .show()
            })

        }
    }

    private fun startRecyclerView() {
        recyclerView.adapter = adapter
        recyclerView.addOnItemTouchListener(
                RecyclerItemClickListener(
                        this,
                        recyclerView, createOnItemClickListener()
                )
        )
        val layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        recyclerView.layoutManager = layoutManager
        endlessRecyclerViewScrollListener = startEndlessRecyclerViewScrollListener(layoutManager)
        endlessRecyclerViewScrollListener.let { recyclerView.addOnScrollListener(it) }

    }

    private fun createOnItemClickListener():
            OnItemClickListener {
        return object : OnItemClickListener {
            override fun onItemClick(view: View, position: Int) {
                adapter.run {
                    navigateToDetail(getItem(position))
                }
            }

            override fun onItemLongClick(view: View, position: Int) {}
        }
    }


    private fun startEndlessRecyclerViewScrollListener(layout: LinearLayoutManager): EndlessRecyclerViewScrollListener {
        return object : EndlessRecyclerViewScrollListener(layout, PAGE_SIZE) {
            override fun onLoadMore(page: Long, totalItemsCount: Int, view: RecyclerView) {
                loadData(page)
            }
        }
    }

    private fun navigateToDetail(selectedMovie: Movie) {

        val intent = Intent(this, MovieDetailActivity::class.java).apply {
            putExtra(MOVIE_ID, selectedMovie.id.toLong())
        }
        startActivity(intent)
    }

}
