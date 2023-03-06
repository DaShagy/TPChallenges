package com.dashagy.tpchallenges.data.mappers

import com.dashagy.tpchallenges.data.service.mappers.toMovieList
import com.dashagy.tpchallenges.data.service.responseSchemas.MovieListResponseSchema
import com.dashagy.tpchallenges.data.service.responseSchemas.MovieResponseSchema
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class ServiceMovieMapperTest {

    private lateinit var movie1: MovieResponseSchema
    private lateinit var movie2: MovieResponseSchema
    private lateinit var movieList: MovieListResponseSchema

    @Before
    fun init() {
        movie1 = MovieResponseSchema(ID_1, TITLE_1, OVERVIEW_1, POSTER_1)
        movie2 = MovieResponseSchema(ID_2, TITLE_2, OVERVIEW_2, null)

        movieList = MovieListResponseSchema(listOf(movie1, movie2))
    }

    @Test
    fun `mapping service responses to movie domain entity`(){
        val mappedList = movieList.toMovieList()

        Assert.assertEquals(ID_1, mappedList[0].id)
        Assert.assertEquals(TITLE_1, mappedList[0].title)
        Assert.assertEquals(OVERVIEW_1, mappedList[0].overview)
        Assert.assertEquals(POSTER_1, mappedList[0].poster)

        Assert.assertEquals(ID_2, mappedList[1].id)
        Assert.assertEquals(TITLE_2, mappedList[1].title)
        Assert.assertEquals(OVERVIEW_2, mappedList[1].overview)
        Assert.assertEquals(null, mappedList[1].poster)
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