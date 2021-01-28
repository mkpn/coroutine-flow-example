package dev.cuzira.coroutineflowexample.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import dev.cuzira.coroutineflowexample.R
import dev.cuzira.coroutineflowexample.databinding.DetailFragmentBinding
import dev.cuzira.coroutineflowexample.model.Future

@AndroidEntryPoint
class DetailFragment : Fragment() {
    private val TAG = this::class.java.simpleName

    private val viewModel: DetailViewModel by viewModels()
    private val args: DetailFragmentArgs by navArgs()
    private var _binding: DetailFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DetailFragmentBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val postId = args.id

        viewModel.postId = postId
        binding.postId.text = getString(R.string.formatted_id, postId.toString())
        viewModel.refresh()

        viewModel.postLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is Future.Proceeding -> {
                    binding.progressIndicator.show()
                }
                is Future.Success -> {
                    binding.progressIndicator.hide()
                    binding.title.text = it.data.title
                    binding.body.text = it.data.body
                }
                is Future.Error -> {
                    binding.progressIndicator.hide()
                    Toast.makeText(requireContext(), it.error.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}