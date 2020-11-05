package com.github.cheapmon.balalaika.ui

import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.VectorAsset
import com.github.cheapmon.balalaika.R

sealed class Screen(
    val route: String,
    @StringRes val titleId: Int,
    val icon: VectorAsset
) {
    object Dictionary : Screen(
        route = "dictionary",
        titleId = R.string.menu_dictionary,
        icon = Icons.Default.Book
    )

    object Search : Screen(
        route = "search",
        titleId = R.string.menu_search,
        icon = Icons.Default.Search
    )

    object History : Screen(
        route = "history",
        titleId = R.string.menu_history,
        icon = Icons.Default.History
    )

    object Bookmarks : Screen(
        route = "bookmarks",
        titleId = R.string.menu_bookmarks,
        icon = Icons.Default.Bookmark
    )

    object Dictionaries : Screen(
        route = "dictionaries",
        titleId = R.string.menu_selection,
        icon = Icons.Default.LibraryBooks
    )

    object About : Screen(
        route = "about",
        titleId = R.string.menu_about,
        icon = Icons.Default.Info
    )
}

val balalaikaScreens: List<List<Screen>> = listOf(
    listOf(
        Screen.Dictionary,
        Screen.Search,
        Screen.History,
        Screen.Bookmarks
    ),
    listOf(
        Screen.Dictionaries
    ),
    listOf(
        Screen.About
    )
)

/** Get screen from navigation resource ID for backwards compatibility */
fun screenFor(@IdRes id: Int): Screen? =
    when (id) {
        R.id.nav_home -> Screen.Dictionary
        R.id.nav_search -> Screen.Search
        R.id.nav_history -> Screen.History
        R.id.nav_bookmarks -> Screen.Bookmarks
        R.id.nav_selection -> Screen.Dictionaries
        R.id.nav_preferences -> Screen.About
        else -> null
    }

/** Get navigation resource ID from screen for backwards compatibility */
@IdRes
fun Screen.id(): Int =
    when (this) {
        Screen.Dictionary -> R.id.nav_home
        Screen.Search -> R.id.nav_search
        Screen.History -> R.id.nav_history
        Screen.Bookmarks -> R.id.nav_bookmarks
        Screen.Dictionaries -> R.id.nav_selection
        Screen.About -> R.id.nav_preferences
    }
