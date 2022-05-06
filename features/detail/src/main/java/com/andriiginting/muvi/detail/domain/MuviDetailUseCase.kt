package com.andriiginting.muvi.detail.domain

import com.andriiginting.core_network.DetailsMovieData
import com.andriiginting.core_network.MovieItem
import com.andriiginting.muvi.detail.data.MuviDetailRepository
import com.andriiginting.uttils.maybeIo
import com.andriiginting.uttils.singleIo
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import javax.inject.Inject

interface MuviDetailUseCase {
    fun getDetailMovies(movieId: String): Single<DetailsMovieData>
    fun storeToDatabase(data: MovieItem): Flow<Long?>
    fun removeFromDatabase(movieId: String): Flow<Long?>
    fun checkFavoriteMovie(movieId: String): Flow<MovieItem?>
}

class MuviDetailUseCaseImpl @Inject constructor(
    private val repository: MuviDetailRepository,
    private val mapper: MuviDetailMapper
) : MuviDetailUseCase {

    override fun getDetailMovies(movieId: String): Single<DetailsMovieData> {
        return Single.zip(
            repository.getDetailMovie(movieId),
            repository.getSimilarMovie(movieId).map { it.resultsIntent },
            BiFunction { movieItem, similarMovies ->
                DetailsMovieData(similarMovies, movieItem)
            }
        )
    }

    override fun storeToDatabase(data: MovieItem): Flow<Long?> {
        val movieEntity = mapper.mapToMuviFavorite(data)
        return flow { emit(repository.storeToDatabase(movieEntity)) }
    }

    override fun removeFromDatabase(movieId: String): Flow<Long?> {
        return flow { emit(repository.removeFromDatabase(movieId))}
    }

    override fun checkFavoriteMovie(movieId: String): Flow<MovieItem> {
        return repository.isFavoriteMovie(movieId.toInt()).map(mapper::mapToMovieItem)
    }
}