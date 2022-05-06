package com.andriiginting.muvi.detail

import android.os.Bundle
import androidx.activity.viewModels
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import coil.compose.rememberImagePainter
import com.airbnb.deeplinkdispatch.DeepLink
import com.andriiginting.base_ui.MuviBaseAdapter
import com.andriiginting.base_ui.MuviBaseComposeActivity
import com.andriiginting.core_network.BuildConfig
import com.andriiginting.core_network.MovieItem
import com.andriiginting.muvi.detail.presentation.MovieDetailViewState
import com.andriiginting.muvi.detail.presentation.MuviDetailViewHolder
import com.andriiginting.muvi.detail.presentation.MuviDetailViewModel
import com.andriiginting.navigation.DetailNavigator
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
                        MovieDetail()
                        Text(
                            text = stringResource(id = R.string.more_like_this),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 16.dp, start = 8.dp, end = 8.dp, bottom = 8.dp),
                            color = Color.Black
                        )
                        SimilarMovies()
                    }
                }
            }
        }
    }

    @Composable
    private fun MovieDetail() {
        val state = viewModel.state.collectAsState().value
        if(state is MovieDetailViewState.GetMovieData){
            Text(
                text = state.data.title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(8.dp),
                color = Color.Black
            )
            Text(
                text = state.data.overview,
                fontSize = 14.sp,
                modifier = Modifier.padding(horizontal = 8.dp),
            )
        }
    }

    @Composable
    private fun DetailBanner() {
        val state = viewModel.state.collectAsState().value
        Box() {
            if (state is MovieDetailViewState.GetMovieData) {
                Image(
                    painter = rememberImagePainter(BuildConfig.IMAGE_BASE_URL + state.data.backdropPath),
                    contentDescription = null,
                    modifier = Modifier
                        .height(200.dp)
                        .fillMaxWidth(),
                    contentScale = ContentScale.Crop,
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    modifier = Modifier
                        .padding(8.dp)
                        .background(shape = CircleShape, color = Color.White), onClick = {
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
                        modifier = Modifier
                            .size(24.dp)
                    )
                }
                val isFavorite = viewModel.favoritedMovie.value
                IconButton(
                    modifier = Modifier
                        .padding(8.dp)
                        .background(shape = CircleShape, color = Color.White), onClick = {
                        favoriteClickListener(isFavorite)
                    }) {
                    val favIcon = if (isFavorite) R.drawable.ic_favorite_active
                    else R.drawable.ic_favorite_inactive
                    Image(
                        painter = rememberImagePainter(
                            ContextCompat.getDrawable(
                                this@MuviDetailActivity,
                                favIcon
                            )
                        ),
                        contentDescription = null,
                        modifier = Modifier
                            .size(24.dp)
                            .background(shape = CircleShape, color = Color.White)
                    )
                }
            }
        }
    }

    @OptIn(ExperimentalFoundationApi::class, ExperimentalMaterialApi::class)
    @Composable
    private fun SimilarMovies() {
        val similarMovies = viewModel.haveSimilarMovie.value
        LazyVerticalGrid(
            cells = GridCells.Fixed(4),
            content = {
                items(similarMovies) { movie ->
                    Card(
                        onClick = {
                            DetailNavigator
                                .getDetailPageIntent(movie.id)
                                .also(::startActivity)
                        },
                        shape = RectangleShape
                    ) {
                        Image(
                            painter = rememberImagePainter(data = BuildConfig.IMAGE_BASE_URL + movie.posterPath),
                            contentDescription = null,
                            modifier = Modifier.height(100.dp),
                            contentScale = ContentScale.Crop,
                        )
                    }
                }
            },
        )
    }

    override fun setData() {
        viewModel.getDetailMovie(movieId)
    }

    private fun favoriteClickListener(isFavorite: Boolean) {
        if (isFavorite) {
            viewModel.removeFavoriteMovie(movieId)
        } else {
            viewModel.storeFavoriteMovie()
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