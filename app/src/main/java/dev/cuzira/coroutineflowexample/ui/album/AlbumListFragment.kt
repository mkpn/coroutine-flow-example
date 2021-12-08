package dev.cuzira.coroutineflowexample.ui.album

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import dev.cuzira.coroutineflowexample.databinding.AlbumListFragmentBinding
import dev.cuzira.coroutineflowexample.databinding.RowPostBinding
import dev.cuzira.coroutineflowexample.model.Future
import dev.cuzira.coroutineflowexample.model.Post
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AlbumListFragment : Fragment() {
    private val viewModel: AlbumListViewModel by viewModels()
    private var _binding: AlbumListFragmentBinding? = null
    private val binding get() = _binding!!
//    private val directions = MainFragmentDirections

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
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.albumStateFlow.collect {
                    when(it){
                        is Future.Proceeding -> {
                            binding.progressIndicator.show()
                        }
                        is Future.Success -> {
                            println("デバッグ success $it")
                            binding.progressIndicator.hide()
                            binding.listContainer.removeAllViews()
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
//        viewModel.postsLiveData.observe(viewLifecycleOwner) {
//            when (it) {
//                is Future.Proceeding -> {
//                    binding.progressIndicator.show()
//                }
//                is Future.Success -> {
//                    binding.progressIndicator.hide()
//                    binding.listContainer.removeAllViews()
////                    binding.listContainer.adapter = PostsAdapter(it.value) {
////                        val action = directions.actionMainFragmentToDetailFragment(it.id)
////                        findNavController().navigate(action)
////                    }
//                }
//                is Future.Error -> {
//                    binding.progressIndicator.hide()
//                    Toast.makeText(requireContext(), it.error.message, Toast.LENGTH_LONG).show()
//                }
//            }
//        }

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


    inner class PostsAdapter(
        private val items: List<Post>,
        private val onClickListener: (Post) -> Unit,
    ) : RecyclerView.Adapter<PostsAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val binding =
                RowPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ViewHolder(binding)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = items[position]
            holder.binding.title.text = item.title
            holder.binding.body.text = item.body
            holder.binding.root.setOnClickListener { onClickListener(item) }
        }

        override fun getItemCount(): Int = items.size

        inner class ViewHolder(val binding: RowPostBinding) : RecyclerView.ViewHolder(binding.root)
    }
}