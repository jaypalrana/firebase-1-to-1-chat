package com.firebasechatkotlin.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import android.text.TextUtils
import android.util.Log
import android.view.MenuItem
import androidx.databinding.DataBindingUtil
import com.firebasechatkotlin.R
import com.firebasechatkotlin.databinding.ActivitySignUpBinding
import com.google.firebase.auth.UserProfileChangeRequest


class SignUpActivity : AppCompatActivity(), View.OnClickListener {

    private var firebaseAuth: FirebaseAuth? = null
    lateinit var activitySignUpBinding : ActivitySignUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activitySignUpBinding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up)
        setTitle(R.string.signup_title)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        firebaseAuth = FirebaseAuth.getInstance()

        activitySignUpBinding.btnSignUp.setOnClickListener(this)

        activitySignUpBinding.progress.visibility = View.GONE
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item?.getItemId() == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)

    }

    private fun signUp() {
        activitySignUpBinding.progress.visibility = View.VISIBLE
        activitySignUpBinding.btnSignUp.visibility = View.GONE
        Log.d("TAG", "signUp:== "+activitySignUpBinding.etEmailId.text.toString()+"---"+activitySignUpBinding.etPassword.text.toString())
        firebaseAuth?.createUserWithEmailAndPassword(activitySignUpBinding.etEmailId.text.toString(), activitySignUpBinding.etPassword.text.toString())
            ?.addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = firebaseAuth?.getCurrentUser()

                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName(activitySignUpBinding.etName.text.toString())
                        .build()

                    user?.updateProfile(profileUpdates)
                        ?.addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(this, "Sign up successfully", Toast.LENGTH_SHORT).show();
                                finish()
                            }
                        }
                } else {
                    Toast.makeText(this, "Authentication failed. Please try again", Toast.LENGTH_SHORT).show();
                    Log.d("TAG", "signUp:=== "+task.exception.toString())
                    activitySignUpBinding.progress.visibility = View.GONE
                    activitySignUpBinding.btnSignUp.visibility = View.VISIBLE
                }
            }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnSignUp -> {
                if (TextUtils.isEmpty(activitySignUpBinding.etName.text.toString().trim())) {
                    Toast.makeText(this, "Please enter name", Toast.LENGTH_SHORT).show()
                } else if (TextUtils.isEmpty(activitySignUpBinding.etEmailId.text.toString().trim())) {
                    Toast.makeText(this, "Please enter email id", Toast.LENGTH_SHORT).show()
                } else if (TextUtils.isEmpty(activitySignUpBinding.etPassword.text.toString().trim())) {
                    Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show()
                } else if (activitySignUpBinding.etPassword.text.toString().trim().length < 8) {
                    Toast.makeText(this, "Minimum 8 character required for password", Toast.LENGTH_SHORT).show()
                } else if (TextUtils.isEmpty(activitySignUpBinding.etConfirmPassword.text.toString().trim())) {
                    Toast.makeText(this, "Please enter confirm password", Toast.LENGTH_SHORT).show()
                } else if (!activitySignUpBinding.etPassword.text.toString().trim().equals(activitySignUpBinding.etConfirmPassword.text.toString().trim())) {
                    Toast.makeText(this, "Password does not match", Toast.LENGTH_SHORT).show()
                } else {
                    signUp()
                }

            }
        }
    }

}
