package com.andriiginting.muvi.detail

import android.os.Bundle
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import com.airbnb.deeplinkdispatch.DeepLink
import com.andriiginting.base_ui.MuviBaseActivity
import com.andriiginting.base_ui.MuviBaseAdapter
import com.andriiginting.core_network.MovieItem
import com.andriiginting.muvi.detail.di.MuviDetailInjector
import com.andriiginting.muvi.detail.presentation.MovieDetailViewState
import com.andriiginting.muvi.detail.presentation.MuviDetailViewHolder
import com.andriiginting.muvi.detail.presentation.MuviDetailViewModel
import com.andriiginting.uttils.loadImage
import com.andriiginting.uttils.makeGone
import com.andriiginting.uttils.makeVisible
import com.andriiginting.uttils.setGridView
import kotlinx.android.synthetic.main.activity_muvi_detail.*

@DeepLink("muvi://detail/{id}")
class MuviDetailActivity : MuviBaseActivity<MuviDetailViewModel>() {

    private var movieId: String = ""
    private var movieItem: MovieItem = MovieItem.default()
    private var isFavorite: Boolean = false

    override fun getLayoutId(): Int = R.layout.activity_muvi_detail

    override fun setupView() {
        setupComposeView()
        if (intent.getBooleanExtra(DeepLink.IS_DEEP_LINK, false)) {
            val params: Bundle? = intent.extras
            movieId = params?.getString(MOVIE_ID_PARAMS).toString()
        }

        ivBackNavigation.setOnClickListener {
            onBackPressed()
        }
        viewModel.checkFavoriteMovie(movieId)
        setUpObserver()
    }

    private fun setupComposeView() {
        compose_view.apply {
            setContent {
                Scaffold(modifier = Modifier.padding(8.dp)) {
                    Box(
                        modifier = Modifier.fillMaxWidth().height(200.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            IconButton(onClick = {
                                onBackPressed() }) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_back_arrow_back_24),
                                    null
                                )
                            }
                            IconButton(onClick = {
                                favoriteClickListener(isFavorite) }) {
                                Icon(
                                    painter = painterResource(id = R.drawable.favorite_icon_selector),
                                    null
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    override fun setData() {
        viewModel.getDetailMovie(movieId)
    }

    override fun setupInjector() = MuviDetailInjector.of(this)

    override fun setViewModel(): Class<MuviDetailViewModel> = MuviDetailViewModel::class.java

    override fun setObserver(): FragmentActivity = this

    private fun setUpDetailScreen(data: MovieItem) {
        tvMovieTitle.text = data.title
        tvMovieDescription.text = data.overview
        ivPosterBackdrop.loadImage(data.backdropPath.orEmpty())
        fabFavorite.setOnClickListener {
            favoriteClickListener(isFavorite)
        }

        fabBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun setUpSimilarMovies(list: List<MovieItem>) {
        val detailAdapter: MuviBaseAdapter<MovieItem, MuviDetailViewHolder> =
            MuviBaseAdapter({ parent, _ ->
                MuviDetailViewHolder.inflate(parent)
            }, { viewHolder, _, item ->
                viewHolder.bind(item.posterPath.orEmpty())
            })

        rvSimilarMovie.apply {
            setGridView(GRID_COLUMN_COUNT)
            adapter = detailAdapter
        }
        detailAdapter.safeAddAll(list)
    }

    private fun setUpFavoriteButton(isFavorite: Boolean) {
        fabFavorite.isSelected = isFavorite
    }

    private fun favoriteClickListener(isFavorite: Boolean) {
        setUpFavoriteButton(!isFavorite)
        if (isFavorite) {
            viewModel.removeFavoriteMovie(movieId)
        } else {
            movieItem.let(viewModel::storeFavoriteMovie)
        }
    }

    private fun setUpObserver() {
        viewModel.state.observe(this, Observer { state ->
            when (state) {
                is MovieDetailViewState.ShowLoading -> {
                    pbDetailScreen.makeVisible()
                    fabFavorite.isClickable = false
                }

                is MovieDetailViewState.HideLoading -> {
                    pbDetailScreen.makeGone()
                    fabFavorite.isClickable = true
                }
                is MovieDetailViewState.GetMovieData -> {
                    movieItem = state.data
                    setUpDetailScreen(state.data)

                }
                is MovieDetailViewState.GetSimilarMovieData -> {
                    layoutEmptyStates.hideEmptyScreen()
                    pbDetailScreen.makeGone()
                    setUpSimilarMovies(state.data)
                }

                is MovieDetailViewState.SimilarMovieEmpty -> {
                    tvMore.makeGone()
                    layoutEmptyStates.showEmptyScreen()
                    pbDetailScreen.makeGone()
                    setUpSimilarMovies(emptyList())
                }

                is MovieDetailViewState.GetMovieDataError -> {
                    pbDetailScreen.makeGone()
                    layoutError.showErrorScreen()
                }

                is MovieDetailViewState.StoredFavoriteMovie -> {
                    isFavorite = true
                    fabFavorite.isClickable = true
                    setUpFavoriteButton(isFavorite)
                }

                is MovieDetailViewState.FailedStoreFavoriteMovie -> {
                    Toast.makeText(
                        this,
                        getString(R.string.toast_error_message),
                        Toast.LENGTH_SHORT
                    ).show()
                }

                is MovieDetailViewState.RemovedFavoriteMovie -> {
                    isFavorite = false
                    setUpFavoriteButton(isFavorite)
                }

                is MovieDetailViewState.FailedRemoveFavoriteMovie -> {
                    Toast.makeText(
                        this,
                        getString(R.string.toast_error_message),
                        Toast.LENGTH_SHORT
                    ).show()
                }

                is MovieDetailViewState.FavoriteMovie -> {
                    isFavorite = state.isFavorite
                    setUpFavoriteButton(state.isFavorite)
                }
            }
        })
    }

    companion object {
        private const val MOVIE_ID_PARAMS = "id"
        private const val GRID_COLUMN_COUNT = 4
    }
}