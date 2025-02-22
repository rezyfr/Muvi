package com.andriiginting.common_database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.andriiginting.common_database.Constants.DELETE_FAVORITE_MOVIE_WITH_ID
import com.andriiginting.common_database.Constants.FILTER_FAVORITE_MOVIE_WITH_ID
import com.andriiginting.common_database.Constants.GET_ALL_FAVORITE_MOVIE
import io.reactivex.Flowable
import io.reactivex.Maybe
import kotlinx.coroutines.flow.Flow

@Dao
interface MuviFavoriteDAO {
    @Query(GET_ALL_FAVORITE_MOVIE)
    fun getAllFavoriteMovie(): Flowable<List<MuviFavorites>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFavoriteMovie(data: MuviFavorites): Long

    @Query(FILTER_FAVORITE_MOVIE_WITH_ID)
    fun isFavorite(movieId: Int): Flow<MuviFavorites?>

    @Query(DELETE_FAVORITE_MOVIE_WITH_ID)
    fun deleteMovie(movieId: String): Long
}