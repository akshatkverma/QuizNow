package akshat.assignment.quiznow.dao

import akshat.assignment.quiznow.entities.User
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class UserDao {

    private val dataBase= FirebaseFirestore.getInstance()
    private val userCollection=dataBase.collection("users")

    fun addQuizDetails(score : String, user : User) {
        val docRef= userCollection.document(user.uid)
        docRef.get()
            .addOnSuccessListener {
                document->
                if(document == null){
                    user.quizScores.add(score)
                    user.let {
                        //Let it work on background
                        GlobalScope.launch(Dispatchers.IO) {
                            userCollection.document(user.uid).set(it)
                        }
                    }
                }
//                else
//                {
//                    val check = document.toObject(User::class.java) !!
//                    check.quizScores.add(score)
//                    user.let {
//                        //Let it work on background
//                        GlobalScope.launch(Dispatchers.IO) {
//                            userCollection.document(user.uid).set(it)
//                        }
//                    }
//                }
            }
    }



}