package akshat.assignment.quiznow.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import akshat.assignment.quiznow.databinding.FragmentResultBinding
import android.graphics.Color
import androidx.navigation.fragment.findNavController
import nl.dionsegijn.konfetti.models.Shape
import nl.dionsegijn.konfetti.models.Size
import kotlin.math.max
import kotlin.properties.Delegates

class ResultFragment : Fragment() {

    private var _binding: FragmentResultBinding? = null
    private val binding get() = _binding!!

    private lateinit var score: String
    private lateinit var maxScore : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            score = it.getInt("score").toString()
            maxScore = it.getInt("maxScore").toString()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.confetti.build()
            .addColors(Color.GREEN, Color.YELLOW, Color.MAGENTA)
            .setDirection(0.0, 359.0)
            .setSpeed(1f, 5f)
            .setFadeOutEnabled(true)
            .setTimeToLive(2000L)
            .addShapes(Shape.Square, Shape.Circle)
            .addSizes(Size(12))
            .setPosition(-50f, binding.confetti.width + 50f, -50f)
            .streamFor(300, 3000L)

        binding.scoreTV.text = "$score out of $maxScore"

        binding.gotoHomeScreenButton.setOnClickListener {
            findNavController().navigate(ResultFragmentDirections.actionResultFragmentToStartGameFragment())
        }

        binding.startQuizButton.setOnClickListener {
            findNavController().navigate(ResultFragmentDirections.actionResultFragmentToQuestionsFragment())
        }
    }

}