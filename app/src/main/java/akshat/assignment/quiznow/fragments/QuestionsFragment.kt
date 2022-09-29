package akshat.assignment.quiznow.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import akshat.assignment.quiznow.R
import akshat.assignment.quiznow.databinding.FragmentQuestionsBinding
import akshat.assignment.quiznow.entities.Questions
import android.widget.Toast
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley


class QuestionsFragment : Fragment() {

    private var _binding: FragmentQuestionsBinding? = null
    private val binding get() = _binding!!

    private val questions = mutableListOf<Questions>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQuestionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getData()
    }

    private fun getData() {
        val url = "https://b4e7d359-c58f-4aa3-a314-726b3baa3852.mock.pstmn.io/?quiz=true"
        val queue = Volley.newRequestQueue(requireContext())

        val jsonObjectRequest = object : JsonObjectRequest(
            Method.GET, url, null,
            {
                val tempQuestions = it.getJSONObject("result").getJSONArray("questions")
                for(i in 0 until tempQuestions.length()) {
                    val jsonObject = tempQuestions.getJSONObject(i)
                    val options = jsonObject.getJSONArray("options")
                    questions.add(
                        Questions(
                            jsonObject.getString("lable"),
                            options.getJSONObject(0).getString("lable"),
                            options.getJSONObject(1).getString("lable"),
                            options.getJSONObject(2).getString("lable"),
                            options.getJSONObject(3).getString("lable")
                        )
                    )
                }

                binding.progressBar.visibility = View.GONE
                binding.includeQuestions.questionsFormatLL.visibility = View.VISIBLE
                binding.questionTextView.text = questions.toString()
            },
            {
                Toast.makeText(requireContext(), "Some Error Occurred $it", Toast.LENGTH_SHORT)
                    .show()
            }
        ) {

        }
        queue.add(jsonObjectRequest)
    }



}