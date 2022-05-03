package com.andriiginting.muvi.home.domain

enum class Filter(val value: String) {
    ALL("All"),
    LATEST("Latest"),
    NOW_PLAYING("Now Playing"),
    TOP_RATED("Top Rated"),
    UPCOMING("Upcoming"),
}

fun getAllFilters(): List<Filter>{
    return listOf(
        Filter.ALL, Filter.LATEST, Filter.NOW_PLAYING, Filter.TOP_RATED, Filter.UPCOMING
    )
}

fun getFilter(value: String): Filter? {
    val map = Filter.values().associateBy(Filter::value)
    return map[value]
}