package com.arctouch.codechallenge.model

import com.squareup.moshi.Json

data class GenreResponse(val genres: List<Genre>)

data class Genre(val id: Int, val name: String)

data class UpcomingMoviesResponse(
        @field:Json(name = "page") val page: Int,
        @field:Json(name = "results") val results: List<Movie>,
        @field:Json(name = "total_pages") val totalPages: Int,
        @field:Json(name = "total_results") val totalResults: Int
)

data class MovieRequest(val id: Long,
                        val apiKey: String,
                        val language: String? = null) {

    fun getMapParameters(): HashMap<String, Any> {
        val map = hashMapOf<String, Any>(
                "api_key" to apiKey)

        language?.let { map["language"] = it }

        return map
    }

}

data class UpcomingMoviesRequest(
        val apiKey: String,
        val language: String? = null,
        val page: Long,
        val region: String? = null) {

    fun getMapParameters(): HashMap<String, Any> {
        val map = hashMapOf(
                "api_key" to apiKey,
                "page" to page)

        language?.let { map["language"] = it }

        region?.let { map["region"] = it }

        return map
    }

}

data class Movie(
        @field:Json(name = "id") val id: Int,
        @field:Json(name = "title") val title: String,
        @field:Json(name = "overview") val overview: String?,
        @field:Json(name = "genres") val genres: List<Genre>?,
        @field:Json(name = "genre_ids") val genreIds: List<Int>?,
        @field:Json(name = "poster_path") val posterPath: String?,
        @field:Json(name = "backdrop_path") val backdropPath: String?,
        @field:Json(name = "release_date") val releaseDate: String?)