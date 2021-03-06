package com.github.cheapmon.balalaika.ui.selection

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.LibraryBooks
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.VectorAsset
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.viewModel
import androidx.navigation.NavController
import com.github.cheapmon.balalaika.MainViewModel
import com.github.cheapmon.balalaika.R
import com.github.cheapmon.balalaika.model.Dictionary
import com.github.cheapmon.balalaika.model.DownloadableDictionary
import com.github.cheapmon.balalaika.model.InstalledDictionary
import com.github.cheapmon.balalaika.model.SimpleDictionary
import com.github.cheapmon.balalaika.ui.BalalaikaScaffold
import com.github.cheapmon.balalaika.util.exhaustive
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

private enum class DictionaryTab(
    val titleId: Int,
    val icon: VectorAsset
) {
    Library(
        R.string.selection_tab_list,
        Icons.Default.LibraryBooks
    ),
    Download(
        R.string.selection_tab_download,
        Icons.Default.CloudDownload
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DictionaryScreen(
    activityViewModel: MainViewModel,
    navController: NavController
) {
    val viewModel: SelectionViewModel = viewModel()

    var selectedTab: DictionaryTab by remember { mutableStateOf(DictionaryTab.Library) }
    var progress: Boolean by remember { mutableStateOf(false) }
    var listProgress: Boolean by remember { mutableStateOf(false) }

    val installedDictionaries by viewModel.installedDictionaries
        .collectAsState(initial = emptyList())
    val downloadableDictionaries by viewModel.downloadableDictionaries
        .onStart { listProgress = true }
        .onEach { listProgress = false }
        .collectAsState(initial = emptyList())

    val scope = rememberCoroutineScope()

    val onOpen: (InstalledDictionary) -> Unit = { activityViewModel.openDictionary(it) }
    val onClose: (InstalledDictionary) -> Unit = { activityViewModel.closeDictionary() }
    val onAdd: (DownloadableDictionary) -> Unit = { dic ->
        scope.launch {
            activityViewModel.installDictionary(dic)
                .onStart { progress = true }
                .onCompletion {
                    activityViewModel.openDictionary(dic.installed())
                    progress = false
                    viewModel.refresh()
                }.collect()
        }
    }
    val onRemove: (InstalledDictionary) -> Unit = {
        scope.launch {
            activityViewModel.removeDictionary(it)
                .onStart { progress = true }
                .onCompletion {
                    progress = false
                    viewModel.refresh()
                }.collect()
        }
    }

    BalalaikaScaffold(
        navController = navController,
        title = stringResource(id = R.string.menu_selection)
    ) {
        Column {
            if (progress || listProgress) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
            Tabs(
                selectedTab,
                onSelectTab = { selectedTab = it }
            )
            Body(
                installedDictionaries,
                downloadableDictionaries,
                selectedTab,
                onOpen,
                onClose,
                onAdd,
                onRemove
            )
        }
    }
}

@Composable
private fun Tabs(
    selectedTab: DictionaryTab = DictionaryTab.Library,
    onSelectTab: (DictionaryTab) -> Unit = {}
) {
    TabRow(selectedTabIndex = selectedTab.ordinal) {
        DictionaryTab.values().forEachIndexed { idx, tab ->
            Tab(
                selected = idx == selectedTab.ordinal,
                onClick = { onSelectTab(DictionaryTab.values()[idx]) },
                text = { Text(text = stringResource(id = tab.titleId)) },
                icon = { Icon(asset = tab.icon) }
            )
        }
    }
}

@Composable
private fun Body(
    installedDictionaries: List<SimpleDictionary>?,
    downloadableDictionaries: List<SimpleDictionary>?,
    selectedTab: DictionaryTab = DictionaryTab.Library,
    onOpen: (InstalledDictionary) -> Unit = {},
    onClose: (InstalledDictionary) -> Unit = {},
    onAdd: (DownloadableDictionary) -> Unit = {},
    onRemove: (InstalledDictionary) -> Unit = {}
) {
    Crossfade(current = selectedTab) {
        when (selectedTab) {
            DictionaryTab.Library -> {
                DictionaryList(
                    dictionaries = installedDictionaries.orEmpty(),
                    onOpen = onOpen,
                    onClose = onClose,
                    onAdd = onAdd,
                    onRemove = onRemove
                )
            }
            DictionaryTab.Download -> {
                DictionaryList(
                    dictionaries = downloadableDictionaries.orEmpty(),
                    onOpen = onOpen,
                    onClose = onClose,
                    onAdd = onAdd,
                    onRemove = onRemove
                )
            }
        }.exhaustive
    }
}

private fun DownloadableDictionary.installed(): InstalledDictionary = InstalledDictionary(
    dictionary = Dictionary(
        id = id,
        version = version,
        name = name,
        summary = summary,
        authors = authors,
        additionalInfo = additionalInfo
    ),
    isOpened = false
)
