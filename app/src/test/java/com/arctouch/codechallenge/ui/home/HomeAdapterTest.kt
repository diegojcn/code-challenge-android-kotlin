package com.arctouch.codechallenge.ui.home

import com.arctouch.codechallenge.model.Movie
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.spyk
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before

class HomeAdapterTest {

    private lateinit var adapter: HomeAdapter

    private val listSize2 = listOf(
            getMovieStub(),
            getMovieStub())

    private val listSize3 = listOf(
            getMovieStub(),
            getMovieStub(),
            getMovieStub()
    )

    fun getMovieStub(): Movie =
            Movie(1,
                    "",
                    "",
                    listOf(),
                    listOf(),
                    "",
                    "",
                    "")


    @Before
    fun setup() {

        adapter = spyk(HomeAdapter())

        every { adapter.notifyItemChanged(any(), any()) } just Runs
        every { adapter.notifyItemRangeChanged(any(), any(), any()) } just Runs
        every { adapter.notifyItemRangeInserted(any(), any()) } just Runs
        every { adapter.notifyDataSetChanged() } just Runs
        every { adapter.notifyItemChanged(any()) } just Runs

    }

    @Test
    fun shouldHaveValues_whenUpdateAdapter() {
        val expectedSelectedItemsCount = 2

        adapter.update(listSize2)

        assertEquals(expectedSelectedItemsCount, adapter.itemCount)
    }

    @Test
    fun shouldReturnItemValues_whenAskToGetAItem() {

        adapter.update(listSize3)
        val item: Movie = adapter.getItem(2)

        assertEquals(item, listSize3[2])
    }

}