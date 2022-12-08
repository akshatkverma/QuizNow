package akshat.assignment.quiznow.fragments

import akshat.assignment.quiznow.dao.UserDao
import akshat.assignment.quiznow.databinding.FragmentQuestionsBinding
import akshat.assignment.quiznow.entities.Questions
import akshat.assignment.quiznow.entities.User
import android.content.Context
import android.graphics.Color
import android.os.*
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth


class QuestionsFragment : Fragment() {

    private var _binding: FragmentQuestionsBinding? = null
    private val binding get() = _binding!!

    private val questions = mutableListOf<Questions>()

    private var selectedAnswers = mutableListOf(false, false, false, false)

    private var timeEachQuestion: Int = 0
    private var currentQuestion: Int = 0
    private var score: Int = 0

    private lateinit var firebaseAuth: FirebaseAuth


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQuestionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.includeQuestions.option1.setOnClickListener {
            toggleColor(binding.includeQuestions.option1, 0)
        }
        binding.includeQuestions.option2.setOnClickListener {
            toggleColor(binding.includeQuestions.option2, 1)
        }
        binding.includeQuestions.option3.setOnClickListener {
            toggleColor(binding.includeQuestions.option3, 2)
        }
        binding.includeQuestions.option4.setOnClickListener {
            toggleColor(binding.includeQuestions.option4, 3)
        }

        binding.includeQuestions.skipButton.setOnClickListener {
            currentQuestion++
            updateQuestion()
        }

        binding.includeQuestions.submitButton.setOnClickListener {
            checkAnswers()
            currentQuestion++
            updateQuestion()
        }

        getData()

    }

    // Function to get questions data from API
    private fun getData() {
        val url = "https://b4e7d359-c58f-4aa3-a314-726b3baa3852.mock.pstmn.io/?quiz=true"
        val queue = Volley.newRequestQueue(requireContext())

        val jsonObjectRequest = object : JsonObjectRequest(
            Method.GET, url, null,
            {
                val tempQuestions = it.getJSONObject("result").getJSONArray("questions")
                timeEachQuestion = it.getJSONObject("result").getString("timeInMinutes").toInt()

                for (i in 0 until tempQuestions.length()) {
                    val jsonObject = tempQuestions.getJSONObject(i)
                    val options = jsonObject.getJSONArray("options")
                    val correctAnswersJSON = jsonObject.getJSONArray("correct_answers")

                    val correctAnswers = mutableListOf(false, false, false, false)

                    for (j in 0 until correctAnswersJSON.length())
                        correctAnswers[correctAnswersJSON.getInt(j) - 1] = true

                    val ques = Questions(
                        jsonObject.getString("lable"),
                        options.getJSONObject(0).getString("lable"),
                        options.getJSONObject(1).getString("lable"),
                        options.getJSONObject(2).getString("lable"),
                        options.getJSONObject(3).getString("lable"),
                        correctAnswers
                    )
                    questions.add(ques)
                }

                binding.progressBar.visibility = View.GONE
                binding.includeQuestions.questionsFormatLL.visibility = View.VISIBLE
                currentQuestion = 0
                score = 0
                runCounters()
                updateQuestion()
            },
            {
                Toast.makeText(requireContext(), "Some Error Occurred $it", Toast.LENGTH_SHORT)
                    .show()
            }
        ) {

        }
        queue.add(jsonObjectRequest)
    }

    // Set-up the timer on the top
    private fun runCounters() {
        var counter = 0
        object : CountDownTimer((timeEachQuestion * 60 * 1000).toLong(), 1000) {
            override fun onTick(millisUntilFinished: Long) {
                binding.includeQuestions.timer.text = (timeEachQuestion * 60 - counter).toString()
                counter++
            }

            override fun onFinish() {
                Toast.makeText(requireContext(), "Time Over", Toast.LENGTH_SHORT).show()
                endQuiz()
            }
        }.start()
    }

    // Function to update questions.
    private fun updateQuestion() {
        if (currentQuestion >= questions.size) {
            endQuiz()
            return
        }
        selectedAnswers = mutableListOf(false, false, false, false)

        binding.includeQuestions.questionTL.text = questions[currentQuestion].question

        binding.includeQuestions.option1.text = questions[currentQuestion].option1
        binding.includeQuestions.option1.setBackgroundColor(Color.BLACK)
        binding.includeQuestions.option1.isEnabled = true

        binding.includeQuestions.option2.text = questions[currentQuestion].option2
        binding.includeQuestions.option2.setBackgroundColor(Color.BLACK)
        binding.includeQuestions.option2.isEnabled = true

        binding.includeQuestions.option3.text = questions[currentQuestion].option3
        binding.includeQuestions.option3.setBackgroundColor(Color.BLACK)
        binding.includeQuestions.option3.isEnabled = true

        binding.includeQuestions.option4.text = questions[currentQuestion].option4
        binding.includeQuestions.option4.setBackgroundColor(Color.BLACK)
        binding.includeQuestions.option4.isEnabled = true
    }

    // Function to vibrate phone, will be used after each question ends.
    private fun Fragment.vibratePhone() {
        val vibrator = context?.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= 26) {
            vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            vibrator.vibrate(200)
        }
    }

    private fun toggleColor(button: MaterialButton, index: Int) {
        if (currentQuestion >= questions.size) {
            endQuiz()
            return
        }
        selectedAnswers[index] = true
        button.isEnabled = false
        if (questions[currentQuestion].correctAnswers[index])
            button.setBackgroundColor(Color.GREEN)
        else
            button.setBackgroundColor(Color.RED)
    }

    private fun checkAnswers() {
        if (currentQuestion >= questions.size) {
            endQuiz()
            return
        }
        if (questions[currentQuestion].correctAnswers == selectedAnswers)
            score++
        binding.includeQuestions.scoreText.text = "Current Score : $score"
    }

    private fun endQuiz() {
//        Toast.makeText(requireContext(), "$score && ${questions.size}", Toast.LENGTH_SHORT).show()
        val dao = UserDao()
        val currentUser = firebaseAuth.currentUser !!
        val user = User(currentUser.uid, currentUser.displayName.toString() , mutableListOf())
        dao.addQuizDetails("$score out of ${questions.size}", user)
        findNavController().navigate(QuestionsFragmentDirections.actionQuestionsFragmentToResultFragment(score = score, maxScore = questions.size))
    }

}