package com.firebasechatkotlin.activity

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.firebasechatkotlin.R
import com.firebasechatkotlin.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase


class LoginActivity : AppCompatActivity(), View.OnClickListener {

    private var firebaseAuth: FirebaseAuth? = null
    private var database: FirebaseDatabase? = null
    lateinit var activityLoginBinding : ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       activityLoginBinding = DataBindingUtil.setContentView(this,R.layout.activity_login)

        setTitle(R.string.login_title)

        firebaseAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        activityLoginBinding.btnLogin.setOnClickListener(this)
        activityLoginBinding.tvSignup.setOnClickListener(this)

        activityLoginBinding.progress.visibility = View.GONE


    }

    private fun checkLogin() {
        activityLoginBinding.progress.visibility = View.VISIBLE
        activityLoginBinding.btnLogin.visibility = View.GONE

        firebaseAuth?.signInWithEmailAndPassword(
            activityLoginBinding.etEmailId.text.toString(),
            activityLoginBinding.etPassword.text.toString()
        )
            ?.addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
//                    val user = firebaseAuth?.currentUser
                    Toast.makeText(this, "Login successfully.", Toast.LENGTH_SHORT).show()
                    startChatUserListActivity()
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT).show()
                    activityLoginBinding.progress.visibility = View.GONE
                    activityLoginBinding.btnLogin.visibility = View.VISIBLE

                }
            }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnLogin -> {
                if (TextUtils.isEmpty(activityLoginBinding.etEmailId.text)) {
                    Toast.makeText(this, "Please enter email id", Toast.LENGTH_SHORT).show()
                } else if (TextUtils.isEmpty(activityLoginBinding.etPassword.text)) {
                    Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show()
                } else {
                    checkLogin()
                }
            }
            R.id.tvSignup -> {
                startActivity(Intent(this, SignUpActivity::class.java))
            }
        }
    }

    private fun startChatUserListActivity() {
        val intent = Intent(this, ChatUserListActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

//    private fun insertUpdateUser(currentUser: FirebaseUser) {
//        val firebase = database?.reference?.child("users")?.child(currentUser.uid)
//        firebase?.addListenerForSingleValueEvent(object : ValueEventListener {
//            override fun onDataChange(p0: DataSnapshot) {
//                startChatUserListActivity()
//            }
//
//            override fun onCancelled(p0: DatabaseError) {
//
//            }
//
//        })
//
//        var userData = User(
//            currentUser.uid,
//            currentUser.displayName.toString(),
//            currentUser.email.toString(),
//            false,
//            currentUser.photoUrl.toString()
//        )
//        firebase?.setValue(userData)
//    }

}
