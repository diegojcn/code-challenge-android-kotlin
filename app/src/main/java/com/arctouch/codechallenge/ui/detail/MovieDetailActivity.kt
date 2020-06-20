package com.arctouch.codechallenge.ui.detail

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.arctouch.codechallenge.R
import com.arctouch.codechallenge.api.TmdbApi
import com.arctouch.codechallenge.model.MovieRequest
import com.arctouch.codechallenge.ui.home.MOVIE_ID
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import kotlinx.android.synthetic.main.movie_detail_activity.*
import org.koin.android.ext.android.inject


class MovieDetailActivity : AppCompatActivity() {

    private val viewModel: MovieDetailViewModel by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.movie_detail_activity)

        actionBar?.setDisplayHomeAsUpEnabled(true)

        intent.extras?.let {

            viewModel.loadMovie(MovieRequest(it.getLong(MOVIE_ID), TmdbApi.API_KEY, TmdbApi.DEFAULT_LANGUAGE))

            subscribeToLiveData()
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.home -> {
                // app icon in action bar clicked; goto parent activity.
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun subscribeToLiveData() {

        with(viewModel) {

            movieData.observe(this@MovieDetailActivity, Observer {
                movie_name.text = it.title
                movie_genders.text = it.genders
                movie_release_date.text = it.releaseDate
                overview.text = it.overview

                Glide.with(this@MovieDetailActivity)
                        .load(it.posterPath)
                        .listener(object : RequestListener<Drawable> {
                            override fun onLoadFailed(e: GlideException?,
                                                      model: Any?,
                                                      target: Target<Drawable>?,
                                                      isFirstResource: Boolean): Boolean {

                                return false
                            }

                            override fun onResourceReady(resource: Drawable?,
                                                         model: Any?,
                                                         target: Target<Drawable>?,
                                                         dataSource: DataSource?,
                                                         isFirstResource: Boolean): Boolean {
                                poster_image.visibility = View.VISIBLE
                                progress_poster_image.visibility = View.GONE

                                return false
                            }


                        })
                        .apply(RequestOptions().placeholder(R.drawable.ic_image_placeholder))
                        .into(poster_image)

                Glide.with(this@MovieDetailActivity)
                        .load(it.backdropPath)
                        .apply(RequestOptions().placeholder(R.drawable.ic_image_placeholder))
                        .listener(object : RequestListener<Drawable> {
                            override fun onLoadFailed(e: GlideException?,
                                                      model: Any?,
                                                      target: Target<Drawable>?,
                                                      isFirstResource: Boolean): Boolean {

                                return false
                            }

                            override fun onResourceReady(resource: Drawable?,
                                                         model: Any?,
                                                         target: Target<Drawable>?,
                                                         dataSource: DataSource?,
                                                         isFirstResource: Boolean): Boolean {
                                backdrop_image.visibility = View.VISIBLE
                                progress_backdrop_image.visibility = View.GONE
                                return false
                            }


                        })
                        .into(backdrop_image)


                progressBar.visibility = View.GONE

            })

            error.observe(this@MovieDetailActivity, Observer {
                Toast.makeText(this@MovieDetailActivity,
                        getString(R.string.unexpected_error), Toast.LENGTH_SHORT).show()
                finish()
            })

        }
    }


    override fun onDestroy() {
        super.onDestroy()
        viewModel.clear()
    }

}