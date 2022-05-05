package com.andriiginting.muvi.detail.presentation

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.andriiginting.base_ui.MuviBaseFlowViewModel
import com.andriiginting.core_network.DetailsMovieData
import com.andriiginting.core_network.MovieItem
import com.andriiginting.muvi.detail.domain.MuviDetailUseCase
import com.andriiginting.uttils.singleIo
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
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

    fun getDetailMovie(movieId: String) {
        useCase.getDetailMovies(movieId)
            .doOnSubscribe { showLoading() }
            .compose(singleIo())
            .subscribe({ data ->
                _state.value = MovieDetailViewState.GetMovieData(data.movie)
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

    fun storeFavoriteMovie(movieItem: MovieItem) {
        showLoading()
        useCase.storeToDatabase(movieItem)
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                _state.value = MovieDetailViewState.StoredFavoriteMovie
            }, { error ->
                Timber.e(error, "failed to store favorite movie")
                _state.value = MovieDetailViewState.FailedStoreFavoriteMovie
            })
            .let(addDisposable::add)
    }

    fun removeFavoriteMovie(movieId: String) {
        useCase.removeFromDatabase(movieId)
            .doOnSubscribe { showLoading() }
            .subscribe({
                _state.value = MovieDetailViewState.RemovedFavoriteMovie
            }, { error ->
                Timber.e(error, "failed to remove favorite movie")
                _state.value = MovieDetailViewState.FailedRemoveFavoriteMovie
            })
            .let(addDisposable::add)
    }

    fun checkFavoriteMovie(movieId: String) {
        useCase.checkFavoriteMovie(movieId)
            .subscribe({ data ->
                if (data != null) {
                    _state.value = MovieDetailViewState.FavoriteMovie(true)
                }
            }, { error ->
                Timber.e(error, "failed to remove favorite movie")
                _state.value = MovieDetailViewState.FavoriteMovie(false)
            })
            .let(addDisposable::add)
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