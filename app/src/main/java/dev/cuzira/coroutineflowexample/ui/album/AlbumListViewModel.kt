package dev.cuzira.coroutineflowexample.ui.album

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.cuzira.coroutineflowexample.model.Future
import dev.cuzira.coroutineflowexample.model.album.Album
import dev.cuzira.coroutineflowexample.repository.AlbumRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlbumListViewModel @Inject constructor(
    private val albumRepository: AlbumRepository,
) : ViewModel() {
    val albumStateFlow = MutableStateFlow<Future<List<Album>>>(Future.Proceeding)

    init {
        refresh()
    }

    fun refresh() = viewModelScope.launch {
        albumRepository.getAlbumsFlow()
            .onEach {
                println("デバッグ album list onEachって何が来るの？ $it")
                albumStateFlow.value = it
            }
            .collect()
    }
}