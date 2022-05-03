package com.andriiginting.muvi.home.ui

import androidx.lifecycle.viewModelScope
import com.andriiginting.base_ui.MuviBaseViewModel
import com.andriiginting.core_network.HomeBannerData
import com.andriiginting.core_network.MovieResponse
import com.andriiginting.muvi.home.domain.Filter
import com.andriiginting.muvi.home.domain.MuviHomeUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class MuviHomeViewModel @Inject constructor(
    private val useCase: MuviHomeUseCase
) : MuviBaseViewModel<HomeViewState>() {

    private val _bannerState: MutableStateFlow<HomeBannerState> =
        MutableStateFlow(HomeBannerState.BannerEmpty)
    val bannerState: StateFlow<HomeBannerState>
        get() = _bannerState

    private val _filterState: MutableStateFlow<Filter> = MutableStateFlow(Filter.ALL)
    val filterState: StateFlow<Filter>
        get() = _filterState

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
                _state.value = HomeViewState.GetMovieDataError(error)
            }).let(addDisposable::add)
    }

    fun getHomeBanner() {
        useCase.getHomeBanner()
            .subscribe({ data ->
                viewModelScope.launch(Dispatchers.IO) {
                    _bannerState.emit(HomeBannerState.GetHomeBannerData(data))
                }
            }, {
                _bannerState.value = HomeBannerState.BannerError
            }).let(addDisposable::add)
    }

    private fun getLatestMovieData() {
        useCase.getLatestMovies()
            .doOnSubscribe { _state.value = HomeViewState.ShowLoading }
            .doAfterTerminate { _state.value = HomeViewState.HideLoading }
            .subscribe({ data ->
                handleDataSuccess(data)
            }, { error ->
                _state.value = HomeViewState.GetMovieDataError(error)
            }).let(addDisposable::add)
    }

    private fun getNowPlayingMovieData() {
        useCase.getNowPlayingMovies()
            .doOnSubscribe { _state.value = HomeViewState.ShowLoading }
            .doAfterTerminate { _state.value = HomeViewState.HideLoading }
            .subscribe({ data ->
                handleDataSuccess(data)
            }, { error ->
                _state.value = HomeViewState.GetMovieDataError(error)
            }).let(addDisposable::add)
    }

    private fun getTopRatedMovieData() {
        useCase.getTopRatedMovies()
            .doOnSubscribe { _state.value = HomeViewState.ShowLoading }
            .doAfterTerminate { _state.value = HomeViewState.HideLoading }
            .subscribe({ data ->
                handleDataSuccess(data)
            }, { error ->
                _state.value = HomeViewState.GetMovieDataError(error)
            }).let(addDisposable::add)
    }

    private fun getUpcomingMovieData() {
        useCase.getUpcomingMovies()
            .doOnSubscribe { _state.value = HomeViewState.ShowLoading }
            .doAfterTerminate { _state.value = HomeViewState.HideLoading }
            .subscribe({ data ->
                handleDataSuccess(data)
            }, { error ->
                _state.value = HomeViewState.GetMovieDataError(error)
            }).let(addDisposable::add)
    }

    private fun handleDataSuccess(data: MovieResponse) {
        if (data.resultsIntent.isEmpty()) {
            _state.value = HomeViewState.EmptyScreen
        } else {
            _state.postValue(HomeViewState.GetMovieData(data))
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