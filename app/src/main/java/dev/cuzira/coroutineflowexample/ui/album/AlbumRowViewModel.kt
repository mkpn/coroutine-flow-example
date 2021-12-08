package dev.cuzira.coroutineflowexample.ui.album

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dev.cuzira.coroutineflowexample.model.album.Album

class AlbumRowViewModel(val album: Album) : ViewModel() {

    class Factory(private val album: Album) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            // ViewModelを手動生成する
            return AlbumRowViewModel(album) as T
        }
    }
}