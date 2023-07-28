package com.firebasechatkotlin.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.firebasechatkotlin.R
import com.firebasechatkotlin.databinding.ActivityLoginBinding
import com.firebasechatkotlin.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class LoginActivity : AppCompatActivity(), View.OnClickListener {

    private var firebaseAuth: FirebaseAuth? = null
    private var database: FirebaseDatabase? = null
    lateinit var loginActivityBinding : ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginActivityBinding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        setTitle(R.string.login_title)

        firebaseAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        loginActivityBinding.btnLogin.setOnClickListener(this)
        loginActivityBinding.tvSignup.setOnClickListener(this)

        loginActivityBinding.progress.visibility = View.GONE

    }

    private fun checkLogin() {
        loginActivityBinding.progress.visibility = View.VISIBLE
        loginActivityBinding.btnLogin.visibility = View.GONE

        firebaseAuth?.signInWithEmailAndPassword(loginActivityBinding.etEmailId.text.toString(), loginActivityBinding.etPassword.text.toString())
            ?.addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val user = firebaseAuth?.currentUser
                    Toast.makeText(this, "Login successfully.", Toast.LENGTH_SHORT).show()
                    insertUpdateUser(user!!)
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT).show()
                    loginActivityBinding.progress.visibility = View.GONE
                    loginActivityBinding.btnLogin.visibility = View.VISIBLE

                }
            }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnLogin -> {
                if (TextUtils.isEmpty(loginActivityBinding.etEmailId.text)) {
                    Toast.makeText(this, "Please enter email id", Toast.LENGTH_SHORT).show()
                } else if (TextUtils.isEmpty(loginActivityBinding.etPassword.text)) {
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

    private fun insertUpdateUser(currentUser: FirebaseUser) {
        val firebase = database?.reference?.child("users")?.child(currentUser.uid)
        firebase?.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                startChatUserListActivity()
            }

            override fun onCancelled(p0: DatabaseError) {

            }

        })

        var userData = User(currentUser.uid, currentUser.displayName.toString(), currentUser.email.toString())
        firebase?.setValue(userData)
    }

}
