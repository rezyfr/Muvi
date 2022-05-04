package com.andriiginting.muvi.home.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import coil.compose.rememberImagePainter
import com.andriiginting.base_ui.MuviBaseActivity
import com.andriiginting.base_ui.MuviBaseAdapter
import com.andriiginting.core_network.MovieItem
import com.andriiginting.muvi.home.R
import com.andriiginting.muvi.home.di.MuviHomeInjector
import com.andriiginting.muvi.home.domain.Filter
import com.andriiginting.muvi.home.domain.getAllFilters
import com.andriiginting.muvi.home.domain.getFilter
import com.andriiginting.muvi.home.ui.filter.Chip
import com.andriiginting.navigation.DetailNavigator
import com.andriiginting.navigation.FavoriteNavigator
import com.andriiginting.navigation.SearchNavigator
import com.andriiginting.uttils.BuildConfig
import com.andriiginting.uttils.setGridView
import kotlinx.android.synthetic.main.activity_home.*

private const val HOME_COLUMN_SIZE = 2

class HomeActivity : MuviBaseActivity<MuviHomeViewModel>() {

    private lateinit var homeAdapter: MuviBaseAdapter<MovieItem, HomeViewHolder>

    override fun getLayoutId(): Int = R.layout.activity_home

    override fun setupView() {
        setUpAdapter()
        setUpHome()
        setupObserver()
        setupFavoriteButton()
    }

    override fun setData() = viewModel.getMovieData()

    override fun setupInjector() = MuviHomeInjector.of(this)

    override fun setViewModel(): Class<MuviHomeViewModel> = MuviHomeViewModel::class.java

    override fun setObserver(): FragmentActivity = this

    override fun onResume() {
        super.onResume()
        viewModel.getHomeBanner()
    }

    private fun setUpHome() {
        rvMovies.apply {
            setGridView(HOME_COLUMN_SIZE)
            adapter = homeAdapter
        }
        compose_view.apply {
            setContent {
                Scaffold(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    floatingActionButton = { HomeFloatingButton() }) {
                    Column() {
                        SearchBar()
                        HomeBanner()
                        HomeFilter(
                            selectedFilter = viewModel.filterState.collectAsState().value
                        ) {
                            viewModel.setFilterType(getFilter(it))
                        }
                        HomeContent()
                    }
                }
            }
        }
    }

