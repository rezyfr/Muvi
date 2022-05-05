package com.andriiginting.muvi.home.ui

import androidx.lifecycle.viewModelScope
import com.andriiginting.base_ui.MuviBaseFlowViewModel
import com.andriiginting.core_network.HomeBannerData
import com.andriiginting.core_network.MovieResponse
import com.andriiginting.muvi.home.domain.Filter
import com.andriiginting.muvi.home.domain.MuviHomeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MuviHomeViewModel @Inject constructor(
    private val useCase: MuviHomeUseCase
) : MuviBaseFlowViewModel<HomeViewState>() {

    private val _bannerState: MutableStateFlow<HomeBannerState> =
        MutableStateFlow(HomeBannerState.BannerEmpty)
    val bannerState: StateFlow<HomeBannerState>
        get() = _bannerState

    private val _filterState: MutableStateFlow<Filter> = MutableStateFlow(Filter.ALL)
    val filterState: StateFlow<Filter>
        get() = _filterState

    override val initialState: HomeViewState
        get() = HomeViewState.EmptyScreen

    fun setFilterType(filter: Filter?) {
        if (filter != null) {
            _filterState.value = filter
            when (filter) {
                Filter.ALL -> getMovieData()
                Filter.LATEST -> getLatestMovieData()
                Filter.NOW_PLAYING -> getNowPlayingMovieData()
                Filter.TOP_RATED -> getTopRatedMovieData()
                Filter.UPCOMING -> getUpcomingMovieData()
            }
        }
    }

    fun getMovieData() {
        useCase.getAllMovies()
            .doOnSubscribe { _state.value = HomeViewState.ShowLoading }
            .doAfterTerminate { _state.value = HomeViewState.HideLoading }
            .subscribe({ data ->
                handleDataSuccess(data)
            }, { error ->
                handleError(error)
            }).let(addDisposable::add)
    }

    fun getHomeBanner() {
        useCase.getHomeBanner()
            .subscribe({ data ->
                viewModelScope.launch(Dispatchers.IO) {
                    _bannerState.emit(HomeBannerState.GetHomeBannerData(data))
                }
            }, {
                viewModelScope.launch { _bannerState.emit(HomeBannerState.BannerError) }
            }).let(addDisposable::add)
    }

    private fun getLatestMovieData() {
        useCase.getLatestMovies()
            .doOnSubscribe { _state.value = HomeViewState.ShowLoading }
            .doAfterTerminate { _state.value = HomeViewState.HideLoading }
            .subscribe({ data ->
                handleDataSuccess(data)
            }, { error ->
                handleError(error)
            }).let(addDisposable::add)
    }

    private fun getNowPlayingMovieData() {
        useCase.getNowPlayingMovies()
            .doOnSubscribe { _state.value = HomeViewState.ShowLoading }
            .doAfterTerminate { _state.value = HomeViewState.HideLoading }
            .subscribe({ data ->
                handleDataSuccess(data)
            }, { error ->
                handleError(error)
            }).let(addDisposable::add)
    }

    private fun getTopRatedMovieData() {
        useCase.getTopRatedMovies()
            .doOnSubscribe { _state.value = HomeViewState.ShowLoading }
            .doAfterTerminate { _state.value = HomeViewState.HideLoading }
            .subscribe({ data ->
                handleDataSuccess(data)
            }, { error ->
                handleError(error)
            }).let(addDisposable::add)
    }

    private fun getUpcomingMovieData() {
        useCase.getUpcomingMovies()
            .doOnSubscribe { _state.value = HomeViewState.ShowLoading }
            .doAfterTerminate { _state.value = HomeViewState.HideLoading }
            .subscribe({ data ->
                handleDataSuccess(data)
            }, { error ->
                handleError(error)
            }).let(addDisposable::add)
    }

    private fun handleError(error: Throwable) {
        viewModelScope.launch {
            _state.emit(HomeViewState.GetMovieDataError(error))
        }
    }

    private fun handleDataSuccess(data: MovieResponse) {
        viewModelScope.launch(Dispatchers.IO) {
            if (data.resultsIntent.isEmpty()) {
                _state.emit(HomeViewState.EmptyScreen)
            } else {
                _state.emit(HomeViewState.GetMovieData(data))
            }
        }
    }
}
    sealed class HomeBannerState {
        object BannerError : HomeBannerState()
        object BannerEmpty : HomeBannerState()
        data class GetHomeBannerData(val data: HomeBannerData) : HomeBannerState()
    }

    sealed class HomeViewState {
        object ShowLoading : HomeViewState()
        object HideLoading : HomeViewState()
        object EmptyScreen : HomeViewState()

        data class GetMovieData(val data: MovieResponse) : HomeViewState()
        data class GetMovieDataError(val error: Throwable) : HomeViewState()
    }
