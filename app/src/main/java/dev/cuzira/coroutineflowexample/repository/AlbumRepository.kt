package dev.cuzira.coroutineflowexample.repository

import dev.cuzira.coroutineflowexample.api.PostApi
import dev.cuzira.coroutineflowexample.model.Comment
import dev.cuzira.coroutineflowexample.model.Future
import dev.cuzira.coroutineflowexample.model.Post
import dev.cuzira.coroutineflowexample.model.album.Album
import dev.cuzira.coroutineflowexample.utils.apiFlow
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AlbumRepository @Inject constructor(private val postApi: PostApi) {
    fun getAlbumsFlow(): Flow<Future<List<Album>>> = apiFlow { postApi.fetchAlbums() }
}