package akshat.assignment.quiznow.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import akshat.assignment.quiznow.R
import akshat.assignment.quiznow.databinding.FragmentStartGameBinding
import androidx.navigation.fragment.findNavController

class StartGameFragment : Fragment() {

    private var _binding: FragmentStartGameBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStartGameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.startGameButton.setOnClickListener {
            findNavController().navigate(StartGameFragmentDirections.actionStartGameFragmentToQuestionsFragment())
        }

        binding.profileSectionButton.setOnClickListener {
            findNavController().navigate(StartGameFragmentDirections.actionStartGameFragmentToProfileFragment())
        }
    }
}