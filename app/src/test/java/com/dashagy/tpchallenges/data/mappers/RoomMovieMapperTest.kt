package com.dashagy.tpchallenges.data.mappers

import com.dashagy.tpchallenges.data.database.entities.RoomMovie
import com.dashagy.tpchallenges.data.database.mappers.toMovie
import com.dashagy.tpchallenges.data.database.mappers.toMovieList
import com.dashagy.tpchallenges.data.database.mappers.toRoomMovie
import com.dashagy.tpchallenges.domain.entities.Movie
import com.dashagy.tpchallenges.utils.TimeUtil
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class RoomMovieMapperTest {

    private lateinit var domainMovie: Movie
    private lateinit var roomMovie: RoomMovie
    private lateinit var roomMovieList: List<RoomMovie>

    @Before
    fun init() {
        domainMovie = Movie(ID_1, TITLE_1, OVERVIEW_1, POSTER_1)
        roomMovie = RoomMovie(ID_2, TITLE_2, OVERVIEW_2, null, TimeUtil.getTimestamp())

        roomMovieList = listOf(roomMovie)
    }

    @Test
    fun `mapping room movies to domain movies`(){
        val mappedList = roomMovieList.toMovieList()

        Assert.assertEquals(ID_2, mappedList[0].id)
        Assert.assertEquals(TITLE_2, mappedList[0].title)
        Assert.assertEquals(OVERVIEW_2, mappedList[0].overview)
        Assert.assertEquals(null, mappedList[0].poster)
    }

    @Test
    fun `map a room movie to domain movie`() {
        val result = roomMovie.toMovie()

        Assert.assertEquals(ID_2, result.id)
        Assert.assertEquals(TITLE_2, result.title)
        Assert.assertEquals(OVERVIEW_2, result.overview)
        Assert.assertEquals(null, result.poster)
    }

    @Test
    fun `map a domain movie to room movie`() {
        val result = domainMovie.toRoomMovie()

        Assert.assertEquals(ID_1, result.id)
        Assert.assertEquals(TITLE_1, result.title)
        Assert.assertEquals(OVERVIEW_1, result.overview)
        Assert.assertEquals(POSTER_1, result.poster)
    }

    companion object {
        private const val ID_1 = 101
        private const val TITLE_1 = "Scarface"
        private const val OVERVIEW_1 = "Overview of scarface"
        private const val POSTER_1 = "Some URL"

        private const val ID_2 = 102
        private const val TITLE_2 = "The Lord of The rings"
        private const val OVERVIEW_2 = "Overview The Lord of The rings"
    }
}