    private fun setupFavoriteButton() {
        rvMovies.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0 || dy < 0) {
//                    fabFavoriteMovie.hide()
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
//                    fabFavoriteMovie.show()
                }
            }
        })
    }


    private fun setUpAdapter() {
        homeAdapter = MuviBaseAdapter({ parent, _ ->
            HomeViewHolder.inflate(parent)
        }, { viewHolder, _, item ->
            viewHolder.bind(item.posterPath.orEmpty())
            viewHolder.setPosterAction {
                DetailNavigator
                    .getDetailPageIntent(item.id)
                    .also(::startActivity)
            }
        })
    }

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    private fun HomeBanner() {
        when (val state = viewModel.bannerState.collectAsState().value) {
            is HomeBannerState.GetHomeBannerData -> {
                Card(shape = RoundedCornerShape(16.dp),
                    elevation = 4.dp,
                    backgroundColor = Color(0xFFDDDDDD),
                    modifier = Modifier
                        .padding(top = 8.dp),
                    onClick = {
                        DetailNavigator
                            .getDetailPageIntent(state.data.movie.id)
                            .also(::startActivity)
                    }) {
                    Column() {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                        ) {
                            Image(
                                rememberImagePainter(data = BuildConfig.IMAGE_BASE_URL + state.data.movie.backdropPath.orEmpty()),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxSize()
                            )
                        }
                        Text(
                            state.data.movie.title,
                            color = Color.White,
                            style = TextStyle(fontWeight = FontWeight.Bold),
                            modifier = Modifier
                                .background(Color(0x37000000))
                                .fillMaxWidth()
                                .padding(8.dp)
                        )
                    }
                }
            }
            else -> {}
        }
    }

    @Composable
    private fun HomeFilter(
        filters: List<Filter> = getAllFilters(),
        selectedFilter: Filter? = null,
        onSelectedChanged: (String) -> Unit = {}
    ) {
        Column(modifier = Modifier.padding(top = 8.dp)) {
            Text(
                text = stringResource(id = R.string.home_filter_title),
                color = Color.Black,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            LazyRow {
                items(filters) { filter ->
                    Chip(
                        name = filter.value,
                        isSelected = selectedFilter == filter,
                        onSelectionChanged = {
                            onSelectedChanged(it)
                        },
                    )
                }
            }
        }
    }

    @Composable
    private fun HomeContent() {
        val state = viewModel.state.collectAsState().value
        when (state) {
            is HomeViewState.ShowLoading -> {
                HomeShimmer()
            }
            is HomeViewState.HideLoading -> {
//                    ivLoadingIndicator.apply {
//                        stopShimmer()
//                        makeGone()
//                    }
//
//                    fabFavoriteMovie.makeVisible()
//                    rvMovies.makeVisible()
            }
            is HomeViewState.GetMovieData -> {
                MovieList(state.data.resultsIntent)
            }
            else -> {

            }
        }
    }

    @Composable
    private fun HomeShimmer() {
        LazyColumn {
            repeat(3) {
                item {
                    ShimmerAnimation()
                }
            }
        }
    }

    @Composable
    private fun HomeFloatingButton() {
        val state = viewModel.state.collectAsState().value
        if (state !is HomeViewState.ShowLoading) {
            FloatingActionButton(onClick = {
                FavoriteNavigator
                    .getFavoritePageIntent()
                    .let(::startActivity)
            }) {
                Image(
                    painter = rememberImagePainter(
                        ContextCompat.getDrawable(
                            this@HomeActivity,
                            R.drawable.ic_baseline_favorite_24
                        )
                    ), contentDescription = null, modifier = Modifier.size(24.dp)
                )
            }
        }
    }

    @OptIn(ExperimentalFoundationApi::class, ExperimentalMaterialApi::class)
    @Composable
    private fun MovieList(movies: List<MovieItem>) {
        LazyVerticalGrid(
            cells = GridCells.Fixed(HOME_COLUMN_SIZE),
            contentPadding = PaddingValues(8.dp),
            content = {
                items(movies) { movie ->
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        elevation = 2.dp,
                        modifier = Modifier.padding(8.dp),
                        onClick = {
                            DetailNavigator
                                .getDetailPageIntent(movie.id)
                                .also(::startActivity)
                        }
                    ) {
                        Image(
                            painter = rememberImagePainter(data = BuildConfig.IMAGE_BASE_URL + movie.posterPath),
                            contentDescription = null,
                            modifier = Modifier.height(250.dp),
                            contentScale = ContentScale.Crop,
                        )
                    }
                }
            },
        )
    }

    private fun setupObserver() {
//        viewModel.state.observe(this, Observer { state ->
//            when (state) {
//                is HomeViewState.ShowLoading -> {
//                    ivLoadingIndicator.apply {
//                        startShimmer()
//                        makeVisible()
//                    }
//
//                    fabFavoriteMovie.makeGone()
//                    rvMovies.makeGone()
//                    layoutEmpty.hideEmptyScreen()
//                }
//                is HomeViewState.HideLoading -> {
//                    ivLoadingIndicator.apply {
//                        stopShimmer()
//                        makeGone()
//                    }
//
//                    fabFavoriteMovie.makeVisible()
//                    rvMovies.makeVisible()
//                }
//                is HomeViewState.GetMovieDataError -> {
//                    layoutError.showErrorScreen()
//                }
//                is HomeViewState.GetMovieData -> {
//                    homeAdapter.safeAddAll(state.data.resultsIntent)
//                    layoutError.hideErrorScreen()
//                    layoutEmpty.hideEmptyScreen()
//                }
//                is HomeViewState.EmptyScreen -> {
//                    homeAdapter.clear()
//                    rvMovies.makeGone()
//                    layoutEmpty.showEmptyScreen()
//                }
//            }
//        })
    }

    @Composable
    fun SearchBar() {
        Box(modifier = Modifier
            .fillMaxWidth()
            .clickable {
                SearchNavigator
                    .getSearchPageIntent()
                    .also(this@HomeActivity::startActivity)
            }) {
            Image(
                rememberImagePainter(
                    ContextCompat.getDrawable(
                        this@HomeActivity,
                        R.drawable.rounded_stroke_grey
                    )
                ),
                contentDescription = null,
                contentScale = ContentScale.FillBounds,
                modifier = Modifier.matchParentSize()
            )
            Row(modifier = Modifier.padding(8.dp)) {
                Image(
                    rememberImagePainter(
                        ContextCompat.getDrawable(
                            this@HomeActivity,
                            R.drawable.ic_search_grey
                        )
                    ), contentDescription = null, Modifier.size(24.dp)
                )
                Text(
                    text = stringResource(id = R.string.muvi_search_hint),
                    modifier = Modifier.padding(start = 16.dp),
                    color = Color(0xFFA0A4A8)
                )
            }
        }
    }
}