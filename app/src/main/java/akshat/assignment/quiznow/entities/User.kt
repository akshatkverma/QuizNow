package akshat.assignment.quiznow.entities

data class User (
    var uid : String,
    var name : String,
    var quizScores : MutableList<String> = mutableListOf()
)