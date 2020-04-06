package com.arctouch.codechallenge.ui.model

import com.arctouch.codechallenge.model.Movie
import com.arctouch.codechallenge.util.MovieImageUrlBuilder

class MovieView(val id: Int,
                val title: String,
                val overview: String?,
                val genders: String?,
                val posterPath: String?,
                val backdropPath: String?,
                val releaseDate: String?) {


    constructor(movie: Movie, builder: MovieImageUrlBuilder) : this(
            id = movie.id,
            title = movie.title,
            overview = movie.overview,
            genders = movie.genres?.joinToString(separator = ", ") { it.name },
            posterPath = movie.posterPath?.let { builder.buildPosterUrl(it) },
            backdropPath = movie.backdropPath?.let { builder.buildBackdropUrl(it) },
            releaseDate = movie.releaseDate

    )
}