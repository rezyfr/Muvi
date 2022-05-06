package com.andriiginting.muvi.detail.data

import com.andriiginting.common_database.MuviDatabase
import com.andriiginting.common_database.MuviFavorites
import com.andriiginting.core_network.MovieItem
import com.andriiginting.core_network.MovieResponse
import com.andriiginting.core_network.MuviDetailService
import io.reactivex.Maybe
import io.reactivex.Single
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface MuviDetailRepository {
    fun getDetailMovie(movieId: String): Single<MovieItem>
    fun getSimilarMovie(movieId: String): Single<MovieResponse>
    fun storeToDatabase(data: MuviFavorites): Long
    fun isFavoriteMovie(movieId: Int): Flow<MuviFavorites?>
    fun removeFromDatabase(movieId: String): Long
}

class MuviDetailRepositoryImpl @Inject constructor(
    private val service: MuviDetailService,
    private val database: MuviDatabase
) : MuviDetailRepository {
    override fun getDetailMovie(movieId: String): Single<MovieItem> {
        return service.getDetailMovies(movieId)
    }

    override fun getSimilarMovie(movieId: String): Single<MovieResponse> {
        return service.getSimilarMovie(movieId)
    }

    override fun storeToDatabase(data: MuviFavorites): Long {
        return database.theaterDAO().insertFavoriteMovie(data)
    }

    override fun isFavoriteMovie(movieId: Int): Flow<MuviFavorites?> {
        return database.theaterDAO().isFavorite(movieId)
    }

    override fun removeFromDatabase(movieId: String): Long {
        return database.theaterDAO().deleteMovie(movieId)
    }
}