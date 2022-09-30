package akshat.assignment.quiznow.entities

data class Questions(
    var question: String,
    var option1: String,
    var option2: String,
    var option3: String,
    var option4: String,
    var correctAnswers: MutableList<Boolean> = mutableListOf(false, false, false, false)
)