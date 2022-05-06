package com.andriiginting.muvi.detail.presentation

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.andriiginting.base_ui.MuviBaseFlowViewModel
import com.andriiginting.core_network.DetailsMovieData
import com.andriiginting.core_network.MovieItem
import com.andriiginting.muvi.detail.domain.MuviDetailUseCase
import com.andriiginting.uttils.singleIo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MuviDetailViewModel @Inject constructor(
    private val useCase: MuviDetailUseCase
) : MuviBaseFlowViewModel<MovieDetailViewState>() {

    override val initialState: MovieDetailViewState
        get() = MovieDetailViewState.ShowLoading

    private val _haveSimilarMovie: MutableState<List<MovieItem>> = mutableStateOf(listOf())
    val haveSimilarMovie: State<List<MovieItem>>
        get() = _haveSimilarMovie

    private var currentMovieItem: MovieItem = MovieItem()

    private val _favoritedMovie: MutableState<Boolean> = mutableStateOf(false)
    val favoritedMovie: State<Boolean>
        get() = _favoritedMovie

    fun getDetailMovie(movieId: String) {
        useCase.getDetailMovies(movieId)
            .doOnSubscribe { showLoading() }
            .compose(singleIo())
            .subscribe({ data ->
                _state.value = MovieDetailViewState.GetMovieData(data.movie)
                currentMovieItem = data.movie
                handleSimilarMovieData(data)
            }, { error ->
                _state.value = MovieDetailViewState.GetMovieDataError(error)
            }).let(addDisposable::add)
    }

    private fun showLoading() {
        viewModelScope.launch {
            _state.emit(MovieDetailViewState.ShowLoading)
        }
    }

    fun storeFavoriteMovie() {
        viewModelScope.launch(Dispatchers.IO) {
            useCase.storeToDatabase(currentMovieItem).collect {
                _favoritedMovie.value = it != null
            }
        }
    }

    fun removeFavoriteMovie(movieId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            useCase.removeFromDatabase(movieId).collect {
                _favoritedMovie.value = it != null
            }
        }
    }

    fun checkFavoriteMovie(movieId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            useCase.checkFavoriteMovie(movieId).collect {
                _favoritedMovie.value = !it?.title.isNullOrEmpty()
            }
        }
    }

    private fun handleSimilarMovieData(data: DetailsMovieData) {
        viewModelScope.launch(Dispatchers.IO) {
            if (data.similarMovies.isEmpty()) {
                _haveSimilarMovie.value = listOf()
            } else {
                _haveSimilarMovie.value = data.similarMovies
            }
        }
    }
}

sealed class MovieDetailViewState {
    object ShowLoading : MovieDetailViewState()
    object StoredFavoriteMovie : MovieDetailViewState()
    object FailedStoreFavoriteMovie : MovieDetailViewState()
    object RemovedFavoriteMovie : MovieDetailViewState()
    object FailedRemoveFavoriteMovie : MovieDetailViewState()

    data class GetMovieData(val data: MovieItem) : MovieDetailViewState()
    data class GetMovieDataError(val error: Throwable) : MovieDetailViewState()
    data class FavoriteMovie(val isFavorite: Boolean) : MovieDetailViewState()
}