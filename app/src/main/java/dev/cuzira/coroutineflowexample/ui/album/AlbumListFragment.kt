package dev.cuzira.coroutineflowexample.ui.album

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import dev.cuzira.coroutineflowexample.databinding.AlbumListFragmentBinding
import dev.cuzira.coroutineflowexample.databinding.RowAlbumBinding
import dev.cuzira.coroutineflowexample.model.Future
import dev.cuzira.coroutineflowexample.model.album.Album
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AlbumListFragment : Fragment() {
    private val viewModel: AlbumListViewModel by viewModels()
    private var _binding: AlbumListFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = AlbumListFragmentBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.listContainer.layoutManager = LinearLayoutManager(requireContext())
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.albumStateFlow.collect {
                    when (it) {
                        is Future.Proceeding -> {
                            binding.progressIndicator.show()
                        }
                        is Future.Success -> {
                            binding.progressIndicator.hide()
                            val viewModelList = it.value.map { album ->
                                val viewModel = ViewModelProvider(
                                    this@AlbumListFragment,
                                    AlbumRowViewModel.Factory(album)
                                )[AlbumRowViewModel::class.java]
                                viewModel
                            }
                            val albumsAdapter = AlbumsAdapter {}
                            binding.listContainer.adapter = albumsAdapter
                            albumsAdapter.submitList(viewModelList)
                        }
                        is Future.Error -> {
                            binding.progressIndicator.hide()
                            Toast.makeText(requireContext(), it.error.message, Toast.LENGTH_LONG)
                                .show()
                        }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private object DiffCallback : DiffUtil.ItemCallback<AlbumRowViewModel>() {
        override fun areItemsTheSame(oldItem: AlbumRowViewModel, newItem: AlbumRowViewModel): Boolean {
            return oldItem.album.id == newItem.album.id
        }

        override fun areContentsTheSame(oldItem: AlbumRowViewModel, newItem: AlbumRowViewModel): Boolean {
            return oldItem.album.id == newItem.album.id
        }
    }

    inner class AlbumsAdapter(
        private val onClickListener: (Album) -> Unit,
    ) : ListAdapter<AlbumRowViewModel, AlbumsAdapter.ViewHolder>(DiffCallback) {
        inner class ViewHolder(val binding: RowAlbumBinding) : RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val binding =
                RowAlbumBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ViewHolder(binding)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = currentList[position]
            holder.binding.title.text = item.album.title
            holder.binding.body.text = item.album.id.toString()
            holder.binding.root.setOnClickListener { onClickListener(item.album) }
        }
    }
}