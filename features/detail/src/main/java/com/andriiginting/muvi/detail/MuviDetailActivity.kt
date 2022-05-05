package com.andriiginting.muvi.detail

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import coil.compose.rememberImagePainter
import com.airbnb.deeplinkdispatch.DeepLink
import com.andriiginting.base_ui.MuviBaseAdapter
import com.andriiginting.base_ui.MuviBaseComposeActivity
import com.andriiginting.core_network.BuildConfig
import com.andriiginting.core_network.MovieItem
import com.andriiginting.muvi.detail.presentation.MovieDetailViewState
import com.andriiginting.muvi.detail.presentation.MuviDetailViewHolder
import com.andriiginting.muvi.detail.presentation.MuviDetailViewModel
import com.andriiginting.uttils.loadImage
import com.andriiginting.uttils.makeGone
import com.andriiginting.uttils.makeVisible
import com.andriiginting.uttils.setGridView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_muvi_detail.*

@DeepLink("muvi://detail/{id}")
@AndroidEntryPoint
class MuviDetailActivity : MuviBaseComposeActivity() {

    private val viewModel by viewModels<MuviDetailViewModel>()

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

        viewModel.checkFavoriteMovie(movieId)
        setUpObserver()
    }

    private fun setupComposeView() {
        compose_view.apply {
            setContent {
                Surface() {
                    Column {
                        DetailBanner()
                    }
                }
            }
        }
    }

    @Composable
    private fun DetailBanner(){
        val state = viewModel.state.collectAsState().value
        val similarMovies = viewModel.haveSimilarMovie.value
        Box() {
            if(state is MovieDetailViewState.GetMovieData) {
                Image(
                    painter = rememberImagePainter(BuildConfig.IMAGE_BASE_URL + state.data.backdropPath),
                    contentDescription = null,
                    modifier = Modifier.height(200.dp).fillMaxWidth(),
                    contentScale = ContentScale.Crop,
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = {
                    onBackPressed()
                }) {
                    Image(
                        painter = rememberImagePainter(
                            ContextCompat.getDrawable(
                                this@MuviDetailActivity,
                                R.drawable.ic_back_arrow_back_24
                            )
                        ),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                }
                IconButton(onClick = {
                    favoriteClickListener(isFavorite)
                }) {
                    Image(
                        painter = rememberImagePainter(
                            ContextCompat.getDrawable(
                                this@MuviDetailActivity,
                                R.drawable.favorite_icon_selector
                            )
                        ),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }

    override fun setData() {
        viewModel.getDetailMovie(movieId)
    }

    private fun setUpDetailScreen(data: MovieItem) {
        tvMovieTitle.text = data.title
        tvMovieDescription.text = data.overview
        ivPosterBackdrop.loadImage(data.backdropPath.orEmpty())
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
//        fabFavorite.isSelected = isFavorite
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
//        viewModel.state.observe(this, Observer { state ->
//            when (state) {
//                is MovieDetailViewState.ShowLoading -> {
//                    pbDetailScreen.makeVisible()
////                    fabFavorite.isClickable = false
//                }
//
//                is MovieDetailViewState.HideLoading -> {
//                    pbDetailScreen.makeGone()
////                    fabFavorite.isClickable = true
//                }
//                is MovieDetailViewState.GetMovieData -> {
//                    movieItem = state.data
//                    setUpDetailScreen(state.data)
//
//                }
//                is MovieDetailViewState.GetSimilarMovieData -> {
//                    layoutEmptyStates.hideEmptyScreen()
//                    pbDetailScreen.makeGone()
//                    setUpSimilarMovies(state.data)
//                }
//
//                is MovieDetailViewState.SimilarMovieEmpty -> {
//                    tvMore.makeGone()
//                    layoutEmptyStates.showEmptyScreen()
//                    pbDetailScreen.makeGone()
//                    setUpSimilarMovies(emptyList())
//                }
//
//                is MovieDetailViewState.GetMovieDataError -> {
//                    pbDetailScreen.makeGone()
//                    layoutError.showErrorScreen()
//                }
//
//                is MovieDetailViewState.StoredFavoriteMovie -> {
//                    isFavorite = true
////                    fabFavorite.isClickable = true
//                    setUpFavoriteButton(isFavorite)
//                }
//
//                is MovieDetailViewState.FailedStoreFavoriteMovie -> {
//                    Toast.makeText(
//                        this,
//                        getString(R.string.toast_error_message),
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
//
//                is MovieDetailViewState.RemovedFavoriteMovie -> {
//                    isFavorite = false
//                    setUpFavoriteButton(isFavorite)
//                }
//
//                is MovieDetailViewState.FailedRemoveFavoriteMovie -> {
//                    Toast.makeText(
//                        this,
//                        getString(R.string.toast_error_message),
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
//
//                is MovieDetailViewState.FavoriteMovie -> {
//                    isFavorite = state.isFavorite
//                    setUpFavoriteButton(state.isFavorite)
//                }
//            }
//        })
    }

    companion object {
        private const val MOVIE_ID_PARAMS = "id"
        private const val GRID_COLUMN_COUNT = 4
    }
}