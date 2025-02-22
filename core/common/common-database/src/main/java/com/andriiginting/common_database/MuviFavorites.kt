package com.andriiginting.common_database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.andriiginting.common_database.Constants.TABLE_NAME

@Entity(tableName = TABLE_NAME)
data class MuviFavorites(
    @ColumnInfo(name = "movie_id")  @PrimaryKey(autoGenerate = false) var movieFavoriteId: String = "",
    @ColumnInfo(name = "original_title") var movieTitle: String = "",
    @ColumnInfo(name = "poster_path") var posterPath: String = "",
    @ColumnInfo(name = "overview") var overview: String = "",
    @ColumnInfo(name = "backdrop_path") var backdropPath: String = "",
    @ColumnInfo(name = "release_data") var releaseDate: String = "",
)