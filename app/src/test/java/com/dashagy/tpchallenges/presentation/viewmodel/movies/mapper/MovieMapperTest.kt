package com.dashagy.tpchallenges.presentation.viewmodel.movies.mapper

import com.dashagy.domain.entities.Movie
import com.dashagy.tpchallenges.presentation.viewmodel.movies.model.ViewModelMovie
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class MovieMapperTest {

    private lateinit var domainMovie: Movie
    private lateinit var viewModelMovieList: List<Movie>
    private lateinit var viewModelMovie: ViewModelMovie

    @Before
    fun init() {
        domainMovie = Movie(ID_1, TITLE_1, OVERVIEW_1, POSTER_1)
        viewModelMovie = ViewModelMovie(
            ID_2,
            TITLE_2,
            OVERVIEW_2,
            null,
            TIMESTAMP
        )

        viewModelMovieList = listOf(domainMovie)
    }


    @Test
    fun `map a view model movie to domain movie`() {
        val result = viewModelMovie.toMovie()

        Assert.assertEquals(ID_2, result.id)
        Assert.assertEquals(TITLE_2, result.title)
        Assert.assertEquals(OVERVIEW_2, result.overview)
        Assert.assertEquals(null, result.poster)
    }

    @Test
    fun `map a domain movie to view model movie`() {
        val result = domainMovie.toViewModelMovie()

        Assert.assertEquals(ID_1, result.id)
        Assert.assertEquals(TITLE_1, result.title)
        Assert.assertEquals(OVERVIEW_1, result.overview)
        Assert.assertEquals(POSTER_1, result.poster)
    }

    @Test
    fun `mapping domain movies to view model movie list`(){
        val mappedList = viewModelMovieList.toViewModelMovieList()

        Assert.assertEquals(ID_1, mappedList[0].id)
        Assert.assertEquals(TITLE_1, mappedList[0].title)
        Assert.assertEquals(OVERVIEW_1, mappedList[0].overview)
        Assert.assertEquals(POSTER_1, mappedList[0].poster)
    }

    companion object {
        private const val ID_1 = 101
        private const val TITLE_1 = "Scarface"
        private const val OVERVIEW_1 = "Overview of scarface"
        private const val POSTER_1 = "Some URL"

        private const val ID_2 = 102
        private const val TITLE_2 = "The Lord of The rings"
        private const val OVERVIEW_2 = "Overview The Lord of The rings"

        private const val TIMESTAMP = 1000L
    }
}