package com.andriiginting.favorite.presentation

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import com.airbnb.deeplinkdispatch.DeepLink
import com.andriiginting.base_ui.MuviBaseActivity
import com.andriiginting.base_ui.MuviBaseAdapter
import com.andriiginting.base_ui.MuviBaseComposeActivity
import com.andriiginting.core_network.MovieItem
import com.andriiginting.favorite.R
import com.andriiginting.uttils.makeGone
import com.andriiginting.uttils.makeVisible
import com.andriiginting.uttils.setGridView
import kotlinx.android.synthetic.main.activity_favorite.*
import kotlinx.android.synthetic.main.item_loading_state.*

@DeepLink("muvi://favorite")
class FavoriteActivity : MuviBaseComposeActivity() {

    private val favoriteAdapter: MuviBaseAdapter<MovieItem, MuviFavoriteViewHolder> by lazy {
        MuviBaseAdapter<MovieItem, MuviFavoriteViewHolder>({ parent, _ ->
            MuviFavoriteViewHolder.inflate(parent)
        }, { viewHolder, _, item ->
            viewHolder.bind(item.posterPath.orEmpty())
        })
    }

    override fun getLayoutId(): Int = R.layout.activity_favorite

    override fun setupView() {
        setUpRecyclerView()
        setUpObserver()

        fabBackNavigation.setOnClickListener {
            onBackPressed()
        }
    }

    override fun setData() {
//        viewModel.getMovies()
    }

    private fun setUpRecyclerView() {
        rvFavorite.apply {
            setGridView()
            adapter = favoriteAdapter
        }
    }

    private fun setUpObserver() {
//        viewModel.state.observe(this, Observer { state ->
//            when (state) {
//                is FavoriteViewState.ShowLoading -> {
//                    ivLoadingIndicator.apply {
//                        makeVisible()
//                        startShimmer()
//                    }
//                }
//                is FavoriteViewState.HideLoading -> {
//                    ivLoadingIndicator.apply {
//                        makeGone()
//                        stopShimmer()
//                    }
//                }
//                is FavoriteViewState.GetFavoriteMovie -> {
//                    favoriteAdapter.safeAddAll(state.data)
//                    rvFavorite.makeVisible()
//                }
//                is FavoriteViewState.ShowError -> {
//                    layoutError.makeVisible()
//                    layoutError.showErrorScreen()
//                    emptyScreen.hideEmptyScreen()
//                    rvFavorite.makeGone()
//                }
//
//                is FavoriteViewState.ShowEmptyState -> {
//                    layoutError.makeGone()
//                    emptyScreen.apply {
//                        makeVisible()
//                        showEmptyScreen()
//                    }
//                    rvFavorite.makeGone()
//                }
//            }
//        })
    }
}